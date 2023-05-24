package server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVClient {
	private final URI uri;
	private final String apiToken;
	private final HttpClient httpClient;

		public KVClient(URI uri) throws IOException, InterruptedException {
			this.uri = uri;
			HttpRequest request = HttpRequest.newBuilder()
					.GET()
					.uri(URI.create(uri + "/register"))
					.build();
		httpClient = HttpClient.newHttpClient();
		HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
		apiToken = httpClient.send(request, handler).body();
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public String load(String key) {
		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.uri(URI.create(uri + "/load/" + key + "?" + "API_TOKEN=" + apiToken))
				.build();
		HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
		HttpResponse<String> response = null;
		try {
			response = httpClient.send(request, handler);
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
		return response.body();
	}

	public void save(String key, String value) {
		HttpRequest request = HttpRequest.newBuilder()
				.POST(HttpRequest.BodyPublishers.ofString(value))
				.uri(URI.create(uri + "/save/" + key + "?" + "API_TOKEN=" + apiToken))
				.build();
		HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
		try {
			httpClient.send(request, handler).body();
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
