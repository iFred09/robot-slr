package fr.tp.inf112.projects.robotsim.model;

import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.canvas.model.CanvasChooser;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Component;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;


public class RemoteFileCanvasChooser implements CanvasChooser {
	
	private static final Logger LOGGER = Logger.getLogger(RemoteFileCanvasChooser.class.getName());
	
	private static final char EXTENSION_SEPARATOR_CHAR = '.';
    /**
     * Used by the {@code JFileChooser} to filter the files presented to the user according to specific file extensions.
     */
    private final FileNameExtensionFilter fileNameFilter;
    
    /**
	 * The UI component that will serve as the parent of the {@code JFileChooser} displayed to the user. This is typically
	 * a Java AWT component such as a {@code CanvasViewer} object. 
	 */
	private Component viewer;

    private String host;
    private int port;

    public RemoteFileCanvasChooser(String fileExtension, String documentTypeLabel, String host, int port) {
        this.host = host;
        this.port = port;
        fileNameFilter = new FileNameExtensionFilter(documentTypeLabel + " files " + "(*." + fileExtension + ")", fileExtension);
    }
    
    private String browseCanvases(final boolean open) {
        if (open) {
                try (Socket socket = new Socket(host, port)) {
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                    output.writeObject(new Request(null));
                    InputStream input = socket.getInputStream();
                    ObjectInputStream objIn = new ObjectInputStream(input);
                    ArrayList<Canvas> canvas = (ArrayList<Canvas>) objIn.readObject();
                    ArrayList<String> canvasId = new ArrayList<String>();
                    for (Canvas c : canvas) {
                    	canvasId.add(c.getId());
                    }
                    return openMenu(canvasId);
                } catch (IOException | ClassNotFoundException e) {
                    LOGGER.severe("Error: " + e.getMessage());
                    throw new RuntimeException(e);
                }
        } else {
            return saveFile();
        }
    }

    private String openMenu(ArrayList<String> canvases) {
        int choice = JOptionPane.showOptionDialog(null, "Open Canvas", "Choose a canvas", JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null, canvases.toArray(), canvases.get(0));
        return canvases.get(choice);
    }

    private String saveFile() {
        String fileName;
        do {
            fileName = JOptionPane.showInputDialog("Enter a file name");
        } while (fileName == null || fileName.isEmpty());
        return fileName;

    }

    public static class Request implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 202411301823L;
		final String canvaName;

        public Request(String canvaName) {
            this.canvaName = canvaName;
        }
    }

    public ArrayList<Canvas> getCanvases() {
        File savedCanvas = new File("./saves");
        if(!savedCanvas.exists()) {
            if(!savedCanvas.mkdir()) {
                LOGGER.severe("Error: Could not create save directory");
                return new ArrayList<>();
            }
        }
        File[] files = Arrays.stream(Objects.requireNonNull(savedCanvas.listFiles())).filter(file -> file.getName().endsWith(".factory")).toArray(File[]::new);
        ArrayList<Canvas> canvas = new ArrayList<>();

        Arrays.stream(files).forEach(file -> {
            try {
                ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(file));
                canvas.add((Canvas) objIn.readObject());
                objIn.close();

            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        return canvas;
    }
    
    
    /**
	 * {@inheritDoc}
	 */
	@Override
	public String newCanvasId() throws IOException {
		String canvasId = browseCanvases(false);
    	
    	if (canvasId != null && !isFileExtensionValid(canvasId)) {
    		canvasId = canvasId.concat(EXTENSION_SEPARATOR_CHAR + getFileExtension());
    	}
    	
    	if (!isValid(canvasId)) {
			throw new IOException("Invalid canvas file name '" + canvasId + "'.");
    	}
    	
    	return canvasId;
	}
	
	private boolean isValid(final String canvasId) {
		return canvasId != null && !canvasId.isEmpty() && isFileExtensionValid(canvasId);
	}
	
	private boolean isFileExtensionValid(final String canvasId) {
		return canvasId.endsWith(EXTENSION_SEPARATOR_CHAR + getFileExtension());
	}
	
	private String getFileExtension() {
		return fileNameFilter.getExtensions()[0];
	}

	@Override
	public String choseCanvas() throws IOException {
		return browseCanvases(true);
	}
	
	public void setViewer(Component viewer) {
		this.viewer = viewer;
	}

}
