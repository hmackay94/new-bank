package uk.ac.bath.compsci.server;

import java.util.Date;
import java.util.HashMap;

import static java.util.Objects.isNull;
import static uk.ac.bath.compsci.Utils.convertToTitleCaseSplitting;

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
        john.addAccount(new Account("Checking", johnOpeningBalance, new Transaction(CURRENT_DATE, "Opening Balance", johnOpeningBalance)));
        customers.put("John", john);

        Customer paul = new Customer();
        final double paulOpeningBalanceMain = 250;
        final double paulOpeningBalanceSavings = 500;
        paul.addAccount(new Account("Main", paulOpeningBalanceMain, new Transaction(CURRENT_DATE, "Opening Balance", paulOpeningBalanceMain)));
        paul.addAccount(new Account("Savings", paulOpeningBalanceSavings, new Transaction(CURRENT_DATE, "Opening Balance", paulOpeningBalanceSavings)));
        customers.put("Paul", paul);
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

    public synchronized String createCustomer(String userName, String password) {
        if (customers.containsKey(userName)) {
            return "NOT UNIQUE USERNAME";
        }
        else if (password.length()<8) {
            return "PASSWORD TOO SHORT";
        }
        else {
            Customer newCustomer = new Customer();
            final double openingBalance = 0;
            final Date currentDate = new Date();
            newCustomer.addAccount(new Account("Main", openingBalance, new Transaction(currentDate, "Opening Balance", openingBalance)));
            customers.put(userName, newCustomer);
            return "REGISTERED AND MAIN ACCOUNT CREATED";
        }

    }

    // commands from the NewBank customer are processed in this method
    public synchronized String processRequest(CustomerID customer, String request) {
        if (customers.containsKey(customer.getKey())) {

            String[] request2 = request.split(" ");
            String cmd = request2[0];
            String rtn = "";

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
                case "PAYFRIEND":
                    //TODO - Pay a friend
                    return "FAIL";
                case "PAY":
                    if (!request2[1].isEmpty()) {
                        if (request2[1].equalsIgnoreCase("?")) {
                            return "PAY <PayeeName>, <FromAccount>, <Amount>";
                        }
                    }
                    try {
                        if (request2[1].isEmpty() || request2[2].isEmpty()) {
                            return "FAIL - invalid command";
                        }
                        return pay(customer, request2[1], request2[2], Double.parseDouble(request2[3]));
                    } catch (NumberFormatException e) {
                        return "FAIL - invalid amount";
                    }
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
                    rtn = "FAIL";
                    if (!request2[1].isEmpty()) {
                        rtn = printStatement(customer, request2[1]);
                    }
                    return rtn;
                case "WITHDRAW":
                    //Withdraw money from one of your accounts
                    if (!request2[1].isEmpty()) {
                        if (request2[1].equalsIgnoreCase("?")) {
                            return "WITHDRAW <FromAccount>, <Amount>";
                        }
                    }
                    try {
                        if (request2[1].isEmpty() || request2[2].isEmpty()) {
                            return "FAIL - invalid command";
                        }
                        return withdraw(customer, request2[1], Double.parseDouble(request2[2]));
                    } catch (NumberFormatException e) {
                        return "FAIL - invalid amount";
                    }
                case "FINDTRANSACTION":
                    //TODO - search for a transaction
                    return "FAIL";
                case "ADDFRIEND":
                    //TODO - add a friend (payee)
                    return "FAIL";
                case "ADDPAYEE":
                    //Add a new payee
                    //AccNum, SortCode, PayeeName, Bank, Reference
                    rtn = "";
                    if (!request2[1].isEmpty()) {
                        if (request2[1].equalsIgnoreCase("?")) {
                            return "ADDPAYEE <AccNum>, <SortCode>, <PayeeName>, <Bank>";
                        }
                    }
                    if (request2[1].isEmpty() || request2[2].isEmpty() || request2[3].isEmpty() || request2[4].isEmpty()) {
                        rtn = "FAIL";
                    } else {
                        rtn = addPayee(customer, Integer.parseInt(request2[1]), request2[2], request2[3], request2[4]);
                    }
                    return rtn;
                case "SHOWMYPAYEES":
                    //Print a list of payees
                    rtn = printListOfPayees(customer);
                    return rtn;
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

    private String withdraw(CustomerID customer, String AccountName, Double amount) {
        Account customerAccount = (customers.get(customer.getKey())).getAccount(AccountName);
        if (customerAccount == null) {
            return "FAIL - Account does not exist";
        }
        // TODO: Move Transaction creation
        customerAccount.withdraw(amount, new Transaction(new Date(), "Customer withdrawal", amount));
        return "SUCCESS";
    }

    private String printStatement(CustomerID customer, String AccountName) {
        Account customerAccount = (customers.get(customer.getKey())).getAccount(AccountName);
        if (customerAccount == null) {
            return "FAIL - Account does not exist";
        }
        return customerAccount.printTransactions();
    }

    private String addPayee(CustomerID customer, Integer AccNum, String SortCode, String PayeeName, String Bank) {
        Customer myCustomer = customers.get(customer.getKey());
        if (myCustomer == null) {
            return "FAIL";
        }
        myCustomer.addPayee(new Payee(AccNum, SortCode, PayeeName, Bank));
        return "SUCCESS";
    }

    private String printListOfPayees(CustomerID customer) {
        Customer myCustomer = customers.get(customer.getKey());
        if (myCustomer == null) {
            return "FAIL";
        }
        return myCustomer.printPayees();
    }

    private String pay(CustomerID customer, String PayeeName, String FromAccount, double amount) {
        Account customerAccount = customers.get(customer.getKey()).getAccount(FromAccount);
        Payee customerPayee = customers.get(customer.getKey()).getPayee(PayeeName);
        if (customerAccount == null) {
            return "FAIL - Account does not exist";
        }
        if (customerPayee == null) {
            return "FAIL - Payee does not exist";
        }
        boolean OK = customerAccount.withdraw(amount, new Transaction(new Date(), "Pay " + PayeeName, amount));
        if (OK && customerPayee.isNewBankAccount()) {
            //TODO Deposit money into New Bank customers account
        }
        return "SUCCESS - Payment made to " + customerPayee.getPayeeName() + " " + customerPayee.getSortCode() + " " + customerPayee.getAccountNumber();
    }

    private String newAccount(final CustomerID customerID, final String accountName) {
        final double openingBalance = 0;
        if (isNull(customers.get(customerID.getKey()).getAccount(accountName))) {
            final Account newAccount = new Account(convertToTitleCaseSplitting(accountName), openingBalance,
                    new Transaction(CURRENT_DATE, "Opening Balance", openingBalance));
            customers.get(customerID.getKey()).addAccount(newAccount);
        }

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
        try {
            accountFrom.withdraw(amount, new Transaction(CURRENT_DATE, "Moved money to " + accountNameTo, amount));
            accountTo.deposit(amount, new Transaction(CURRENT_DATE, "Moved money from " + accountNameFrom, amount));
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
        return "SUCCESS";
    }
}
