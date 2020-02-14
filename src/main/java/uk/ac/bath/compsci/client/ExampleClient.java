package uk.ac.bath.compsci.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ExampleClient extends Thread {
    private Socket server;
    private PrintWriter bankServerOut;
    private BufferedReader userInput;
    private Thread bankServerResponceThread;

    public ExampleClient(String ip, int port) throws UnknownHostException, IOException {
        server = new Socket(ip, port);
        userInput = new BufferedReader(new InputStreamReader(System.in));
        bankServerOut = new PrintWriter(server.getOutputStream(), true);

        bankServerResponceThread = new Thread() {
            private BufferedReader bankServerIn = new BufferedReader(new InputStreamReader(server.getInputStream()));

            public void run() {
                try {
                    while (true) {
                        if (Thread.interrupted()) {
                            break;
                        }
                        String responce = bankServerIn.readLine();
                        System.out.println(responce);
                        if (Thread.interrupted()) {
                            throw new InterruptedException();
                        }
                    }
                } catch (IOException e) {
                    if (bankServerResponceThread.isInterrupted()) {
                        System.out.println("Thanks for banking with NewBank........");
                        return;
                    }
                    e.printStackTrace();
                    return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        bankServerResponceThread.start();
    }


    public void run() {
        while (true) {
            if (Thread.interrupted()) {
                System.out.println("Thread interrupted!");
                break;
            }

            try {
                while (true) {
                    String command = userInput.readLine();
                    bankServerOut.println(command);
                    // If command = EXIT then disconnect from the server
                    if (command.equalsIgnoreCase("EXIT")) {
                        System.out.println("SUCCESS " + command);
                        server.close();
                        userInput.close();
                        bankServerOut.close();
                        bankServerResponceThread.interrupt();
                        System.exit(2);
                        break;
                    }
                    //
                    //bankServerOut.println(command);
                }
            } catch (IOException e) {
                bankServerResponceThread.interrupt();
                if (bankServerResponceThread.isInterrupted()) {
                    System.out.println("Thanks for banking with NewBank........");
                    return;
                }
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    ;

    public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
        new ExampleClient("localhost", 14002).start();
    }
}
