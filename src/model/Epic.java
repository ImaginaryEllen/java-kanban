package model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
	private int duration;
	private Instant startTime;
	private Instant endTime;
	private final List<SubTask> subTaskList = new ArrayList<>();

	public Epic(String name, String description) {
		super(name, Status.NEW, description);
		this.startTime = getStartTime();
		this.duration = getDuration();
		this.endTime = getEndTime();
	}

	@Override
	public int getDuration() {
		int sum = 0;
		for (SubTask subTask : subTaskList) {
			sum += subTask.getDuration();
		}
		duration = sum;
		return duration;
	}

	@Override
	public Instant getStartTime() {
		for (SubTask subTask : subTaskList) {
			if (subTaskList.size() == 1) {
				startTime = subTask.getStartTime();
				break;
			}
			if (subTask.getStartTime().isBefore(startTime)) {
				startTime = subTask.getStartTime();
			}
		}
		return startTime;
	}

	@Override
	public Instant getEndTime() {
		for (SubTask subTask : subTaskList) {
			if (subTaskList.size() == 1) {
				endTime = subTask.getEndTime();
				break;
			}
			if (subTask.getEndTime().isAfter(endTime)) {
				endTime = subTask.getEndTime();
			}
		}

		return endTime;
	}

	@Override
	public TaskType getType() {
		return TaskType.EPIC;
	}

	public List<SubTask> getSubTaskList() {
		return subTaskList;
	}

	public void addToSubTaskList(SubTask subTask) {
		subTaskList.add(subTask);
	}

	public void deleteSubTaskByEpic(SubTask subTask) {
		subTaskList.remove(subTask);
	}

	public void deleteAllSubTask() {
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
