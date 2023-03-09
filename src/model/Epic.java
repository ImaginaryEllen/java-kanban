package model;
import java.util.ArrayList;

public class Epic extends Task {

	public ArrayList<SubTask> subTaskList = new ArrayList<>(); //список подзадач

	public Epic(int id, String name, String status, String description) {
		super(id, name, status, description);
	}

	public ArrayList<SubTask> getSubTaskList() {
		return subTaskList;
	}

    public void addToSubTaskList(SubTask subTask){
		subTaskList.add(subTask);
	}

	public ArrayList<SubTask> getSubTasks() {
		return subTaskList;
	}

	public void setSubTasks(ArrayList<SubTask> subTaskList) {
		this.subTaskList = subTaskList;
	}

	@Override
	public String toString() {
		return "Epic{" +
				"subTaskList=" + subTaskList +
				", id=" + id +
				", name='" + name + '\'' +
				", status='" + status + '\'' +
				", description='" + description + '\'' +
				'}';
	}
}
