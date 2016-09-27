package search;

public interface BTStruct<T> {
	public void add(T value);
	public T getNext();
	public void clear();
	public boolean isEmpty();
	public int size();
}