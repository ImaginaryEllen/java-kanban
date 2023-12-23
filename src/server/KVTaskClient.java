package server;

import model.ManagerException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
	private final String url;
	private String apiToken;
	private HttpClient httpClient;

		public KVTaskClient(String url) {
			this.url = url;
			register();
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void register() {
			try {
				HttpRequest request = HttpRequest.newBuilder()
						.GET()
						.uri(URI.create(url + "register/"))
						.build();
				httpClient = HttpClient.newHttpClient();
				HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
				apiToken = httpClient.send(request, handler).body();
			} catch (IOException e) {
				throw new ManagerException("Error: KVTaskClient -> register", e);
			} catch (InterruptedException e) {
				throw new ManagerException("Error: KVTaskClient -> register", e);
			}
	}

	public String load(String key) {
		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.uri(URI.create(url + "load/" + key + "?" + "API_TOKEN=" + apiToken))
				.build();
		HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
		HttpResponse<String> response;
		try {
			response = httpClient.send(request, handler);
		} catch (IOException e) {
			throw new ManagerException("Error: KVTaskClient -> load", e);
		} catch (InterruptedException e) {
			throw new ManagerException("Error: KVTaskClient -> load", e);
		}
		return response.body();
	}

	public void put(String key, String value) {
		HttpRequest request = HttpRequest.newBuilder()
				.POST(HttpRequest.BodyPublishers.ofString(value))
				.uri(URI.create(url + "save/" + key + "?" + "API_TOKEN=" + apiToken))
				.build();
		HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
		try {
			httpClient.send(request, handler).body();
		} catch (IOException e) {
			throw new ManagerException("Error: KVTaskClient -> put", e);
		} catch (InterruptedException e) {
			throw new ManagerException("Error: KVTaskClient -> put", e);
		}
	}
}
