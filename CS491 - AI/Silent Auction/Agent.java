
/**
 * File: Agent.java
 * Author: Tyler Rusch
 * Date: Spring 2015
 * Description:
 * 	An individual agent, controls itself and makes a decision.
 * 	1) Implements simulated Annealing / Steepest Ascent.
 * 	2) Makes a bidding decision using the value of the item given by the user to determine the bid it will make. 
 *			This is done by adding a weight attribute to the item, giving a decreased probability of bidding over time.
 * 			Hypothesis: this may give simulated annealing a challenge to win a bid.
 */
	
public class Agent{
	
	public enum Type{SIM, STEP, CONT}
	
	//the agent's initial starting resources.
	private int itemWeight;
	private int itemValue;
	private String name;
	Type agentType;
	private double temp = 200.0;
	private double coolingRate = 5;
	private int ourBid;
	private int previousBid;
	
	public Agent(){
		itemWeight = 1;
		itemValue = 1;
		agentType = Type.SIM;
	}
	
	
	public Agent(int itemValue, int det){
		//Enum determines the algorithm used.
		this.itemValue = itemValue;
		calcWeight();
		if(det == 1){
			agentType = Type.SIM;
			//this is simulated annealing
		}else if(det == 2){
			agentType = Type.STEP;
			//this is steepest ascent
		}else{
			agentType = Type.CONT;
			//contingency plan. its a backup which will simple bet the value that is given.
		}
	}
	
	//this is how far ach individual agent will go Above the actual value.
	//it gives each agent a different likely hood of stopping, but at ANY point, the agent may stop.
	public void calcWeight(){
		this.itemWeight = (int)(((itemValue * Math.random()) + itemValue));
	}
	
	//this method is the main method that carries out the bidding process via self examination and calculation.
	public int makeABid(int currBid){
		//don't bid if you control the lead!
		if(ourBid == currBid){
			return -1;
		}
		previousBid = currBid;
		int bid;
		if(this.agentType == Type.SIM){
			//System.out.println("Simulated Annealing chosen");
			bid = anneal(currBid);
		}else if(this.agentType == Type.STEP){
			//System.out.println("Steepest Ascent chosen");
			bid = steepest(currBid);
		}else{
			//Since all bidding is done based on current bid, we can end early via acceleration.
			System.out.println("ERROR: BACKUP USED, ENDING BIDDING VIA ACCELERATION. ");
			bid = itemValue * 50;
		}
		//System.out.println("CHECK Last Bid: " + bid);
		ourBid = bid;
		return bid;
	}
	
	//bids should be the probability of failure in the endeavour to bid. this should make steepest ascent work properly.
	
	//returns a bid if the weight to win is greater than the value of the current bid. Bid may 1.5x the current bid.  
	private int steepest(int currBid){
		int bid;
		if(itemWeight > currBid){
			bid = (int)Math.round(currBid +(currBid * (Math.random()/2.0)));
			if( itemWeight >= bid){
				return bid;
			}else{
				bid = (int)Math.round(currBid + (currBid *.05));	
				return bid;
			}
		}else{
			//rejected, stop bidding.
			return -1;
		}
	}
	
	//will do the probability calculations and then return a bidding amount.
	private int anneal(int currBid){
		int bid;	
		//checkprob determines whether we are beyond our limit for bidding.if we are, we will determine acceptance probability.
		double prob = checkProb(itemWeight, currBid, temp);
		//check to see if we made a probability less than a dice roll!
		//if we did, new bigger bid! up to 1.5x.
        if(prob == 1) {
			//System.out.println("We've decided to go forward with the best solution.");
			bid = (int)Math.round(currBid +(currBid * (Math.random()/2.0)));
        }else if(Math.random() > prob){
			//we failed the check! We'll make a smaller bid, and see where it gets us.
           	//System.out.println("We've tried Taking a possibly bad move.");
			bid = (int)Math.round(currBid + (currBid *.05));
        }else{
			//this is the rejection case, where we want to stop bidding!
			return -1;
		}
        temp = temp - coolingRate;
		return bid;
		
	}
	
	private double checkProb(double currEn, double newEn, double temp){
		//check to see if the energy of the new place is better...
		if(currEn > newEn){
			//New move is better, accepting that one;
			return 1.0;
		}
		//if its not, then we want to calculate the acceptance probability.
		return Math.exp((currEn - newEn)/temp);
	}
	
	//simple print method to see data.
	public void printAgent(){
		System.out.println("Agent: "+name+"\nPrevious Bid: " + previousBid + "\nItem Weight:" + itemWeight +"\nOur last Bid: "+ ourBid);
		if(agentType == Type.SIM){
			System.out.println("Type: Simulated Annealing Agent\n");
		}else if(agentType == Type.STEP){
			System.out.println("Type: Steepest Ascent Agent\n");			
		}else if(agentType == Type.CONT){
			System.out.println("Type: Control Agent. If you see this, Something went wrong.\n");		
		}
	}
	
	//returns the agents name as a String
	public String getName(){
		return name;
	}
	
	//sets the agents name
	public void setName(String name){
		this.name = name;
	}	
	
	//returns the agents type as a String
	public String getType(){
		if(agentType == Type.SIM){
			return("Simulated Annealing");
		}else if(agentType == Type.STEP){
			return("Steepest Ascent Agent");			
		}else{
			return("Control");		
		}
	}
	
}