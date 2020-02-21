package uk.ac.bath.compsci.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NewBankClientHandler extends Thread {

    private NewBank bank;
    private BufferedReader in;
    private PrintWriter out;


    public NewBankClientHandler(Socket s) throws IOException {
        bank = NewBank.getBank();
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(s.getOutputStream(), true);
    }

    public void run() {
        // keep getting requests from the client and processing them
        try {
            // ask for user name
            String username = "";
            String password = "";
            Customer customer;

            out.println("Enter LOGIN or SIGNUP");
            String initCmd = in.readLine();
            if (initCmd.equals("LOGIN")) {

                out.println("Enter Username");
                username = in.readLine();
                // ask for password
                out.println("Enter Password");
                password = in.readLine();
                out.println("Checking Details...");
            }

            if (initCmd.equals("SIGNUP")) {
                out.println("Enter Requested Username");
                username = in.readLine();
                // ask for password
                out.println("Enter Password");
                password = in.readLine();
                out.println("Setting up account");
                // create customer account
                try {
                    bank.createCustomer(username, password);
                } catch (IllegalArgumentException e) {
                    out.println("FAIL - " + e.getMessage());
                }
            }

            // authenticate user and get customer ID token from bank for use in subsequent requests
            customer = bank.checkLogInDetails(username, password);

            // if the user is authenticated then get requests from the user and process them
            if (customer != null) {
                out.println("Log In Successful. What do you want to do?");
                System.out.println(Thread.currentThread().getName() + " running for " + customer.getUsername());
                while (true) {
                    String request = in.readLine();
                    System.out.println(Thread.currentThread().getName() + " - Request from " + customer.getUsername() + " : " + request);
                    if (!Thread.currentThread().isAlive()) {
                        System.out.println("Thread interrupted for " + Thread.currentThread().getName());
                        return;
                    }

                    // If command = EXIT then disconnect from the server
                    if (request.equalsIgnoreCase("EXIT")) {
                        out.close();
                        in.close();
                        Thread.currentThread().interrupt();
                        System.out.println(Thread.currentThread().getName() + " stopped - " + customer.getUsername() + " logged off");
                        break;
                    }
                    String response = bank.processRequest(customer, request);
                    out.println(response);
                }
            } else {
                out.println("Log In Failed. Please restart");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                System.out.println("Thread stopped for " + Thread.currentThread().getName());
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
