package remoteHelloWorld;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 2222);
            
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Send "Hello World" message to the server
            String message = "Hello World";
            out.println(message);
            System.out.println("Sent message: " + message);
            
            // Read the response from the server
            String response = in.readLine();
            System.out.println("Server response: " + response);
            
            socket.close();
        } catch (IOException e) {
            System.out.println("Error in client: " + e);
        }
    }
}