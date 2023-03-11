package service;
import model.Epic;
import model.SubTask;
import model.Task;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

	int idNumber;
	private HashMap<Integer, Task> tasks;
	private HashMap<Integer, Epic> epics;
	private HashMap<Integer, SubTask> subTasks;

	public TaskManager() {
		this.tasks = new HashMap<>();
		this.epics = new HashMap<>();
		this.subTasks = new HashMap<>();
	}

	public Task createTask(Task task) {
		task.setId(++idNumber);
		tasks.put(idNumber, task);
		return task;
	}

	public Epic createEpic(Epic epic) { //автоматически присвоить статус
		epic.setId(++idNumber);
		epic.setStatus("NEW"); //или сделать вызов метода со статусами
		epics.put(idNumber, epic);
		return epic;
	}

	public SubTask createSubTask(SubTask subTask) { //При создании сабтаска стоит пересчитать статус эпика
		subTask.setId(++idNumber);
		Epic epic = epics.get(subTask.getEpicId());
		subTasks.put(idNumber, subTask);
		epic.addToSubTaskList(subTask);
		calculateEpicStatus(epic);
		return subTask;
	}

	public Task getTask(int id) {
		if (tasks == null) {
			return null;
		}
		return tasks.get(id);
	}

	public Epic getEpic(int id) {
		if (epics == null) {
			return null;
		}
		return epics.get(id);
	}

	public SubTask getSubTask(int id) {
		if (subTasks == null) {
			return null;
		}
		return subTasks.get(id);
	}

	public void updateTask(Task task) {
		tasks.put(task.getId(), task);
	}

	public void updateEpic(Epic epic) {
		Epic epicSaved = epics.get(epic.getId());
		epicSaved.setName(epic.getName());
		epicSaved.setDescription(epic.getDescription());
	}

	public void updateSubTask(SubTask subTask) {
		subTasks.put(subTask.getId(), subTask);
		Epic epic = epics.get(subTask.getEpicId());
		if (epic != null) {
			calculateEpicStatus(epic);
		}
	}

	private String calculateEpicStatus(Epic epic) { //полностью переделать
		int subTaskNewCount = 0;
		int subTaskDoneCount = 0;
		String statusSubTask;
		ArrayList<SubTask> subTaskList = epic.getSubTaskList();
		for (SubTask subTask : subTaskList) {
			statusSubTask = subTask.getStatus();
			if (statusSubTask.equals("NEW")) {
				++subTaskNewCount;
			} else if (statusSubTask.equals("DONE")) {
				++subTaskDoneCount;
			}
		}
		int size = subTaskList.size();
		if (size == subTaskNewCount || size == 0) {
			return "NEW";
		} else if (size == subTaskDoneCount) {
			return "DONE";
		} else {
		}
		return "IN_PROGRESS";
	}

	public ArrayList<Task> getTaskList() {
		if (tasks == null) {
			return null;
		}
		return new ArrayList<>(tasks.values());
	}

	public ArrayList<Epic> getEpicList() {
		if (epics == null) {
			return null;
		}
		return new ArrayList<>(epics.values());
	}

	public ArrayList<SubTask> getSubTaskList() {
		if (subTasks == null) {
			return null;
		}
		return new ArrayList<>(subTasks.values());
	}

	public ArrayList<SubTask> getSubTasksByEpic(Epic epic) {
		if (epic == null) {
			return null;
		}
		return epic.getSubTaskList();
	}

	public void deleteTask(int id) {
		tasks.remove(id);
	}

/*	public void deleteEpic(int id) {
		Epic epic = epics.get(id);
		ArrayList<SubTask> list = epic.getSubTaskList();
		for (SubTask subTask : list) {
			subTasks.remove(subTask.getId());
		}
		epics.remove(id);
	}*/

	public void deleteSubTask(int id) {
		SubTask subTask = subTasks.get(id);
		int subTaskEpicId = subTask.getEpicId();
		Epic epic = epics.get(subTaskEpicId);
		epic.deleteSubTaskByEpic(subTask);
		subTasks.remove(id);
		calculateEpicStatus(epic);
	}

	public void deleteAllTask() {
		if (!tasks.isEmpty()) {
			tasks.clear();
		}
	}

	public void deleteAllEpic() { //При удалении всех эпиков необходимо удалять все сабтаски.
		if (!epics.isEmpty()) {
			epics.clear();
		}
	}

	public void deleteAllSubTask() { //При удалении всех сабтасков стоит пересчитать статусы всех эпиков.
		if (!subTasks.isEmpty()) {
			subTasks.clear();
		}
	}
}
