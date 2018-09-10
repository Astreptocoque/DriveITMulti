package ch.astrepto.robot.capteurs;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.NXTUltrasonicSensor;

public class UltrasonicSensor extends Capteur {
	
	private NXTUltrasonicSensor ultrasonic;

	public UltrasonicSensor(Port port) {
		this.port = port;
		this.ultrasonic = new NXTUltrasonicSensor(port);
		this.sensor = this.ultrasonic.getDistanceMode();
		this.sampleSensor = new float[this.sensor.sampleSize()];
	}

	public float getValue() {
		float distance = super.getValue();
		
		if (distance == Float.POSITIVE_INFINITY)
			distance = 250;
		return distance;
	}
	
	public void close() {
		ultrasonic.close();
	}
}
