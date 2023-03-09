package service;
import model.Epic;
import model.SubTask;
import model.Task;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
	int taskId;
	int subTaskId;
	int epicId;

	public HashMap<Integer, Task> tasks;
	public HashMap<Integer, Epic> epics;
	public HashMap<Integer, SubTask> subTasks;

	public TaskManager() {
		this.tasks = new HashMap<>();
		this.epics = new HashMap<>();
		this.subTasks = new HashMap<>();
	}

	public Task createTask(Task task) {
		this.taskId = task.getId();
		tasks.put(task.getId(), task);
		return task;
	}

	public Epic createEpic(Epic epic) {
		this.epicId = epic.getId();
		epics.put(epicId, epic);
		return epic;
	}

	public SubTask createSubTask(SubTask subTask) {
		this.subTaskId = subTask.getId();
		Epic epic = epics.get(subTask.epicId);
		subTask.epic = epic;
		subTasks.put(subTaskId, subTask);
		epic.addToSubTaskList(subTask);
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
		epicSaved.setStatus(epic.getStatus());
	}

	public void updateSubTask(SubTask subTask) {
		if (subTasks.containsKey(subTask.getId())) {
			subTasks.put(subTask.getId(), subTask);
		}
		Epic epic = epics.get(subTask.getEpicId());
		if (epic != null && epics.containsKey(subTask.getEpicId())) {
			calculateEpicStatus(epic, subTask);
		}
	}

	public String calculateEpicStatus(Epic epic, SubTask subTask) {
		String subTaskStatus = subTask.getStatus();
		if (subTaskStatus.equals("NEW") || epic.subTaskList.isEmpty()) {
			return "NEW";
		} else if (subTaskStatus.equals("DONE")) {
			return "DONE";
		} else {
			return "IN_PROGRESS";
		}
	}

	public ArrayList<Task> getListTask() {
		if (tasks == null) {
			return null;
		}
		return new ArrayList<>(tasks.values());
	}

	public ArrayList<Epic> getListEpic() {
		if (epics == null) {
			return null;
		}
		return new ArrayList<>(epics.values());
	}

	public ArrayList<SubTask> getListSubTask() {
		if (subTasks == null) {
			return null;
		}
		return new ArrayList<>(subTasks.values());
	}

	public ArrayList<SubTask> getSubTaskByEpic(Epic epic) {
		if (epic == null) {
			return null;
		}
		return epic.getSubTaskList();
	}

	public void deleteTask(int id) {
		tasks.remove(id);
	}

	public void deleteEpic(int id) {
		Epic epic = epics.get(id);
		ArrayList<SubTask> subTask = epic.getSubTaskList();
		for (SubTask list : subTask) {
			subTasks.remove(list.getId());
		}
		epics.remove(id);
	}

	public void deleteSubTask(int id) {
		SubTask subTask = subTasks.get(id);
		int subTaskEpicId = subTask.getEpicId();
		Epic epic = epics.get(subTaskEpicId);
		epic.subTaskList.remove(subTask);
		calculateEpicStatus(epic, subTask);
	}

	public void deleteAllTask() {
		if (tasks.isEmpty()) {
			System.out.println("Список актуальных Task-задач отсутствует");
		} else {
			tasks.clear();
		}
	}

	public void deleteAllEpic() {
		if (epics.isEmpty()) {
			System.out.println("Список актуальных Epic-задач отсутствует");
		} else {
			epics.clear();
		}
	}

	public void deleteAllSubTask() {
		if (subTasks.isEmpty()) {
			System.out.println("Список актуальных SubTask-задач отсутствует");
		} else {
			subTasks.clear();
		}
	}
}
