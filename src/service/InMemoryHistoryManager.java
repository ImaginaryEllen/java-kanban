package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

	private final HashMap<Integer, Node> history = new HashMap<>();
	private Node first;
	private Node last;
	private int size;

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
		final ArrayList<Task> historyList = new ArrayList<>();
		Node current = first;
		while (current != null) {
			historyList.add(current.getTask());
			current = current.getNext();
		}
		return historyList;
	}

	private void removeNode(Node node) {
		if (first == null || node == null) {
			return;
		}
		if (node.getPrev() == null) {
			first = node.getNext();
			first.setPrev(null);
		}
		if (node.getNext() == null) {
			last = node.getPrev();
			last.setNext(null);
		}
		if (node.getNext() != null) {
			node.getNext().setPrev(node.getPrev());
		}
		if (node.getPrev() != null) {
			node.getPrev().setNext(node.getNext());
		}
		history.remove(node.getTask().getId());
		size--;
	}

	private void linkLast(Task task) {
		Node oldLastNode = last;
		Node newNode = new Node(oldLastNode, task, null);
		last = newNode;
		if (oldLastNode == null) {
			first = newNode;
		} else {
			oldLastNode.setNext(newNode);
		}
	}
}

