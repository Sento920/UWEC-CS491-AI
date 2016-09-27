package cockroach;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.SampleProvider;

public class Gyro {
	
	Port port;
	EV3GyroSensor sensor;
	
	public Gyro() {
		port = LocalEV3.get().getPort("S3");
		sensor = new EV3GyroSensor(port);
	}
	
	//simplified function to retrieve how far the gyro has rotated
	public float getDistance() {
		SampleProvider distance= sensor.getAngleMode();
		float[] sample = new float[1];
		distance.fetchSample(sample, 0);
		return sample[0];
	}
	
	public void reset() {
		sensor.reset();
	}
}