package uk.ac.bath.compsci.server;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Customer {
    private String username;
    private String password;
    private ArrayList<Account> accounts;
    private ArrayList<Payee> payees;
    private ArrayList<Customer> friends;

    public Customer(String username, String password) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Customer username cannot be empty.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Customer password cannot be empty.");
        }
        this.username = username;
        this.password = password;
        accounts = new ArrayList<>();
        payees = new ArrayList<>();
        friends = new ArrayList<>();
    }


    public String getUsername() {
        return this.username;
    }

    public boolean passwordMatches(String password) {
        return this.password.equals(password);
    }

    public String printAccounts() {
        return accounts.stream()
                .map(Account::toString)
                .collect(Collectors.joining("\n"));
    }

    public void addAccount(Account account) throws IllegalArgumentException {
        if (getAccount(account.getAccountName()) != null) {
            throw new IllegalArgumentException("Account " + account.getAccountName() + " already exists for user " + getUsername() + ".");
        }
        accounts.add(account);
    }

    public Account getAccount(String accountName) {
        return accounts.stream()
                .filter(account -> account.getAccountName().equals(accountName))
                .findFirst()
                .orElse(null);
    }

    public void addPayee(Payee payee) throws IllegalArgumentException {
        if (getPayee(payee.getPayeeName()) != null) {
            throw new IllegalArgumentException("Payee " + payee.getPayeeName() + " already exists for user " + getUsername() + ".");
        }
        payees.add(payee);
    }

    public String printPayees() {
        return payees.stream()
                .map(Payee::toString)
                .collect(Collectors.joining("\n"));
    }

    public Payee getPayee(String payeeName) {
        return payees.stream()
                .filter(payee -> payee.getPayeeName().equals(payeeName))
                .findFirst()
                .orElse(null);
    }

    public void addFriend(Customer friend) throws IllegalArgumentException {
        if (getFriend(friend.getUsername()) != null) {
            throw new IllegalArgumentException("Friend " + friend.getUsername() + " already exists for user " + getUsername() + ".");
        }
        friends.add(friend);
    }

    public void removeFriend(Customer friend) throws IllegalArgumentException {
        if (getFriend(friend.getUsername()) == null) {
            throw new IllegalArgumentException(friend.getUsername() + " is not a friend of user " + getUsername() + ".");
        }
        friends.remove(friend);
    }


    public String printFriends() {
        if (friends.isEmpty()) {
            return "No friend yet recorded...";
        } else {
            return friends.stream()
                    .map(Customer::getUsername)
                    .sorted()
                    .collect(Collectors.joining("\n"));
        }
    }


    public String showFriendAccounts(String friendName) {

        Customer customerFriend = getFriend(friendName);

        return customerFriend.accounts.stream()
                .map(Account::getAccountName)
                .collect(Collectors.joining(" - "));

    }


    public Customer getFriend(String friendName) {
        return friends.stream()
                .filter(friend -> friend.getUsername().equals(friendName))
                .findFirst()
                .orElse(null);
    }

}   // end of Customer
