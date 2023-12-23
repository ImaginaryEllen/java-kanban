package service;

import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
	private File file;

	public FileBackedTasksManager(HistoryManager historyManager, File file) {
		super(historyManager);
		this.file = file;
	}

	public FileBackedTasksManager() {
		super();
	}

	public static void main(String[] args) throws IOException {
		TaskManager taskManager = new FileBackedTasksManager(
				Managers.getDefaultHistoryManager(), new File("repository/task.csv"));
		Task task = taskManager.createTask(new Task(
				"Task", Status.NEW, "Description", Instant.now(), 15));
		Epic epic = taskManager.createEpic(new Epic("Epic", "Description"));
		taskManager.createSubTask(new SubTask(
				"SubTask", Status.NEW, "Description",
				task.getEndTime().plusMillis(100000), 10, epic.getId()));
		taskManager.getTaskById(1);
		taskManager.getEpicById(2);
		taskManager.getSubTaskById(3);
		TaskManager fileManager = FileBackedTasksManager.loadFromFile(new File("repository/task.csv"));
		System.out.println(fileManager.getTaskList());
		System.out.println(fileManager.getEpicList());
		System.out.println(fileManager.getSubTaskList());
		System.out.println((fileManager.getHistory()));
	}

	@Override
	public Task createTask(Task task) throws IOException {
		Task newTask = super.createTask(task);
		save();
		return newTask;
	}

	@Override
	public Epic createEpic(Epic epic) throws IOException {
		Epic newEpic = super.createEpic(epic);
		save();
		return newEpic;
	}

	@Override
	public SubTask createSubTask(SubTask subTask) throws IOException {
		SubTask newSubTask = super.createSubTask(subTask);
		save();
		return newSubTask;
	}

	@Override
	public void updateTask(Task task) throws IOException {
		super.updateTask(task);
		save();
	}

	@Override
	public void updateEpic(Epic epic) throws IOException {
		super.updateEpic(epic);
		save();
	}

	@Override
	public void updateSubTask(SubTask subTask) throws IOException {
		super.updateSubTask(subTask);
		save();
	}

	@Override
	public Task getTaskById(int id) throws IOException {
		Task task = super.getTaskById(id);
		save();
		return task;
	}

	@Override
	public Epic getEpicById(int id) throws IOException {
		Epic epic = super.getEpicById(id);
		save();
		return epic;
	}

	@Override
	public SubTask getSubTaskById(int id) throws IOException {
		SubTask subTask = super.getSubTaskById(id);
		save();
		return subTask;
	}

	@Override
	public List<Task> getTaskList() throws IOException {
		List<Task> tasks = super.getTaskList();
		save();
		return tasks;
	}

	@Override
	public List<Epic> getEpicList() throws IOException {
		List<Epic> epics = super.getEpicList();
		save();
		return epics;
	}

	@Override
	public List<SubTask> getSubTaskList() throws IOException {
		List<SubTask> subTasks = super.getSubTaskList();
		save();
		return subTasks;
	}

	@Override
	public List<SubTask> getSubTasksByEpic(int id) {
		List<SubTask> subTasksByEpic = super.getSubTasksByEpic(id);
		save();
		return subTasksByEpic;
	}

	@Override
	public void deleteTask(int id) throws IOException {
		super.deleteTask(id);
		save();
	}

	@Override
	public void deleteEpic(int id) throws IOException {
		super.deleteEpic(id);
		save();
	}

	@Override
	public void deleteSubTask(int id) throws IOException {
		super.deleteSubTask(id);
		save();
	}

	@Override
	public void deleteAllTasks() throws IOException {
		super.deleteAllTasks();
		save();
	}

	@Override
	public void deleteAllEpics() throws IOException {
		super.deleteAllEpics();
		save();
	}

	@Override
	public void deleteAllSubTasks() throws IOException {
		super.deleteAllSubTasks();
		save();
	}

	@Override
	public List<Task> getHistory() throws IOException {
		return super.getHistory();
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
		FileBackedTasksManager fileManager = new FileBackedTasksManager(Managers.getDefaultHistoryManager(), file);
		fileManager.load();
		return fileManager;
	}

	protected void load() {
		int max = 0;
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
						getTasks().put(task.getId(), task);
						break;
					case EPIC:
						getEpics().put(task.getId(), (Epic) task);
						break;
					case SUBTASK:
						super.getSubTasks().put(task.getId(), (SubTask) task);
						if (getEpics().get(((SubTask) task).getEpicId()) != null) {
							getEpics().get(((SubTask) task).getEpicId()).addToSubTaskList((SubTask) task);
						}
						break;
				}
				if (max < task.getId()) {
					max = task.getId();
				}
			}
			setIdNumber(max);
			String line = reader.readLine();
			final List<Integer> idFromHistory = historyFromString(line);
			for (Integer id : idFromHistory) {
				if (getTasks().containsKey(id)) {
					getHistoryManager().add(getTasks().get(id));
				} else if (getEpics().containsKey(id)) {
					getHistoryManager().add(getEpics().get(id));
				} else {
					getHistoryManager().add(getSubTasks().get(id));
				}
			}
		} catch (IOException exception) {
			throw new ManagerException("Error reading from file");
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Error reading empty file");
		}
	}

	protected void save() {
		if (file != null) {
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
