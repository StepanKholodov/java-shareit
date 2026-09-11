package ru.practicum.shareit.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NullOrNotBlankValidatorTest {

    private final NullOrNotBlankValidator validator = new NullOrNotBlankValidator();

    @Test
    void isValid_null_returnsTrue() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void isValid_nonBlankString_returnsTrue() {
        assertThat(validator.isValid("Ivan", null)).isTrue();
    }

    @Test
    void isValid_emptyString_returnsFalse() {
        assertThat(validator.isValid("", null)).isFalse();
    }

    @Test
    void isValid_whitespaceOnly_returnsFalse() {
        assertThat(validator.isValid("   ", null)).isFalse();
    }
}
