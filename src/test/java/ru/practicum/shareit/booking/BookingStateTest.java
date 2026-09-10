package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class BookingStateTest {

    @Test
    void from_withKnownValue_returnsState() {
        assertThat(BookingState.from("CURRENT")).contains(BookingState.CURRENT);
    }

    @Test
    void from_withUnknownValue_returnsEmpty() {
        Optional<BookingState> result = BookingState.from("UNSUPPORTED_STATUS");

        assertThat(result).isEmpty();
    }
}
