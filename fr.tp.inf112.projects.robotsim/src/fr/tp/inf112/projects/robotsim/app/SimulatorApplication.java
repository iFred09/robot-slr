package fr.tp.inf112.projects.robotsim.app;

import java.awt.Component;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import fr.tp.inf112.projects.canvas.model.impl.BasicVertex;
import fr.tp.inf112.projects.canvas.view.CanvasViewer;
import fr.tp.inf112.projects.canvas.view.FileCanvasChooser;
import fr.tp.inf112.projects.robotsim.model.Area;
import fr.tp.inf112.projects.robotsim.model.Battery;
import fr.tp.inf112.projects.robotsim.model.ChargingStation;
import fr.tp.inf112.projects.robotsim.model.Conveyor;
import fr.tp.inf112.projects.robotsim.model.Door;
import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.model.FactoryPersistenceManager;
import fr.tp.inf112.projects.robotsim.model.Machine;
import fr.tp.inf112.projects.robotsim.model.RemoteFileCanvasChooser;
import fr.tp.inf112.projects.robotsim.model.Robot;
import fr.tp.inf112.projects.robotsim.model.Room;
import fr.tp.inf112.projects.robotsim.model.path.CustomDijkstraFactoryPathFinder;
import fr.tp.inf112.projects.robotsim.model.path.FactoryPathFinder;
import fr.tp.inf112.projects.robotsim.model.path.JGraphTDijkstraFactoryPathFinder;
import fr.tp.inf112.projects.robotsim.model.shapes.BasicPolygonShape;
import fr.tp.inf112.projects.robotsim.model.shapes.CircularShape;
import fr.tp.inf112.projects.robotsim.model.shapes.RectangularShape;

public class SimulatorApplication {
	
	private static final Logger LOGGER = Logger.getLogger(SimulatorApplication.class.getName());
	
	private static final String host = "localhost";
	private static final int port = 2222;


	public static void main(String[] args) {
		
		LOGGER.info("Starting the robot simulator...");
		LOGGER.config("With parameters " + Arrays.toString(args) + ".");
		
		final Factory factory = new Factory(200, 200, "Simple Test Puck Factory");
		final Room room1 = new Room(factory, new RectangularShape(20, 20, 75, 75), "Production Room 1");
		new Door(room1, Room.WALL.BOTTOM, 10, 20, true, "Entrance");
		final Area area1 = new Area(room1, new RectangularShape(35, 35, 50, 50), "Production Area 1");
		final Machine machine1 = new Machine(area1, new RectangularShape(50, 50, 15, 15), "Machine 1");

		final Room room2 = new Room(factory, new RectangularShape( 120, 22, 75, 75 ), "Production Room 2");
		new Door(room2, Room.WALL.LEFT, 10, 20, true, "Entrance");
		final Area area2 = new Area(room2, new RectangularShape( 135, 35, 50, 50 ), "Production Area 1");
		final Machine machine2 = new Machine(area2, new RectangularShape( 150, 50, 15, 15 ), "Machine 1");
		
		final int baselineSize = 3;
		final int xCoordinate = 10;
		final int yCoordinate = 165;
		final int width =  10;
		final int height = 30;
		final BasicPolygonShape conveyorShape = new BasicPolygonShape();
		conveyorShape.addVertex(new BasicVertex(xCoordinate, yCoordinate));
		conveyorShape.addVertex(new BasicVertex(xCoordinate + width, yCoordinate));
		conveyorShape.addVertex(new BasicVertex(xCoordinate + width, yCoordinate + height - baselineSize));
		conveyorShape.addVertex(new BasicVertex(xCoordinate + width + baselineSize, yCoordinate + height - baselineSize));
		conveyorShape.addVertex(new BasicVertex(xCoordinate + width + baselineSize, yCoordinate + height));
		conveyorShape.addVertex(new BasicVertex(xCoordinate - baselineSize, yCoordinate + height));
		conveyorShape.addVertex(new BasicVertex(xCoordinate - baselineSize, yCoordinate + height - baselineSize));
		conveyorShape.addVertex(new BasicVertex(xCoordinate, yCoordinate + height - baselineSize));

		final Room chargingRoom = new Room(factory, new RectangularShape(125, 125, 50, 50), "Charging Room");
		new Door(chargingRoom, Room.WALL.RIGHT, 10, 20, true, "Entrance");
		final ChargingStation chargingStation = new ChargingStation(factory, new RectangularShape(150, 145, 15, 15), "Charging Station");
		/*
		 * final FactoryPathFinder jgraphPahtFinder = new
		 * JGraphTDijkstraFactoryPathFinder(factory, 5); final Robot robot1 = new
		 * Robot(factory, jgraphPahtFinder, new CircularShape(5, 5, 2), new Battery(10),
		 * "Robot 1"); robot1.addTarget(machine1); robot1.addTarget(machine2);
		 * robot1.addTarget(new Conveyor(factory, conveyorShape, "Conveyor 1")); //
		 * robot1.addTarget(chargingStation);
		 */
		
		for (int i = 1 ; i <= 5 ; i++) {
			final FactoryPathFinder customPathFinder = new JGraphTDijkstraFactoryPathFinder(factory, 5);
			final Robot robot = new Robot(factory, customPathFinder, new CircularShape(i * 5, 5, 2), new Battery(10), "Robot " + i);
			robot.addTarget(machine1);
			robot.addTarget(machine2);
			robot.addTarget(new Conveyor(factory, conveyorShape, "Conveyor 1"));
			robot.addTarget(chargingStation);
			
		}
		
//		robot2.addTarget(chargingStation);
		
		SwingUtilities.invokeLater(new Runnable() {
			  
			@Override
	        public void run() {
				final RemoteFileCanvasChooser canvasChooser = new RemoteFileCanvasChooser("factory", "Puck Factory", host, port);
				final Component factoryViewer = new CanvasViewer(new SimulatorController(factory, new FactoryPersistenceManager(canvasChooser)));
				canvasChooser.setViewer(factoryViewer);
				//new CanvasViewer(factory);
			}
		});
	}
}
