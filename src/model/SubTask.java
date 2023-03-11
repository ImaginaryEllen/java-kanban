package model;

public class SubTask extends Task {
	private int epicId;

	public SubTask(String name, String status, String description, int epicId) {
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
				", id=" + id +
				", name='" + name + '\'' +
				", status='" + status + '\'' +
				", description='" + description + '\'' +
				'}';
	}
}
