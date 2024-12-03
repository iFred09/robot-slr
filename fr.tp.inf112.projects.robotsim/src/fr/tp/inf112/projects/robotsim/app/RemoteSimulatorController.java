package fr.tp.inf112.projects.robotsim.app;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Logger;

import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.canvas.model.CanvasPersistenceManager;
import fr.tp.inf112.projects.robotsim.client.HTTPClient;
import fr.tp.inf112.projects.robotsim.model.Factory;


public class RemoteSimulatorController extends SimulatorController {
	private static final Logger LOGGER = Logger.getLogger(RemoteSimulatorController.class.getName());
	
	Thread update;
	Factory test;

	public RemoteSimulatorController(CanvasPersistenceManager persistenceManager) {
		super(persistenceManager);
	}

	public RemoteSimulatorController(Factory factoryModel, CanvasPersistenceManager persistenceManager) {
		super(factoryModel, persistenceManager);
		test = factoryModel;
	}
	
	@Override
	public void startAnimation() {
		LOGGER.info(((Factory) getCanvas()).getObservers().toString());
		HTTPClient client = new HTTPClient("/simulation/" + getCanvas().getId());
		Factory f = client.request(true);
		if (f == null) {
			LOGGER.info(getCanvas().getId() + "not found");
			return;
		}
		((Factory) getCanvas()).update(f);
//		FactorySimulationEventConsumer consumer = new FactorySimulationEventConsumer(this);
//		consumer.consumeMessages();
		try {
			updateViewer();
		} catch (InterruptedException | URISyntaxException | IOException e) {
			LOGGER.severe(e.getMessage());
			stopAnimation();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stopAnimation() {
		HTTPClient client = new HTTPClient("/stop/" + getCanvas().getId());
		Factory f = client.request(true);
		((Factory) getCanvas()).update(f);
//		setCanvas(f);
	}

	private void updateViewer() throws InterruptedException, URISyntaxException, IOException {
		while (((Factory) getCanvas()).isSimulationStarted()) {
			HTTPClient client = new HTTPClient("/simulation/" + getCanvas().getId());
			Factory f = client.request(true);
			((Factory) getCanvas()).update(f);
			Thread.sleep(100);
		}
	}

	public void update(Canvas c) {
		((Factory) getCanvas()).update((Factory) c);
	}


}
