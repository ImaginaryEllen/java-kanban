package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

	@BeforeEach
	@Override
	void init() {
		taskManager = new InMemoryTaskManager(Managers.getDefaultHistoryManager());
	}

	@Test
	void shouldAddToPrioritizedTask() {
		Task task = taskManager.createTask(new Task(
				"TestTask", Status.NEW, "TestDescription", Instant.now(), 20));
		Epic epic = taskManager.createEpic(new Epic("TestEpic", "TestDescription"));
		taskManager.createSubTask(new SubTask("TestSubTask", Status.NEW,
				"TestDescription", task.getEndTime().plusMillis(100000), 40, epic.getId()));

		final TreeSet<Task> tasks = taskManager.getPrioritizedTasks();
		assertEquals(2, tasks.size(), "Incorrect size prioritized tasks");
		assertEquals(tasks.first(), task, "Incorrect compare start time");
	}
}