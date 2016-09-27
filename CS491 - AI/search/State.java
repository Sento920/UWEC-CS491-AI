package search;

import java.util.List;

public interface State extends Comparable<State> {
	public int getDepth();
	
	public boolean hasMoreChildren();
	
	public State nextChild(); // returns only possible children ...
	
	public boolean isSolved();
	
	public int getBound(); // value of the board
	
	public List<State> getPath();
	
	public String toString(boolean verbose);
}
