package uk.ac.bath.compsci.server;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class NewBankTest {

    @Test
    void newAccountWillCreateNewAccount() {
        final NewBank bank = NewBank.getBank();
        final Customer customer = mock(Customer.class);
        given(customer.getUsername()).willReturn("John");

        final String actual = bank.processRequest(customer, "NEWACCOUNT Main");

        assertThat(actual).isEqualTo("SUCCESS");
    }

    @Test
    void newAccountWillFailIfCommandIsEmpty() {
        final NewBank bank = NewBank.getBank();
        final Customer customer = mock(Customer.class);
        given(customer.getUsername()).willReturn("John");

        final String actual = bank.processRequest(customer, "NEWACCOUNT");

        assertThat(actual).isEqualTo("FAIL - invalid command");
    }

    @Test
    void newAccountWillFailIfAccountAlreadyExists() {
        final NewBank bank = NewBank.getBank();
        final Customer customer = bank.createCustomer("Joanne","password");

        final String actual = bank.processRequest(customer, "NEWACCOUNT Main");
        assertThat(actual).isEqualTo("FAIL - Account Main already exists for user Joanne.");
    }

    @Test
    void moveWillMoveMoneyBetweenAccounts() {
        final NewBank bank = NewBank.getBank();
        final Customer customer = mock(Customer.class);
        given(customer.getUsername()).willReturn("John");
        given(customer.getAccount("Main")).willReturn(new Account("Main",100.0));
        given(customer.getAccount("Checking")).willReturn(new Account("Main",100.0));

        assertThat(bank.processRequest(customer, "MOVE 100 Checking Main")).isEqualTo("SUCCESS");
    }

    @Test
    void moveWillFailIfCommandIsEmpty() {
        final NewBank bank = NewBank.getBank();
        final Customer customer = mock(Customer.class);
        given(customer.getUsername()).willReturn("John");

        final String actual = bank.processRequest(customer, "MOVE");

        assertThat(actual).isEqualTo("FAIL - invalid command");
    }

    @Test
    void moveWillFailIfNotProvidedValidAmount() {
        final NewBank bank = NewBank.getBank();
        final Customer customer = mock(Customer.class);
        given(customer.getUsername()).willReturn("John");

        final String actual = bank.processRequest(customer, "MOVE a Checking Main");

        assertThat(actual).isEqualTo("FAIL - cannot process amount");
    }

    @Test
    void moveWillFailIfBankAccountDoesNotExist() {
        final NewBank bank = NewBank.getBank();
        final Customer customer = mock(Customer.class);
        given(customer.getUsername()).willReturn("John");
        given(customer.getAccount("Main")).willReturn(new Account("Main",0.0));
        final String actual = bank.processRequest(customer, "MOVE 100 Main Savings");

        assertThat(actual).isEqualTo("FAIL - Account to does not exist");
    }
}