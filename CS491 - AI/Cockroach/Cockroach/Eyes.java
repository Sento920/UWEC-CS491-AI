package cockroach;


import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MeanFilter;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;

public class Eyes {
	
	Port port;
	SensorModes sensor;
	
	public Eyes() {
		port = LocalEV3.get().getPort("S4");
		sensor = new EV3UltrasonicSensor(port);
	}
	
	//simplified function to retrieved how far away an object is
	public float getDistance() {
		SampleProvider distance= sensor.getMode("Distance");
		SampleProvider average = new MeanFilter(distance, 5);
		float[] sample = new float[distance.sampleSize()];
		average.fetchSample(sample, 0);
		return sample[0];
	}
	

}
