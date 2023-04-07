package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

	private final HashMap<Integer, Node> history = new HashMap<>();
	private final ArrayList<Task> historyList = new ArrayList<>();
	private Node first;
	private Node last;
	int size;

	@Override
	public void add(Task task) {
		if (history.containsKey(task.getId())) {
			Node node = history.get(task.getId());
			removeNode(node);
		}
		linkLast(task);
		history.put(task.getId(), last);
		size++;
	}

	@Override
	public void remove(int id) {
		Node node = history.get(id);
		removeNode(node);
	}

	@Override
	public List<Task> getHistory() {
		Node current = first;
		while (current != null) {
			if (historyList.contains(current.task)) {
				historyList.remove(current.task);
			}
			historyList.add(current.task);
			current = current.next;
		}
		return historyList;
	}

	private void removeNode(Node node) {
		if (first == null || node == null) {
			return;
		}
		if (node == first) {
			first = node.next;
		}
		if (node == last) {
			last = node.prev;
		}
		if (node.next != null) {
			node.next.prev = node.prev;
		}
		if (node.prev != null) {
			node.prev.next = node.next;
		}
		history.remove(node.task.getId());
		historyList.remove(node.task);
		size--;
	}

	private void linkLast(Task task) {
		Node node = last;
		Node newNode = new Node(node, task, null);
		last = newNode;
		if (node == null) {
			first = newNode;
		} else {
			node.next = newNode;
		}
	}
}

