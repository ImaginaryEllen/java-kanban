package controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;
import service.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
	KVServer kvServer;
	HttpTaskServer taskServer;
	Gson gson = Managers.getGson();

	@BeforeEach
	void beforeEach() throws IOException, InterruptedException {
		kvServer = new KVServer();
		kvServer.start();
		taskServer = new HttpTaskServer();
		taskServer.start();
	}

	@Test
	void shouldCreateTask() throws IOException, InterruptedException {
		Task task = new Task("TestTask", Status.NEW, "des", Instant.now(), 5);
		URI url = URI.create("http://localhost:8080/tasks/task/");
		String json = gson.toJson(task);
		final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
		HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
		HttpResponse<String> response = taskServer.httpTaskManager.getClient().getHttpClient().send(
				request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode(), "Not correct code");
		assertEquals("Task is create", response.body(), "Task is not create");
		assertEquals(1, taskServer.httpTaskManager.getTasks().size(), "Not correct size");
	}

	@Test
	void shouldCreateEpic() throws IOException, InterruptedException {
		Epic epic = new Epic("TestEpic", "des");
		URI url = URI.create("http://localhost:8080/tasks/epic/");
		String json = gson.toJson(epic);
		final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
		HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
		HttpResponse<String> response = taskServer.httpTaskManager.getClient().getHttpClient().send(
				request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode(), "Not correct code");
		assertEquals("Epic is create", response.body(), "Epic is not create");
		assertEquals(1, taskServer.httpTaskManager.getEpics().size(), "Not correct size");
	}

	@Test
	void shouldCreateSubTask() throws IOException, InterruptedException {
		Epic epic = taskServer.httpTaskManager.createEpic(new Epic("TestEpic", "des"));
		SubTask subTask = new SubTask(
				"TestSubTask", Status.NEW, "des", Instant.now(), 10, epic.getId());
		URI url = URI.create("http://localhost:8080/tasks/subTask/");
		String json = gson.toJson(subTask);
		final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
		HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
		HttpResponse<String> response = taskServer.httpTaskManager.getClient().getHttpClient().send(
				request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode(), "Not correct code");
		assertEquals("SubTask is create", response.body(), "SubTask is not create");
	}

	@Test
	void shouldUpdateTask() throws IOException, InterruptedException {
		Task task = taskServer.httpTaskManager.createTask(
				new Task("TestTask", Status.NEW, "des", Instant.now(), 5));
		URI url = URI.create("http://localhost:8080/tasks/task/");
		task.setName("NewTask");
		String json = gson.toJson(task);
		final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
		HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
		HttpResponse<String> response = taskServer.httpTaskManager.getClient().getHttpClient().send(
				request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode(), "Not correct code");
		assertEquals("Task is update", response.body(), "Task is not update");
		assertEquals("NewTask", taskServer.httpTaskManager.getTasks().get(task.getId()).getName(),
				"Not correct update Task");
	}

	@Test
	void shouldUpdateEpic() throws IOException, InterruptedException {
		Epic epic = taskServer.httpTaskManager.createEpic(new Epic("TestEpic", "des"));
		URI url = URI.create("http://localhost:8080/tasks/epic/");
		epic.setName("NewEpic");
		String json = gson.toJson(epic);
		final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
		HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
		HttpResponse<String> response = taskServer.httpTaskManager.getClient().getHttpClient().send(
				request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode(), "Not correct code");
		assertEquals("Epic is update", response.body(), "Epic is not update");
		assertEquals("NewEpic", taskServer.httpTaskManager.getEpics().get(epic.getId()).getName(),
				"Not correct update Epic");
	}

	@Test
	void shouldUpdateSubTask() throws IOException, InterruptedException {
		Epic epic = taskServer.httpTaskManager.createEpic(new Epic("TestEpic", "des"));
		SubTask subTask = taskServer.httpTaskManager.createSubTask(new SubTask(
				"TestSubTask", Status.NEW, "des", Instant.now(), 10, epic.getId()));
		URI url = URI.create("http://localhost:8080/tasks/subTask/");
		subTask.setName("NewSubTask");
		String json = gson.toJson(subTask);
		final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
		HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
		HttpResponse<String> response = taskServer.httpTaskManager.getClient().getHttpClient().send(
				request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode(), "Not correct code");
		assertEquals("SubTask is update", response.body(), "SubTask is not update");
		assertEquals("NewSubTask", taskServer.httpTaskManager.getSubTasks().get(subTask.getId()).getName(),
				"Not correct update SubTask");
	}

	@Test
	void shouldReturnTaskList() throws IOException, InterruptedException {
		Task task = new Task("TestTask", Status.NEW, "des", Instant.now(), 5);
		URI url = URI.create("http://localhost:8080/tasks/task/");
		taskServer.httpTaskManager.createTask(task);
		String json = gson.toJson(taskServer.httpTaskManager.getTaskList());
		HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
		HttpResponse<String> response = taskServer.httpTaskManager.getClient().getHttpClient().send(
				request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode(), "Not correct code");
		assertEquals(json, response.body(), "Not correct return all Tasks list");
		final List<Task> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
		}.getType());
		assertNotNull(tasks);
		Task newTask = tasks.get(0);
		assertNotNull(newTask);
		assertEquals(task.getId(), newTask.getId(), "Task-ID not equals");
		assertEquals(task.getName(), newTask.getName(), "Task-Name not equals");
		assertEquals(task.getDescription(), newTask.getDescription(), "Task-Description not equals");
		assertEquals(task.getStartTime(), newTask.getStartTime(), "Task-StartTime not equals");
		assertEquals(task.getStatus(), newTask.getStatus(), "Task-Status not equals");
		assertEquals(task.getDuration(), newTask.getDuration(), "Task-Duration not equals");
	}

	@Test
	void shouldReturnEpicList() throws IOException, InterruptedException {
		Epic epic = new Epic("TestEpic", "des");
		URI url = URI.create("http://localhost:8080/tasks/epic/");
		taskServer.httpTaskManager.createEpic(epic);
		String json = gson.toJson(taskServer.httpTaskManager.getEpicList());
		HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
		HttpResponse<String> response = taskServer.httpTaskManager.getClient().getHttpClient().send(
				request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode(), "Not correct code");
		assertEquals(json, response.body(), "Not correct return all Epics list");
		final List<Epic> epics = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>() {
		}.getType());
		assertNotNull(epics);
		Epic newEpic = epics.get(0);
		assertNotNull(newEpic);
		assertEquals(epic.getId(), newEpic.getId(), "Epic-ID not equals");
		assertEquals(epic.getName(), newEpic.getName(), "Epic-Name not equals");
		assertEquals(epic.getDescription(), newEpic.getDescription(), "Epic-Description not equals");
		assertEquals(epic.getStartTime(), newEpic.getStartTime(), "Epic-StartTime not equals");
		assertEquals(epic.getStatus(), newEpic.getStatus(), "Epic-Status not equals");
		assertEquals(epic.getDuration(), newEpic.getDuration(), "Epic-Duration not equals");
	}

	@Test
	void shouldReturnSubTaskList() throws IOException, InterruptedException {
		Epic epic = taskServer.httpTaskManager.createEpic(new Epic("TestEpic", "des"));
		SubTask subTask = taskServer.httpTaskManager.createSubTask(new SubTask(
				"TestSubTask", Status.NEW, "des", Instant.now(), 10, epic.getId()));
		URI url = URI.create("http://localhost:8080/tasks/subTask/");
		String json = gson.toJson(taskServer.httpTaskManager.getSubTaskList());
		HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
		HttpResponse<String> response = taskServer.httpTaskManager.getClient().getHttpClient().send(
				request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode(), "Not correct code");
		assertEquals(json, response.body(), "Not correct return all SubTasks list");
		final List<SubTask> subTasks = gson.fromJson(response.body(), new TypeToken<ArrayList<SubTask>>() {
		}.getType());
		assertNotNull(subTasks);
		SubTask newSubTask = subTasks.get(0);
		assertNotNull(newSubTask);
		assertEquals(subTask.getId(), newSubTask.getId(), "SubTask-ID not equals");
		assertEquals(subTask.getName(), newSubTask.getName(), "SubTask-Name not equals");
		assertEquals(subTask.getDescription(), newSubTask.getDescription(), "SubTask-Description not equals");
		assertEquals(subTask.getStartTime(), newSubTask.getStartTime(), "SubTask-StartTime not equals");
		assertEquals(subTask.getStatus(), newSubTask.getStatus(), "SubTask-Status not equals");
		assertEquals(subTask.getDuration(), newSubTask.getDuration(), "SubTask-Duration not equals");
		assertEquals(subTask.getEpicId(), newSubTask.getEpicId(), "SubTask-EpicId not equals");
	}

	@Test
	void shouldDeleteTasks() throws IOException, InterruptedException {
		taskServer.httpTaskManager.createTask(new Task(
				"Task 1", Status.IN_PROGRESS, "des", Instant.now(), 5));
		taskServer.httpTaskManager.createTask(new Task(
				"Task 2", Status.NEW, "des", Instant.now().plusSeconds(500000), 10));
		URI url = URI.create("http://localhost:8080/tasks/task/");
		HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
		HttpResponse<String> response = taskServer.httpTaskManager.getClient().getHttpClient().send(
				request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode(), "Not correct code");
		assertEquals("All Tasks deleted", response.body(), "Tasks is not deleted");
		assertEquals(0, taskServer.httpTaskManager.getTasks().size(), "Not correct size");
	}

	@Test
	void shouldDeleteEpics() throws IOException, InterruptedException {
		Epic epic = taskServer.httpTaskManager.createEpic(new Epic("TestEpic", "des"));
		taskServer.httpTaskManager.createSubTask(new SubTask(
				"TestSubTask", Status.NEW, "des", Instant.now(), 10, epic.getId()));
		URI url = URI.create("http://localhost:8080/tasks/epic/");
		HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
		HttpResponse<String> response = taskServer.httpTaskManager.getClient().getHttpClient().send(
				request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode(), "Not correct code");
		assertEquals("All Epics deleted", response.body(), "Epics is not deleted");
		assertEquals(0, taskServer.httpTaskManager.getEpics().size(), "Not correct Epic size");
		assertEquals(0, taskServer.httpTaskManager.getSubTasks().size(), "Not correct SubTask size");
	}

	@Test
	void shouldDeleteSubTasks() throws IOException, InterruptedException {
		Epic epic = taskServer.httpTaskManager.createEpic(new Epic("TestEpic", "des"));
		taskServer.httpTaskManager.createSubTask(new SubTask(
				"TestSubTask", Status.NEW, "des", Instant.now(), 10, epic.getId()));
		URI url = URI.create("http://localhost:8080/tasks/subTask/");
		HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
		HttpResponse<String> response = taskServer.httpTaskManager.getClient().getHttpClient().send(
				request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode(), "Not correct code");
		assertEquals("All SubTasks deleted", response.body(), "SubTasks is not deleted");
		assertEquals(1, taskServer.httpTaskManager.getEpics().size(), "Not correct Epic size");
		assertEquals(0, taskServer.httpTaskManager.getSubTasks().size(), "Not correct SubTask size");
	}

	@Test
	void shouldReturnTaskById() throws IOException, InterruptedException {
		Task task = taskServer.httpTaskManager.createTask(new Task(
				"TestTask", Status.NEW, "des", Instant.now(), 5));
		String json = gson.toJson(task);
		URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
		HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
		HttpResponse<String> response =
				taskServer.httpTaskManager.getClient().getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode(), "Not correct code");
		assertEquals(json, response.body(), "Not correct return Task");
		Task newTask = gson.fromJson(response.body(), Task.class);
		assertNotNull(newTask);
		assertEquals(task.getId(), newTask.getId(), "Task-ID not equals");
		assertEquals(task.getName(), newTask.getName(), "Task-Name not equals");
		assertEquals(task.getDescription(), newTask.getDescription(), "Task-Description not equals");
		assertEquals(task.getStartTime(), newTask.getStartTime(), "Task-StartTime not equals");
		assertEquals(task.getStatus(), newTask.getStatus(), "Task-Status not equals");
		assertEquals(task.getDuration(), newTask.getDuration(), "Task-Duration not equals");
	}

	@Test
	void shouldReturnEpicById() throws IOException, InterruptedException {
		Epic epic = taskServer.httpTaskManager.createEpic(new Epic("TestEpic", "des"));
		String json = gson.toJson(epic);
		URI url = URI.create("http://localhost:8080/tasks/epic/?id=1");
		HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
		HttpResponse<String> response =
				taskServer.httpTaskManager.getClient().getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode(), "Not correct code");
		assertEquals(json, response.body(), "Not correct return Epic");
		Epic newEpic = gson.fromJson(response.body(), Epic.class);
		assertNotNull(newEpic);
		assertEquals(epic.getId(), newEpic.getId(), "Epic-ID not equals");
		assertEquals(epic.getName(), newEpic.getName(), "Epic-Name not equals");
		assertEquals(epic.getDescription(), newEpic.getDescription(), "Epic-Description not equals");
		assertEquals(epic.getStartTime(), newEpic.getStartTime(), "Epic-StartTime not equals");
		assertEquals(epic.getStatus(), newEpic.getStatus(), "Epic-Status not equals");
		assertEquals(epic.getDuration(), newEpic.getDuration(), "Epic-Duration not equals");
	}

	@Test
	void shouldReturnSubTaskById() throws IOException, InterruptedException {
		Epic epic = taskServer.httpTaskManager.createEpic(new Epic("TestEpic", "des"));
		SubTask subTask = taskServer.httpTaskManager.createSubTask(new SubTask(
				"TestSubTask", Status.NEW, "des", Instant.now(), 10, epic.getId()));
		String json = gson.toJson(subTask);
		URI url = URI.create("http://localhost:8080/tasks/subTask/?id=2");
		HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
		HttpResponse<String> response =
				taskServer.httpTaskManager.getClient().getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode(), "Not correct code");
		assertEquals(json, response.body(), "Not correct return SubTask");
		SubTask newSubTask = gson.fromJson(response.body(), SubTask.class);
		assertNotNull(newSubTask);
		assertEquals(subTask.getId(), newSubTask.getId(), "SubTask-ID not equals");
		assertEquals(subTask.getName(), newSubTask.getName(), "SubTask-Name not equals");
		assertEquals(subTask.getDescription(), newSubTask.getDescription(), "SubTask-Description not equals");
		assertEquals(subTask.getStartTime(), newSubTask.getStartTime(), "SubTask-StartTime not equals");
		assertEquals(subTask.getStatus(), newSubTask.getStatus(), "SubTask-Status not equals");
		assertEquals(subTask.getDuration(), newSubTask.getDuration(), "SubTask-Duration not equals");
		assertEquals(subTask.getEpicId(), newSubTask.getEpicId(), "SubTask-EpicId not equals");
	}

	@Test
	void shouldDeleteTaskById() throws IOException, InterruptedException {
		Task task = taskServer.httpTaskManager.createTask(new Task(
				"TestTask", Status.NEW, "des", Instant.now(), 5));
		URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
		HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).DELETE().build();
		HttpResponse<String> httpResponse =
				taskServer.httpTaskManager.getClient().getHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, httpResponse.statusCode(), "Not correct code");
		assertEquals("Task by id:1 is delete", httpResponse.body(), "Not correct delete Task by id");
		assertFalse(taskServer.httpTaskManager.getTasks().containsKey(task.getId()), "Not correct delete");
	}

	@Test
	void shouldDeleteEpicById() throws IOException, InterruptedException {
		Epic epic = taskServer.httpTaskManager.createEpic(new Epic("TestEpic", "des"));
		URI url = URI.create("http://localhost:8080/tasks/epic/?id=1");
		HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).DELETE().build();
		HttpResponse<String> httpResponse =
				taskServer.httpTaskManager.getClient().getHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, httpResponse.statusCode(), "Not correct code");
		assertEquals("Epic by id:1 is delete", httpResponse.body(), "Not correct delete Epic by id");
		assertFalse(taskServer.httpTaskManager.getEpics().containsKey(epic.getId()), "Not correct delete");
	}

	@Test
	void shouldDeleteSubTaskById() throws IOException, InterruptedException {
		Epic epic = taskServer.httpTaskManager.createEpic(new Epic("TestEpic", "des"));
		SubTask subTask = taskServer.httpTaskManager.createSubTask(new SubTask(
				"TestSubTask", Status.NEW, "des", Instant.now(), 10, epic.getId()));
		URI url = URI.create("http://localhost:8080/tasks/subTask/?id=2");
		HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).DELETE().build();
		HttpResponse<String> httpResponse =
				taskServer.httpTaskManager.getClient().getHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, httpResponse.statusCode(), "Not correct code");
		assertEquals("SubTask by id:2 is delete", httpResponse.body(), "Not correct delete SubTask by id");
		assertFalse(taskServer.httpTaskManager.getSubTasks().containsKey(subTask.getId()), "Not correct delete");
	}

	@Test
	void shouldReturnSubTasksByEpic() throws IOException, InterruptedException {
		Epic epic = taskServer.httpTaskManager.createEpic(new Epic("TestEpic", "des"));
		taskServer.httpTaskManager.createSubTask(new SubTask(
				"TestSubTask", Status.NEW, "des", Instant.now(), 10, epic.getId()));
		URI url = URI.create("http://localhost:8080/tasks/subTask/epic/?id=1");
		HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
		HttpResponse<String> httpResponse =
				taskServer.httpTaskManager.getClient().getHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
		String json = gson.toJson(taskServer.httpTaskManager.getSubTasksByEpic(epic.getId()));
		assertEquals(200, httpResponse.statusCode(), "Not correct code");
		assertEquals(json, httpResponse.body(), "Not correct SubTask list by Epic id");
	}

	@Test
	void shouldReturnHistory() throws IOException, InterruptedException {
		Task task = taskServer.httpTaskManager.createTask(new Task(
				"TestTask", Status.NEW, "des", Instant.now(), 5));
		taskServer.httpTaskManager.getTaskById(task.getId());
		String json = gson.toJson(taskServer.httpTaskManager.getHistory());
		URI url = URI.create("http://localhost:8080/tasks/history/");
		HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
		HttpResponse<String> response =
				taskServer.httpTaskManager.getClient().getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode(), "Not correct code");
		assertEquals(json, response.body(), "Not correct return History");

		final List<Task> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
		}.getType());
		assertNotNull(tasks);
		Task newTask = tasks.get(0);
		assertNotNull(newTask);
		assertEquals(task.getId(), newTask.getId(), "Task-ID not equals");
		assertEquals(task.getName(), newTask.getName(), "Task-Name not equals");
		assertEquals(task.getDescription(), newTask.getDescription(), "Task-Description not equals");
		assertEquals(task.getStartTime(), newTask.getStartTime(), "Task-StartTime not equals");
		assertEquals(task.getStatus(), newTask.getStatus(), "Task-Status not equals");
		assertEquals(task.getDuration(), newTask.getDuration(), "Task-Duration not equals");
	}

	@Test
	void shouldReturnPrioritizedTasks() throws IOException, InterruptedException {
		taskServer.httpTaskManager.createTask(new Task(
				"Task 1", Status.IN_PROGRESS, "des", Instant.now(), 5));
		taskServer.httpTaskManager.createTask(new Task(
				"Task 2", Status.NEW, "des", Instant.now().plusSeconds(500000), 10));
		String json = gson.toJson(taskServer.httpTaskManager.getPrioritizedTasks());
		URI url = URI.create("http://localhost:8080/tasks/");
		HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
		HttpResponse<String> response =
				taskServer.httpTaskManager.getClient().getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode(), "Not correct code");
		assertEquals(json, response.body(), "Not correct return Prioritized Tasks");

		final List<Task> tasks = gson.fromJson(json, new TypeToken<ArrayList<Task>>() {
		}.getType());
		assertNotNull(tasks);
		Task newTask1 = tasks.get(0);
		Task newTask2 = tasks.get(1);
		boolean task1BeforeTask2 = newTask1.getEndTime().isBefore(newTask2.getStartTime());
		assertTrue(task1BeforeTask2, "Not correct Prioritized List");

		final List<Task> tasksReturn = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
		}.getType());
		assertNotNull(tasksReturn);
		Task returnTask1 = tasksReturn.get(0);
		Task returnTask2 = tasksReturn.get(1);
		boolean newTask1BeforeNewTask2 = returnTask1.getEndTime().isBefore(returnTask2.getStartTime());
		assertTrue(newTask1BeforeNewTask2, "Not correct return Prioritized List");
	}

	@AfterEach
	void stop() {
		kvServer.stop();
		taskServer.stop();
	}

}