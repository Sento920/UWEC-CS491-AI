package search;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class XbXPuzzleSolver {

	public static void main(String[] args) {
		
		
		boolean verbose = false;
		for(String arg: args) {
			if(arg.toUpperCase().equals("-V")) {
				System.out.println("- Verbose Mode -");
				verbose = true;
			}
		}
		
		boolean ignRep = true;
		for(String arg: args) {
			if(arg.toUpperCase().equals("-AREP")) {
				System.out.println("- Allowing Repeats -");
				ignRep = false;
			}
		}
		
		int mDepth = 30;
		for(String arg: args) {
			if(Pattern.matches("-[xX][dD]=[0-9]+", arg)) {
				StringTokenizer sTok = new StringTokenizer(arg, "=");
				sTok.nextToken();
				mDepth = Integer.parseInt(sTok.nextToken());
				System.out.println("- Max depth set to ["+mDepth+"] -");
				ignRep = false;
			}
		}
		
		int searchType = -1;
		
		for(String arg: args) {
			if(arg.toUpperCase().equals("-BREADTH")) {
				searchType = 0;
			} else if(arg.toUpperCase().equals("-DEPTH")) {
				searchType = 1;
			} else if(arg.toUpperCase().equals("-BEST")) {
				searchType = 2;
			}
		}
		
		if(searchType<0) {
			System.out.println("Please run program with one of the follwing arguments:");
			System.out.println("\t-breadth : for breadth-first search");
			System.out.println("\t-depth   : for depth-first search");
			System.out.println("\t-best    : for best-first search");
			System.exit(0);
		}
		
		
		System.out.println();
		System.out.println("Please enter states as comma delimited strings. Use a '*' for the blank.");
		System.out.println();
		System.out.println("Enter starting state...");
		String start = null;
		try {
			start = System.console().readLine();
		} catch(Exception e){}
		System.out.println();
		System.out.println();
		System.out.println("Enter goal state...");
		String goal = null;
		try {
			goal = System.console().readLine();
		} catch(Exception e){}
		System.out.println();
		System.out.println();
		
		/*
		// bound 0
		//start = "1, 2, 3, 4, 5, 6, 7, 8, *";
		//goal = "1, 2, 3, 4, 5, 6, 7, 8, *";
		*/
		
		/*
		// bound 2
		//start = "1, 2, 3, 4, 5, 6, 7, *, 8";
		//goal = "1, 2, 3, 4, 5, 6, 7, 8, *";
		/**/
		
		/*
		//
		//start = "1, 2, 3, 4, 5, 6, *, 7, 8";
		//goal = "1, 2, 3, 4, 5, 6, 7, 8, *";
		/**/
		
		/*
		// bound 8
		//start = "3, 4, 2, 1, *, 5, 7, 6, 8";
		//goal = "1, 2, 3, 4, 5, 6, 7, 8, *";
		/**/
		
		/*
		// Not solvable :D
		//start = "1, 2, 3, 4, 5, 6, 8, 7, *";
		//goal = "1, 2, 3, 4, *, 5, 6, 7, 8";
		/**/
		
		XbXPuzzleState startState = XbXPuzzleState.createBoard(3, start);
		startState.setGoalState(XbXPuzzleState.createBoard(3, goal));
		
		// can the state be solved?
		boolean canSolve = true;
		if(startState.isSolvable()) {
			System.out.println("Board is solvable.");
		} else {
			canSolve = false;
			System.out.println("CANNOT solve board!");
		}
		
		BTStruct queue = null;
		switch(searchType) {
		case 0:
			System.out.println("Starting Breath-First Search...");
			queue = new BreathStruct();
			break;
		case 1:
			System.out.println("Starting Depth-First Search...");
			queue = new DepthStruct();
			break;
		case 2:
			System.out.println("Starting Best-First Search...");
			queue = new BestStruct();
			break;
		default:
			queue = new BestStruct();
		}
		System.out.println();
		
		Backtracker bt = new Backtracker(queue, mDepth, verbose);
		State solved = bt.backtracker(startState, ignRep);
		
		if(!canSolve && solved==null) {
			System.out.println("FAILED to solve! ... I told you :p");
			System.exit(0);
		} else if(solved==null) {
			System.out.println();
			System.out.println();
			System.out.println("*** Was NOT able to solve with max depth ["+mDepth+"] ***");
			System.exit(0);
		}
		List<State> path = solved.getPath();
		
		System.out.println();
		System.out.println();
		
		switch(searchType) {
		case 0:
			System.out.println("Breath-First");
			break;
		case 1:
			System.out.println("Depth-First");
			break;
		case 2:
			System.out.println("Best-First");
			break;
		}
		System.out.println("Path Length    : "+path.size());
		System.out.println(path);
	}
}
