package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.ManagerException;
import model.Task;
import service.HttpTaskManager;
import service.Managers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class TaskHandler implements HttpHandler {
	private final HttpTaskManager httpTaskManager;
	Gson gson = Managers.getGson();

	public TaskHandler(HttpTaskManager httpTaskManager) {
		this.httpTaskManager = httpTaskManager;
	}

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		String method = httpExchange.getRequestMethod();
		String response = null;
		switch (method) {
			case "GET": {
				response = getTask(httpExchange);
				break;
			}
			case "POST": {
				response = postTask(httpExchange);
				break;
			}
			case "DELETE": {
				response = deleteTask(httpExchange);
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

	private String getTask(HttpExchange httpExchange) {
		String path = httpExchange.getRequestURI().toString();
		String[] arrayPath = path.split("/");
		String response = null;
		if (path.contains("id")) {
			int id = Integer.parseInt(arrayPath[3].split("=")[1]);
			if (!httpTaskManager.getTasks().containsKey(id)) {
				response = "Task with id: " + id + " not a found";
				return response;
			}
			try {
				response = gson.toJson(httpTaskManager.getTaskById(id));
				return response;
			} catch (Exception e) {
				throw new ManagerException("Error GET TASK BY ID");
			}
		} else if (arrayPath[2].contains("task")) {
			try {
				if (httpTaskManager.getTasks().isEmpty()) {
					response = "Tasks not a found";
					return response;
				}
				response = gson.toJson(httpTaskManager.getTaskList());
			} catch (Exception e) {
				throw new ManagerException("Error GET ALL TASKS");
			}
		}
		return response;
	}

	private String postTask(HttpExchange httpExchange) throws IOException {
		String response = null;
		String taskJson = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
		Task task = gson.fromJson(taskJson, Task.class);
		if (httpTaskManager.getTasks().containsKey(task.getId())) {
			try {
				httpTaskManager.updateTask(task);
				response = "Task is update";
			} catch (Exception e) {
				throw new ManagerException("Error UPDATE TASK");
			}
		} else {
			try {
				httpTaskManager.createTask(task);
				response = "Task is create";
			} catch (Exception e) {
				throw new ManagerException("Error CREATE TASK");
			}
		}
		return response;
	}

	private String deleteTask(HttpExchange httpExchange) {
		String path = httpExchange.getRequestURI().toString();
		String[] arrayPath = path.split("/");
		String response = null;
		if (path.contains("id")) {
			int id = Integer.parseInt(arrayPath[3].split("=")[1]);
			try {
				if (!httpTaskManager.getTasks().containsKey(id)) {
					response = "Task with id: " + id + " not a found";
					return response;
				}
				httpTaskManager.deleteTask(id);
				response = "Task by id:" + id + " is delete";
				return response;
			} catch (Exception e) {
				throw new ManagerException("Error DELETE TASK BY ID");
			}
		} else if (arrayPath[2].contains("task")) {
			try {
				if (httpTaskManager.getTasks().isEmpty()) {
					response = "Tasks not a found";
					return response;
				}
				httpTaskManager.deleteAllTasks();
				response = "All Tasks deleted";
			} catch (Exception e) {
				throw new ManagerException("Error DELETE ALL TASKS");
			}
		}
		return response;
	}
}
