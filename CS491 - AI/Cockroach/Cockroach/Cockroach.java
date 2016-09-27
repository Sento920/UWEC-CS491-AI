package cockroach;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;

import java.util.concurrent.TimeUnit;

public class Cockroach {
	public static int BIG = 100;
	
	public static void main(String[] args) {
		Brick brick=BrickFinder.getDefault();
		Light light = new Light(brick);
		RegulatedMotor leftMotor=new EV3LargeRegulatedMotor(brick.getPort("D"));
        RegulatedMotor rightMotor=new EV3LargeRegulatedMotor(brick.getPort("A"));
		DifferentialPilot pilot = new DifferentialPilot(5.24, 10.5, leftMotor, rightMotor);
        Gyro gyro = new Gyro(); 
        Eyes eyes = new Eyes();
        pilot.setRotateSpeed(100);
        pilot.setTravelSpeed(30);
        float start = 0, next = 0;
        
        double temp = 2;
    	double coolingRate = .05;
        
        start = light.getIntensity() * 100;
        System.out.println("Start: " + start);
        float begin = 100;
        //wait for light to increase by a meaningful amount before running away
        while(start + 5 > begin) {
        	begin = light.getIntensity() * 100;
        }
        
        pilot.reset();
        while(temp > 1) {
        	start = light.getIntensity() * 100;
        	System.out.println("---------------");
            System.out.println("Start: " + start);
        	int n = (int)(Math.random() * 4 + 1);
            switch(n) {
            	case 1:
            		System.out.println("1");
            		rotate(pilot, gyro, 90);
            		move(pilot, eyes, 30);
            		break;
            	case 2:
            		System.out.println("2");
            		rotate(pilot, gyro, -90);
            		move(pilot, eyes, 30);
            		break;
            	case 3:
            		System.out.println("3");
            		rotate(pilot, gyro, 180);
            		move(pilot, eyes, 30);
            		break;
            	case 4:
            		System.out.println("4");
            		move(pilot, eyes, 30);
            		break;
            }
            Sound.beep();
            next = light.getIntensity() * 100;
            System.out.println("Next: " + next);
            
            double random = Math.random();
            System.out.println("random: " + random);
            double prob = checkProb(start, next, temp);
            System.out.println("prob: " + prob);
            if(random > checkProb(start, next, temp)) {
            	System.out.println("--Roll failed ("+prob+"%): Backtrack");
	            float backtrack = pilot.getMovementIncrement();
	        	pilot.travel(-backtrack);
            } else {
            	System.out.println("**Roll success ("+prob+"%): Taking bad move");
            }
            temp = temp - coolingRate;
        }

        
	}
	
	public static void move(DifferentialPilot pilot, Eyes eyes, int n) {
		pilot.travel(n, true);
		while(pilot.isMoving()) {
			float distance = eyes.getDistance();
			if(distance < .2) {
				pilot.stop();
				Sound.beep();
			}
		}
	}
	
	public static void rotate(DifferentialPilot pilot, Gyro gyro, double angle) {
		gyro.reset();
        float start = gyro.getDistance();
        pilot.rotate(BIG*angle, true);
        //use absolute value to check the angle traveled
        while(Math.abs((gyro.getDistance() - start)) < Math.abs(angle)-10) {
        	//System.out.println(Math.abs((gyro.getDistance() - start)));
        	}
        pilot.stop();
        
	}
	
	public static double checkProb(double currEn, double newEn, double temp){
		// check to see if the energy of the new place is better...
		if(currEn > newEn){
			System.out.println("New move is better, accepting");
			return 1.0;
		}
		//if its not, then we want to calculate the acceptance probability.
		return Math.exp((currEn - newEn)/temp);
	}
}
