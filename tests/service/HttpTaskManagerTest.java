package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<FileBackedTasksManager> {
	KVServer server;

	@BeforeEach
	protected void beforeEach() throws IOException, InterruptedException {
		server = new KVServer();
		server.start();
		taskManager = new HttpTaskManager();
	}

	@Test
	void shouldSaveInAndLoadTasks() throws IOException {
		Task task = taskManager.createTask(new Task(
				"TestTask", NEW, "TestDescription", Instant.now(), 15));
		Epic epic = taskManager.createEpic(new Epic(
				"TestEpic", "TestDescription"));
		SubTask subTask = taskManager.createSubTask(new SubTask("TestSubTask", NEW, "TestDescription",
				task.getEndTime().plusMillis(100000), 30, epic.getId()));
		assertEquals(1, taskManager.tasks.size(), "Incorrect size of tasks");
		assertEquals(1, taskManager.epics.size(), "Incorrect size of epics");
		assertEquals(1, taskManager.subTasks.size(), "Incorrect size of subTasks");
		taskManager.save();
		taskManager.load();
		Task newTask = taskManager.tasks.get(1);
		Epic newEpic = taskManager.epics.get(2);
		SubTask newSubTask = taskManager.subTasks.get(3);
		assertEquals(task.getId(), newTask.getId(), "Incorrect return Id for task");
		assertEquals(epic.getId(), newEpic.getId(), "Incorrect return Id for epic");
		assertEquals(subTask.getId(), newSubTask.getId(), "Incorrect return Id for subTask");
		assertEquals(task.getName(), newTask.getName(), "Incorrect return Name for task");
		assertEquals(epic.getName(), newEpic.getName(), "Incorrect return Name for epic");
		assertEquals(subTask.getName(), newSubTask.getName(), "Incorrect return Name for subTask");
		assertEquals(task.getStatus(), newTask.getStatus(), "Incorrect return Status for task");
		assertEquals(epic.getStatus(), newEpic.getStatus(), "Incorrect return Status for epic");
		assertEquals(subTask.getStatus(), newSubTask.getStatus(), "Incorrect return Status for subTask");
		assertEquals(task.getDescription(), newTask.getDescription(), "Incorrect return Description for task");
		assertEquals(epic.getDescription(), newEpic.getDescription(), "Incorrect return Description for epic");
		assertEquals(subTask.getDescription(), newSubTask.getDescription(), "Incorrect return Description for subTask");
		taskManager.getTaskById(task.getId());
		taskManager.save();
		taskManager.load();
		List<Task> history = taskManager.getHistory();
		assertNotNull(history, "History is empty");
		assertEquals(task.getId(), history.get(0).getId(), "Incorrect task from history");
		taskManager.getEpicById(epic.getId());
		taskManager.getSubTaskById(subTask.getId());
		taskManager.save();
		taskManager.load();
		List<Task> allHistory = taskManager.getHistory();
		assertNotNull(allHistory, "History is empty");
		assertEquals(epic.getId(), allHistory.get(1).getId(), "Incorrect epic from history");
		assertEquals(subTask.getId(), allHistory.get(2).getId(), "Incorrect subTask from history");
	}

	@AfterEach
	void stop() {
		server.stop();
	}
}