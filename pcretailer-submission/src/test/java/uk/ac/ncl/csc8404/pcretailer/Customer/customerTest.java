package uk.ac.ncl.csc8404.pcretailer.Customer;

import org.junit.jupiter.api.Test;
import uk.ac.ncl.csc8404.pcretailer.customer.Customer;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void testFactoryMethodTrimsInput() {
        // Testing the factory method with whitespace - should trim automatically
        Customer c = Customer.of("  C-1  ", "  led  ");

        assertEquals("C-1", c.getIdentifier());
        assertEquals("led", c.getDisplayName());
    }

    @Test
    void shouldRejectInvalidInputs() {
        // Let's test all the ways this can fail
        assertThrows(IllegalArgumentException.class, () -> Customer.of(null, "Name"));
        assertThrows(IllegalArgumentException.class, () -> Customer.of("  ", "Name"));

        // Now test the display name validation
        assertThrows(IllegalArgumentException.class, () -> Customer.of("Id", null));
        assertThrows(IllegalArgumentException.class, () -> Customer.of("Id", "   "));
    }

    @Test
    void testEqualsAndHashCodeImplementation() {
        // Create test customers - same ID but different names
        Customer customerA = Customer.of("C-2", "nawaz");
        Customer customerB = Customer.of("C-2", "Different");
        Customer customerC = Customer.of("C-3", "nawaz");

        // These should be equal since they have same identifier
        assertEquals(customerA, customerB);
        assertEquals(customerA.hashCode(), customerB.hashCode());

        // This one has different ID so should not be equal
        assertNotEquals(customerA, customerC);
    }

    @Test
    void checkToStringContainsExpectedInfo() {
        Customer testCustomer = Customer.of("C-9", "led");
        String stringRep = testCustomer.toString();

        // Make sure both pieces of info are there
        assertTrue(stringRep.contains("C-9"));
        assertTrue(stringRep.contains("led"));
    }

    @Test
    void testEqualsWithEdgeCases() {
        Customer customer = Customer.of("TEST", "User");

        // Test some edge cases I always forget about
        assertNotEquals(customer, null);
        assertNotEquals(customer, "not a customer object");
        assertEquals(customer, customer); // reflexive property
    }
}