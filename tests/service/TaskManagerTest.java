package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static model.Status.IN_PROGRESS;
import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
	protected T taskManager;

	@Test
	void shouldCreateNewTask() throws IOException {
		Task task = taskManager.createTask(
				new Task("TestTask", NEW, "TestDescription", Instant.now(), 15));
		final int taskId = task.getId();
		final Task savedTask = taskManager.getTaskById(taskId);

		assertNotNull(savedTask, "Task not found");
		assertEquals(task, savedTask, "Tasks not equals");

		task.setName("New name");
		taskManager.updateTask(task);
		assertEquals("New name", task.getName(), "Tasks not update");

		final List<Task> tasks = taskManager.getTaskList();

		assertNotNull(tasks, "Tasks not found");
		assertEquals(1, tasks.size(), "Not correct quantity tasks");
		assertEquals(task, tasks.get(0), "Tasks not equals");
	}

	@Test
	void shouldCreateNewEpic() throws IOException {
		Epic epic = taskManager.createEpic(new Epic("TestEpic", "TestDescription"));
		final int epicId = epic.getId();
		final Epic savedEpic = taskManager.getEpicById(epicId);

		assertNotNull(savedEpic, "Epic not found");
		assertEquals(epic, savedEpic, "Epic not found by ID");

		epic.setName("New name");
		taskManager.updateEpic(epic);
		assertEquals("New name", epic.getName(), "SubTasks not update");

		final List<Epic> epics = taskManager.getEpicList();
		final List<SubTask> subTasksByEpic = taskManager.getSubTasksByEpic(epic.getId());

		assertNotNull(subTasksByEpic, "Epics not found");
		assertNotNull(epics, "Epics not found");
		assertEquals(1, epics.size(), "Not correct quantity epics");
		assertEquals(NEW, epic.getStatus(), "Not correct status for new epic");
		assertEquals(epic, epics.get(0), "Epics not equals");
	}

	@Test
	void shouldCreateNewSubTask() throws IOException {
		Epic epic = taskManager.createEpic(new Epic("TestEpic", "TestDescription"));
		SubTask subTask = taskManager.createSubTask(new SubTask(
				"TestSubTask", NEW, "TestDescription", Instant.now(), 30, epic.getId()));
		final int subTaskId = subTask.getId();
		final SubTask savedSubTask = taskManager.getSubTaskById(subTaskId);

		assertNotNull(savedSubTask, "SubTask not found");
		assertEquals(subTask, savedSubTask, "SubTask not found by ID");

		subTask.setStatus(IN_PROGRESS);
		taskManager.updateSubTask(subTask);
		assertEquals(IN_PROGRESS, subTask.getStatus(), "SubTasks not update");
		assertEquals(IN_PROGRESS, epic.getStatus(), "Epic status not update");

		final List<SubTask> subTasks = taskManager.getSubTaskList();
		Integer epicId = subTask.getEpicId();

		assertNotNull(subTasks, "SubTasks not found");
		assertNotNull(epicId, "Not correct quantity subTasks");
		assertEquals(1, subTasks.size(), "Not correct quantity subTasks");
		assertEquals(subTask, subTasks.get(0), "SubTasks not equals");
	}

	@Test
	void shouldDeleteTasks() throws IOException {
		Task task1 = taskManager.createTask(
				new Task("TestTask 1", NEW, "TestDescription", Instant.now(), 15));
		taskManager.createTask(
				new Task("TestTask 2", NEW,
						"TestDescription", task1.getEndTime().plusMillis(100000), 40));

		taskManager.deleteTask(task1.getId());
		final List<Task> tasks = taskManager.getTaskList();
		assertEquals(1, tasks.size(), "Not correct delete task");

		taskManager.deleteAllTasks();
		final List<Task> task = taskManager.getTaskList();
		assertEquals(0, task.size(), "Not correct delete tasks");
	}

	@Test
	void shouldDeleteEpics() throws IOException {
		Epic epic1 = taskManager.createEpic(new Epic("TestEpic 1", "TestDescription"));
		taskManager.createSubTask(new SubTask(
				"TestSubTask", NEW, "TestDescription", Instant.now(), 30, epic1.getId()));
		Epic epic2 = taskManager.createEpic(new Epic("TestEpic 2", "TestDescription"));

		taskManager.deleteEpic(epic2.getId());
		final List<Epic> epics = taskManager.getEpicList();
		assertEquals(1, epics.size(), "Not correct delete epic");

		taskManager.deleteAllEpics();
		final List<Epic> epic = taskManager.getEpicList();
		final List<SubTask> subTasks = taskManager.getSubTaskList();
		assertEquals(0, epic.size(), "Not correct delete all epics");
		assertEquals(0, subTasks.size(), "Not correct delete all subTasks from epic");
	}

	@Test
	void shouldDeleteSubTasks() throws IOException {
		Epic epic = taskManager.createEpic(new Epic("TestEpic", "TestDescription"));
		SubTask subTask1 = taskManager.createSubTask(new SubTask(
				"TestSubTask 1", NEW, "TestDescription", Instant.now(), 45, epic.getId()));
		taskManager.createSubTask(new SubTask("TestSubTask 1", NEW, "TestDescription",
				subTask1.getEndTime().plusMillis(100000), 30, epic.getId()));

		taskManager.deleteSubTask(subTask1.getId());
		final List<SubTask> subTasks = taskManager.getSubTaskList();
		assertEquals(1, subTasks.size(), "Not correct delete subTask");

		final List<SubTask> subTaskByEpic = taskManager.getSubTasksByEpic(epic.getId());
		assertEquals(1, subTaskByEpic.size(), "Not correct delete subTask");

		taskManager.deleteAllSubTasks();
		final List<SubTask> subTask = taskManager.getSubTaskList();
		assertEquals(0, subTask.size(), "Not correct delete all subTasks");
	}

	@Test
	void shouldChangeEpicStatus() throws IOException {
		Epic epic = taskManager.createEpic(new Epic("TestEpic", "TestDescription"));
		assertEquals(Status.NEW, epic.getStatus(), "Incorrect status for new epic");
		SubTask subTask1 = taskManager.createSubTask(new SubTask("TestSubTask",
				Status.NEW, "TestDescription", Instant.now(), 15, epic.getId()));
		SubTask subTask2 = taskManager.createSubTask(new SubTask(
				"TestSubTask", Status.NEW, "TestDescription",
				subTask1.getEndTime().plusMillis(100000), 25, epic.getId()));
		assertEquals(Status.NEW, epic.getStatus(), "Incorrect status for new epic");

		subTask2.setStatus(Status.DONE);
		taskManager.updateSubTask(subTask2);
		assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Incorrect status for in progress epic");

		subTask1.setStatus(Status.DONE);
		taskManager.updateSubTask(subTask1);
		assertEquals(Status.DONE, epic.getStatus(), "Incorrect status for done epic");

		subTask2.setStatus(Status.IN_PROGRESS);
		taskManager.updateSubTask(subTask2);
		assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Incorrect status for in progress epic");

		subTask1.setStatus(Status.IN_PROGRESS);
		taskManager.updateSubTask(subTask1);
		assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Incorrect status for in progress epic");
	}
}