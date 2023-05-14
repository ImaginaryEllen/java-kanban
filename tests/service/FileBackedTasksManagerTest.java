package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Instant;
import java.util.List;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
	@BeforeEach
	@Override
	void init() {
		taskManager = new FileBackedTasksManager(Managers.getDefaultHistoryManager(),
				new File("repository/task.csv"));
	}

	@Test
	void shouldSaveInAndLoadOutFileTasks() {
		Task task = taskManager.createTask(new Task(
				"TestTask", NEW, "TestDescription", Instant.now(), 15));
		Epic epic = taskManager.createEpic(new Epic(
				"TestEpic", "TestDescription"));
		SubTask subTask = taskManager.createSubTask(new SubTask("TestSubTask", NEW, "TestDescription",
				task.getEndTime().plusMillis(100000), 30, epic.getId()));
		taskManager.getHistoryManager().add(task);
		taskManager.getHistoryManager().add(epic);
		taskManager.getHistoryManager().add(subTask);

		final List<Task> tasks = taskManager.getTaskList();
		final List<Epic> epics = taskManager.getEpicList();
		final List<SubTask> subTasks = taskManager.getSubTaskList();
		final List<Task> history = taskManager.getHistory();

		assertEquals(1, tasks.size(), "Return incorrect list of tasks");
		assertEquals(1, epics.size(), "Return incorrect list of epics");
		assertEquals(1, subTasks.size(), "Return incorrect list of subTasks");
		assertEquals(3, history.size(), "Return incorrect list from history");

		TaskManager fileManager = FileBackedTasksManager.loadFromFile(new File("repository/task.csv"));

		final List<Task> tasksFromFile = fileManager.getTaskList();
		final List<Epic> epicsFromFile = fileManager.getEpicList();
		final List<SubTask> subTasksFromFile = fileManager.getSubTaskList();
		final List<Task> historyFromFile = fileManager.getHistory();

		assertEquals(tasks.size(), tasksFromFile.size(), "Return incorrect list of tasks");
		assertEquals(epics.size(), epicsFromFile.size(), "Return incorrect list of epics");
		assertEquals(subTasks.size(), subTasksFromFile.size(), "Return incorrect list of subTasks");
		assertEquals(history.size(), historyFromFile.size(), "Return incorrect list from history");
	}

	@Test
	void shouldReturnExceptionWhenTaskAndHistoryIsEmpty() {
		final List<Task> tasks = taskManager.getTaskList();
		final List<Epic> epics = taskManager.getEpicList();
		final List<SubTask> subTasks = taskManager.getSubTaskList();
		final List<Task> history = taskManager.getHistory();

		assertEquals(0, tasks.size(), "Return not empty list of tasks");
		assertEquals(0, epics.size(), "Return not empty list of epics");
		assertEquals(0, subTasks.size(), "Return not empty list of subTasks");
		assertEquals(0, history.size(), "Return not empty list from history");

		IllegalArgumentException ex = assertThrows(
				IllegalArgumentException.class,
				() -> FileBackedTasksManager.loadFromFile(new File("repository/task.csv"))
		);
		assertEquals("Error reading empty file", ex.getMessage());
	}

	@Test
	void shouldReturnExceptionWhenEpicWithoutSubtasks() {
		Epic epic = taskManager.createEpic(new Epic("TestEpic", "TestDescription"));

		final List<Epic> epics = taskManager.getEpicList();
		final List<SubTask> subTasksByEpic = taskManager.getSubTasksByEpic(epic);
		assertEquals(0, subTasksByEpic.size(), "SubTasks by epic should be empty");
		assertEquals(1, epics.size(), "Epics should be not empty");

		IllegalArgumentException ex = assertThrows(
				IllegalArgumentException.class,
				() -> FileBackedTasksManager.loadFromFile(new File("repository/task.csv"))
		);
		assertEquals("Error reading empty file", ex.getMessage());
	}
}