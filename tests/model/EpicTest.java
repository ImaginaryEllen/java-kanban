package model;

import org.junit.jupiter.api.Test;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.time.Instant;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

	TaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager());

	@Test
	void shouldReturnCorrectTimeAndTypeForEpic() throws IOException {
		Epic epic = manager.createEpic(new Epic("TestEpic", "TestDescription"));
		SubTask subTask1 = manager.createSubTask(new SubTask(
				"TestSubTask", NEW, "TestDescription", Instant.now(), 10, epic.getId()));
		SubTask subTask2 = manager.createSubTask(new SubTask(
				"TestSubTask", Status.NEW, "TestDescription",
				subTask1.getEndTime().plusMillis(100000), 20, epic.getId()));

		int epicDuration = subTask1.getDuration() + subTask2.getDuration();
		Instant epicStart = subTask1.getStartTime();
		Instant epicEnd = subTask2.getEndTime();
		TaskType type = epic.getType();

		assertEquals(TaskType.EPIC, type, "Incorrect type for epic");
		assertEquals(epicStart, epic.getStartTime(), "Incorrect start time for epic");
		assertEquals(epicEnd, epic.getEndTime(), "Incorrect end time for epic");
		assertEquals(epicDuration, epic.getDuration(), "Incorrect end time for epic");
	}
}