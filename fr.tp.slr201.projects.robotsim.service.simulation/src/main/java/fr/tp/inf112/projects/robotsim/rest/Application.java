package fr.tp.inf112.projects.robotsim.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.tp.inf112.projects.robotsim.model.Factory;


@SpringBootApplication
@RestController
public class Application {
	
	private static final Logger LOGGER = Logger.getLogger(Application.class.getName());
	
	private HashMap<String, Factory> factories = new HashMap<>();
	
	private static final String host = "localhost";
	private static final int port = 2222;
	
	private ObjectMapper objectMapper;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@GetMapping("/")
	public String index() {
		return "Hello World!";
	}
	
	@GetMapping("/start/{factoryId}")
    public String startSimulation(@PathVariable String factoryId) {
		if (factories.containsKey(factoryId)) {
			LOGGER.fine("Factory " + factoryId + " is already running, sending current state...");
			Factory factory = factories.get(factoryId);
			try {
				return objectMapper.writeValueAsString(factory);
			} catch (JsonProcessingException ex) {
				LOGGER.severe("Write Factory as String error: " + ex.getMessage());
				return null;
			}
		}
        try (Socket socket = new Socket(host, port)){            
        	ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
			output.writeObject(factoryId);
			InputStream input = socket.getInputStream();
			ObjectInputStream objIn = new ObjectInputStream(input);
			Factory factory = (Factory) objIn.readObject();
			factories.put(factory.getId(), factory);
			LOGGER.info("Starting simulation for factory ID: " + factoryId);
			factory.startSimulation();
            
			return objectMapper.writeValueAsString(factory);
        } catch (ClassNotFoundException | IOException e) {
            LOGGER.severe("Failed to start simulation for factory ID: " + factoryId + " - " + e);
        }
        return null;
    }


    @GetMapping("/stop/{factoryId}")
    public String stopSimulation(@PathVariable String factoryId) {
        LOGGER.info("Stopping simulation for factory ID: " + factoryId);
        Factory factory = factories.remove(factoryId);
        if (factory == null) {
        	LOGGER.info("Factory " + factoryId + " not found");
        	return null;
        }
        LOGGER.info("Factory " + factoryId + " found, stopping simulation...");
        factory.stopSimulation();
        
        try {
        	return objectMapper.writeValueAsString(factory);
        } catch (JsonProcessingException ex) {
        	LOGGER.severe("JSON Processing error: " + ex);
        }
        return null;
    }

}
