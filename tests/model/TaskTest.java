package model;

import org.junit.jupiter.api.Test;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

	TaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager());

	@Test
	void shouldReturnCorrectTask() {
		Task task = manager.createTask(new Task(
				"TestTask", Status.NEW, "TestDescription", Instant.now(), 30));

		TaskType type = task.getType();
		Instant start = Instant.now();
		Instant end = start.plusMillis(30 * 60000);

		assertEquals(TaskType.TASK, type, "Incorrect type for task");
		assertEquals(start, task.getStartTime(), "Incorrect start for task");
		assertEquals(30, task.getDuration(), "Incorrect epic duration for task");
		assertEquals(end, task.getEndTime(), "Incorrect end for task");
	}
}