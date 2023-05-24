package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.HttpTaskManager;
import service.Managers;

import java.io.IOException;
import java.io.OutputStream;

public class HistoryHandler implements HttpHandler {
	private final HttpTaskManager httpTaskManager;
	Gson gson = Managers.getGson();

	public HistoryHandler(HttpTaskManager httpTaskManager) {
		this.httpTaskManager = httpTaskManager;
	}

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		String response = null;
		String method = httpExchange.getRequestMethod();
		String path = httpExchange.getRequestURI().getPath();
		String[] arrayPath = path.split("/");
		if (method.equals("GET") && arrayPath[2].equals("history")) {
			response = gson.toJson(httpTaskManager.getHistory());
			httpExchange.sendResponseHeaders(200, 0);
		} else {
			response = "History not a found method: " + method;
			httpExchange.sendResponseHeaders(404, 0);
		}
		try (OutputStream os = httpExchange.getResponseBody()) {
			os.write(response.getBytes());
		}
	}
}
