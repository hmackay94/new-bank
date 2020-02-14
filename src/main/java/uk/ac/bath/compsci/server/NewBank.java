package uk.ac.bath.compsci.server;

import java.util.Date;
import java.util.HashMap;

public class NewBank {
    private static final NewBank bank = new NewBank();
    private HashMap<String, Customer> customers;

    private NewBank() {
        customers = new HashMap<>();
        addTestData();
    }

    private void addTestData() {
        final Date currentDate = new Date();

        Customer bhagy = new Customer();
        final double bhagyOpeningBalance = 1000;
        bhagy.addAccount(new Account("Main", bhagyOpeningBalance, new Transaction(currentDate, "Opening Balance", bhagyOpeningBalance)));
        customers.put("Bhagy", bhagy);

        Customer christina = new Customer();
        final double christinaOpeningBalance = 1500;
        christina.addAccount(new Account("Savings", christinaOpeningBalance, new Transaction(currentDate, "Opening Balance", christinaOpeningBalance)));
        customers.put("Christina", christina);

        Customer john = new Customer();
        final double johnOpeningBalance = 250;
        john.addAccount(new Account("Checking", johnOpeningBalance, new Transaction(currentDate, "Opening Balance", johnOpeningBalance)));
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
            String cmd = request2[0];
            String rtn = "";

            switch (cmd) {
                case "SHOWMYACCOUNTS":
                    return showMyAccounts(customer);
                case "NEWACCOUNT":
                    //TODO - open a new account
                    return "FAIL";
                case "DEPOSIT":
                    //Deposit money into one of your accounts
                    try {
                        if (request2[1].isEmpty() || request2[2].isEmpty()) {
                            return "FAIL - invalid command";
                        }
                        return deposit(customer, request2[1], Double.parseDouble(request2[2]));
                    } catch (NumberFormatException e) {
                        return "FAIL - invalid amount";
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
                    //TODO - move money between your accounts
                    return "FAIL";
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
}
