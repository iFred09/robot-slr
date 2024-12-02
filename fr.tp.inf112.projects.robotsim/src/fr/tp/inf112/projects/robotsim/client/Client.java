package fr.tp.inf112.projects.robotsim.client;

import java.io.*;
import java.net.*;
import fr.tp.inf112.projects.robotsim.model.Factory;

public class Client {
    public static void main(String[] args) {
        try (
            Socket socket = new Socket("localhost", 2222);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            // Create a Factory object
            Factory factory = new Factory(100, 100, "ClientFactory");
            
            // Send the Factory object to the server
            out.writeObject(factory);
            System.out.println("Sent Factory object: " + factory.getName());
            
            // Read the response from the server
            Object response = in.readObject();
            if (response instanceof String) {
                System.out.println("Server response: " + response);
            } else if (response instanceof Factory) {
                Factory receivedFactory = (Factory) response;
                System.out.println("Received Factory object: " + receivedFactory.getName());
            } else {
                System.out.println("Received unexpected object type");
            }
            
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error in client: " + e);
        }
    }
}