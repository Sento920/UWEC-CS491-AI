package search;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class XbXPuzzleState implements State {
	
	/**
	 * Takes in comma delimited string. Non number values are turned into nulls 
	 * @param sqrSize the length of one side of the board
	 * @param boardRep the string to be split apart
	 * @return
	 */
	public static XbXPuzzleState createBoard(int sqrSize, String boardRep) {
		List<Integer> intList = new ArrayList<>();
		
		StringTokenizer sTok = new StringTokenizer(boardRep, ",");
		String tok;
		while(sTok.hasMoreTokens()) {
			tok = sTok.nextToken().trim();
			try {
				intList.add(Integer.parseInt(tok));
			} catch(Exception e) {
				intList.add(null);
			}
		}
		return new XbXPuzzleState(sqrSize, intList.toArray(new Integer[intList.size()]));
	}
	
	private int sqrSize, spaceIdx, cIdx, depth;
	private Integer boundValue;
	
	private List<Integer> board;
	
	/**
	 * Stores the possible moves allowed for its children. cIdx keeps track of the next child to generate
	 */
	private List<String> cStates;
	
	private XbXPuzzleState gState;
	// child values
	private XbXPuzzleState parent;
	
	/***
	 * This is the move that was taken from parent to here
	 */
	private String moveToState;
	
	private static final HashMap<String, Point> VALID_MOVES;
	
	static {
		VALID_MOVES = new HashMap<>();
		VALID_MOVES.put("U", new Point(0, -1)); // UP
		VALID_MOVES.put("R", new Point(1, 0)); // RIGHT
		VALID_MOVES.put("D", new Point(0, 1)); // DOWN
		VALID_MOVES.put("L", new Point(-1, 0)); // LEFT
	
	}
	
	public XbXPuzzleState(int sqrSize, Integer... boardEle) {
		this.sqrSize = sqrSize;
		if(boardEle.length != sqrSize*sqrSize)  {
			String errMsg = "The number of board values does not match the dimention of board.\n" +
					"Num values: " + boardEle.length + " Required: " + (sqrSize*sqrSize) + "\n" +
					Arrays.toString(boardEle);
			throw new RuntimeException(errMsg);
		}
		
		// make sure boardEle contains one null value
		int nullCount = 0;
		for(int i=0; i<boardEle.length; i++) {
			if(boardEle[i] == null) {
				nullCount++;
				spaceIdx = i;
			}
		}
		if(nullCount!=1) {
			String errMsg = "Invalid number of spaces found in board!\n" +
					"Found: " + nullCount + "Required: 1" + "\n" +
					Arrays.toString(boardEle);
			throw new RuntimeException(errMsg);
		}
		
		// board is good!
		board = new ArrayList<>();
		for(int i=0; i<boardEle.length; i++) {
			board.add(boardEle[i]);
		}
	}
	
	public XbXPuzzleState(XbXPuzzleState state) {
		sqrSize = state.sqrSize;
		spaceIdx = state.spaceIdx;
		gState = state.gState;
		depth = state.depth;
		
		board = new ArrayList<>(state.board);
	}
	
	public XbXPuzzleState(XbXPuzzleState parent, String moveKey) {
		this(parent);
		
		this.parent = parent;
		moveToState = moveKey;
		
		// update board!
		Point sLoc = getPoint(null);
		
		Point move = VALID_MOVES.get(moveKey);
		
		Point newLoc = new Point(sLoc.x+move.x, sLoc.y+move.y);
		int newIdx = pointToIndex(newLoc);
		
		//System.out.println("Space at: "+sLoc);
		//System.out.println("Now   at: "+newLoc);
		
		// swap values in array
		board.set(spaceIdx, board.get(newIdx));
		board.set(newIdx, null);
		spaceIdx = newIdx;
		depth++;
	}
	
	@Override
	public int compareTo(State s) {
		int b1 = getBound();
		int b2 = s.getBound();
		return b1-b2;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		if (getClass() != o.getClass())
			return false;
		XbXPuzzleState other = (XbXPuzzleState)o;
		if (!board.equals(other.board))
			return false;
		return true;
	}
	
	@Override
	public int getBound() {
		if(boundValue==null) {
			// calc new bound ...
			boundValue = 0;
			Integer v1, v2;
			for(int i=0; i<board.size(); i++) {
				v1 = board.get(i);
				v2 = gState.board.get(i);
				if(v1==null && v2!=null || (v1!=null && !v1.equals(v2))) {
					boundValue++;
				}
			}
		}
		return boundValue;
	}
	
	protected int getIndex(Integer cellvalue) {
		if(cellvalue==null) return spaceIdx;
		int idx = -1, i=0;
		while(idx<0 && i<board.size()) {
			if(board.get(i)==cellvalue) {
				idx = i;
			}
			i++;
		}
		return idx;
	}

	@Override
	public List<State> getPath() {
		List<State> pPath = new LinkedList<>();
		XbXPuzzleState s = this;
		while(s!=null) {
			if(s.moveToState!=null) pPath.add(0, s);
			s = s.parent;
		}
		return pPath;
	}

	public Point getPoint(Integer cellVal) {
		int idx = getIndex(cellVal);
		return new Point(idx % sqrSize, idx / sqrSize);
	}

	public int getX(Integer cellVal) {
		int idx = getIndex(cellVal);
		return idx % sqrSize;
	}

	public int getY(Integer cellVal) {
		int idx = getIndex(cellVal);
		return idx / sqrSize;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + board.hashCode();
		return result;
	}
	
	@Override
	public boolean hasMoreChildren() {
		if(cStates==null) {
			populateChildren();
		}
		return cIdx<cStates.size();
	}
	
	@Override
	public boolean isSolved() {
		return this.equals(gState);
	}
	
	@Override
	public State nextChild() {
		if(hasMoreChildren()) {
			XbXPuzzleState next = new XbXPuzzleState(this, cStates.get(cIdx));
			cIdx++;
			
			return next;
		}
		return null;
	}
	
	protected int pointToIndex(Point p) {
		return p.y*sqrSize+p.x;
	}

	protected void populateChildren() {
		cStates = new ArrayList<>();
		
		Point sPoint = getPoint(null);
		Point tmp, p;
		Map.Entry<String, Point> pair;
		Iterator<Map.Entry<String, Point>> itr = VALID_MOVES.entrySet().iterator();
		
		while(itr.hasNext()) {
			pair = itr.next();
			p = pair.getValue();
			tmp = new Point(sPoint.x+p.x, sPoint.y+p.y);
			if(tmp.x>=0 && tmp.y>=0 && tmp.x<sqrSize && tmp.y<sqrSize) {
				cStates.add(pair.getKey());
			}
		}
		cIdx = 0;
	}

	public void setGoalState(XbXPuzzleState gState) {
		if(sqrSize!=gState.sqrSize) {
			String errMsg = "Goal state must be the same size as current states board.";
			throw new RuntimeException(errMsg);
		}
		
		// check boards have same values ...
		Set<Integer> tmp = new HashSet<Integer>(board);
		tmp.removeAll(gState.board);
		
		Set<Integer> tmp2 = new HashSet<Integer>(gState.board);
		tmp2.removeAll(board);
		
		tmp.addAll(tmp2);
		
		if(!tmp.isEmpty()) {
			throw new RuntimeException("Value mismatch between current state and goal state. "+tmp.toString());
		}
		
		this.gState = gState;
	}
	
	public String toString() {
		return moveToState;
	}
	
	public String toString(boolean verbose) {
		String str;
		if(verbose) {
			str = "Move here: "+moveToState+"\n";
			str = "Depth    : "+depth+"\n";
			//str += board.toString(); // flat board ...
			
			/**/ // 2d board
			for(int y=0; y<sqrSize; y++) {
				for(int x=0; x<sqrSize; x++) {
					int idx = pointToIndex(new Point(x, y));
					Integer val = board.get(idx);
					String tmp = val==null ? "*" : val+"";
					str += tmp+" ";
				}
				str += "\n";
			}
		} else {
			str = moveToState;
		}
		/**/
		return str;
	}

	public boolean isSolvable() {
		// this only works if board is an odd size ...
		if(sqrSize%2!=1) {
			// returning true because we don't is it cannot be solved with this algro.
			return true;
		}
		
		// This algo only works when the space is in bottom right corner.
		// So, lets move the space there for both boards ...
		XbXPuzzleState pcState = moveBlankToCorner();
		XbXPuzzleState pgState = gState.moveBlankToCorner();
		
		// Map goal to an ascending number order ...
		HashMap<Integer, Integer> map = new HashMap<>();
		Integer val;
		for(int i=0; i<pgState.board.size(); i++) {
			val = pgState.board.get(i);
			map.put(val, pgState.board.size()-i);
		}
		
		// Count inversions using the mapping from the goal state
		
		/**/
		int inv = 0;
		for(int i=0; i<pcState.board.size()-1; i++) {
			// get mapped value of current cell
			Integer mVal1 = map.get(pcState.board.get(i));
			
			// Check if a larger number exists after the current
			// place in the list, if so increment inversions.
			for(int j=i+1; j<pcState.board.size(); j++) {
				// get mapped value of cell being checked
				Integer mVal2 = map.get(pcState.board.get(j));
				
				if(mVal1 > mVal2) inv++;
			}
		  
			// Determine if the distance of the blank space from the bottom
			// right is even or odd, and increment inversions if it is odd.
			//if(p[i] == 0 && i % 2 == 1) inv++;
		}
		  
		// If inversions is even, the puzzle is solvable.
		return (inv % 2 == 0);
		/**/
		//return false;
	}
	
	/**
	 * Return a state where the blank space has been moved to the bottom right corner
	 * @return the child state with the blank moved into corner
	 */
	public XbXPuzzleState moveBlankToCorner() {
		XbXPuzzleState s = this;
		
		while(s.spaceIdx < s.board.size()-1) {
			if(s.getX(null)<s.sqrSize-1) {
				s = new XbXPuzzleState(s, "R");
			}
			if(s.getY(null)<s.sqrSize-1) {
				s = new XbXPuzzleState(s, "D");
			}
		}
		
		return s;
	}

	@Override
	public int getDepth() {
		return depth;
	}
}
