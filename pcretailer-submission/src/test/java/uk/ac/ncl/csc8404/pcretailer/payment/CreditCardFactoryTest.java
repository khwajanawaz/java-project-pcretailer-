package uk.ac.ncl.csc8404.pcretailer.payment;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class CreditCardFactoryTest {

    // checking unique card nums and expiry stuff coz scary

    private final CreditCardFactory factory = new CreditCardFactory();

    @Test
    void registersUniqueCards() {
        CreditCard card = factory.register("12345678", futureDate(2030, Calendar.JANUARY, 1), "naseema");
        assertEquals("12345678", card.getNumber());
        assertEquals("naseema", card.getHolderName());
        assertTrue(card.isValid(futureDate(2025, Calendar.JANUARY, 1)));
        assertThrows(IllegalStateException.class,
                () -> factory.register("12345678", futureDate(2030, Calendar.FEBRUARY, 1), "naseema"));
    }

    @Test
    void detectsExpiredCards() {
        CreditCard card = factory.register("87654321", futureDate(2024, Calendar.JANUARY, 1), "jai");
        assertFalse(card.isValid(futureDate(2025, Calendar.JANUARY, 1)));
    }

    private Date futureDate(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, dayOfMonth, 0, 0, 0);
        return calendar.getTime();
    }
}
