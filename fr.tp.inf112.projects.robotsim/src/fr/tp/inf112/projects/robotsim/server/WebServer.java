package fr.tp.inf112.projects.robotsim.server;

import java.io.IOException;
import java.net.*;
import java.util.logging.Logger;

import fr.tp.inf112.projects.canvas.view.FileCanvasChooser;

public class WebServer {
	
	private final static Logger LOGGER = Logger.getLogger(WebServer.class.getName());
	
	public static void main(String args[]) {
		FileCanvasChooser fileCanvasChooser = new FileCanvasChooser("file", "file");
		try (
			ServerSocket serverSocket = new ServerSocket(2222);
		) {
			serverSocket.setReuseAddress(true);
			do {
				try {
					Socket socket = serverSocket.accept();
					Runnable reqProcessor = new RequestProcessor(socket, fileCanvasChooser);
					new Thread(reqProcessor).start();
				}
				catch (IOException Ex) {
					LOGGER.severe("Server request error: " + Ex.getMessage());
				}
			} while(true);
		}
		catch (IOException ex) {
			LOGGER.severe("Server launch error: " + ex.getMessage());
		}
	}
}
