package model;

public class SubTask extends Task {
	public Epic epic; //ссылка на подзадачи из Epic

	public int epicId;

	public SubTask(int id, String name, String status, String description, int epicId) {
		super(id, name, status, description);
		this.epicId = epicId;
	}

	public Epic getEpic() {
		return epic;
	}

	public void setEpic(Epic epic) {
		this.epic = epic;
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
