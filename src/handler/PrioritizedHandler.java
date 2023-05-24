package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.ManagerException;
import service.HttpTaskManager;
import service.Managers;

import java.io.IOException;
import java.io.OutputStream;

public class PrioritizedHandler implements HttpHandler {
	private final HttpTaskManager httpTaskManager;
	Gson gson = Managers.getGson();

	public PrioritizedHandler(HttpTaskManager httpTaskManager) {
		this.httpTaskManager = httpTaskManager;
	}

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		String response = null;
		String method = httpExchange.getRequestMethod();
		String path = httpExchange.getRequestURI().getPath();
		String[] arrayPath = path.split("/");
		if (method.equals("GET") && arrayPath[1].equals("tasks")) {
			try {
				response = gson.toJson(httpTaskManager.getPrioritizedTasks());
				httpExchange.sendResponseHeaders(200, 0);
			} catch (Exception e) {
				throw new ManagerException("Error GET PRIORITIZED TASKS");
			}
		} else {
			response = "PrioritizedTask not a found method: " + method;
			httpExchange.sendResponseHeaders(404, 0);
		}
		try (OutputStream os = httpExchange.getResponseBody()) {
			os.write(response.getBytes());
		}
	}
}
