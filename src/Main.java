import controller.HttpTaskServer;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import server.KVServer;

import java.io.IOException;
import java.time.Instant;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {

		KVServer server = new KVServer();
		server.start();

		HttpTaskServer taskServer = new HttpTaskServer();

		Task task = taskServer.getHttpTaskManager().createTask(new Task(
				"Task 1", Status.NEW, "Description", Instant.now(), 10));
		Epic epic = taskServer.getHttpTaskManager().createEpic(new Epic("Epic 1", "Description"));
		taskServer.getHttpTaskManager().createSubTask(new SubTask(
				"SubTask 1", Status.NEW, "Description",
				task.getEndTime().plusMillis(100000), 15, epic.getId()));

		taskServer.start();

		System.out.println("История после создания задач: " + taskServer.getHttpTaskManager().getHistory());

		taskServer.getHttpTaskManager().getTaskById(1);
		System.out.println("История после вызова task: " + taskServer.getHttpTaskManager().getHistory());

		taskServer.getHttpTaskManager().getEpicById(2);
		System.out.println("История после вызова epic: " + taskServer.getHttpTaskManager().getHistory());

		taskServer.getHttpTaskManager().getEpicById(2);
		taskServer.getHttpTaskManager().getTaskById(1);
		System.out.println("История после повтора: " + taskServer.getHttpTaskManager().getHistory());

		taskServer.getHttpTaskManager().getSubTaskById(3);
		System.out.println("История после вызова subTask: " + taskServer.getHttpTaskManager().getHistory());

		System.out.println("Список задач в порядке приоритета" + taskServer.getHttpTaskManager().getPrioritizedTasks());

		taskServer.getHttpTaskManager().deleteAllEpics();
		System.out.println("История после удаления epics: " + taskServer.getHttpTaskManager().getHistory());

		taskServer.stop();
		server.stop();
	}
}
