package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private DataAccess dataAccess;
   private  UserAuthenticator UserAuthenticator;
    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.dataAccess = new DataAccess();
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

            String request;
            while ((request = reader.readLine()) != null) {
                System.out.println("Received from client: " + request);

                String response = processRequest(request);

                writer.println(response);
            }

            reader.close();
            writer.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String processRequest(String request) {
        String[] parts = request.split(" ");
        if (parts.length > 0) {
            String command = parts[0];
            String response;

            switch (command) {
                case "authenticate":
                    if (parts.length >= 3) {
                        String username = parts[1];
                        String password = parts[2];
                        response = authenticateUserAndGetRole(username, password);
                    } else {
                        response = "Invalid authenticate command.";
                    }
                    break;


                default:
                    response = "Unknown command.";
            }

            return response;
        }

        return "Invalid request.";
    }

    private String authenticateUserAndGetRole(String username, String password) {
        String role = UserAuthenticator.authenticateUserAndGetRole(username, password);
        return role != null ? role : "unauthenticated";
    }

}