package uk.ac.ncl.csc8404.pcretailer.order;

import uk.ac.ncl.csc8404.pcretailer.customer.Customer;
import uk.ac.ncl.csc8404.pcretailer.payment.CreditCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Default immutable order implementation with controlled state transitions.
 */
final class StandardOrder implements CustomerOrder {

    // keep order state under control or bugs get wild

    private final UUID id;
    private final Customer customer;
    private final List<OrderLine> orderLines;
    private final CreditCard paymentMethod;
    private final Date placedAt;
    private OrderStatus status;

    StandardOrder(UUID id, Customer customer, List<OrderLine> lines, CreditCard paymentMethod, Date placedAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.customer = Objects.requireNonNull(customer, "customer");
        this.orderLines = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(lines, "lines")));
        this.paymentMethod = Objects.requireNonNull(paymentMethod, "paymentMethod");
        this.placedAt = new Date(Objects.requireNonNull(placedAt, "placedAt").getTime());
        if (orderLines.isEmpty()) {
            throw new IllegalArgumentException("order must contain at least one line");
        }
        this.status = OrderStatus.PLACED;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public Customer getCustomer() {
        return customer;
    }

    @Override
    public List<OrderLine> getOrderLines() {
        return orderLines;
    }

    @Override
    public CreditCard getPaymentMethod() {
        return paymentMethod;
    }

    @Override
    public Date getPlacedAt() {
        return new Date(placedAt.getTime());
    }

    @Override
    public OrderStatus getStatus() {
        return status;
    }

    void markCancelled() {
        status = OrderStatus.CANCELLED;
    }

    void markFulfilled() {
        status = OrderStatus.FULFILLED;
    }

    boolean isCancelled() {
        return status == OrderStatus.CANCELLED;
    }

    boolean isFulfilled() {
        return status == OrderStatus.FULFILLED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StandardOrder that)) {
            return false;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "StandardOrder{" +
                "id=" + id +
                ", customer=" + customer +
                ", lines=" + orderLines +
                ", status=" + status +
                '}';
    }
}
