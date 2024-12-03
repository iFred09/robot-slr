package fr.tp.inf112.projects.robotsim.rest;

import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.canvas.model.CanvasChooser;
import fr.tp.inf112.projects.robotsim.app.SimulatorController;
import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.model.FactoryPersistenceManager;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.logging.Logger;

@RestController
public class SimulationRestController {

    private static final Logger LOGGER = Logger.getLogger(SimulationRestController.class.getName());

    private final SimulatorController simulatorController;
    private final FactoryPersistenceManager persistenceManager;

    public SimulationRestController(CanvasChooser canvasChooser) {
        this.persistenceManager = new FactoryPersistenceManager(canvasChooser);
        this.simulatorController = new SimulatorController(persistenceManager);
    }

    @PostMapping("/start/{factoryId}")
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

    @DeleteMapping("/stop/{factoryId}")
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