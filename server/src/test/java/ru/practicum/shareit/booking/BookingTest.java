package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookingTest {

    @Test
    void equals_sameReference_returnsTrue() {
        Booking booking = new Booking();
        booking.setId(1L);

        assertThat(booking.equals(booking)).isTrue();
    }

    @Test
    void equals_null_returnsFalse() {
        Booking booking = new Booking();
        booking.setId(1L);

        assertThat(booking.equals(null)).isFalse();
    }

    @Test
    void equals_differentClass_returnsFalse() {
        Booking booking = new Booking();
        booking.setId(1L);

        assertThat(booking.equals("not a booking")).isFalse();
    }

    @Test
    void equals_sameId_returnsTrue() {
        Booking first = new Booking();
        first.setId(1L);
        Booking second = new Booking();
        second.setId(1L);
        second.setStatus(BookingStatus.APPROVED);

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void equals_differentId_returnsFalse() {
        Booking first = new Booking();
        first.setId(1L);
        Booking second = new Booking();
        second.setId(2L);

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void equals_withNullId_returnsFalse() {
        Booking first = new Booking();
        Booking second = new Booking();

        assertThat(first).isNotEqualTo(second);
    }
}
