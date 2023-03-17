import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefaultTaskManager();

        Task task = taskManager.createTask(new Task("Task 1", Status.NEW, "Description"));
        taskManager.createTask(new Task("Task 2", Status.NEW, "Description"));
        taskManager.createTask(new Task("Task 3", Status.DONE, "Description"));
        taskManager.createTask(new Task("Task 4", Status.IN_PROGRESS, "Description"));

        Epic epic = taskManager.createEpic(new Epic("Epic 1", "Description"));
        Epic epic1 = taskManager.createEpic(new Epic("Epic 2", "Description"));

        SubTask subTask = taskManager.createSubTask(new SubTask("SubTask 1", Status.NEW, "Description", epic.getId()));
        taskManager.createSubTask(new SubTask("SubTask 2", Status.IN_PROGRESS, "Description", epic.getId()));
        taskManager.createSubTask(new SubTask("SubTask 3", Status.NEW, "Description", epic1.getId()));
        taskManager.createSubTask(new SubTask("SubTask 4", Status.NEW, "Description", epic1.getId()));

        System.out.println("История после создания задач: " + taskManager.getHistory());

        taskManager.getTask(1);
        taskManager.getTask(2);

        System.out.println("История после вызовов 2 задач: " + taskManager.getHistory());

        taskManager.getEpic(5);
        taskManager.getEpic(6);
        taskManager.getSubTask(7);
        taskManager.getSubTask(8);
        taskManager.getSubTask(9);
        taskManager.getSubTask(10);

        System.out.println("История после 8 вызовов: " + taskManager.getHistory());

        task.setName("New Name");
        subTask.setDescription("New Description");

        taskManager.getTask(3);
        taskManager.getTask(4);
        taskManager.getTask(1);

        System.out.println("История после обновления и 11 вызовов: " + taskManager.getHistory());

        taskManager.deleteAllEpics();
        taskManager.deleteAllTasks();

        System.out.println("История после удаления задач: " + taskManager.getHistory());

    }
}
