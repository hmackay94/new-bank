package uk.ac.bath.compsci.server;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AccountTest {

    @Nested
    class MustNotCreateAccountWithInvalidParameters {
        @ParameterizedTest
        @ValueSource(strings = " ")
        @NullAndEmptySource
        void accountNameMustNotBeBlank(final String accountName) {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new Account(accountName, 0, mock(Transaction.class)))
                    .withMessage("accountName must not be blank");
        }

        @Test
        void openingBalanceMustNotBeNegative() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new Account("Test", -1, mock(Transaction.class)))
                    .withMessage("openingBalance must not be negative");
        }

        @Test
        void initialTransactionMustNotBeNull() {
            assertThatNullPointerException()
                    .isThrownBy(() -> new Account("Test", 1, null))
                    .withMessage("initialTransaction must not be null");
        }
    }

    @Test
    void toStringShouldPrintTheAccountNameThenBalance() {
        final Account testAccount = new Account("Test", 1, mock(Transaction.class));
        final String actual = testAccount.toString();
        assertThat(actual).isEqualTo("Test: £1.00 - ");
    }

    @Test
    void printTransactionsShouldReturnInitialTransactionToStringSeparatedByNewLine() {
        final Transaction mockTransaction = mock(Transaction.class);
        final Account testAccount = new Account("Test", 1, mockTransaction);

        final String transactionToString = "01/01/2020 : Opening Balance : £0.00";
        given(mockTransaction.toString()).willReturn(transactionToString);

        final String actual = testAccount.printTransactions();
        assertThat(actual).isEqualTo(transactionToString + "\n");
    }


    @Nested
    class Deposit {
        @Test
        void amountMustNotBeNegative() {
            final Account testAccount = new Account("Test", 1, mock(Transaction.class));

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> testAccount.deposit(-1, mock(Transaction.class)))
                    .withMessage("amount must not be negative");
        }

        @Test
        void depositTransactionMustNotBeNull() {
            final Account testAccount = new Account("Test", 1, mock(Transaction.class));

            assertThatNullPointerException()
                    .isThrownBy(() -> testAccount.deposit(1, null))
                    .withMessage("depositTransaction must not be null");
        }

        @Test
        void depositShouldIncreaseBalanceByAmountAndAddTransactionToList() {
            final Account testAccount = new Account("Test", 1, mock(Transaction.class));

            assertThat(testAccount.getBalance()).isEqualTo(1);
            assertThat(testAccount.getTransactionList()).hasSize(1);

            testAccount.deposit(1, mock(Transaction.class));

            assertThat(testAccount.getBalance()).isEqualTo(2);
            assertThat(testAccount.getTransactionList()).hasSize(2);
        }
    }

    @Nested
    class Withdraw {
        @Test
        void amountMustNotBeNegative() {
            final Account testAccount = new Account("Test", 1, mock(Transaction.class));

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> testAccount.withdraw(-1, mock(Transaction.class)))
                    .withMessage("amount must not be negative");
        }

        @Test
        void withdrawalTransactionMustNotBeNull() {
            final Account testAccount = new Account("Test", 1, mock(Transaction.class));

            assertThatNullPointerException()
                    .isThrownBy(() -> testAccount.withdraw(1, null))
                    .withMessage("withdrawalTransaction must not be null");
        }

        @Test
        void amountMustNotBeGreaterThanBalance() {
            final Account testAccount = new Account("Test", 1, mock(Transaction.class));

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> testAccount.withdraw(2, mock(Transaction.class)))
                    .withMessage("FAIL - Insufficient funds - amount must not be greater than balance");
        }

        @Test
        void withdrawShouldDecreaseBalanceByAmountAndAddTransactionToList() {
            final Account testAccount = new Account("Test", 1, mock(Transaction.class));

            assertThat(testAccount.getBalance()).isEqualTo(1);
            assertThat(testAccount.getTransactionList()).hasSize(1);

            testAccount.withdraw(1, mock(Transaction.class));

            assertThat(testAccount.getBalance()).isEqualTo(0);
            assertThat(testAccount.getTransactionList()).hasSize(2);
        }
    }
}