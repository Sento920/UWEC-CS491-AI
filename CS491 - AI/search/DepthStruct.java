package search;

import java.util.Deque;
import java.util.LinkedList;

public class DepthStruct implements BTStruct<State> {

	private Deque<State> queue;
	
	public DepthStruct() {
		queue = new LinkedList<State>();
	}
	
	@Override
	public void add(State value) {
		queue.push(value);
	}

	@Override
	public State getNext() {
		return queue.pop();
	}

	@Override
	public void clear() {
		//queue.clear();
	}

	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public int size() {
		return queue.size();
	}
}
