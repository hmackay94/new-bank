package uk.ac.bath.compsci.server;

import java.util.Date;
import java.util.HashMap;

import static java.util.Objects.isNull;

public class NewBank {
    private static final NewBank bank = new NewBank();
    private HashMap<String, Customer> customers;

    private NewBank() {
        customers = new HashMap<>();
        addTestData();
    }

    private void addTestData() {
        Customer bhagy = new Customer("Bhagy","password");
        createAccount(bhagy,"Main", 1000.0);
        customers.put(bhagy.getUsername(), bhagy);

        Customer christina = new Customer("Christina","password");
        createAccount(christina,"Savings", 1500.0);
        customers.put(christina.getUsername(), christina);

        Customer john = new Customer("John","password");
        createAccount(john,"Checking", 250.0);
        customers.put(john.getUsername(), john);

        Customer paul = new Customer("Paul","password");
        createAccount(paul,"Main", 250.0);
        createAccount(paul,"Savings", 500.0);
        customers.put(paul.getUsername(), paul);
    }

    public static NewBank getBank() {
        return bank;
    }

    public synchronized Customer checkLogInDetails(String username, String password) {
        if (customers.containsKey(username)) {
            Customer customer = customers.get(username);
            if (customer.passwordMatches(password)) {
                return customer;
            }
        }
        return null;
    }

    public synchronized Customer createCustomer(String username, String password) throws IllegalArgumentException {
        if (customers.containsKey(username)) {
            throw new IllegalArgumentException("User "+username+" already exists.");
        }
        else if (password.length()<8) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
        }
        else {
            Customer customer = new Customer(username,password);
            final double openingBalance = 0;
            customer.addAccount(new Account("Main", openingBalance));
            customers.put(username, customer);
            return customer;
        }

    }

    // commands from the NewBank customer are processed in this method
    public synchronized String processRequest(Customer customer, String request) {
        if (customer != null && customers.containsKey(customer.getUsername())) {

            String[] requestArray = request.split(" ");
            String cmd = requestArray[0];

            switch (cmd) {
                case "SHOWMYACCOUNTS":
                    return customer.printAccounts();
                case "NEWACCOUNT":
                    if (requestArray.length != 2) {
                        return "FAIL - invalid command";
                    } else {
                        return createAccount(customer, requestArray[1], 0.0);
                    }
                case "DEPOSIT":
                    //Deposit money into one of your accounts
                    try {
                        if (!requestArray[1].isEmpty()) {
                            if (requestArray[1].equalsIgnoreCase("?")) {
                                return "DEPOSIT <AccountName> <Amount>";
                            }
                        }
                        if (requestArray[1].isEmpty() || requestArray[2].isEmpty()) {
                            return "FAIL - invalid command";
                        }
                        return deposit(customer, requestArray[1], Double.parseDouble(requestArray[2]));
                    } catch (IllegalArgumentException | NullPointerException e) {
                        return "FAIL - " + e.getMessage();
                    }
                case "WITHDRAW":
                    //Withdraw money from one of your accounts
                    if (!requestArray[1].isEmpty()) {
                        if (requestArray[1].equalsIgnoreCase("?")) {
                            return "WITHDRAW <FromAccount> <Amount>";
                        }
                    }
                    try {
                        if (requestArray[1].isEmpty() || requestArray[2].isEmpty()) {
                            return "FAIL - invalid command";
                        }
                        return withdraw(customer, requestArray[1], Double.parseDouble(requestArray[2]));
                    } catch (NumberFormatException e) {
                        return "FAIL - invalid amount";
                    }
                case "ADDTRANSACTION":
                    //Add a transaction into one of your accounts
                    try {
                        if (!requestArray[1].isEmpty()) {
                            if (requestArray[1].equalsIgnoreCase("?")) {
                                return "ADDTRANSACTION <AccountName> <Category> <Description> <Amount>";
                            }
                        }
                        if (requestArray[1].isEmpty() || requestArray[2].isEmpty()) {
                            return "FAIL - invalid command";
                        }
                        //<AccountName> <Date> <Category> <Description> <Amount>
                        Date transactionDate = new Date();
                        return addTransaction(customer, requestArray[1], transactionDate, requestArray[2], requestArray[3], Double.parseDouble(requestArray[4]));
                    } catch (IllegalArgumentException | NullPointerException e) {
                        return "FAIL - " + e.getMessage();
                    }
                case "MOVE":
                    try {
                        if (requestArray.length != 4) {
                            return "FAIL - invalid command";
                        } else {
                            return moveMoneyBetweenAccounts(customer,
                                    Double.parseDouble(requestArray[1]),
                                    requestArray[2],
                                    requestArray[3]);
                        }
                    } catch (NumberFormatException e) {
                        return "FAIL - cannot process amount";
                    }
                case "PAY":
                    if (!requestArray[1].isEmpty()) {
                        if (requestArray[1].equalsIgnoreCase("?")) {
                            return "PAY <PayeeName> <FromAccount> <Amount>";
                        }
                    }
                    try {
                        if (requestArray[1].isEmpty() || requestArray[2].isEmpty()) {
                            return "FAIL - invalid command";
                        }
                        return pay(customer, requestArray[1], requestArray[2], Double.parseDouble(requestArray[3]));
                    } catch (NumberFormatException e) {
                        return "FAIL - invalid amount";
                    }
                case "PRINTSTATEMENT":
                    //Print a statement of balances and recent transactions to screen
                    if (requestArray.length >= 2 && !requestArray[1].isEmpty()) {
                        if (requestArray[1].equalsIgnoreCase("?")) {
                            return "PRINTSTATEMENT <AccountName>";
                        }
                    }
                    if (!requestArray[1].isEmpty()) {
                        return printStatement(customer, requestArray[1]);
                    }
                    return "FAIL";
                case "ADDFRIEND":
                    if (requestArray[1].isEmpty()) {
                        return "FAIL - specify a username to add as a friend";
                    }
                    Customer friendToAdd = customers.get(requestArray[1]);
                    if (friendToAdd == null) {
                        return "FAIL - no customer with username "+requestArray[1];
                    }
                    try {
                        customer.addFriend(friendToAdd);
                        friendToAdd.addFriend(customer);
                    }
                    catch (IllegalArgumentException e) {
                        return "FAIL - "+e.getMessage();
                    }
                    return "SUCCESS - "+customer.getUsername()+" and "+friendToAdd.getUsername()+" are now friends.";
                case "SHOWMYFRIENDS":
                    return customer.printFriends();
                case "REMOVEFRIEND":
                    if (requestArray[1].isEmpty()) {
                        return "FAIL - specify a username to remove as a friend";
                    }
                    Customer friendToRemove = customers.get(requestArray[1]);
                    if (friendToRemove == null) {
                        return "FAIL - no customer with username "+requestArray[1];
                    }
                    try {
                        customer.removeFriend(friendToRemove);
                        friendToRemove.removeFriend(customer);
                    }
                    catch (IllegalArgumentException e) {
                        return "FAIL - "+e.getMessage();
                    }
                    return "SUCCESS - "+customer.getUsername()+" and "+friendToRemove.getUsername()+" are no longer friends.";
                case "ADDPAYEE":
                    //Add a new payee
                    //AccNum, SortCode, PayeeName, Bank, Reference
                    if (!requestArray[1].isEmpty()) {
                        if (requestArray[1].equalsIgnoreCase("?")) {
                            return "ADDPAYEE <AccNum> <SortCode> <PayeeName> <Bank>";
                        }
                    }
                    if (requestArray[1].isEmpty() || requestArray[2].isEmpty() || requestArray[3].isEmpty() || requestArray[4].isEmpty()) {
                        return "FAIL";
                    } else {
                        return addPayee(customer, Integer.parseInt(requestArray[1]), requestArray[2], requestArray[3], requestArray[4]);
                    }
                case "SHOWMYPAYEES":
                    //Print a list of payees
                    return customer.printPayees();
                case "FINDTRANSACTION":
                    //TODO - search for a transaction
                    return "FAIL";
                case "REQUESTLOAN":
                    //TODO - request a mirco loan from NewBank
                    return "FAIL";
                case "MAKELOANPAYMENT":
                    //TODO - make a payment to a loan
                    return "FAIL";
                case "EXIT":
                    // EXIT command handled in NewBankClientHandler and ExampleClient classes
                    return "SUCCESS";
                default:
                    return "Command not recognized";
            }
        }
        return "FAIL";
    }

    private String deposit(Customer customer, String accountName, Double amount) {
        Account customerAccount = (customer.getAccount(accountName));
        if (customerAccount == null) {
            return "FAIL - Account does not exist";
        }
        // TODO: Move Transaction creation
        customerAccount.deposit(new Transaction(new Date(), "Customer deposit", amount));
        return "SUCCESS";
    }

    private String withdraw(Customer customer, String accountName, Double amount) {
        Account customerAccount = (customer.getAccount(accountName));
        if (customerAccount == null) {
            return "FAIL - Account does not exist";
        }
        // TODO: Move Transaction creation
        customerAccount.withdraw(new Transaction(new Date(), "Customer withdrawal", amount));
        return "SUCCESS";
    }

    private String printStatement(Customer customer, String accountName) {
        Account customerAccount = (customer.getAccount(accountName));
        if (customerAccount == null) {
            return "FAIL - Account does not exist";
        }
        return customerAccount.printTransactions();
    }

    private String addPayee(Customer customer, Integer accountNumber, String sortCode, String payeeName, String bank) {
        try {
            customer.addPayee(new Payee(accountNumber, sortCode, payeeName, bank));
        }
        catch (IllegalArgumentException e) {
            return "FAIL - "+e.getMessage();
        }
        return "SUCCESS";
    }

    private String pay(Customer customer, String payeeName, String fromAccount, double amount) {
        Account customerAccount = customer.getAccount(fromAccount);
        if (customerAccount == null) {
            return "FAIL - Account does not exist";
        }
        Payee customerPayee = customer.getPayee(payeeName);
        if (customerPayee == null) {
            return "FAIL - Payee does not exist";
        }
        Date transactionDate = new Date();
        customerAccount.withdraw(new Transaction(transactionDate, "Pay " + payeeName, amount));
        if (customerPayee.isNewBankAccount()) {
            Account payeeAccount = customers.get(payeeName).getAccount("Main");
            payeeAccount.deposit(new Transaction(transactionDate,"Received from "+customer.getUsername(),amount));
        }
        return "SUCCESS - Payment made to " + customerPayee.getPayeeName() + " " + customerPayee.getSortCode() + " " + customerPayee.getAccountNumber();
    }

    private String createAccount(final Customer customer, final String accountName, double initialBalance) {
        final Account newAccount = new Account(accountName, initialBalance);
        try {
            customer.addAccount(newAccount);
        }
        catch (IllegalArgumentException e) {
            return "FAIL - "+e.getMessage();
        }
        return "SUCCESS";
    }

    private String moveMoneyBetweenAccounts(final Customer customer, final double amount, final String accountNameFrom, final String accountNameTo) {
        final Account accountFrom = customer.getAccount(accountNameFrom);
        if (accountFrom == null) {
            return "FAIL - Account from does not exist";
        }
        final Account accountTo = customer.getAccount(accountNameTo);
        if (isNull(accountTo)) {
            return "FAIL - Account to does not exist";
        }
        Date transactionDate = new Date();
        try {
            accountFrom.withdraw(new Transaction(transactionDate, "Moved money to " + accountNameTo, amount));
            accountTo.deposit(new Transaction(transactionDate, "Moved money from " + accountNameFrom, amount));
        } catch (IllegalArgumentException e) {
            return "FAIL - " + e.getMessage();
        }
        return "SUCCESS";
    }

    private String addTransaction(Customer customer, String accountName, Date txnDate, String txnCategory, String txnDescription, Double amount) {
        Account customerAccount = (customer.getAccount(accountName));
        Double absAmount = java.lang.Math.abs(amount);
        if (customerAccount == null) {
            return "FAIL - Account does not exist";
        }
        if (amount == 0) {
            return "FAIL - Amount must not be zero";
        }
        if (amount < 0) {
            customerAccount.withdraw(new Transaction(txnDate, txnDescription, absAmount, txnCategory));
        } else {
            customerAccount.deposit(new Transaction(txnDate, txnDescription, absAmount, txnCategory));
        }
        return "SUCCESS";
    }

}   //end of NewBank
