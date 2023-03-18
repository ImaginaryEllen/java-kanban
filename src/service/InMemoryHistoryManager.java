package service;

import model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

	private final LinkedList<Task> history = new LinkedList<>();

	@Override
	public void add(Task task) {
		if (history.size() > 9) {
			history.removeFirst();
		}
		history.add(task);
	}

	@Override
	public List<Task> getHistory() {
		return history;
	}
}
