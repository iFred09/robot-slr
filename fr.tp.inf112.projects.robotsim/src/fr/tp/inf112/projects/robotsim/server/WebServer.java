package fr.tp.inf112.projects.robotsim.server;

import java.io.IOException;
import java.net.*;

import fr.tp.inf112.projects.canvas.view.FileCanvasChooser;

public class WebServer {
	public static void main(String args[]) {
		FileCanvasChooser fileCanvasChooser = new FileCanvasChooser("file", "file");
		try (
			ServerSocket serverSocket = new ServerSocket(2222);
		) {
			do {
				try {
					Socket socket = serverSocket.accept();
					Runnable reqProcessor = new RequestProcessor(socket, fileCanvasChooser);
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
