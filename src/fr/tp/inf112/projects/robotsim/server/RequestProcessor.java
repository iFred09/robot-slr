package fr.tp.inf112.projects.robotsim.server;

import java.io.*;
import java.net.Socket;

import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.canvas.view.FileCanvasChooser;
import fr.tp.inf112.projects.robotsim.model.FactoryPersistenceManager;

public class RequestProcessor implements Runnable {

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
            	String canvasId = (String)readObject;
            	Canvas canvasObject = persistenceManager.read(canvasId);
            	output.writeObject(canvasObject);
            	System.out.println("Read and sent canvas with ID: " + canvasId);
            }
            else if (readObject instanceof Canvas) {
            	Canvas factory = (Canvas)readObject;
            	persistenceManager.persist(factory);
            	output.writeObject("Factory persisted successfully");
            	System.out.println("Persisted factory with ID: " + factory.getId());
            }
            else {
            	output.writeObject("invalid object");
            	System.out.println("Received invalid object type");
            }
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