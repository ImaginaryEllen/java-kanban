package service;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

	HistoryManager historyManager;
	InMemoryTaskManager taskManager;
	Task task1;
	Task task2;
	Task task3;

	@BeforeEach
	void beforeEach() throws IOException {
		historyManager = new InMemoryHistoryManager();
		taskManager = new InMemoryTaskManager(historyManager);
		task1 = taskManager.createTask(new Task(
				"TestTask 1", NEW, "TestDescription", Instant.now(), 45));
		task2 = taskManager.createTask(new Task("TestTask 2",
				NEW, "TestDescription", task1.getEndTime().plusMillis(100000), 20));
		task3 = taskManager.createTask(new Task("TestTask 3",
				NEW, "TestDescription", task2.getEndTime().plusMillis(200000), 35));
	}

	@Test
	void shouldAddAndRemoveTasksByHistory() {
		historyManager.add(task1);
		historyManager.add(task2);
		historyManager.add(task3);
		assertEquals(3, historyManager.getHistory().size(), "History is not empty");

		historyManager.remove(task2.getId());
		assertFalse(historyManager.getHistory().contains(task2), "Remove can`t delete average-task in history");
		historyManager.remove(task1.getId());
		assertFalse(historyManager.getHistory().contains(task1), "Remove can`t delete first-task in history");
		historyManager.remove(task3.getId());
		assertFalse(historyManager.getHistory().contains(task3), "Remove can`t delete last-task in history");

		final List<Task> history = historyManager.getHistory();
		assertEquals(0, history.size(), "History is not empty");
	}

	@Test
	void shouldNotSaveDuplicationInHistory() {
		historyManager.add(task1);
		historyManager.add(task1);
		final List<Task> history = historyManager.getHistory();
		assertEquals(1, history.size(), "History size is not correct");
	}
}