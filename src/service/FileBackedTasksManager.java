package service;

import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
	private final File file;

	public FileBackedTasksManager(HistoryManager historyManager, File file) {
		super(historyManager);
		this.file = file;
	}

	public static void main(String[] args) {
		TaskManager taskManager = new FileBackedTasksManager(
				Managers.getDefaultHistoryManager(), new File("repository/task.csv"));
		Task task = taskManager.createTask(new Task(
				"Task", Status.NEW, "Description", Instant.now(), 15));
		Epic epic = taskManager.createEpic(new Epic("Epic", "Description"));
		taskManager.createSubTask(new SubTask(
				"SubTask 1", Status.NEW, "Description",
				task.getEndTime().plusMillis(100000), 10, epic.getId()));
		taskManager.getTask(1);
		taskManager.getEpic(2);
		taskManager.getSubTask(3);
		TaskManager fileManager = FileBackedTasksManager.loadFromFile(new File("repository/task.csv"));
		System.out.println(fileManager.getTaskList());
		System.out.println(fileManager.getEpicList());
		System.out.println(fileManager.getSubTaskList());
		System.out.println((fileManager.getHistory()));
	}

	@Override
	public HashMap<Integer, Task> getTasks() {
		return super.getTasks();
	}

	@Override
	public HashMap<Integer, Epic> getEpics() {
		return super.getEpics();
	}

	@Override
	public HashMap<Integer, SubTask> getSubTasks() {
		return super.getSubTasks();
	}

	@Override
	public void setIdNumber(int idNumber) {
		super.setIdNumber(idNumber);
	}

	@Override
	public Task createTask(Task task) {
		Task newTask = super.createTask(task);
		save();
		return newTask;
	}

	@Override
	public Epic createEpic(Epic epic) {
		Epic newEpic = super.createEpic(epic);
		save();
		return newEpic;
	}

	@Override
	public SubTask createSubTask(SubTask subTask) {
		SubTask newSubTask = super.createSubTask(subTask);
		save();
		return newSubTask;
	}

	@Override
	public void updateTask(Task task) {
		super.updateTask(task);
		save();
	}

	@Override
	public void updateEpic(Epic epic) {
		super.updateEpic(epic);
		save();
	}

	@Override
	public void updateSubTask(SubTask subTask) {
		super.updateSubTask(subTask);
		save();
	}

	@Override
	public Task getTask(int id) {
		Task task = super.getTask(id);
		save();
		return task;
	}

	@Override
	public Epic getEpic(int id) {
		Epic epic = super.getEpic(id);
		save();
		return epic;
	}

	@Override
	public SubTask getSubTask(int id) {
		SubTask subTask = super.getSubTask(id);
		save();
		return subTask;
	}

	@Override
	public List<Task> getTaskList() {
		List<Task> tasks = super.getTaskList();
		save();
		return tasks;
	}

	@Override
	public List<Epic> getEpicList() {
		List<Epic> epics = super.getEpicList();
		save();
		return epics;
	}

	@Override
	public List<SubTask> getSubTaskList() {
		List<SubTask> subTasks = super.getSubTaskList();
		save();
		return subTasks;
	}

	@Override
	public List<SubTask> getSubTasksByEpic(Epic epic) {
		List<SubTask> subTasksByEpic = super.getSubTasksByEpic(epic);
		save();
		return subTasksByEpic;
	}

	@Override
	public void deleteTask(int id) {
		super.deleteTask(id);
		save();
	}

	@Override
	public void deleteEpic(int id) {
		super.deleteEpic(id);
		save();
	}

	@Override
	public void deleteSubTask(int id) {
		super.deleteSubTask(id);
		save();
	}

	@Override
	public void deleteAllTasks() {
		super.deleteAllTasks();
		save();
	}

	@Override
	public void deleteAllEpics() {
		super.deleteAllEpics();
		save();
	}

	@Override
	public void deleteAllSubTasks() {
		super.deleteAllSubTasks();
		save();
	}

	@Override
	public List<Task> getHistory() {
		List<Task> history = super.getHistory();
		save();
		return history;
	}

	private static String historyToString(HistoryManager manager) {
		StringBuilder ids = new StringBuilder();
		for (Task task : manager.getHistory()) {
			ids.append(task.getId()).append(",");
		}
		return ids.toString();
	}

	private static List<Integer> historyFromString(String value) {
		final String[] ids = value.split(",");
		List<Integer> idFromHistory = new ArrayList<>();
		for (String id : ids) {
			idFromHistory.add(Integer.valueOf(id));
		}
		return idFromHistory;
	}

	public static FileBackedTasksManager loadFromFile(File file) {
		int max = 0;
		FileBackedTasksManager fileManager = new FileBackedTasksManager(Managers.getDefaultHistoryManager(), file);
		try (final BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
			reader.readLine();
			while (true) {
				String line = reader.readLine();
				if (line.isEmpty()) {
					break;
				}
				String[] arrayLine = line.split(",");
				TaskType type = TaskType.valueOf(arrayLine[1]);
				final Task task = fromString(line);
				switch (type) {
					case TASK:
						fileManager.getTasks().put(task.getId(), task);
						break;
					case EPIC:
						fileManager.getEpics().put(task.getId(), (Epic) task);
						break;
					case SUBTASK:
						fileManager.getSubTasks().put(task.getId(), (SubTask) task);
						if (fileManager.getEpics().get(((SubTask) task).getEpicId()) != null) {
							fileManager.getEpics().get(((SubTask) task).getEpicId()).addToSubTaskList((SubTask) task);
						}
						break;
				}
				if (max < task.getId()) {
					max = task.getId();
				}
			}
			fileManager.setIdNumber(max);
			String line = reader.readLine();
			final List<Integer> idFromHistory = historyFromString(line);
			for (Integer id : idFromHistory) {
				if (fileManager.getTasks().containsKey(id)) {
					fileManager.getHistoryManager().add(fileManager.getTasks().get(id));
				} else if (fileManager.getEpics().containsKey(id)) {
					fileManager.getHistoryManager().add(fileManager.getEpics().get(id));
				} else {
					fileManager.getHistoryManager().add(fileManager.getSubTasks().get(id));
				}
			}
		} catch (IOException exception) {
			throw new ManagerException("Error reading from file");
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Error reading empty file");
		}
		return fileManager;
	}

	private void save() {
		try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
			writer.append("id,type,name,status,description,start,duration,epicID");
			writer.newLine();
			for (Task task : getTasks().values()) {
				writer.append(toString(task));
				writer.newLine();
			}
			for (Epic epic : getEpics().values()) {
				writer.append(toString(epic));
				writer.newLine();
			}
			for (SubTask subTask : getSubTasks().values()) {
				writer.append(toString(subTask));
				writer.newLine();
			}
			writer.newLine();
			writer.write(historyToString(getHistoryManager()));
			writer.newLine();

		} catch (IOException e) {
			throw new ManagerException("Error writing to file");
		}
	}

	private static String toString(Task task) {
		if (task.getType().equals(TaskType.SUBTASK)) {
			SubTask subTask = (SubTask) task;
			return task.getId() + "," + task.getType() + "," + task.getName() + ","
					+ task.getStatus() + "," + task.getDescription()
					+ "," + task.getStartTime() + "," + task.getDuration() + "," + subTask.getEpicId();
		}
		return task.getId() + "," + task.getType() + "," + task.getName() + ","
				+ task.getStatus() + "," + task.getDescription() + "," + task.getStartTime() + "," + task.getDuration();
	}

	private static Task fromString(String value) {
		String[] arrayTask = value.split(",");
		Task task = null;
		TaskType type = TaskType.valueOf(arrayTask[1]);
		switch (type) {
			case TASK:
				task = new Task(Integer.parseInt(arrayTask[0]), arrayTask[2],
						Status.valueOf(arrayTask[3]), arrayTask[4],
						Instant.parse(arrayTask[5]), Integer.parseInt(arrayTask[6]));
				break;
			case EPIC:
				task = new Epic(arrayTask[2], arrayTask[4]);
				task.setId(Integer.parseInt(arrayTask[0]));
				task.setStatus(Status.valueOf(arrayTask[3]));
				if (arrayTask[5].equals("null") && Integer.parseInt(arrayTask[6]) == 0) {
					task.setStartTime(null);
					task.setDuration(0);
					break;
				}
				task.setStartTime(Instant.parse(arrayTask[5]));
				task.setDuration(Integer.parseInt(arrayTask[6]));
				break;
			case SUBTASK:
				task = new SubTask(Integer.parseInt(arrayTask[0]), arrayTask[2],
						Status.valueOf(arrayTask[3]), arrayTask[4],
						Instant.parse(arrayTask[5]), Integer.parseInt(arrayTask[6]), Integer.parseInt(arrayTask[7]));
				break;
			default:
				break;
		}
		return task;
	}
}
