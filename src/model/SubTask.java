package model;

import java.time.Instant;

public class SubTask extends Task {
	final private int epicId;

	public SubTask(String name, Status status, String description, Instant startTime, int duration, int epicId) {
		super(name, status, description, startTime, duration);
		this.epicId = epicId;
	}

	public SubTask(int id, String name, Status status, String description, Instant startTime, int duration, int epicId) {
		super(id, name, status, description, startTime, duration);
		this.epicId = epicId;
	}

	@Override
	public TaskType getType() {
		return TaskType.SUBTASK;
	}

	public int getEpicId() {
		return epicId;
	}

	@Override
	public String toString() {
		return "SubTask{" +
				"epicId=" + epicId +
				", id=" + getId() +
				", " + getName() + '\'' +
				", " + getStatus() + '\'' +
				", " + getDescription() + '\'' +
				'}';
	}
}
