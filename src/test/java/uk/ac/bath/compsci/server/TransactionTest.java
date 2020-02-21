package uk.ac.bath.compsci.server;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class TransactionTest {

    public static final Date DEFAULT_DATE = new Date();
    public static final String DEFAULT_DESCRIPTION = "Transaction description";
    public static final Double DEFAULT_AMOUNT = 1.0;
    private static final NumberFormat FORMATTER = NumberFormat.getCurrencyInstance();

    @Nested
    class MustNotCreateTransactionWithInvalidParameters {
        @Test
        void initialTransactionDateMustNotBeNull() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new Transaction(null, DEFAULT_DESCRIPTION, DEFAULT_AMOUNT))
                    .withMessage("Transaction date cannot be null.");
        }

        @ParameterizedTest
        @ValueSource(strings = "")
        @NullAndEmptySource
        void transactionDescriptionMustNotBeBlank(final String txnDesc) {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new Transaction(DEFAULT_DATE, txnDesc, DEFAULT_AMOUNT))
                    .withMessage("Transaction description cannot be empty.");
        }

        @Test
        void transactionAmountMustNotBeNegative() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new Transaction(DEFAULT_DATE, DEFAULT_DESCRIPTION, -1.0))
                    .withMessage("Transaction amount cannot be negative.");
        }
    }

    @Test
    void toStringShouldPrintTheTransactionDetails() {
        final Transaction testTransaction = new Transaction(DEFAULT_DATE, DEFAULT_DESCRIPTION, DEFAULT_AMOUNT);
        final String actual = testTransaction.toString();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        assertThat(actual).isEqualTo(dateFormat.format(DEFAULT_DATE) + ": No category: " + DEFAULT_DESCRIPTION + ": " + FORMATTER.format(DEFAULT_AMOUNT));
    }

}
