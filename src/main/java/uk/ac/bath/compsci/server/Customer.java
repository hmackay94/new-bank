package uk.ac.bath.compsci.server;

import java.util.ArrayList;

public class Customer {
    private ArrayList<Account> accounts;

    public Customer() {
        accounts = new ArrayList<>();
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
    }   // end of Account

}
