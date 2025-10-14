package uk.ac.ncl.csc8404.pcretailer.order;

import uk.ac.ncl.csc8404.pcretailer.model.ComputerModel;

import java.util.Objects;

/**
 * Represents a single line item within an order.
 */
public final class OrderLine {

    // one model plus quantity, thats the whole line lol

    private final ComputerModel model;
    private final int quantity;

    private OrderLine(ComputerModel model, int quantity) {
        this.model = model;
        this.quantity = quantity;
    }

    /**
     * Creates a new order line after validating input.
     *
     * @param model    the computer model being ordered.
     * @param quantity quantity ordered; must be positive.
     * @return immutable order line.
     */
    public static OrderLine of(ComputerModel model, int quantity) {
        Objects.requireNonNull(model, "model");
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        return new OrderLine(model, quantity);
    }

    public ComputerModel getModel() {
        return model;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderLine that)) {
            return false;
        }
        return quantity == that.quantity && model.equals(that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model, quantity);
    }

    @Override
    public String toString() {
        return "OrderLine{" +
                "model=" + model +
                ", quantity=" + quantity +
                '}';
    }
}
