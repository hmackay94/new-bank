package uk.ac.bath.compsci.server;


import java.text.NumberFormat;
import java.util.ArrayList;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class Account {
    private final String accountName;
    private double balance;
    private ArrayList<Transaction> transactionList = new ArrayList<>();
    private static final NumberFormat FORMATTER = NumberFormat.getCurrencyInstance();

    public Account(final String accountName, final double openingBalance, final Transaction initialTransaction) {
        this.accountName = requireNonBlank(accountName, "accountName must not be blank");
        this.balance = requireNonNegative(openingBalance, "openingBalance must not be negative");
        transactionList.add(requireNonNull(initialTransaction, "initialTransaction must not be null"));
    }

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

    public void deposit(final double amount, final Transaction depositTransaction) {
        requireNonNegative(amount, "amount must not be negative");
        requireNonNull(depositTransaction, "depositTransaction must not be null");
        this.transactionList.add(depositTransaction);
        this.balance = balance + amount;
    }

    public void withdraw(final double amount, final Transaction withdrawalTransaction) {
        requireNonNegative(amount, "amount must not be negative");
        requireNonNull(withdrawalTransaction, "withdrawalTransaction must not be null");
        if (this.balance >= amount) {
            this.transactionList.add(withdrawalTransaction);
            this.balance = (balance - amount);
        } else {
            throw new IllegalArgumentException("amount must not be greater than balance");
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
}
