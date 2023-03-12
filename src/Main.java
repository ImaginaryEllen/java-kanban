import model.Epic;
import model.SubTask;
import model.Task;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task task = taskManager.createTask(new Task("Task 1", "NEW", "Description"));
        taskManager.createTask(new Task("Task 2", "NEW", "Description"));

        Epic epic = taskManager.createEpic(new Epic("Epic 1", "Description"));

        SubTask subTask = new SubTask("SubTask 1", "DONE", "Description", epic.getId());
        taskManager.createSubTask(new SubTask("SubTask 2", "NEW", "Description", epic.getId()));
        taskManager.createSubTask(subTask);
        subTask.setStatus("IN_PROGRESS");
        //Обновляем Подзадачу:
        SubTask subTask1 = new SubTask(subTask.getName(), subTask.getStatus(), subTask.getDescription(), subTask.getEpicId());
        subTask1.setId(subTask.getId());
        subTask1.setStatus("NEW");
        taskManager.updateSubTask(subTask1);
        System.out.println("Вывод состояния Эпика после обновлений и добавлений Подзадач:");
        System.out.println(taskManager.getEpic(epic.getId()));

        System.out.println("Получение списка Task: " + taskManager.getTaskList());
        System.out.println("Получение списка Epic: " + taskManager.getEpicList());
        System.out.println("Получение списка SubTask: " + taskManager.getSubTaskList());
        System.out.println("Получение списка SubTask по Epic: " + taskManager.getSubTasksByEpic(epic));

        Task taskFromManager = taskManager.getTask(task.getId());
        Epic epicFromManager = taskManager.getEpic(epic.getId());
        SubTask subTaskFromManager = taskManager.getSubTask(subTask.getId());

        System.out.println("Получение Task по идентификатору: " + taskFromManager);
        System.out.println("Получение Epic по идентификатору: " + epicFromManager);
        System.out.println("Получение SubTask по идентификатору: " + subTaskFromManager);

        taskFromManager.setName("NewName");
        taskManager.updateTask(taskFromManager);
        System.out.println("Обновили Task: " + taskFromManager);

        epicFromManager.setDescription("NewDescription");
        taskManager.updateEpic(epicFromManager);
        System.out.println("Обновили Epic: " + epicFromManager);

        taskManager.deleteTask(taskFromManager.getId());
        System.out.println("Удаляем по ID Task: " + taskFromManager);
        taskManager.deleteEpic(epicFromManager.getId());
        System.out.println("Удаляем по ID Epic: " + epicFromManager);
        //taskManager.deleteSubTask(subTaskFromManager.getId()); //для теста работы метода
        //System.out.println("Удаляем по ID SubTask: " + subTaskFromManager);

        taskManager.deleteAllTasks();
        System.out.println("Удаляем полностью Task: " + taskFromManager);
        //taskManager.deleteAllEpics(); //для теста работы метода
        //System.out.println("Удаляем полностью Epic: " + epicFromManager);
        taskManager.deleteAllSubTasks();
        System.out.println("Удаляем полностью SubTask: " + subTaskFromManager);

    }
}
