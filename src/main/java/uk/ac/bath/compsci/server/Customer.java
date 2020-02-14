package uk.ac.bath.compsci.server;

import java.util.ArrayList;

public class Customer {
    private ArrayList<Account> accounts;
    private ArrayList<Payee> payees;

    public Customer() {
        accounts = new ArrayList<>();
        payees = new ArrayList<>();
    }

    public String accountsToString() {
        String s = "";
        for (Account a : accounts) {
            s += a.toString();
        }
        return s;
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public Account getAccount(String accountName) {
        Account rtn = null;
        for (Account a : accounts) {
            if (a.getAccountName().equalsIgnoreCase(accountName)) {
                rtn = a;
            }
        }
        return rtn;
    }

    public void addPayee(Payee payee) {
        payees.add(payee);
    }

    public String printPayees() {
        StringBuilder rtn = new StringBuilder();
        for (Payee payee : payees) {
            rtn.append(payee).append("\n");
        }
        return rtn.toString();
    }

    public Payee getPayee(String payeeName) {
        Payee rtn = null;
        for (Payee p : payees) {
            if (p.getPayeeName().equalsIgnoreCase(payeeName)) {
                rtn = p;
            }
        }
        return rtn;
    }

}   // end of Customer
