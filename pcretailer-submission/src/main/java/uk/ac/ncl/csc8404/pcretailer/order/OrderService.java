package uk.ac.ncl.csc8404.pcretailer.order;

import uk.ac.ncl.csc8404.pcretailer.customer.Customer;
import uk.ac.ncl.csc8404.pcretailer.model.ComputerModel;
import uk.ac.ncl.csc8404.pcretailer.model.CustomComputerModel;
import uk.ac.ncl.csc8404.pcretailer.model.PresetComputerModel;
import uk.ac.ncl.csc8404.pcretailer.payment.CreditCard;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Core service responsible for order lifecycle management and analytics.
 */
public final class OrderService {

    //  this class glues things together

    private final Supplier<Date> clock;
    private final Map<UUID, StandardOrder> orders = new HashMap<>();
    private final Map<Customer, Integer> fulfilledByCustomer = new HashMap<>();
    private final Map<String, Map<String, Integer>> fulfilledPresetCounts = new TreeMap<>();
    private final Map<String, Integer> fulfilledCustomParts = new TreeMap<>();

    /**
     * Creates a service configured with the system clock.
     */
    public OrderService() {
        this(Date::new);
    }

    /**
     * Creates a service with an injected clock, primarily for testing.
     *
     * @param clock supplier used to obtain timestamps.
     */
    public OrderService(Supplier<Date> clock) {
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    /**
     * Places a new order while validating inputs and payment details.
     *
     * @param customer customer placing the order.
     * @param card     credit card used for payment.
     * @param lines    lines included in the order.
     * @return immutable order record.
     */
    public CustomerOrder placeOrder(Customer customer, CreditCard card, List<OrderLine> lines) {
        Objects.requireNonNull(customer, "customer");
        Objects.requireNonNull(card, "card");
        Objects.requireNonNull(lines, "lines");
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("order must contain lines");
        }

        Date now = clock.get();
        if (!card.isValid(now)) {
            throw new IllegalArgumentException("credit card is expired");
        }

        List<OrderLine> safeLines = new ArrayList<>(lines);
        UUID id = UUID.randomUUID();
        StandardOrder order = new StandardOrder(id, customer, safeLines, card, now);
        orders.put(id, order);
        return order;
    }

    /**
     * Cancels an order if it has not yet been fulfilled.
     *
     * @param orderId identifier of the order.
     */
    public void cancelOrder(UUID orderId) {
        StandardOrder order = requireOrder(orderId);
        if (order.isFulfilled()) {
            throw new IllegalStateException("cannot cancel a fulfilled order");
        }
        order.markCancelled();
    }

    /**
     * Performs fulfilment of the supplied order and produces the plan for downstream processes.
     *
     * @param orderId identifier of the order.
     * @return aggregated fulfilment plan.
     */
    public FulfillmentPlan fulfillOrder(UUID orderId) {
        StandardOrder order = requireOrder(orderId);
        if (order.isCancelled()) {
            throw new IllegalStateException("cannot fulfil a cancelled order");
        }
        if (order.isFulfilled()) {
            throw new IllegalStateException("order already fulfilled");
        }

        Map<String, Map<String, Integer>> presetAggregation = new TreeMap<>();
        Map<String, Integer> customAggregation = new TreeMap<>();

        for (OrderLine line : order.getOrderLines()) {
            ComputerModel model = line.getModel();
            if (model instanceof PresetComputerModel preset) {
                aggregatePreset(line.getQuantity(), preset, presetAggregation);
            } else if (model instanceof CustomComputerModel) {
                aggregateCustom(line.getQuantity(), model, customAggregation);
            } else {
                aggregateCustom(line.getQuantity(), model, customAggregation);
            }
        }

        order.markFulfilled();
        updateAnalytics(order, presetAggregation, customAggregation);
        return new FulfillmentPlan(presetAggregation, customAggregation);
    }

    /**
     * @return immutable view of analytics computed so far.
     */
    public OrderAnalytics analytics() {
        Customer largestCustomer = findLargestCustomer();
        PresetSelection presetSelection = findTopPresetModel();
        String presetModel = presetSelection == null ? null : presetSelection.model();
        String manufacturer = presetSelection == null ? null : presetSelection.manufacturer();
        String customPart = findTopCustomPart();
        return new OrderAnalytics(largestCustomer, presetModel, manufacturer, customPart);
    }

    private String findTopCustomPart() {
        String winner = null;
        int winnerCount = 0;
        for (Map.Entry<String, Integer> entry : fulfilledCustomParts.entrySet()) {
            String part = entry.getKey();
            int count = entry.getValue();
            if (winner == null
                    || count > winnerCount
                    || (count == winnerCount && part.compareTo(winner) < 0)) {
                winner = part;
                winnerCount = count;
            }
        }
        return winner;
    }

    private PresetSelection findTopPresetModel() {
        String winnerManufacturer = null;
        String winnerModel = null;
        int winnerCount = 0;
        for (Map.Entry<String, Map<String, Integer>> manufacturerEntry : fulfilledPresetCounts.entrySet()) {
            String manufacturer = manufacturerEntry.getKey();
            for (Map.Entry<String, Integer> modelEntry : manufacturerEntry.getValue().entrySet()) {
                String model = modelEntry.getKey();
                int count = modelEntry.getValue();
                if (winnerModel == null
                        || count > winnerCount
                        || (count == winnerCount && compareManufacturerModel(
                        manufacturer, model, winnerManufacturer, winnerModel) < 0)) {
                    winnerManufacturer = manufacturer;
                    winnerModel = model;
                    winnerCount = count;
                }
            }
        }
        return winnerModel == null ? null : new PresetSelection(winnerManufacturer, winnerModel, winnerCount);
    }

    private Customer findLargestCustomer() {
        Customer winner = null;
        int winnerCount = 0;
        for (Map.Entry<Customer, Integer> entry : fulfilledByCustomer.entrySet()) {
            Customer candidate = entry.getKey();
            int count = entry.getValue();
            if (winner == null
                    || count > winnerCount
                    || (count == winnerCount && compareCustomers(candidate, winner) < 0)) {
                winner = candidate;
                winnerCount = count;
            }
        }
        return winner;
    }

    private void updateAnalytics(StandardOrder order,
                                 Map<String, Map<String, Integer>> presetAggregation,
                                 Map<String, Integer> customAggregation) {
        fulfilledByCustomer.merge(order.getCustomer(), 1, Integer::sum);
        for (Map.Entry<String, Map<String, Integer>> manufacturerEntry : presetAggregation.entrySet()) {
            Map<String, Integer> models = fulfilledPresetCounts.computeIfAbsent(
                    manufacturerEntry.getKey(), key -> new TreeMap<>());
            for (Map.Entry<String, Integer> modelEntry : manufacturerEntry.getValue().entrySet()) {
                models.merge(modelEntry.getKey(), modelEntry.getValue(), Integer::sum);
            }
        }
        for (Map.Entry<String, Integer> part : customAggregation.entrySet()) {
            fulfilledCustomParts.merge(part.getKey(), part.getValue(), Integer::sum);
        }
    }

    private void aggregateCustom(int quantity, ComputerModel model, Map<String, Integer> customAggregation) {
        for (String part : model.getParts()) {
            customAggregation.merge(part, quantity, Integer::sum);
        }
    }

    private void aggregatePreset(int quantity,
                                 PresetComputerModel preset,
                                 Map<String, Map<String, Integer>> presetAggregation) {
        Map<String, Integer> models = presetAggregation.computeIfAbsent(
                preset.getManufacturer(), key -> new TreeMap<>());
        models.merge(preset.getName(), quantity, Integer::sum);
    }

    private StandardOrder requireOrder(UUID orderId) {
        Objects.requireNonNull(orderId, "orderId");
        StandardOrder order = orders.get(orderId);
        if (order == null) {
            throw new IllegalArgumentException("order not found: " + orderId);
        }
        return order;
    }

    private int compareManufacturerModel(String manufacturerA, String modelA, String manufacturerB, String modelB) {
        int manufacturerComparison = manufacturerA.compareTo(manufacturerB);
        if (manufacturerComparison != 0) {
            return manufacturerComparison;
        }
        return modelA.compareTo(modelB);
    }

    private int compareCustomers(Customer left, Customer right) {
        int byName = left.getDisplayName().compareTo(right.getDisplayName());
        if (byName != 0) {
            return byName;
        }
        return left.getIdentifier().compareTo(right.getIdentifier());
    }

    private record PresetSelection(String manufacturer, String model, int quantity) {
    }
}
