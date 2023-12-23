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
	protected void beforeAll() throws IOException {
		server = new KVServer();
		server.start();
		taskManager = new HttpTaskManager();
	}

	@Test
	void shouldSaveAndLoadTasks() throws IOException {
		Task task = taskManager.createTask(new Task(
				"TestTask", NEW, "TestDescription", Instant.now(), 15));
		assertEquals(1, taskManager.getTasks().size(),
				"Incorrect size of tasks:" + taskManager.getTasks().size());
		taskManager.save();
		taskManager.load();
		assertNotNull(task, "Not save and load task");
		Task newTask = taskManager.getTasks().get(1);
		assertEquals(1, newTask.getId(), "Incorrect load Id for task:" + newTask.getId());
		assertEquals("TestTask", newTask.getName(), "Incorrect Name for task:" + newTask.getName());
		assertEquals("TestDescription", newTask.getDescription(),
				"Incorrect Description for task:" + newTask.getDescription());
		assertEquals(NEW, newTask.getStatus(), "Incorrect load Status for task:" + newTask.getStatus());
		assertEquals(task.getStartTime(), newTask.getStartTime(),
				"Incorrect load StartTime for task:" + newTask.getStartTime());
		assertEquals(15, newTask.getDuration(), "Incorrect load Duration for task:" + newTask.getId());
	}

	@Test
	void shouldSaveAndLoadEpics() throws IOException {
		Epic epic = taskManager.createEpic(new Epic(
				"TestEpic", "TestDescription"));
		assertEquals(1, taskManager.getEpics().size(),
				"Incorrect size of epics:" + taskManager.getEpics().size());
		taskManager.save();
		taskManager.load();
		assertNotNull(epic, "Not save and load epic");
		Epic newEpic = taskManager.getEpics().get(1);
		assertEquals(1, newEpic.getId(), "Incorrect load Id for epic:" + newEpic.getId());
		assertEquals("TestEpic", newEpic.getName(), "Incorrect Name for epic:" + newEpic.getName());
		assertEquals("TestDescription", newEpic.getDescription(),
				"Incorrect Description for epic:" + newEpic.getDescription());
		assertEquals(NEW, newEpic.getStatus(), "Incorrect load Status for epic:" + newEpic.getStatus());
		assertNull(newEpic.getStartTime(),
				"Incorrect load StartTime for epic:" + newEpic.getStartTime());
		assertEquals(0, newEpic.getDuration(), "Incorrect load Duration for epic:" + newEpic.getId());
	}

	@Test
	void shouldSaveAndLoadSubTasks() throws IOException {
		Epic epic = taskManager.createEpic(new Epic(
				"TestEpic", "TestDescription"));
		SubTask subTask = taskManager.createSubTask(new SubTask("TestSubTask", NEW, "TestDescription",
				Instant.now(), 30, epic.getId()));
		assertEquals(1, taskManager.getSubTasks().size(),
				"Incorrect size of subTasks:" + taskManager.getSubTasks().size());
		taskManager.save();
		taskManager.load();
		assertNotNull(subTask, "Not save and load subTask");
		SubTask newSubTask = taskManager.getSubTasks().get(2);
		assertEquals(2, newSubTask.getId(), "Incorrect load Id for task:" + newSubTask.getId());
		assertEquals("TestSubTask", newSubTask.getName(), "Incorrect Name for task:" + newSubTask.getName());
		assertEquals("TestDescription", newSubTask.getDescription(),
				"Incorrect Description for task:" + newSubTask.getDescription());
		assertEquals(NEW, newSubTask.getStatus(), "Incorrect load Status for task:" + newSubTask.getStatus());
		assertEquals(subTask.getStartTime(), newSubTask.getStartTime(),
				"Incorrect load StartTime for task:" + newSubTask.getStartTime());
		assertEquals(30, newSubTask.getDuration(), "Incorrect load Duration for task:" + newSubTask.getId());
	}

	@Test
	void shouldSaveAndLoadHistory() throws IOException {
		Task task = taskManager.createTask(new Task(
		"TestTask", NEW, "TestDescription", Instant.now(), 15));
		taskManager.getTaskById(1);
		List<Task> history = taskManager.getHistory();
		assertNotNull(history, "Incorrect history list");
		taskManager.save();
		taskManager.load();
		List<Task> newHistory = taskManager.getHistory();
		assertNotNull(newHistory, "History is empty");
		Task newTask = newHistory.get(0);
		assertEquals(1, newTask.getId(), "Incorrect task Id from history:" + newTask.getId());
		assertEquals("TestTask", newTask.getName(),
				"Incorrect Name for task from history:" + newTask.getName());
		assertEquals("TestDescription", newTask.getDescription(),
				"Incorrect Description for task:" + newTask.getDescription());
		assertEquals(NEW, newTask.getStatus(),
				"Incorrect load Status for task from history:" + newTask.getStatus());
		assertEquals(task.getStartTime(), newTask.getStartTime(),
				"Incorrect load StartTime for task from history:" + newTask.getStartTime());
		assertEquals(15, newTask.getDuration(),
				"Incorrect load Duration for task from history:" + newTask.getId());
	}

	@AfterEach
	void stop() {
		server.stop();
	}
}