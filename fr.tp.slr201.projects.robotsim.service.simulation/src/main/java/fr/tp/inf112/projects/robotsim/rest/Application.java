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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.robotsim.model.Factory;


@SpringBootApplication
@RestController
public class Application {
	
	private static final Logger LOGGER = Logger.getLogger(Application.class.getName());
	
	private HashMap<String, Factory> factories = new HashMap<>();

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@GetMapping("/")
	public String default() {
		return "Hello World!";
	}
	
	@GetMapping("/start/{factoryId}")
    public boolean startSimulation(@PathVariable String factoryId) {
        LOGGER.info("Starting simulation for factory ID: " + factoryId);
        try {
            Canvas canvas = persistenceManager.read(factoryId);
            if (canvas instanceof Factory) {
                simulatorController.setCanvas(canvas);
                simulatorController.startAnimation();
                LOGGER.info("Simulation started successfully for factory ID: " + factoryId);
                return true;
            } else {
                LOGGER.severe("Canvas with ID: " + factoryId + " is not a Factory instance.");
                return false;
            }
        } catch (IOException e) {
            LOGGER.severe("Failed to start simulation for factory ID: " + factoryId + " - " + e);
            return false;
        }
    }

    @GetMapping("/{factoryId}")
    public Factory getSimulation(@PathVariable String factoryId) {
        LOGGER.info("Retrieving simulation for factory ID: {}", factoryId);
        Canvas canvas = simulatorController.getCanvas();
        if (canvas instanceof Factory && ((Factory) canvas).getId().equals(factoryId)) {
            logger.info("Simulation retrieved successfully for factory ID: {}", factoryId);
            return (Factory) canvas;
        } else {
            logger.warn("No active simulation found for factory ID: {}", factoryId);
            return null;
        }
    }

    @GetMapping("/stop/{factoryId}")
    public boolean stopSimulation(@PathVariable String factoryId) {
        logger.info("Stopping simulation for factory ID: {}", factoryId);
        Canvas canvas = simulatorController.getCanvas();
        if (canvas instanceof Factory && ((Factory) canvas).getId().equals(factoryId)) {
            simulatorController.stopAnimation();
            logger.info("Simulation stopped successfully for factory ID: {}", factoryId);
            return true;
        } else {
            logger.warn("No active simulation found for factory ID: {}", factoryId);
            return false;
        }
    }

}