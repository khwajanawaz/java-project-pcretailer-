package uk.ac.ncl.csc8404.pcretailer.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ncl.csc8404.pcretailer.customer.Customer;
import uk.ac.ncl.csc8404.pcretailer.model.CustomModel;
import uk.ac.ncl.csc8404.pcretailer.model.CustomModelFactory;
import uk.ac.ncl.csc8404.pcretailer.model.PresetModel;
import uk.ac.ncl.csc8404.pcretailer.payment.CreditCard;
import uk.ac.ncl.csc8404.pcretailer.payment.CreditCardFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    // my tests try full order flow, kinda nervous but seems ok

    private CreditCardFactory cardFactory;
    private CustomModelFactory customFactory;
    private OrderService service;
    private Date now;

    @BeforeEach
    void setUp() {
        cardFactory = new CreditCardFactory();
        customFactory = new CustomModelFactory();
        now = buildDate(2025, Calendar.JANUARY, 1);
        service = new OrderService(() -> new Date(now.getTime()));
    }

    @Test
    void placeOrderRejectsExpiredCard() {
        Customer customer = Customer.of("C-1", "naseema");
        CreditCard expired = cardFactory.register("13572468", buildDate(2020, Calendar.JANUARY, 1), "naseema");
        PresetModel preset = PresetModel.of("ACME", "Starter", List.of("CPU"));
        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(customer, expired, List.of(OrderLine.of(preset, 1))));
    }

    @Test
    void analyticsEmptyBeforeFulfilment() {
        OrderAnalytics analytics = service.analytics();
        assertTrue(analytics.largestCustomer().isEmpty());
        assertTrue(analytics.mostOrderedPresetModel().isEmpty());
        assertTrue(analytics.presetManufacturer().isEmpty());
        assertTrue(analytics.mostOrderedCustomPart().isEmpty());
    }

    @Test
    void fulfilmentProducesPlanAndAnalytics() {
        Customer alice = Customer.of("C-1", "nawaz");
        CreditCard card = cardFactory.register("12345670", buildDate(2030, Calendar.JANUARY, 1), "nawaz");

        PresetModel acmeBolt = PresetModel.of("ACME", "Bolt", List.of("CPU", "RAM"));
        PresetModel techNano = PresetModel.of("TechCorp", "Nano", List.of("CPU", "SSD"));
        CustomModel customRig = customFactory.create("Rig", List.of("CPU", "GPU"));

        CustomerOrder order = service.placeOrder(alice, card, List.of(
                OrderLine.of(acmeBolt, 2),
                OrderLine.of(techNano, 1),
                OrderLine.of(customRig, 3)
        ));

        FulfillmentPlan plan = service.fulfillOrder(order.getId());

        assertEquals(2, plan.getPresetModelCounts().get("ACME").get("Bolt"));
        assertEquals(1, plan.getPresetModelCounts().get("TechCorp").get("Nano"));
        assertEquals(3, plan.getCustomPartCounts().get("CPU"));
        assertEquals(3, plan.getCustomPartCounts().get("GPU"));

        OrderAnalytics analytics = service.analytics();
        assertEquals(alice, analytics.largestCustomer().orElseThrow());
        assertEquals("Bolt", analytics.mostOrderedPresetModel().orElseThrow());
        assertEquals("ACME", analytics.presetManufacturer().orElseThrow());
        assertEquals("CPU", analytics.mostOrderedCustomPart().orElseThrow());
    }

    @Test
    void analyticsResolveAlphabeticalTies() {
        Customer alice = Customer.of("C-1", "Adill");
        Customer bob = Customer.of("C-2", "nawaz");
        CreditCard aliceCard = cardFactory.register("23456781", buildDate(2031, Calendar.FEBRUARY, 1), "Alice");
        CreditCard bobCard = cardFactory.register("34567812", buildDate(2031, Calendar.MARCH, 1), "nawaz");

        PresetModel acmeAlpha = PresetModel.of("Acme", "Alpha", List.of("CPU"));
        PresetModel zetaAlpha = PresetModel.of("Zeta", "Alpha", List.of("CPU"));
        CustomModel custom = customFactory.create("Custom", List.of("CPU", "GPU"));

        CustomerOrder orderAlice = service.placeOrder(alice, aliceCard, List.of(
                OrderLine.of(acmeAlpha, 1),
                OrderLine.of(custom, 1)
        ));
        CustomerOrder orderBob = service.placeOrder(bob, bobCard, List.of(
                OrderLine.of(zetaAlpha, 1),
                OrderLine.of(custom.withPart("SSD"), 1)
        ));

        service.fulfillOrder(orderAlice.getId());
        service.fulfillOrder(orderBob.getId());

        OrderAnalytics analytics = service.analytics();
        assertEquals(alice, analytics.largestCustomer().orElseThrow(), "Alphabetical tie-break expected Alice");
        assertEquals("Acme", analytics.presetManufacturer().orElseThrow());
        assertEquals("Alpha", analytics.mostOrderedPresetModel().orElseThrow());
        assertEquals("CPU", analytics.mostOrderedCustomPart().orElseThrow());
    }

    @Test
    void cannotFulfilCancelledOrder() {
        Customer customer = Customer.of("C-3", "fathima");
        CreditCard card = cardFactory.register("45678123", buildDate(2032, Calendar.JANUARY, 1), "khwaja");
        PresetModel preset = PresetModel.of("ACME", "Starter", List.of("CPU"));

        CustomerOrder order = service.placeOrder(customer, card, List.of(OrderLine.of(preset, 1)));
        UUID id = order.getId();
        service.cancelOrder(id);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertThrows(IllegalStateException.class, () -> service.fulfillOrder(id));
    }

    private Date buildDate(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, dayOfMonth, 0, 0, 0);
        return calendar.getTime();
    }
}
