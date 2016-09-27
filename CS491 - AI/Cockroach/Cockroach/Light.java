package cockroach;

import lejos.hardware.Brick;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;


public class Light {

	EV3ColorSensor light;
	SampleProvider sp_light;
	float[] lightSample;
	
	public Light(Brick brick) {
		
		this.light = new EV3ColorSensor(brick.getPort("S2"));
		this.sp_light = light.getAmbientMode();
		this.lightSample = new float[sp_light.sampleSize()];
	}
	
	public float getIntensity() {
		float n = 0;
		for(int i = 0; i < 10; i++) {
			sp_light.fetchSample(lightSample, 0);
			n+=lightSample[0];
		}
		n = n/10;
		return n;
	}
}