package uk.ac.bath.compsci;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.ac.bath.compsci.Utils.convertToTitleCaseSplitting;

class UtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {"hello", "HELLO", "hElLo"})
    void canCovertDifferentCasesStringToTitleCase(final String hello) {
        final String actual = convertToTitleCaseSplitting(hello);

        assertThat(actual).isEqualTo("Hello");
    }

    @Test
    void canCovertSentenceToTitleCase() {
        final String actual = convertToTitleCaseSplitting("hEllo WORld");

        assertThat(actual).isEqualTo("Hello World");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void providingBlankStringReturnsBlank(final String emptyString) {
        final String actual = convertToTitleCaseSplitting(emptyString);

        assertThat(actual).isEqualTo(emptyString);
    }
}