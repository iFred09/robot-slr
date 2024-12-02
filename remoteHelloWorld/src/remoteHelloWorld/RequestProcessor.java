package remoteHelloWorld;

import java.io.*;
import java.net.Socket;

public class RequestProcessor implements Runnable {

    private Socket socket;

    public RequestProcessor(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream inpStr = socket.getInputStream();
            Reader strReader = new InputStreamReader(inpStr);
            BufferedReader buffReader = new BufferedReader(strReader);

            // Read and decode input request
            String message = buffReader.readLine();
            System.out.println("Received message: " + message);

            OutputStream outStr = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outStr, true); // Autoflush

            // Build and write response
            String response = "I received " + message + "!";
            writer.println(response);
            System.out.println("Sent response: " + response);

        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}