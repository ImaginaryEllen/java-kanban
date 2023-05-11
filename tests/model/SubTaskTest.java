package model;

import org.junit.jupiter.api.Test;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.time.Instant;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

	TaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager());

	@Test
	void shouldReturnCorrectSubTask() {
		Epic epic = manager.createEpic(new Epic("TestEpic", "TestDescription"));
		SubTask subTask = manager.createSubTask(new SubTask(
				"TestSubTask", NEW, "TestDescription", Instant.now(), 15, epic.getId()));

		int epicId = epic.getId();
		TaskType type = subTask.getType();
		Instant start = Instant.now();
		Instant end = start.plusMillis(15 * 60000);

		assertEquals(epicId, subTask.getEpicId(), "Incorrect epic ID for subTask");
		assertEquals(TaskType.SUBTASK, type, "Incorrect type for subTask");
		assertEquals(start, subTask.getStartTime(), "Incorrect start for subTask");
		assertEquals(15, subTask.getDuration(), "Incorrect epic duration for subTask");
		assertEquals(end, subTask.getEndTime(), "Incorrect end for subTask");
	}
}