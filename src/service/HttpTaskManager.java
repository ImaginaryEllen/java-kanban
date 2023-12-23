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
		client.put("tasks", gson.toJson(getTasks()));
		client.put("epics", gson.toJson(getEpics()));
		client.put("subTasks", gson.toJson(getSubTasks()));
		client.put("history", gson.toJson(getHistoryManager().getHistory().stream()
				.map(Task::getId)
				.collect(Collectors.toList())));
	}

	@Override
	public void load() {
		String jsonTasks = client.load("tasks");
		setTasks(gson.fromJson(
				jsonTasks, new TypeToken<HashMap<Integer, Task>>() {
				}.getType()));

		String jsonEpics = client.load("epics");
		setEpics(gson.fromJson(
				jsonEpics, new TypeToken<HashMap<Integer, Epic>>() {
				}.getType()));

		String jsonSubTasks = client.load("subTasks");
		setSubTasks(gson.fromJson(
				jsonSubTasks, new TypeToken<HashMap<Integer, SubTask>>() {
				}.getType()));

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
			if (getTasks().get(id) != null) {
				getHistoryManager().add(getTasks().get(id));
			} else if (getEpics().get(id) != null) {
				getHistoryManager().add(getEpics().get(id));
			} else if (getSubTasks().get(id) != null) {
				getHistoryManager().add(getSubTasks().get(id));
			}
	}
}
