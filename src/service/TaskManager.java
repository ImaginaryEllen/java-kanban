package service;

import model.Epic;
import model.SubTask;
import model.Task;
import java.util.List;

public interface TaskManager {
	Task createTask(Task task);

	Epic createEpic(Epic epic);

	SubTask createSubTask(SubTask subTask);

	Task getTask(int id);

	Epic getEpic(int id);

	SubTask getSubTask(int id);

	void updateTask(Task task);

	void updateEpic(Epic epic);

	void updateSubTask(SubTask subTask);

	List<Task> getTaskList();

	List<Epic> getEpicList();

	List<SubTask> getSubTaskList();

	List<SubTask> getSubTasksByEpic(Epic epic);

	void deleteTask(int id);

	void deleteEpic(int id);

	void deleteSubTask(int id);

	void deleteAllTasks();

	void deleteAllEpics();

	void deleteAllSubTasks();

	List<Task> getHistory();
}
