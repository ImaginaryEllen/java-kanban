package model;

public class SubTask extends Task {
	final private int epicId;

	public SubTask(String name, Status status, String description, int epicId) {
		super(name, status, description);
		this.epicId = epicId;
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
