package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

	private int idNumber;
	private final HashMap<Integer, Task> tasks;
	private final HashMap<Integer, Epic> epics;
	private final HashMap<Integer, SubTask> subTasks;
	private final HistoryManager historyManager;

	public InMemoryTaskManager(HistoryManager historyManager) {
		this.historyManager = historyManager;
		this.tasks = new HashMap<>();
		this.epics = new HashMap<>();
		this.subTasks = new HashMap<>();
	}

	@Override
	public Task createTask(Task task) {
		task.setId(++idNumber);
		tasks.put(idNumber, task);
		return task;
	}

	@Override
	public Epic createEpic(Epic epic) {
		epic.setId(++idNumber);
		epics.put(idNumber, epic);
		return epic;
	}

	@Override
	public SubTask createSubTask(SubTask subTask) {
		subTask.setId(++idNumber);
		subTasks.put(idNumber, subTask);
		final Epic epic = epics.get(subTask.getEpicId());
		epic.addToSubTaskList(subTask);
		calculateEpicStatus(epic);
		return subTask;
	}

	@Override
	public Task getTask(int id) {
		final Task task = tasks.get(id);
		if (task == null) {
			return null;
		}
		historyManager.add(task);
		return task;
	}

	@Override
	public Epic getEpic(int id) {
		final Epic epic = epics.get(id);
		if (epic == null) {
			return null;
		}
		historyManager.add(epic);
		return epic;
	}

	@Override
	public SubTask getSubTask(int id) {
		final SubTask subTask = subTasks.get(id);
		if (subTask == null) {
			return null;
		}
		historyManager.add(subTask);
		return subTask;
	}

	@Override
	public void updateTask(Task task) {
		final Task saved = tasks.get(task.getId());
		saved.setName(task.getName());
		saved.setDescription(task.getDescription());
		saved.setStatus(task.getStatus());
		tasks.put(task.getId(), task);
	}

	@Override
	public void updateEpic(Epic epic) {
		final Epic saved = epics.get(epic.getId());
		saved.setName(epic.getName());
		saved.setDescription(epic.getDescription());
		calculateEpicStatus(epic);
		epics.put(epic.getId(), epic);
	}

	@Override
	public void updateSubTask(SubTask subTask) {
		final SubTask saved = subTasks.get(subTask.getId());
		saved.setName(subTask.getName());
		saved.setDescription(subTask.getDescription());
		saved.setStatus(subTask.getStatus());
		subTasks.put(subTask.getId(), subTask);
		final Epic epic = epics.get(subTask.getEpicId());
		calculateEpicStatus(epic);
	}

	private void calculateEpicStatus(Epic epic) {
		int subTaskNewCount = 0;
		int subTaskDoneCount = 0;
		Status statusSubTask;
		final List<SubTask> subTaskList = epic.getSubTaskList();
		for (SubTask subTask : subTaskList) {
			statusSubTask = subTask.getStatus();
			if (statusSubTask.equals(Status.NEW)) {
				++subTaskNewCount;
			} else if (statusSubTask.equals(Status.DONE)) {
				++subTaskDoneCount;
			}
		}
		int size = subTaskList.size();
		if (size == subTaskNewCount || size == 0) {
			epic.setStatus(Status.NEW);
		} else if (size == subTaskDoneCount) {
			epic.setStatus(Status.DONE);
		} else {
			epic.setStatus(Status.IN_PROGRESS);
		}
	}

	@Override
	public List<Task> getTaskList() {
		return new ArrayList<>(tasks.values());
	}

	@Override
	public List<Epic> getEpicList() {
		return new ArrayList<>(epics.values());
	}

	@Override
	public List<SubTask> getSubTaskList() {
		return new ArrayList<>(subTasks.values());
	}

	@Override
	public List<SubTask> getSubTasksByEpic(Epic epic) {
		if (epic == null) {
			return null;
		}
		return epic.getSubTaskList();
	}

	@Override
	public void deleteTask(int id) {
		tasks.remove(id);
		historyManager.remove(id);
	}

	@Override
	public void deleteEpic(int id) {
		final Epic epic = epics.get(id);
		final List<SubTask> list = epic.getSubTaskList();
		for (SubTask subTask : list) {
			subTasks.remove(subTask.getId());
			historyManager.remove(subTask.getId());
		}
		epics.remove(id);
		historyManager.remove(id);
	}

	@Override
	public void deleteSubTask(int id) {
		final SubTask subTask = subTasks.get(id);
		final int subTaskEpicId = subTask.getEpicId();
		final Epic epic = epics.get(subTaskEpicId);
		epic.deleteSubTaskByEpic(subTask);
		subTasks.remove(id);
		historyManager.remove(id);
		calculateEpicStatus(epic);
	}

	@Override
	public void deleteAllTasks() {
		final List<Integer> keys = new ArrayList<>(tasks.keySet());
		for (Integer id : keys) {
			historyManager.remove(id);
		}
		if (!tasks.isEmpty()) {
			tasks.clear();
		}
	}

	@Override
	public void deleteAllEpics() {
		final List<Integer> keys = new ArrayList<>(epics.keySet());
		for (Integer id : keys) {
			Epic epic = epics.get(id);
			List<SubTask> list = epic.getSubTaskList();
			if (!list.isEmpty()) {
				for (SubTask subTask : list) {
					historyManager.remove(subTask.getId());
				}
			}
			historyManager.remove(id);
		}
		if (!epics.isEmpty() && !subTasks.isEmpty()) {
			subTasks.clear();
			epics.clear();
		}
	}

	@Override
	public void deleteAllSubTasks() {
		final List<Integer> keys = new ArrayList<>(subTasks.keySet());
		for (Integer id : keys) {
			historyManager.remove(id);
		}
		if (!subTasks.isEmpty()) {
			subTasks.clear();
			final List<Epic> epicList = new ArrayList<>(epics.values());
			for (Epic epic : epicList) {
				epic.deleteAllSubTask();
				calculateEpicStatus(epic);
			}
		}
	}

	@Override
	public List<Task> getHistory() {
		return historyManager.getHistory();
	}
}
