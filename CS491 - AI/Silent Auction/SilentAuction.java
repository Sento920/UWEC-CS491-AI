import java.util.*;

/**
 * File: SilentAuction.java
 * Author: Tyler Rusch
 * Date: Spring 2015
 * Description:
 * 	Acts as a user interface for the manager.
 * 1) User -> Item, Description of the item.
 * 2) User -> Number of Agents
 * 3) User -> Bidding Target.
 */

public class SilentAuction{
	private static String itemName;
	private static String description;
	private static int userValue;
	private static int agentNum;
	private static Scanner in = new Scanner(System.in);
	private static int runningBid;
	private static boolean[] stopped;
	private static Agent[] agentPool;
	private static String winner;

	
	public static void main(String[] args){
		getInfo();
		startBid();
	}
	
	private static void startBid(){
		System.out.println("We will start the bidding at 10.");
		System.out.println("Let the Bids for " + itemName + " begin!");
		stopped = new boolean[agentNum];
		for(int b = 0; b < agentNum; b++){
			stopped[b] = false;
		}
		int turns = 0;
		int tempBid;
		runningBid = 10;
		//we're going to start bidding.
		while(true){
			turns++;
			System.out.println("\nBidding Round " + turns + "\n");
			for(int a = 0; a < agentNum; a++){
				//when stopped is full, we will stop all the bidding, and the winner will have been the last bidder.
				if(stopped[a] != true){
					tempBid = agentPool[a].makeABid(runningBid);
					agentPool[a].printAgent();
					if(tempBid == -1){
						//when stopped is full, then we want to make sure that we stop.
						stopped[a] = true;
					}else{
						winner = agentPool[a].getName();
						runningBid = tempBid;
					}
				}
			}
			System.out.println("\nBidding Round " + turns + " has concluded.\n");
			
			//count up all the stopped agents
			int count = 0;
			for(int x = 0 ; x < stopped.length; x++){
				if(stopped[x] == true){
					count++;
				}
			}
			if(count == agentNum){
				System.out.println("Bidding has Ceased!");
				System.out.println("The Winner of the bid for " +itemName + " is " + winner + "!");
				System.out.println("the Final Bid was: " + runningBid + " dollars!");
				System.out.println("Thank you for testing with our agents. Have a nice day!");
				System.exit(0);
			}
		}
	}
	
	private static void getInfo(){
		System.out.println("Welcome to the text interfaced Silent Auction!");
		//System.out.println("We will be using two agents today to compete for your item!");
		System.out.println("First, please name the item: ");
		itemName = in.nextLine();
		boolean input = false;
		while(!input){
			if(itemName != null ||itemName != ""){
				input = true;
			}else{
				System.out.println("The string you entered was not valid.");
				System.out.println("Please name the item: ");
				itemName = in.nextLine();
			}
		}
		//System.out.println("CHECK Item name : "+ itemName);

		System.out.println("Second, Please enter the item's value numerically. ");
		System.out.println("Please keep the value above 10, and below 8,000.");		
		input = false;
		while(!input){
			userValue = in.nextInt();
			if(userValue > 10 && userValue < 8000){
				input = true;
			}else{
				System.out.println("Pardon me, but the value you've provided is out of the limits specified.");
				System.out.println("Please keep the value above 10, and below 8,000.");
				userValue = in.nextInt();
			}
		}
		//System.out.println("CHECK Item value : "+ userVal);
		input = false;
		System.out.println("Please establish the number of agents you would like to bid in the auction: ");
		System.out.println("Please keep this number positive, and at most 5.");
		agentNum = in.nextInt();
		while(!input){
			if(agentNum > 0 && agentNum <= 5){
				input = true;
			}else{
				System.out.println("Re-enter the number of agents you would like to participate: ");
				agentNum = in.nextInt();
			}
		}
		agentPool = new Agent[agentNum];
		//System.out.println("CHECK number of participants: "+ agentNum);
		for(int i = 0; i < agentNum; i++){
			//System.out.println("Please name your agent: ");
			String temp = null;
			if(temp == null || temp.equals("")){
				temp = "Agent "+ (i+1) ;
			}
			//System.out.println("CHECK name: " + temp);			
			System.out.println("\nPlease enter the type of bidding you would like "+temp+" to do.");
			System.out.println("We ask that you please enter 1 for simulated annealing, and 2 for Steepest ascent.");
			int choice = in.nextInt();
			if(choice < 0 || choice > 2){
				System.out.println("We could not discern your input, so simulated annealing has been chosen to expedite the process.");
				choice = 1;
			}
			agentPool[i] = new Agent(userValue,choice);
			agentPool[i].setName(temp);
		}
	}
}