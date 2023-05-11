import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.time.Instant;

public class Main {

	public static void main(String[] args) {

		TaskManager taskManager = Managers.getDefaultTaskManager();

		Task task = taskManager.createTask(new Task(
				"Task 1", Status.NEW, "Description", Instant.now(), 10));
		Epic epic = taskManager.createEpic(new Epic("Epic 1", "Description"));
		taskManager.createSubTask(new SubTask(
				"SubTask 1", Status.NEW, "Description", task.getEndTime(), 15, epic.getId()));
		System.out.println("История после создания задач: " + taskManager.getHistory());

		taskManager.getTask(1);
		System.out.println("История после вызова task: " + taskManager.getHistory());

		taskManager.getEpic(2);
		System.out.println("История после вызова epic: " + taskManager.getHistory());

		taskManager.getEpic(2);
		taskManager.getTask(1);
		System.out.println("История после повтора: " + taskManager.getHistory());

		taskManager.getSubTask(3);
		System.out.println("История после вызова subTask: " + taskManager.getHistory());

		System.out.println("Список задач в порядке приоритета" + taskManager.getPrioritizedTasks());

		taskManager.deleteAllEpics();
		System.out.println("История после удаления epics: " + taskManager.getHistory());

	}
}
