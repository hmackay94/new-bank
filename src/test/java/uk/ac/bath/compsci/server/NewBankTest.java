package uk.ac.bath.compsci.server;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class NewBankTest {

    @Test
    void newAccountWillCreateNewAccount() {
        final NewBank bank = NewBank.getBank();
        final CustomerID customerID = mock(CustomerID.class);
        given(customerID.getKey()).willReturn("John");

        final String actual = bank.processRequest(customerID, "NEWACCOUNT Main");

        assertThat(actual).isEqualTo("SUCCESS");
    }

    @Test
    void newAccountWillFailIfCommandIsEmpty() {
        final NewBank bank = NewBank.getBank();
        final CustomerID customerID = mock(CustomerID.class);
        given(customerID.getKey()).willReturn("John");

        final String actual = bank.processRequest(customerID, "NEWACCOUNT");

        assertThat(actual).isEqualTo("FAIL - invalid command");
    }

    @Disabled("Should be failing but is currently passing")
    @Test
    void newAccountWillFailIfAccountAlreadyExists() {
        final NewBank bank = NewBank.getBank();
        final CustomerID customerID = mock(CustomerID.class);
        given(customerID.getKey()).willReturn("John");

        final String actual = bank.processRequest(customerID, "NEWACCOUNT Checking");

        assertThat(actual).isEqualTo("FAIL");
    }

    @Test
    void moveWillMoveMoneyBetweenAccounts() {
        final NewBank bank = NewBank.getBank();
        final CustomerID customerID = mock(CustomerID.class);
        given(customerID.getKey()).willReturn("John");

        bank.processRequest(customerID, "NEWACCOUNT Main");
        final String actual = bank.processRequest(customerID, "MOVE 100 Checking Main");

        assertThat(actual).isEqualTo("SUCCESS");
    }

    @Test
    void moveWillFailIfCommandIsEmpty() {
        final NewBank bank = NewBank.getBank();
        final CustomerID customerID = mock(CustomerID.class);
        given(customerID.getKey()).willReturn("John");

        final String actual = bank.processRequest(customerID, "MOVE");

        assertThat(actual).isEqualTo("FAIL - invalid command");
    }

    @Test
    void moveWillFailIfNotProvidedValidAmount() {
        final NewBank bank = NewBank.getBank();
        final CustomerID customerID = mock(CustomerID.class);
        given(customerID.getKey()).willReturn("John");

        final String actual = bank.processRequest(customerID, "MOVE a Checking Main");

        assertThat(actual).isEqualTo("FAIL - cannot process amount");
    }

    @Test
    void moveWillFailIfBankAccountDoesNotExist() {
        final NewBank bank = NewBank.getBank();
        final CustomerID customerID = mock(CustomerID.class);
        given(customerID.getKey()).willReturn("John");

        bank.processRequest(customerID, "NEWACCOUNT Main");
        final String actual = bank.processRequest(customerID, "MOVE 100 Checking Savings");

        assertThat(actual).isEqualTo("FAIL - Account to does not exist");
    }
}