package service;

import model.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
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
		taskManager.createTask(new Task("Task", Status.NEW, "Description"));
		Epic epic = taskManager.createEpic(new Epic("Epic", "Description"));
		taskManager.createSubTask(new SubTask("SubTask 1", Status.NEW, "Description", epic.getId()));
		taskManager.getTask(1);
		taskManager.getEpic(2);
		taskManager.getSubTask(3);
		taskManager.createSubTask(new SubTask("SubTask 2", Status.NEW, "Description", epic.getId()));
		taskManager.getSubTask(4);
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

	private static String historyToString(HistoryManager manager) {
		StringBuilder ids = new StringBuilder();
		for (Task task : manager.getHistory()) {
			ids.append(",").append(task.getId());

		}
		if (!ids.toString().isEmpty()) {
			ids.deleteCharAt(0); // для формата записи: ,1,2,3 -> 1,2,3
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
		FileBackedTasksManager fileManager = new FileBackedTasksManager(Managers.getDefaultHistoryManager(),file);
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
						if (!fileManager.getEpics().isEmpty()) {
							for (Epic epic : fileManager.getEpicList()) {
								if (epic.getId() == ((SubTask) task).getEpicId()) {
									epic.addToSubTaskList((SubTask) task);
								}
							}
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
		} catch (IOException e) {
			throw new ManagerException("Error reading from file", e);
		}
		return fileManager;
	}

	private void save() {
		try(final BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
			writer.append("id,type,name,status,description,epicID");
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
			throw new ManagerException("Error writing to file", e);
		}
	}

	private static String toString(Task task) {
		if (task.getType().equals(TaskType.SUBTASK)) {
			SubTask subTask = (SubTask) task;
			return task.getId() + "," + task.getType() + "," + task.getName() + ","
					+ task.getStatus() + "," + task.getDescription() + "," + subTask.getEpicId();
		}
		return task.getId() + "," + task.getType() + "," + task.getName() + ","
				+ task.getStatus() + "," + task.getDescription();
	}

	private static Task fromString(String value) {
		String[] arrayTask = value.split(",");
		Task task = null;
		TaskType type = TaskType.valueOf(arrayTask[1]);
		switch (type) {
			case TASK:
				task = new Task(Integer.parseInt(arrayTask[0]), arrayTask[2],
						Status.valueOf(arrayTask[3]), arrayTask[4]);
				break;
			case EPIC:
				task = new Epic(Integer.parseInt(arrayTask[0]), arrayTask[2],
						Status.valueOf(arrayTask[3]),  arrayTask[4]);
				break;
			case SUBTASK:
				task = new SubTask(Integer.parseInt(arrayTask[0]), arrayTask[2],
						Status.valueOf(arrayTask[3]), arrayTask[4], Integer.parseInt(arrayTask[5]));
				break;
			default:
				break;
		}
		return task;
	}
}
