package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import model.ManagerException;
import service.HttpTaskManager;
import service.Managers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class EpicHandler implements HttpHandler {
	private final HttpTaskManager httpTaskManager;
	Gson gson = Managers.getGson();

	public EpicHandler(HttpTaskManager httpTaskManager) {
		this.httpTaskManager = httpTaskManager;
	}

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		String method = httpExchange.getRequestMethod();
		String response = null;
		switch (method) {
			case "GET": {
				response = getEpic(httpExchange);
				break;
			}
			case "POST": {
				response = postEpic(httpExchange);
				break;
			}
			case "DELETE": {
				response = deleteEpic(httpExchange);
				break;
			}
			default:
				response = "Don`t find method: " + method;
				httpExchange.sendResponseHeaders(404, 0);
				break;
		}
		httpExchange.sendResponseHeaders(200, 0);
		try (OutputStream os = httpExchange.getResponseBody()) {
			if (response != null) {
				os.write(response.getBytes());
			}
		}
	}

	private String getEpic(HttpExchange httpExchange) {
		String path = httpExchange.getRequestURI().toString();
		String[] arrayPath = path.split("/");
		String response = null;
		if (path.contains("id")) {
			int id = Integer.parseInt(arrayPath[3].split("=")[1]);
			if (!httpTaskManager.getEpics().containsKey(id)) {
				response = "Epic with id: " + id + " not a found";
				return response;
			}
			try {
				response = gson.toJson(httpTaskManager.getEpicById(id));
			} catch (Exception e) {
				throw new ManagerException("Error GET EPIC BY ID");
			}
		} else if (arrayPath[2].contains("epic")) {
			try {
				if (httpTaskManager.getEpics().isEmpty()) {
					response = "Epics not a found";
					return response;
				}
				response = gson.toJson(httpTaskManager.getEpicList());
			} catch (Exception e) {
				throw new ManagerException("Error GET ALL EPICS");
			}
		}
		return response;
	}

	private String postEpic(HttpExchange httpExchange) throws IOException {
		String response = null;
		String epicJson = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
		Epic epic = gson.fromJson(epicJson, Epic.class);
		if (httpTaskManager.getEpics().containsKey(epic.getId())) {
			try {
				httpTaskManager.updateEpic(epic);
				response = "Epic is update";
			} catch (Exception e) {
				throw new ManagerException("Error UPDATE EPIC");
			}
		} else {
			try {
				httpTaskManager.createEpic(epic);
				response = "Epic is create";
			} catch (Exception e) {
				throw new ManagerException("Error CREATE EPIC");
			}
		}
		return response;
	}

	private String deleteEpic(HttpExchange httpExchange) {
		String path = httpExchange.getRequestURI().toString();
		String[] arrayPath = path.split("/");
		String response = null;
		if (path.contains("id")) {
			int id = Integer.parseInt(arrayPath[3].split("=")[1]);
			try {
				if (!httpTaskManager.getEpics().containsKey(id)) {
					response = "Epic with id: " + id + " not a found";
					return response;
				}
				httpTaskManager.deleteEpic(id);
				response = "Epic by id:" + id + " is delete";
				return response;
			} catch (Exception e) {
				throw new ManagerException("Error DELETE EPIC BY ID");
			}
		} else if (arrayPath[2].contains("epic")) {
			try {
				if (httpTaskManager.getEpics().isEmpty()) {
					response = "Epics not a found";
					return response;
				}
				httpTaskManager.deleteAllEpics();
				response = "All Epics deleted";
			} catch (Exception e) {
				throw new ManagerException("Error DELETE ALL EPICS");
			}
		}
		return response;
	}
}
