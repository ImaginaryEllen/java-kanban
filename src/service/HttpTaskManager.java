package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.SubTask;
import model.Task;
import server.KVClient;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
	private final KVClient client;
	private final Gson gson = Managers.getGson();

	public HttpTaskManager() throws IOException, InterruptedException {
		client = new KVClient(URI.create("http://localhost:8078"));
	}

	public KVClient getClient() {
		return client;
	}

	@Override
	public void save() {
		client.save("tasks", gson.toJson(tasks));
		client.save("epics", gson.toJson(epics));
		client.save("subTasks", gson.toJson(subTasks));
		client.save("history", gson.toJson(historyManager.getHistory().stream()
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
				if (tasks.containsKey(id)) {
					historyManager.add(tasks.get(id));
				} else if (epics.containsKey(id)) {
					historyManager.add(epics.get(id));
				} else if (subTasks.containsKey(id)) {
					historyManager.add(subTasks.get(id));
				}
			}
		}
	}
}
