package search;

import java.util.HashSet;

public class Backtracker {
	
	// data structure that contains the btStates
	private BTStruct<State> queue;
	private boolean verbose;
	private int maxDepth;
	
	public Backtracker(BTStruct<State> btStruct, int maxDepth, boolean verbose) {
		queue = btStruct;
		this.maxDepth = maxDepth;
		this.verbose = verbose;
	}
	
	public State backtracker(State s, boolean ignoreRepeats) {
		int numSExp = 0;
		int maxStored = 0; 
 
        State bestSol = null;
 
        queue.add(s);
        
        HashSet<State> usedStates = new HashSet<>();
 
        State currState = null;
        while(!queue.isEmpty() && (currState==null || !currState.isSolved())) {
        	currState = queue.getNext();
        	if(currState.getDepth()<maxDepth && !usedStates.contains(currState)) {
        		if(ignoreRepeats) usedStates.add(currState);
        		
	            numSExp++;
	               
	            println(currState.toString(verbose));
	                       
	            // Check to see if the state is solved
	            if(currState.isSolved()) {
	            	// checking for best, might only need to solve ...
	            	// TODO - check with prof if best needed.
	            	if (bestSol == null || currState.getBound() < bestSol.getBound()) {
	            		bestSol = currState;
	            	}
	            } else { // not solved, continue
	            	if (bestSol == null || (queue instanceof BestStruct && (currState.getBound() < bestSol.getBound()))) {
	            		// Expand the state by producing all the kids!
	            		while(currState.hasMoreChildren()) {
	            			State child = currState.nextChild();
	            			queue.add(child);
	            		}
	            	} else {
	            		// For Best-first only
	            		queue.clear();            		
	            	}
	            }
	            if(queue.size()>maxStored) maxStored = queue.size();
	            if(numSExp%1000000==0) System.out.println("Expanded States: " + numSExp);
        	}
        }
 
        System.out.println();
        System.out.println("Max Stored     : " + maxStored);
        System.out.println("Expanded States: " + numSExp);
        
        return bestSol;
	}   
	
	private void println(String str) {
		if(verbose) {
			System.out.println(str);
		}
	}
}
