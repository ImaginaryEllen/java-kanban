package model;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

	private final List<SubTask> subTaskList = new ArrayList<>(); //список подзадач

	public Epic(String name, String description) {
		super(name, Status.NEW, description);
	}

	public List<SubTask> getSubTaskList() {
		return subTaskList;
	}

    public void addToSubTaskList(SubTask subTask){
		subTaskList.add(subTask);
	}

	public void deleteSubTaskByEpic(SubTask subTask) {
		subTaskList.remove(subTask);
	}

	public void  deleteAllSubTask() {
		subTaskList.clear();
	}

	@Override
	public String toString() {
		return "Epic{" +
				"" + subTaskList +
				", id=" + getId() +
				", " + getName() + '\'' +
				", " + getStatus() + '\'' +
				", " + getDescription() + '\'' +
				'}';
	}
}
