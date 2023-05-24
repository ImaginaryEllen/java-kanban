package controller;

import com.sun.net.httpserver.HttpServer;
import handler.*;
import model.*;
import server.KVServer;
import service.HttpTaskManager;
import service.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Instant;

public class HttpTaskServer {
	public static final int PORT = 8080;
	protected HttpServer server;
	protected HttpTaskManager httpTaskManager;

	public HttpTaskServer() throws IOException, InterruptedException {
		this.httpTaskManager = Managers.getDefaultTaskManager();
		server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
		server.createContext("/tasks/task", new TaskHandler(httpTaskManager));
		server.createContext("/tasks/epic", new EpicHandler(httpTaskManager));
		server.createContext("/tasks/subTask", new SubTaskHandler(httpTaskManager));
		server.createContext("/tasks/history", new HistoryHandler(httpTaskManager));
		server.createContext("/tasks/", new PrioritizedHandler(httpTaskManager));
	}

	public static void main(String[] args) throws IOException, InterruptedException {

		new KVServer().start();

		HttpTaskServer taskServer = new HttpTaskServer();
		Task task = taskServer.httpTaskManager.createTask(new Task("Task", Status.NEW,
				"D", Instant.now(), 15));
		Epic epic = taskServer.httpTaskManager.createEpic(new Epic("Epic", "D"));
		SubTask subTask = taskServer.httpTaskManager.createSubTask(new SubTask("SubTask", Status.NEW,
				"D", task.getEndTime().plusMillis(100000), 10, epic.getId()));

		taskServer.start();

		System.out.println("Tasks before: ");
		System.out.println(task);
		System.out.println(epic);
		System.out.println(subTask);

		taskServer.httpTaskManager.save();
		taskServer.httpTaskManager.load();

		System.out.println("Tasks after: ");
		System.out.println(taskServer.httpTaskManager.getTasks());
		System.out.println(taskServer.httpTaskManager.getEpics());
		System.out.println(taskServer.httpTaskManager.getSubTasks());

		taskServer.stop();
	}

	public HttpTaskManager getHttpTaskManager() {
		return httpTaskManager;
	}

	public void start() {
		System.out.println("Запускаем сервер на порту " + PORT);
		server.start();
	}

	public void stop() {
		System.out.println("Останавливаем работу сервера");
		server.stop(0);
	}
}
