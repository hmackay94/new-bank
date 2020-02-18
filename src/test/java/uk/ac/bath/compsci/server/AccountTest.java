package uk.ac.bath.compsci.server;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class AccountTest {

    @Nested
    class MustNotCreateAccountWithInvalidParameters {
        @ParameterizedTest
        @ValueSource(strings = " ")
        @NullAndEmptySource
        void accountNameMustNotBeBlank(final String accountName) {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new Account(accountName, 0.0))
                    .withMessage("accountName must not be blank");
        }

        @Test
        void openingBalanceMustNotBeNegative() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new Account("Test", -1.0))
                    .withMessage("openingBalance must not be negative");
        }
    }

    @Test
    void toStringShouldPrintTheAccountNameThenBalance() {
        final Account testAccount = new Account("Test", 1.0);
        final String actual = testAccount.toString();
        assertThat(actual).isEqualTo("Test: £1.00");
    }

    @Test
    void printTransactionsShouldReturnInitialTransactionToStringSeparatedByNewLine() {
        final Account testAccount = new Account("Test", 1.0);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        final String transactionToString = dateFormat.format(new Date())+": No category: Opening Balance: £1.00";
//        given(testAccount.getTransactionList().get(0).toString()).willReturn(transactionToString);

        final String actual = testAccount.printTransactions();
        assertThat(actual).isEqualTo(transactionToString + "\n");
    }


    @Nested
    class Deposit {
        @Test
        void depositTransactionMustNotBeNull() {
            final Account testAccount = new Account("Test", 1.0);

            assertThatNullPointerException()
                    .isThrownBy(() -> testAccount.deposit(null))
                    .withMessage("depositTransaction must not be null");
        }

        @Test
        void depositShouldIncreaseBalanceByAmountAndAddTransactionToList() {
            final Account testAccount = new Account("Test", 1.0);

            assertThat(testAccount.getBalance()).isEqualTo(1);
            assertThat(testAccount.getTransactionList()).hasSize(1);

            testAccount.deposit(new Transaction(new Date(),"Description",1.0));

            assertThat(testAccount.getBalance()).isEqualTo(2);
            assertThat(testAccount.getTransactionList()).hasSize(2);
        }
    }

    @Nested
    class Withdraw {
        @Test
        void withdrawalTransactionMustNotBeNull() {
            final Account testAccount = new Account("Test", 1.0);

            assertThatNullPointerException()
                    .isThrownBy(() -> testAccount.withdraw( null))
                    .withMessage("withdrawalTransaction must not be null");
        }

        @Test
        void amountMustNotBeGreaterThanBalance() {
            final Account testAccount = new Account("Test", 1.0);

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> testAccount.withdraw(new Transaction(new Date(),"Description",2.0)))
                    .withMessage("Insufficient funds - amount must not be greater than balance");
        }

        @Test
        void withdrawShouldDecreaseBalanceByAmountAndAddTransactionToList() {
            final Account testAccount = new Account("Test", 1.0);

            assertThat(testAccount.getBalance()).isEqualTo(1);
            assertThat(testAccount.getTransactionList()).hasSize(1);

            testAccount.withdraw(new Transaction(new Date(),"Description",1.0));

            assertThat(testAccount.getBalance()).isEqualTo(0);
            assertThat(testAccount.getTransactionList()).hasSize(2);
        }
    }
}