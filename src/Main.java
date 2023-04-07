import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

public class Main {

	public static void main(String[] args) {

		TaskManager taskManager = Managers.getDefaultTaskManager();

		taskManager.createTask(new Task("Task 1", Status.NEW, "Description"));
		taskManager.createTask(new Task("Task 2", Status.NEW, "Description"));

		Epic epic = taskManager.createEpic(new Epic("Epic 1", "Description"));
		taskManager.createEpic(new Epic("Epic 2", "Description"));

		taskManager.createSubTask(new SubTask("SubTask 1", Status.NEW, "Description", epic.getId()));
		taskManager.createSubTask(new SubTask("SubTask 2", Status.IN_PROGRESS, "Description", epic.getId()));
		taskManager.createSubTask(new SubTask("SubTask 3", Status.NEW, "Description", epic.getId()));

		System.out.println("История после создания задач: " + taskManager.getHistory());

		taskManager.getTask(1);
		taskManager.getTask(2);
		System.out.println("История после вызова tasks: " + taskManager.getHistory());

		taskManager.getEpic(3);
		taskManager.getEpic(4);
		System.out.println("История после вызова epics: " + taskManager.getHistory());

		taskManager.getTask(2);
		taskManager.getTask(1);
		System.out.println("История после повтора tasks: " + taskManager.getHistory());

		taskManager.getSubTask(5);
		taskManager.getSubTask(6);
		taskManager.getSubTask(7);
		System.out.println("История после вызова subTasks: " + taskManager.getHistory());

		taskManager.deleteSubTask(6);
		taskManager.deleteSubTask(7);
		System.out.println("История после удаления 2-х subTasks: " + taskManager.getHistory());

		taskManager.deleteAllEpics();
		System.out.println("История после удаления epics: " + taskManager.getHistory());

	}
}
