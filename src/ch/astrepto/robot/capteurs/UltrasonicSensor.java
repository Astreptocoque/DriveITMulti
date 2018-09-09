package ch.astrepto.robot.capteurs;

import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.NXTUltrasonicSensor;
import lejos.robotics.SampleProvider;

public class UltrasonicSensor extends Capteur {
	
	public final static int maxDetectedDistance = 50;
	private NXTUltrasonicSensor ultrasonic;

	public UltrasonicSensor(Port port) {
		this.port = port;
		this.ultrasonic = new NXTUltrasonicSensor(port);
		this.sensor = this.ultrasonic.getDistanceMode();
		this.sampleSensor = new float[this.sensor.sampleSize()];
	}

	public float getValue() {
		float distance = super.getValue();

		if (distance > maxDetectedDistance || distance == Float.POSITIVE_INFINITY) 
			distance = maxDetectedDistance;

		return distance;
	}
	
	public void close() {
		ultrasonic.close();
	}
}
