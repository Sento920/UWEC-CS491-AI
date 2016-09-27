package cockroach;

import com.jcraft.jsch.jce.Random;

public class SimAnneal{
	// this class just accepts some simulated annealing stuff. its meant for integration, not independent use.
	double temp = 1000;
	double coolingRate = .004;
	// multiply the intensity by 100 to get here.
	double goalEn = 1;
	double neigEn;
	Random gen = new Random();
	
	// the rest of sim annealing is based on the direction of movement, based on the probability.
	
	// cooling is 1-coolingRate, done at the end of each iteration
	
	
}