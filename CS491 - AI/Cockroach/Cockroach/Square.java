package cups;

import java.util.concurrent.TimeUnit;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.hardware.Sound;

public class Square {
// http://lejos.sourceforge.net/tools/eclipse/plugin/ev3
	public static int DIRECTION = 1;
	public static int CIRCLE = 400;
	public static int BIG = 100;
	public static boolean RETURNINGFROMCUP = true;
	public static void main(String[] args) {
        Brick brick=BrickFinder.getDefault();
        
         
        RegulatedMotor leftMotor=new EV3LargeRegulatedMotor(brick.getPort("A"));
        RegulatedMotor rightMotor=new EV3LargeRegulatedMotor(brick.getPort("D"));
        TouchSensors touch = new TouchSensors(brick);
        Eyes eyes = new Eyes();
        Gyro gyro = new Gyro();
        
        DifferentialPilot pilot=new DifferentialPilot(5.24, 10.5, leftMotor, rightMotor);
        pilot.setRotateSpeed(30);
        pilot.setTravelSpeed(8);
        Button.waitForAnyPress();
        
        
        
        int fail = 0;
        
        //Begin by finding a corner so we have a consistent starting position
        findCorner(pilot, touch, gyro, eyes);
        //Exit condition: Robot makes two full sweeps of the table without finding any cups
        while(fail < 2) {
        	//Cut-off condition (for testing purposes:
        	//if both sensors are deactivated (robot is flipped upside-down), quit early
        	if(!(touch.leftIsPressed() && touch.rightIsPressed())) {
        		Sound.beep();
        		Sound.beep();
        		break;
        	}
        	
        	//Sweep in one direction, returns true if something is found
        	if(sweep(pilot, eyes, touch, gyro)) {
        		//If a cup is found, reset the robot (# of fails, direction, realign to corner starting position)
        		fail = 0;
        		DIRECTION = 1;
        		findCorner(pilot, touch, gyro, eyes);
        	} else {
        		//Sweep did not find a cup,
        		//Prepare to sweep in the other direction
        		rotate(pilot, gyro, 90 * DIRECTION, eyes, touch);
        		fail++;
        	}
        	
        }
        
    }
	
	public static boolean sweep(DifferentialPilot pilot, Eyes eyes, TouchSensors touch, Gyro gyro) {
		//Begin the sweep by scanning in the current position
		if(rotate(pilot, gyro, 360, eyes, touch)) {
			return true;
		}
		//Loop until something is found or the sweep has failed
		while(true) {
			//move forward to reposition
			pilot.travel(40, true);
			//Check condition to prevent running off the table
			while(pilot.isMoving()) {
				//if you hit an edge, turn 90 degrees and travel forward, then turn 90 more degrees
				//This pattern creates the "zig-zag" across the table
				if(!(touch.leftIsPressed() && touch.rightIsPressed())) {
					pilot.stop();
					flush(pilot, touch);
					if(rotate(pilot, gyro, 90 * DIRECTION, eyes, touch)){
						return true;
					}
					pilot.travel(40, true);
					//if you hit an edge during this move, you're at the end of the sweep
					while(pilot.isMoving()) {
						if(!(touch.leftIsPressed() && touch.rightIsPressed())) {
							pilot.stop();
							return false;
						}
					}
					//Adjust slightly backwards for safety
					pilot.travel(-5);
					//turn 90 degrees again
					if(rotate(pilot, gyro, 90 * DIRECTION, eyes, touch)) {
						return true;
					}
					//Change the direction of our turns
					DIRECTION = DIRECTION * -1;
				}
			}
			//Full scan
			if(rotate(pilot, gyro, 360, eyes, touch)) {
				return true;
			}
		}
	}
	
	//Flush is an attempt to get the robot to face the edge directly for orientation
	public static void flush(DifferentialPilot pilot, TouchSensors touch) {
		
		//if left is off the edge, turn counter clockwise to get right off the edge
		if(touch.leftIsPressed()) {
			while(touch.leftIsPressed()) {
				if(!pilot.isMoving()) {
				pilot.rotate(20, true);
				}
				if(!touch.leftIsPressed()) {
					pilot.stop();
				}
			}
		//if right is off the edge, turn clockwise to get left off the edge
		} else {  
			while(touch.rightIsPressed()) {
				if(!pilot.isMoving()) {
				pilot.rotate(-20, true);
				}
				if(!touch.rightIsPressed()) {
					pilot.stop();
				}
			}
		}
		//Move away from the edge
		pilot.travel(-20);
	}
	
	//find an edge, face that edge, rotate at a right angle, then find another edge.
	//sets up starting position
	public static void findCorner(DifferentialPilot pilot, TouchSensors touch, Gyro gyro, Eyes eyes) {
		//RETURNINGFROMCUP simply makes sure that the robot does not get "interrupted"
		//on its journey to a corner. Otherwise, the robot can find a cup halfway through, then
		//return to only complete half of the findCorner function
		while(RETURNINGFROMCUP) {
			RETURNINGFROMCUP = false;
			findEdge(pilot, touch);
			while(pilot.isMoving()) {} //wait
			rotate(pilot, gyro, 90, eyes, touch);
			if(!RETURNINGFROMCUP) {
				findEdge(pilot, touch);
				while(pilot.isMoving()) {} //wait
				rotate(pilot, gyro, 90, eyes, touch);
				
			}
		}
		
	}
	
	//find an edge then face it
	public static void findEdge(DifferentialPilot pilot, TouchSensors touch) {
		pilot.forward();
		while(pilot.isMoving()) {
			//if either sensor falls off an edge
			if(!(touch.leftIsPressed() && touch.rightIsPressed())) {
				//stop, then face that edge
				pilot.stop();
				flush(pilot, touch);
			}
		}

	}
	
	//prints out the distance an object is from the ultrasonic sensor
	//used for debugging and scan range calibration
	public static void testEyes(Eyes eyes) {
		 for(int i = 0; i < 10; i++) {
	        	try {
					TimeUnit.SECONDS.sleep(2);
					float distance = eyes.getDistance();
					System.out.println(distance);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		 }
	}
	
	//rotates the robot, scanning for cups in the process
	//robot will rotate a large amount, then stop once a certain distance has been traveled
	//or if a cup has been found
	public static boolean rotate(DifferentialPilot pilot, Gyro gyro, double angle, Eyes eyes, TouchSensors touch) {
		gyro.reset();
        float start = gyro.getDistance();
        pilot.rotate(BIG*angle, true);
        float distance = eyes.getDistance();
        //use absolute value to check the angle traveled
        while(Math.abs((gyro.getDistance() - start)) < Math.abs(angle)-5 && !(distance > 0 && distance < .4)) {
        	distance = eyes.getDistance();
        	System.out.println(Math.abs((gyro.getDistance() - start)));
        };
        pilot.stop();
        
        //if a cup is within scanning range
		if(distance > 0 && distance < .4) {
			//push it off the edge!
			Sound.beep();
			RETURNINGFROMCUP = true;
			findEdge(pilot, touch);
			pilot.travel(-25);
			return true;
		}
		return false;
	}
}
