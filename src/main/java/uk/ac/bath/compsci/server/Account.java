package uk.ac.bath.compsci.server;
import java.util.ArrayList;
import java.util.Date;

public class Account {
    private String accountName;
    private double balance;
    private ArrayList<Transaction> transactionList = new ArrayList<Transaction>();
    private int transactionCount = 0;
    private Date currentDate = new Date();

    public Account(String accountName, double openingBalance) {
        this.accountName = accountName;
        this.balance = openingBalance;
        Transaction t1 = new Transaction(this, currentDate, "Opening Balance", openingBalance);
        transactionList.add(t1);
        this.transactionCount++;
    }

    public String toString() {
        return (accountName + ": " + balance);
    }

    public String printTransactions() {
        String rtn = "";
        for (int i = 0; i < transactionCount; i++) {
            rtn = rtn + transactionList.get(i) + "\n";
        }
        return rtn;
    }

    public void deposit(double amount) {
        Transaction t1 = new Transaction(this, currentDate, "Customer deposit", amount);
        this.transactionList.add(t1);
        this.transactionCount++;
        this.balance = balance + amount;
    }

    public void withdraw(double amount) {
        if (this.balance >= amount) {
            Transaction t1 = new Transaction(this, currentDate, "Customer deposit", (amount * -1));
            this.transactionList.add(t1);
            this.transactionCount++;
            this.balance = (balance - amount);
        } else {
            System.out.println("Not enough money to complete transaction");
        }
    }

    public String getAccountName() {
        return this.accountName;
    }

}
