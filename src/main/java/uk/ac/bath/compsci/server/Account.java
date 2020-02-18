package uk.ac.bath.compsci.server;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class Account {
    private final String accountName;
    private double balance;
    private ArrayList<Transaction> transactionList = new ArrayList<>();
    private static final NumberFormat FORMATTER = NumberFormat.getCurrencyInstance();

    public Account(final String accountName, final double openingBalance) {
        this.accountName = requireNonBlank(accountName, "accountName must not be blank");
        this.balance = requireNonNegative(openingBalance, "openingBalance must not be negative");
        Transaction initialTransaction = new Transaction(new Date(),"Opening Balance",balance);
        transactionList.add(requireNonNull(initialTransaction, "initialTransaction must not be null"));
    }

    @Override
    public String toString() {
        return (accountName + ": " + FORMATTER.format(balance));
    }

    public String printTransactions() {
        StringBuilder rtn = new StringBuilder();
        for (Transaction transaction : transactionList) {
            rtn.append(transaction).append("\n");
        }
        return rtn.toString();
    }

    public void deposit(final Transaction depositTransaction) {
        requireNonNull(depositTransaction, "depositTransaction must not be null");
        double amount = depositTransaction.getAmount();
        requireNonNegative(amount, "amount must not be negative");
        this.transactionList.add(depositTransaction);
        this.balance = balance + amount;
    }

    public void withdraw(final Transaction withdrawalTransaction) {
        requireNonNull(withdrawalTransaction, "withdrawalTransaction must not be null");
        double amount = withdrawalTransaction.getAmount();
        requireNonNegative(amount, "amount must not be negative");
        if (this.balance >= amount) {
            this.transactionList.add(withdrawalTransaction);
            this.balance = (balance - amount);
        }
        else {
            throw new IllegalArgumentException("Insufficient funds - amount must not be greater than balance");
        }
    }

    public String getAccountName() {
        return this.accountName;
    }

    public double getBalance() {
        return this.balance;
    }

    public ArrayList<Transaction> getTransactionList() {
        return this.transactionList;
    }

    private static String requireNonBlank(final String toCheck, final String message) {
        if (isBlank(toCheck))
            throw new IllegalArgumentException(message);
        return toCheck;
    }

    private static double requireNonNegative(final double toCheck, final String message) {
        if (toCheck < 0)
            throw new IllegalArgumentException(message);
        return toCheck;
    }
}   // end of Account
