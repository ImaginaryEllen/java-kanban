package service;

import model.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

	private int idNumber;
	private final HashMap<Integer, Task> tasks;
	private final HashMap<Integer, Epic> epics;
	private final HashMap<Integer, SubTask> subTasks;
	private final HistoryManager historyManager;
	private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

	public InMemoryTaskManager(HistoryManager historyManager) {
		this.historyManager = historyManager;
		this.tasks = new HashMap<>();
		this.epics = new HashMap<>();
		this.subTasks = new HashMap<>();
	}

	public void setIdNumber(int idNumber) {
		this.idNumber = idNumber;
	}

	public HashMap<Integer, Task> getTasks() {
		return tasks;
	}

	public HashMap<Integer, Epic> getEpics() {
		return epics;
	}

	public HashMap<Integer, SubTask> getSubTasks() {
		return subTasks;
	}

	public HistoryManager getHistoryManager() {
		return historyManager;
	}

	@Override
	public TreeSet<Task> getPrioritizedTasks() {
		return prioritizedTasks;
	}

	@Override
	public Task createTask(Task task) {
		task.setId(++idNumber);
		if (checkOverlap(task)) {
			addToPrioritizedTasks(task);
		}
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
		if (checkOverlap(subTask)) {
			addToPrioritizedTasks(subTask);
		}
		if (!epics.isEmpty()) {
			final Epic epic = epics.get(subTask.getEpicId());
			epic.addToSubTaskList(subTask);
			epic.getEpicTime();
			calculateEpicStatus(epic);
		}
		subTasks.put(idNumber, subTask);
		return subTask;
	}

	private void addToPrioritizedTasks(Task task) {
		prioritizedTasks.add(task);
	}

	private void deleteFromPrioritizedTasks(Task task) {
		prioritizedTasks.remove(task);
	}

	private boolean checkOverlap(Task task) {
		if (prioritizedTasks.isEmpty()) {
			return true;
		}
		for (Task taskPriority : prioritizedTasks) {
			if (taskPriority.getEndTime().isBefore(task.getStartTime())
					|| taskPriority.getStartTime().isAfter(task.getEndTime())) {
				return true;
			}
		}
		throw new ManagerException("Error: tasks overlap in time");
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
		if (saved != null) {
			deleteFromPrioritizedTasks(saved);
			if (checkOverlap(task)) {
				addToPrioritizedTasks(task);
			}
			tasks.put(task.getId(), task);
		}
	}

	@Override
	public void updateEpic(Epic epic) {
		Epic saved = epics.get(epic.getId());
		if (saved != null) {
			saved.setName(epic.getName());
			saved.setDescription(epic.getDescription());
		}
	}

	@Override
	public void updateSubTask(SubTask subTask) {
		final SubTask saved = subTasks.get(subTask.getId());
		if (saved != null && subTask.getEpicId() == saved.getEpicId()) {
			deleteFromPrioritizedTasks(saved);
			if (checkOverlap(subTask)) {
				addToPrioritizedTasks(subTask);
			}
			subTasks.put(subTask.getId(), subTask);
			final Epic epic = epics.get(subTask.getEpicId());
			epic.getEpicTime();
			calculateEpicStatus(epic);
		}
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
		historyManager.remove(id);
		deleteFromPrioritizedTasks(tasks.get(id));
		tasks.remove(id);
	}

	@Override
	public void deleteEpic(int id) {
		final Epic epic = epics.get(id);
		final List<SubTask> list = epic.getSubTaskList();
		for (SubTask subTask : list) {
			subTasks.remove(subTask.getId());
			historyManager.remove(subTask.getId());
			deleteFromPrioritizedTasks(subTask);
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
		deleteFromPrioritizedTasks(subTask);
		epic.getEpicTime();
		calculateEpicStatus(epic);
	}

	@Override
	public void deleteAllTasks() {
		final List<Integer> keys = new ArrayList<>(tasks.keySet());
		for (Integer id : keys) {
			historyManager.remove(id);
			deleteFromPrioritizedTasks(tasks.get(id));
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
					deleteFromPrioritizedTasks(subTask);
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
			deleteFromPrioritizedTasks(subTasks.get(id));
		}
		if (!subTasks.isEmpty()) {
			subTasks.clear();
			final List<Epic> epicList = new ArrayList<>(epics.values());
			for (Epic epic : epicList) {
				epic.deleteAllSubTask();
				epic.getEpicTime();
				calculateEpicStatus(epic);
			}
		}
	}

	@Override
	public List<Task> getHistory() {
		return historyManager.getHistory();
	}
}
