package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.SubTask;
import model.Task;
import server.KVTaskClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
	private final KVTaskClient client;
	private final Gson gson = Managers.getGson();

	public HttpTaskManager() {
		client = new KVTaskClient("http://localhost:8078/");
	}

	public KVTaskClient getClient() {
		return client;
	}

	@Override
	public void save() {
		client.put("tasks", gson.toJson(tasks));
		client.put("epics", gson.toJson(epics));
		client.put("subTasks", gson.toJson(subTasks));
		client.put("history", gson.toJson(historyManager.getHistory().stream()
				.map(Task::getId)
				.collect(Collectors.toList())));
	}

	@Override
	public void load() {
		String jsonTasks = client.load("tasks");
		tasks = gson.fromJson(
				jsonTasks, new TypeToken<HashMap<Integer, Task>>() {
				}.getType());

		String jsonEpics = client.load("epics");
		epics = gson.fromJson(
				jsonEpics, new TypeToken<HashMap<Integer, Epic>>() {
				}.getType());

		String jsonSubTasks = client.load("subTasks");
		subTasks = gson.fromJson(
				jsonSubTasks, new TypeToken<HashMap<Integer, SubTask>>() {
				}.getType());

		String jsonHistory = client.load("history");
		ArrayList<Integer> historyId = gson.fromJson(
				jsonHistory, new TypeToken<ArrayList<Integer>>() {
				}.getType());
		if (historyId != null) {
			for (Integer id : historyId) {
					getHistoryTask(id);
			}
		}
	}

	private void getHistoryTask(int id) {
			if (tasks.get(id) != null) {
				historyManager.add(tasks.get(id));
			} else if (epics.get(id) != null) {
				historyManager.add(epics.get(id));
			} else if (subTasks.get(id) != null) {
				historyManager.add(subTasks.get(id));
			}
	}
}
