package cups;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.sensor;

//  http://sourceforge.net/p/lejos/wiki/Home/
//  http://lejos.sourceforge.net/tools/eclipse/plugin/ev3 
public class Cups {
	public static final int NINETY = 360;
	public static regulatedMotor rightM = new EV3LargeRegulatedMotor(MotorPort.D);
	public static regulatedMotor leftM = new EV3LargeRegulatedMotor(MotorPort.D);
	
	public static void main(String[] args) {
		LCD.drawString("Press a Button to begin.", 0, 0);
		Button.waitForAnyPress();
		LCD.clear();
		LCD.drawString(leftM.getPower(),0,0);
		LCD.drawString(rightM.getPower(),1,1);
		//LCD.drawInt(Motor.A.getTachoCount(), 0, 0);
	}

	public void moveforward() {
		if (EV3TouchSensor.getTouchMode() != 1) {
			leftM.stop();
			rightM.stop();
			makeflush();
		} else {
			leftM.forward();
			rightM.forward();
		}
	}

	public void makeflush() {
		//if the right hand side falls off....
		
		if(EV3TouchSensor.four.getTouchMode() != 1){
			
			
		}else{
		//this means the left hand side fell off.
		
		}
	}

	public boolean scan(){
		
		
		return true;
	}
	
	
	
	public void turn90(boolean right) {
		if (right) {
			Motor.A.rotate(NINETY, true);
			Motor.D.rotate(-NINETY);
		} else {
			Motor.A.rotate(-NINETY, true);
			Motor.D.rotate(NINETY);
		}
	}
}