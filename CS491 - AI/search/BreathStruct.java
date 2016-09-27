package search;

import java.util.Deque;
import java.util.LinkedList;

public class BreathStruct implements BTStruct<State> {

	private Deque<State> queue;
	
	public BreathStruct() {
		queue = new LinkedList<State>();
	}
	
	@Override
	public void add(State value) {
		queue.offerLast(value);
	}

	@Override
	public State getNext() {
		return queue.pollFirst();
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
