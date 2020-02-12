package uk.ac.bath.compsci.server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {
    private Date date;
    private String description;
    private Double amount;
    private String category;

    public Transaction(Date txnDate, String txnDesc, Double txnAmt) {
        this.amount = txnAmt;
        this.date = txnDate;
        this.description = txnDesc;
        this.category = "No category";
    }

    public Transaction(Date txnDate, String txnDesc, Double txnAmt, String txnCat) {
        this.amount = txnAmt;
        this.date = txnDate;
        this.description = txnDesc;
        this.category = txnCat;
    }

    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return (dateFormat.format(date) + ": " + category + ": " + description + ": " + amount);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

}
