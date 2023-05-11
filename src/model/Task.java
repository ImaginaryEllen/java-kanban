package model;

import java.time.Instant;

public class Task {
	private int id;
	private String name;
	private Status status;
	private String description;
	private int duration;
	private Instant startTime;
	private Instant endTime;

	//конструктор для старых задач
	public Task(String name, Status status, String description) {
		this.name = name;
		this.status = status;
		this.description = description;
		this.startTime = Instant.now();
		this.duration = 0;
	}

	//конструктор для новых задач
	public Task(String name, Status status, String description, Instant startTime, int duration) {
		this.name = name;
		this.status = status;
		this.description = description;
		this.startTime = startTime;
		this.duration = duration;
	}

	//конструктор для файла
	public Task(int id, String name, Status status, String description, Instant startTime, int duration) {
		this.id = id;
		this.name = name;
		this.status = status;
		this.description = description;
		this.startTime = startTime;
		this.duration = duration;
	}

	public TaskType getType() {
		return TaskType.TASK;
	}

	public int getDuration() {
		return duration;
	}

	public void setEndTime(Instant endTime) {
		this.endTime = endTime;
	}

	public Instant getStartTime() {
		return startTime;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public void setStartTime(Instant startTime) {
		this.startTime = startTime;
	}

	public Instant getEndTime() {
		int durationMilli = duration * 60000;
		endTime = startTime.plusMillis(durationMilli);
		return endTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Task{" +
				"id=" + id +
				", " + name + '\'' +
				", " + status + '\'' +
				", " + description + '\'' +
				'}';
	}
}
