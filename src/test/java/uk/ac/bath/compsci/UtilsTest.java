package uk.ac.bath.compsci;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.ac.bath.compsci.Utils.convertToTitleCaseSplitting;

class UtilsTest {

    @Test
    void canCovertLowerCaseStringToTitleCase() {
        final String actual = convertToTitleCaseSplitting("hello");

        assertThat(actual).isEqualTo("Hello");
    }

    @Test
    void canCovertUpperCaseStringToTitleCase() {
        final String actual = convertToTitleCaseSplitting("HELLO");

        assertThat(actual).isEqualTo("Hello");
    }

    @Test
    void canCovertMixedCaseStringToTitleCase() {
        final String actual = convertToTitleCaseSplitting("hElLo");

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