package fr.tp.inf112.projects.robotsim.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

import fr.tp.inf112.projects.canvas.model.impl.BasicVertex;
import fr.tp.inf112.projects.robotsim.model.Component;
import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.model.shapes.PositionedShape;

public class HTTPClient {
	private static final Logger LOGGER = Logger.getLogger(HTTPClient.class.getName());
	private ObjectMapper objectMapper;

	private static HttpClient httpClient = HttpClient.newHttpClient();
	private final URI uri;

	public HTTPClient(String url) {
		URI uri = null;
		try {
			uri = new URI("http", null, "localhost", 8080, url, null, null);
		} catch (URISyntaxException e) {
			LOGGER.severe(e.getMessage());
		}
		this.uri = uri;
		objectMapper = new ObjectMapper();
		PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
				.allowIfSubType(PositionedShape.class.getPackageName()).allowIfSubType(Component.class.getPackageName())
				.allowIfSubType(ArrayList.class.getName()).allowIfSubType(LinkedHashSet.class.getName())
				.allowIfSubType(BasicVertex.class.getPackageName()).build();
		objectMapper.activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.NON_FINAL);
	}

	public Factory request(boolean readResponse) {
		HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			if (!readResponse) {
				return null;
			}

			Factory factory = objectMapper.readValue(response.body(), Factory.class);
			LOGGER.info(factory.toString());
			return factory;
		} catch (IOException | InterruptedException e) {
			LOGGER.severe(e.getMessage());
		}
		return null;
	}

}

