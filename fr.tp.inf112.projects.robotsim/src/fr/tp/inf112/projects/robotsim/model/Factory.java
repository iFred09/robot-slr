package fr.tp.inf112.projects.robotsim.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import fr.tp.inf112.projects.canvas.controller.Observable;
import fr.tp.inf112.projects.canvas.controller.Observer;
import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.canvas.model.Figure;
import fr.tp.inf112.projects.canvas.model.Style;
import fr.tp.inf112.projects.robotsim.model.motion.Motion;
import fr.tp.inf112.projects.robotsim.model.shapes.PositionedShape;
import fr.tp.inf112.projects.robotsim.model.shapes.RectangularShape;

public class Factory extends Component implements Canvas, Observable {

	private static final long serialVersionUID = 5156526483612458192L;
	
	private static final ComponentStyle DEFAULT = new ComponentStyle(5.0f);


	@JsonInclude
	private boolean simulationStarted;

	@JsonManagedReference
	private List<Component> components;
	private final Random random = new Random();


	private transient List<Observer> observers;
	
	private static final Logger LOGGER = Logger.getLogger(Factory.class.getName());
	
	public Factory() {
		this(0,0,null);
	}
	
	public Factory(final int width,
				   final int height,
				   final String name ) {
		super(null, new RectangularShape(0, 0, width, height), name);
		
		components = new ArrayList<>();
		observers = null;
		simulationStarted = false;
	}
	
	@JsonIgnore
	public List<Observer> getObservers() {
		if (observers == null) {
			observers = new ArrayList<>();
		}
		
		return observers;
	}

	@Override
	public boolean addObserver(Observer observer) {
		return getObservers().add(observer);
	}

	@Override
	public boolean removeObserver(Observer observer) {
		return getObservers().remove(observer);
	}
	
	protected void notifyObservers() {
		for (final Observer observer : getObservers()) {
			observer.modelChanged();
		}
	}
	
	public boolean addComponent(final Component component) {
		if (components.add(component)) {
			notifyObservers();
			
			return true;
		}
		
		return false;
	}

	public boolean removeComponent(final Component component) {
		if (components.remove(component)) {
			notifyObservers();
			
			return true;
		}
		
		return false;
	}

	protected List<Component> getComponents() {
		return components;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@JsonIgnore
	public Collection<Figure> getFigures() {
		return (Collection) components;
	}

	@Override
	public String toString() {
		return super.toString() + " components=" + components + "]";
	}
	
	@Override
	public boolean isSimulationStarted() {
		return simulationStarted;
	}

	public void startSimulation() {
		if (!isSimulationStarted()) {
			this.simulationStarted = true;
			notifyObservers();
			behave();
//			while (isSimulationStarted()) {
//				behave();
//				
//				try {
//					Thread.sleep(100);
//				}
//				catch (final InterruptedException ex) {
//					System.err.println("Simulation was abruptely interrupted");
//				}
//			}
		}
	}

	public void stopSimulation() {
		if (isSimulationStarted()) {
			this.simulationStarted = false;
			
			notifyObservers();
		}
	}

	@Override
	public boolean behave() {
		boolean behaved = true;
		
		for (final Component component : getComponents()) {
			Thread threadBehavior = new Thread(component);
			threadBehavior.start();
		}
		
		return behaved;
	}
	
	@JsonIgnore
	@Override
	public Style getStyle() {
		return DEFAULT;
	}
	
	public boolean hasObstacleAt(final PositionedShape shape) {
		for (final Component component : getComponents()) {
			if (component.overlays(shape) && !component.canBeOverlayed(shape)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean hasMobileComponentAt(final PositionedShape shape,
										final Component movingComponent) {
		for (final Component component : getComponents()) {
			if (component != movingComponent && component.isMobile() && component.overlays(shape)) {
				return true;
			}
		}
		
		return false;
	}
	
	public synchronized int moveComponent(Component c, Motion motion) {
		synchronized (this) {
			for (final Component component : getComponents()) {
				if (component != c && component.isMobile() && component.getPosition().equals(motion.getTargetPosition())) {
					return 0;
				}
			}
			LOGGER.fine("Moving " + c + " to " + motion.getTargetPosition());
			return motion.moveToTarget();
		}
	}
	
	public void update(Factory factoryModel) {
		components = factoryModel.components;
		simulationStarted = factoryModel.simulationStarted;
		setId(factoryModel.getId());
		LOGGER.info("updated canvas : " + this.toString());
		LOGGER.info(getObservers().toString());

		notifyObservers();

	}

}
