import model.Epic;
import model.SubTask;
import model.Task;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task task = taskManager.createTask(new Task(8, "Task 1", "NEW", "Description"));
        taskManager.createTask(new Task(5, "Task 2", "NEW", "Description"));

        Epic epic = taskManager.createEpic(new Epic(2,"Epic 1", "NEW", "Description"));
        taskManager.createEpic(new Epic(4,"Epic 2", "NEW", "Description"));

        SubTask subTask = taskManager.createSubTask(new SubTask(1,"SubTask 1", "NEW", "Description", 2));
        taskManager.createSubTask(new SubTask(7,"SubTask 2", "NEW", "Description", 4));
        taskManager.createSubTask(new SubTask(6,"SubTask 3", "NEW", "Description", 4));

        System.out.println("Получение списка Task: " + taskManager.getListTask());
        System.out.println("Получение списка Epic: " + taskManager.getListEpic());
        System.out.println("Получение списка SubTask: " + taskManager.getListSubTask());
        System.out.println("Получение списка SubTask по Epic: " + taskManager.getSubTaskByEpic(epic));

        Task taskFromManager = taskManager.getTask(task.getId());
        Epic epicFromManager = taskManager.getEpic(epic.getId());
        SubTask subTaskFromManager = taskManager.getSubTask(subTask.getId());

        System.out.println("Получение Task по идентификатору: " + taskFromManager);
        System.out.println("Получение Epic по идентификатору: " + epicFromManager);
        System.out.println("Получение SubTask по идентификатору: " + subTaskFromManager);

        taskFromManager.setName("NewTaskName");
        taskManager.updateTask(taskFromManager);
        System.out.println("Обновили Task: " + taskFromManager);

        epicFromManager.setName("NewEpicName");
        taskManager.updateEpic(epicFromManager);
        System.out.println("Обновили Epic: " + epicFromManager);

        subTaskFromManager.setName("NewSubTaskName");
        taskManager.updateSubTask(subTaskFromManager);
        System.out.println("Обновили SubTask: " + subTaskFromManager);

        taskManager.deleteTask(taskFromManager.getId());
        System.out.println("Удаляем по ID Task: " + taskFromManager);
        taskManager.deleteEpic(epicFromManager.getId());
        System.out.println("Удаляем по ID Epic: " + epicFromManager);
        //taskManager.deleteSubTask(subTaskFromManager.getId()); //для теста работы методы
        //System.out.println("Удаляем по ID SubTask: " + subTaskFromManager);

        taskManager.deleteAllTask();
        System.out.println("Удаляем полностью Task: " + taskFromManager);
        taskManager.deleteAllEpic();
        System.out.println("Удаляем полностью Epic: " + epicFromManager);
        taskManager.deleteAllSubTask();
        System.out.println("Удаляем полностью SubTask: " + subTaskFromManager);

    }
}
