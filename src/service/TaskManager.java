package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

	TreeSet<Task> getPrioritizedTasks();

	Task createTask(Task task) throws IOException;

	Epic createEpic(Epic epic) throws IOException;

	SubTask createSubTask(SubTask subTask) throws IOException;

	Task getTaskById(int id) throws IOException;

	Epic getEpicById(int id) throws IOException;

	SubTask getSubTaskById(int id) throws IOException;

	void updateTask(Task task) throws IOException;

	void updateEpic(Epic epic) throws IOException;

	void updateSubTask(SubTask subTask) throws IOException;

	List<Task> getTaskList() throws IOException;

	List<Epic> getEpicList() throws IOException;

	List<SubTask> getSubTaskList() throws IOException;

	List<SubTask> getSubTasksByEpic(int id) throws IOException;

	void deleteTask(int id) throws IOException;

	void deleteEpic(int id) throws IOException;

	void deleteSubTask(int id) throws IOException;

	void deleteAllTasks() throws IOException;

	void deleteAllEpics() throws IOException;

	void deleteAllSubTasks() throws IOException;

	List<Task> getHistory() throws IOException;
}
