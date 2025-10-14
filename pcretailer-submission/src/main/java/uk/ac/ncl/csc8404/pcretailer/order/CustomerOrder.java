package uk.ac.ncl.csc8404.pcretailer.order;

import uk.ac.ncl.csc8404.pcretailer.customer.Customer;
import uk.ac.ncl.csc8404.pcretailer.payment.CreditCard;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Read-only view of a customer order exposed to clients.
 */
public interface CustomerOrder {

    // this is read only view so no funny edits pls

    /**
     * @return unique identifier of the order.
     */
    UUID getId();

    /**
     * @return customer placing the order.
     */
    Customer getCustomer();

    /**
     * @return lines included in the order.
     */
    List<OrderLine> getOrderLines();

    /**
     * @return credit card used for the order.
     */
    CreditCard getPaymentMethod();

    /**
     * @return timestamp of when the order was placed.
     */
    Date getPlacedAt();

    /**
     * @return current lifecycle status.
     */
    OrderStatus getStatus();
}
