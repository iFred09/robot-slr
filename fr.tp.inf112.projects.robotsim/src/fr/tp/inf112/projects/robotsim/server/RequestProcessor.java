package fr.tp.inf112.projects.robotsim.server;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.canvas.view.FileCanvasChooser;
import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.model.FactoryPersistenceManager;

public class RequestProcessor implements Runnable {
	
	private static final Logger LOGGER = Logger.getLogger(RequestProcessor.class.getName());

    private Socket socket;
    private FactoryPersistenceManager persistenceManager;

    public RequestProcessor(Socket socket, FileCanvasChooser fileCanvasChooser) {
        this.socket = socket;
        this.persistenceManager = new FactoryPersistenceManager(fileCanvasChooser);
    }

    @Override
    public void run() {
        try {
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            
            // Read object
            
            Object readObject = input.readObject();
            
            // Analyzing object
            
            if (readObject instanceof String) {
            	Canvas canvasObject = persistenceManager.read((String)readObject);
            	output.writeObject(canvasObject);
            	LOGGER.info("Read and sent canvas with ID: " + (String)readObject);
            }
            else if (readObject instanceof Factory) {
                persistenceManager.persist((Factory) readObject);
                output.writeObject("Factory persisted successfully");
                LOGGER.info("Persisted factory with ID: " + ((Factory)readObject).getId());
            }
            else {
            	output.writeObject("Invalid object");
            	LOGGER.severe("Received invalid object type");
            }
        } catch (Exception ex) {
            LOGGER.severe("Read object error: " + ex.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.severe("Close socket error: " + e.getMessage());
            }
        }
    }
}