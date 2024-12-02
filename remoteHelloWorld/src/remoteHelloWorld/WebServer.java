package remoteHelloWorld;

import java.io.IOException;
import java.net.*;

public class WebServer {
	public static void main(String args[]) {
		try (
			ServerSocket serverSocket = new ServerSocket(2222);
		) {
			do {
				try {
					Socket socket = serverSocket.accept();
					Runnable reqProcessor = new RequestProcessor(socket);
					new Thread(reqProcessor).start();
				}
				catch (IOException Ex) {
					System.out.println("error : " + Ex);
				}
			} while(true);
		}
		catch (IOException ex) {
			System.out.println("error : " + ex);
		}
	}
}
