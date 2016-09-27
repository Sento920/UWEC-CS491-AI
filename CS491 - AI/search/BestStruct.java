package search;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class BestStruct implements BTStruct<State> {

	private Queue<State> queue;
	
	public BestStruct() {
		queue = new PriorityQueue<State>();
	}
	
	public BestStruct(Comparator<State> comparator) {
		queue = new PriorityQueue<State>(100, comparator);
	}
	
	@Override
	public void add(State value) {
		queue.offer(value);
	}

	@Override
	public State getNext() {
		return queue.poll();
	}
	
	@Override
	public void clear() {
		queue.clear();
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
