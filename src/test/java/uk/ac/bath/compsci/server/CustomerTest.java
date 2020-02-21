package uk.ac.bath.compsci.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.text.NumberFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class CustomerTest {

    private static final NumberFormat FORMATTER = NumberFormat.getCurrencyInstance();

    @Nested
    class MustNotCreateCustomerWithInvalidParameters {
        @ParameterizedTest
        @ValueSource(strings = "")
        @NullAndEmptySource
        void customerNameMustNotBeBlank(final String username) {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new Customer(username, "password"))
                    .withMessage("Customer username cannot be empty.");
        }

        @ParameterizedTest
        @ValueSource(strings = "")
        @NullAndEmptySource
        void customerPasswordMustNotBeBlank(final String password) {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new Customer("username", password))
                    .withMessage("Customer password cannot be empty.");
        }
    }

    @Nested
    class AccountTests {
        Customer customer;

        @BeforeEach
        void setupAccountTests() {
            customer = new Customer("testuser", "testpassword");
            customer.addAccount(new Account("Main", 0.0));
            customer.addAccount(new Account("Checking", 0.0));
        }

        @Test
        void printAccountsShouldListAccounts() {
            assertThat(customer.printAccounts()).isEqualTo("Main: " + FORMATTER.format(0.0) + "\nChecking: " + FORMATTER.format(0.0));
        }

        @Test
        void addAccountShouldWork() {
            Account testAccount = new Account("Savings", 100.0);
            customer.addAccount(testAccount);
            assertThat(customer.getAccount("Savings")).isEqualTo(testAccount);
        }

        @Test
        void addingExistingAccountShouldThrowException() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> customer.addAccount(new Account("Main", 0.0)))
                    .withMessage("Account Main already exists for user testuser.");
        }

        @Test
        void gettingExistingAccountReturnsAccount() {
            assertThat(customer.getAccount("Main")).isInstanceOf(Account.class);
        }

        @Test
        void gettingUnknownAccountReturnsNull() {
            assertThat(customer.getAccount("Other")).isNull();
        }
    }

    @Nested
    class PayeeTests {
        Customer customer;

        @BeforeEach
        void setupPayeeTests() {
            customer = new Customer("testuser", "testpassword");
            customer.addPayee(new Payee(12345678, "01-23-45", "bob", "NEWBANK"));
            customer.addPayee(new Payee(98765432, "67-11-65", "julie", "OLDBANK"));
        }

        @Test
        void printPayeesShouldListPayees() {
            assertThat(customer.printPayees()).isEqualTo("bob: NEWBANK: 12345678: 01-23-45\njulie: OLDBANK: 98765432: 67-11-65");
        }

        @Test
        void addPayeeShouldWork() {
            Payee testPayee = new Payee(57463523, "98-77-23", "frank", "OTHERBANK");
            customer.addPayee(testPayee);
            assertThat(customer.getPayee("frank")).isEqualTo(testPayee);
        }

        @Test
        void addingExistingPayeeShouldThrowException() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> customer.addPayee(new Payee(12345678, "01-23-45", "bob", "NEWBANK")))
                    .withMessage("Payee bob already exists for user testuser.");
        }

        @Test
        void gettingExistingPayeeReturnsPayee() {
            assertThat(customer.getPayee("bob")).isInstanceOf(Payee.class);
        }

        @Test
        void gettingUnknownPayeeReturnsNull() {
            assertThat(customer.getFriend("rashid")).isNull();
        }
    }

    @Nested
    class FriendTests {
        Customer user1, user2, user3;

        @BeforeEach
        void setupFriendTests() {
            user1 = new Customer("testuser1", "testpassword1");
            user2 = new Customer("testuser2", "testpassword2");
            user3 = new Customer("testuser3", "testpassword3");
            user1.addFriend(user2);
            user2.addFriend(user1);
            user1.addFriend(user3);
            user3.addFriend(user1);
        }

        @Test
        void printFriendsShouldListFriends() {
            assertThat(user1.printFriends()).isEqualTo("testuser2\ntestuser3");
        }

        @Test
        void addFriendShouldWork() {
            user2.addFriend(user3);
            user3.addFriend(user2);
            assertThat(user2.getFriend("testuser3")).isEqualTo(user3);
            assertThat(user3.getFriend("testuser2")).isEqualTo(user2);
        }

        @Test
        void addingExistingFriendShouldThrowException() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> user1.addFriend(user2))
                    .withMessage("Friend testuser2 already exists for user testuser1.");
        }

        @Test
        void gettingExistingFriendReturnsFriend() {
            assertThat(user1.getFriend("testuser2")).isInstanceOf(Customer.class);
        }

        @Test
        void gettingUnknownFriendReturnsNull() {
            assertThat(user1.getFriend("someotheruser")).isNull();
        }
    }
}