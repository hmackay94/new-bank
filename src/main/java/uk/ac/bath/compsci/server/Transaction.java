package uk.ac.bath.compsci.server;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {
    private Date date;
    private String description;
    private Double amount;
    private String category;
    private static final NumberFormat FORMATTER = NumberFormat.getCurrencyInstance();

    public Transaction(Date txnDate, String txnDesc, Double txnAmt, String txnCat) {
        if (txnDate == null) {
            throw new IllegalArgumentException("Transaction date cannot be null.");
        }
        if (txnDesc == null || txnDesc.isEmpty()) {
            throw new IllegalArgumentException("Transaction description cannot be empty.");
        }
        if (txnAmt < 0) {
            throw new IllegalArgumentException("Transaction amount cannot be negative.");
        }
        this.amount = txnAmt;
        this.date = txnDate;
        this.description = txnDesc;
        this.category = txnCat;
    }

    public Transaction(Date txnDate, String txnDesc, Double txnAmt) {
        this(txnDate, txnDesc, txnAmt, "No category");
    }

    @Override
    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return (dateFormat.format(date) + ": " + category + ": " + description + ": " + FORMATTER.format(amount));
    }

    public Double getAmount() {
        return amount;
    }
}
