package uk.ac.bath.compsci.server;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import static java.util.Objects.isNull;

public class NewBank {
    private static final NewBank bank = new NewBank();
    private HashMap<String, Customer> customers;
    public static final Date CURRENT_DATE = new Date();

    private NewBank() {
        customers = new HashMap<>();
        addTestData();
    }

    private void addTestData() {

        Customer bhagy = new Customer();
        final double bhagyOpeningBalance = 1000;
        bhagy.addAccount(new Account("Main", bhagyOpeningBalance, new Transaction(CURRENT_DATE, "Opening Balance", bhagyOpeningBalance)));
        customers.put("Bhagy", bhagy);

        Customer christina = new Customer();
        final double christinaOpeningBalance = 1500;
        christina.addAccount(new Account("Savings", christinaOpeningBalance, new Transaction(CURRENT_DATE, "Opening Balance", christinaOpeningBalance)));
        customers.put("Christina", christina);

        Customer john = new Customer();
        final double johnOpeningBalance = 250;
        john.addAccount(new Account("Checking", johnOpeningBalance, new Transaction(CURRENT_DATE, "Opening Balance", christinaOpeningBalance)));
        customers.put("John", john);
    }

    public static NewBank getBank() {
        return bank;
    }

    public synchronized CustomerID checkLogInDetails(String userName, String password) {
        if (customers.containsKey(userName)) {
            return new CustomerID(userName);
        }
        return null;
    }

    // commands from the NewBank customer are processed in this method
    public synchronized String processRequest(CustomerID customer, String request) {
        if (customers.containsKey(customer.getKey())) {

            String[] request2 = request.split(" ");
            Arrays.stream(request2).forEach(System.out::println);
            String cmd = request2[0];

            switch (cmd) {
                case "SHOWMYACCOUNTS":
                    return showMyAccounts(customer);
                case "NEWACCOUNT":
                    if (request2.length != 2) {
                        return "FAIL - invalid command";
                    } else {
                        return newAccount(customer, request2[1]);
                    }
                case "DEPOSIT":
                    //Deposit money into one of your accounts
                    try {
                        if (request2[1].isEmpty() || request2[2].isEmpty()) {
                            return "FAIL - invalid command";
                        }
                        return deposit(customer, request2[1], Double.parseDouble(request2[2]));
                    } catch (IllegalArgumentException | NullPointerException e) {
                        return e.getMessage();
                    }
                case "PAY":
                    //TODO - Pay a friend (payee)
                    return "FAIL";
                case "MOVE":
                    try {
                        if (request2.length != 4) {
                            return "FAIL - invalid command";
                        } else {
                            return moveMoneyBetweenAccounts(customer,
                                    Double.parseDouble(request2[1]),
                                    request2[2],
                                    request2[3]);
                        }
                    } catch (NumberFormatException e) {
                        return "FAIL - cannot process amount";
                    }
                case "PRINTSTATEMENT":
                    //Print a statement of balances and recent transactions to screen
                    String rtn = "FAIL";
                    if (!request2[1].isEmpty()) {
                        rtn = printStatement(customer, request2[1]);
                    }
                    return rtn;
                case "WITHDRAW":
                    //TODO - withdraw money from one of your accounts
                    return "FAIL";
                case "FINDTRANSACTION":
                    //TODO - search for a transaction
                    return "FAIL";
                case "ADDFRIEND":
                    //TODO - add a friend (payee)
                    return "FAIL";
                case "REQUESTLOAN":
                    //TODO - request a mirco loan from NewBank
                    return "FAIL";
                case "MAKELOANPAYMENT":
                    //TODO - make a payment to a loan
                    return "FAIL";
                case "EXIT":
                    //TODO - gracefully end the client session
                    return "SUCCESS";
                default:
                    return "Command not recognized";
            }
        }
        return "FAIL";
    }

    private String showMyAccounts(CustomerID customer) {
        return (customers.get(customer.getKey())).accountsToString();
    }

    private String deposit(CustomerID customer, String AccountName, Double amount) {
        Account customerAccount = (customers.get(customer.getKey())).getAccount(AccountName);
        if (customerAccount == null) {
            return "FAIL - Account does not exist";
        }
        // TODO: Move Transaction creation
        customerAccount.deposit(amount, new Transaction(new Date(), "Customer deposit", amount));
        return "SUCCESS";
    }

    private String printStatement(CustomerID customer, String AccountName) {
        Account customerAccount = (customers.get(customer.getKey())).getAccount(AccountName);
        if (customerAccount == null) {
            return "FAIL - Account does not exist";
        }
        return customerAccount.printTransactions();
    }

    private String newAccount(final CustomerID customerID, final String accountName) {
        final double openingBalance = 0;
        final Account newAccount = new Account(accountName, openingBalance,
                new Transaction(CURRENT_DATE, "Opening Balance", openingBalance));
        customers.get(customerID.getKey()).addAccount(newAccount);
        return "SUCCESS";
    }

    private String moveMoneyBetweenAccounts(final CustomerID customerID, final double amount, final String accountNameFrom, final String accountNameTo) {
        final Account accountFrom = customers.get(customerID.getKey()).getAccount(accountNameFrom);
        final Account accountTo = customers.get(customerID.getKey()).getAccount(accountNameTo);
        if (isNull(accountFrom)) {
            return "FAIL - Account from does not exist";
        }
        if (isNull(accountTo)) {
            return "FAIL - Account to does not exist";
        }
        // TODO: Only do deposit if withdraw was successful
        accountFrom.withdraw(amount, new Transaction(CURRENT_DATE, "Moved money to " + accountNameTo, amount));
        accountTo.deposit(amount, new Transaction(CURRENT_DATE, "Moved money from " + accountNameFrom, amount));
        return "SUCCESS";
    }
}
