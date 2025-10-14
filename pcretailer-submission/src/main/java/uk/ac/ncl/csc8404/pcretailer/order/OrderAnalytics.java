package uk.ac.ncl.csc8404.pcretailer.order;

import uk.ac.ncl.csc8404.pcretailer.customer.Customer;

import java.util.Objects;
import java.util.Optional;

/**
 * Immutable view of analytics computed across fulfilled orders.
 */
public final class OrderAnalytics {

    // store the top stats so reporting isnt a headache

    private final Customer largestCustomer;
    private final String mostOrderedPresetModel;
    private final String presetManufacturer;
    private final String mostOrderedCustomPart;

    OrderAnalytics(Customer largestCustomer,
                   String mostOrderedPresetModel,
                   String presetManufacturer,
                   String mostOrderedCustomPart) {
        this.largestCustomer = largestCustomer;
        this.mostOrderedPresetModel = mostOrderedPresetModel;
        this.presetManufacturer = presetManufacturer;
        this.mostOrderedCustomPart = mostOrderedCustomPart;
    }

    /**
     * @return largest customer by fulfilled orders if available.
     */
    public Optional<Customer> largestCustomer() {
        return Optional.ofNullable(largestCustomer);
    }

    /**
     * @return preset model with highest fulfilled quantity if available.
     */
    public Optional<String> mostOrderedPresetModel() {
        return Optional.ofNullable(mostOrderedPresetModel);
    }

    /**
     * @return manufacturer corresponding to {@link #mostOrderedPresetModel()} if present.
     */
    public Optional<String> presetManufacturer() {
        return Optional.ofNullable(presetManufacturer);
    }

    /**
     * @return most ordered custom part if available.
     */
    public Optional<String> mostOrderedCustomPart() {
        return Optional.ofNullable(mostOrderedCustomPart);
    }

    @Override
    public String toString() {
        return "OrderAnalytics{" +
                "largestCustomer=" + largestCustomer +
                ", mostOrderedPresetModel='" + mostOrderedPresetModel + '\'' +
                ", presetManufacturer='" + presetManufacturer + '\'' +
                ", mostOrderedCustomPart='" + mostOrderedCustomPart + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderAnalytics that)) {
            return false;
        }
        return Objects.equals(largestCustomer, that.largestCustomer)
                && Objects.equals(mostOrderedPresetModel, that.mostOrderedPresetModel)
                && Objects.equals(presetManufacturer, that.presetManufacturer)
                && Objects.equals(mostOrderedCustomPart, that.mostOrderedCustomPart);
    }

    @Override
    public int hashCode() {
        return Objects.hash(largestCustomer, mostOrderedPresetModel, presetManufacturer, mostOrderedCustomPart);
    }
}
