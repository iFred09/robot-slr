package fr.tp.inf112.projects.robotsim.model;

import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.canvas.model.CanvasPersistenceManager;
import fr.tp.inf112.projects.canvas.model.CanvasChooser;

import java.io.*;
import java.net.*;

public class RemoteFactoryPersistenceManager implements CanvasPersistenceManager {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 2222;
    private final CanvasChooser canvasChooser;

    public RemoteFactoryPersistenceManager(CanvasChooser canvasChooser) {
        this.canvasChooser = canvasChooser;
    }

    @Override
    public Canvas read(String canvasId) throws IOException {
        try (
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            out.writeObject("LOAD");
            out.writeObject(canvasId);
            
            Object response = in.readObject();
            if (response instanceof Canvas) {
                return (Canvas) response;
            } else {
                throw new IOException("Unexpected response from server");
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Error in object deserialization", e);
        }
    }

    @Override
    public void persist(Canvas canvasModel) throws IOException {
        try (
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            out.writeObject("SAVE");
            out.writeObject(canvasModel);
            
            Object response = in.readObject();
            if (!(response instanceof String) || !((String) response).equals("OK")) {
                throw new IOException("Unexpected response from server");
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Error in object deserialization", e);
        }
    }

    @Override
    public boolean delete(Canvas canvasModel) throws IOException {
        try (
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            out.writeObject("DELETE");
            out.writeObject(canvasModel);
            
            Object response = in.readObject();
            if (response instanceof Boolean) {
                return (Boolean) response;
            } else {
                throw new IOException("Unexpected response from server");
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Error in object deserialization", e);
        }
    }

    @Override
    public CanvasChooser getCanvasChooser() {
        return canvasChooser;
    }
}