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
	}

	@Override
	public int getDuration() {
		return duration;
	}

	@Override
	public Instant getStartTime() {
		return startTime;
	}

	@Override
	public Instant getEndTime() {
		return endTime;
	}

	public void getEpicTime() {
		int sum = 0;
		startTime = null;
		endTime = null;
		for (SubTask subTask : subTaskList) {
			if (subTask.getStartTime() != null && subTask.getDuration() != 0) {
				sum += subTask.getDuration();
				if (startTime == null && endTime == null) {
					endTime = subTask.getEndTime();
					startTime = subTask.getStartTime();
				}
				if (subTaskList.size() == 1) {
					break;
				}
				if (subTask.getEndTime().isAfter(endTime)) {
					endTime = subTask.getEndTime();
				}
				if (subTask.getStartTime().isBefore(startTime)) {
					startTime = subTask.getStartTime();
				}
			}
		}
		duration = sum;
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
