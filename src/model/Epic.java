package model;
import java.util.ArrayList;

public class Epic extends Task {

	private ArrayList<SubTask> subTaskList = new ArrayList<>(); //список подзадач

	public Epic(String name,String status, String description) {
		super(name, "NEW" ,description);
	}

	public ArrayList<SubTask> getSubTaskList() {
		return subTaskList;
	}

    public void addToSubTaskList(SubTask subTask){
		subTaskList.add(subTask);
	}

	public void deleteSubTaskByEpic(SubTask subTask) {
		subTaskList.remove(subTask);
	}

	//методы удаление и поиск мб сюда добавить
	//создай в классе эпика публичный метод, который будет удалять из списка сабтасков эпика соответствующую сабтаску,

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
