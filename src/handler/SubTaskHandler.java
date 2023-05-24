package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.ManagerException;
import model.SubTask;
import service.HttpTaskManager;
import service.Managers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class SubTaskHandler implements HttpHandler {
	private final HttpTaskManager httpTaskManager;
	Gson gson = Managers.getGson();

	public SubTaskHandler(HttpTaskManager httpTaskManager) {
		this.httpTaskManager = httpTaskManager;
	}

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		String method = httpExchange.getRequestMethod();
		String response = null;
		switch (method) {
			case "GET": {
				response = getSubTask(httpExchange);
				break;
			}
			case "POST": {
				response = postSubTask(httpExchange);
				break;
			}
			case "DELETE": {
				response = deleteSubTask(httpExchange);
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

	private String getSubTask(HttpExchange httpExchange) {
		String path = httpExchange.getRequestURI().toString();
		String[] arrayPath = path.split("/");
		String response = null;
		if (path.contains("id") && arrayPath.length == 4) {
			int id = Integer.parseInt(arrayPath[3].split("=")[1]);
			if (!httpTaskManager.getSubTasks().containsKey(id) && !httpTaskManager.getEpics().containsKey(id)) {
				response = "SubTask with id: " + id + " not a found";
				return response;
			} else if (httpTaskManager.getSubTasks().containsKey(id)) {
				try {
					response = gson.toJson(httpTaskManager.getSubTaskById(id));
					return response;
				} catch (Exception e) {
					throw new ManagerException("Error GET SUBTASK BY ID");
				}
			}
		} else if (path.contains("id") && arrayPath.length == 5) {
			int id = Integer.parseInt(arrayPath[4].split("=")[1]);
			response = gson.toJson(httpTaskManager.getSubTasksByEpic(id));
			return response;
		} else if (arrayPath[2].equals("subTask") && arrayPath.length == 4 || arrayPath.length == 3) {
			try {
				if (httpTaskManager.getSubTasks().isEmpty()) {
					response = "SubTasks not a found";
					return response;
				}
				response = gson.toJson(httpTaskManager.getSubTaskList());
			} catch (Exception e) {
				throw new ManagerException("Error GET ALL SUBTASKS");
			}
		}
		return response;
	}

	private String postSubTask(HttpExchange httpExchange) throws IOException {
		String response = null;
		String taskJson = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
		SubTask subTask = gson.fromJson(taskJson, SubTask.class);
		if (httpTaskManager.getSubTasks().containsKey(subTask.getId())) {
			try {
				httpTaskManager.updateSubTask(subTask);
				response = "SubTask is update";
			} catch (Exception e) {
				throw new ManagerException("Error UPDATE SUBTASK");
			}
		} else {
			try {
				httpTaskManager.createSubTask(subTask);
				response = "SubTask is create";
			} catch (Exception e) {
				throw new ManagerException("Error CREATE SUBTASK");
			}
		}
		return response;
	}

	private String deleteSubTask(HttpExchange httpExchange) {
		String path = httpExchange.getRequestURI().toString();
		String[] arrayPath = path.split("/");
		String response = null;
		if (path.contains("id")) {
			int id = Integer.parseInt(arrayPath[3].split("=")[1]);
			try {
				if (!httpTaskManager.getSubTasks().containsKey(id)) {
					response = "SubTask with id: " + id + " not a found";
					return response;
				}
				httpTaskManager.deleteSubTask(id);
				response = "SubTask by id:" + id + " is delete";
				return response;
			} catch (Exception e) {
				throw new ManagerException("Error DELETE SUBTASK BY ID");
			}
		} else if (arrayPath[2].equals("subTask")) {
			try {
				if (httpTaskManager.getSubTasks().isEmpty()) {
					response = "SubTasks not a found";
					return response;
				}
				httpTaskManager.deleteAllSubTasks();
				response = "All SubTasks deleted";
			} catch (Exception e) {
				throw new ManagerException("Error DELETE ALL SUBTASKS");
			}
		}
		return response;
	}
}
