package ch.astrepto.robot.capteurs;

import lejos.hardware.port.Port;
import lejos.robotics.SampleProvider;

abstract class Capteur {

	protected Port port;
	protected SampleProvider sensor;
	protected float[] sampleSensor;
	
	public float getValue() {
		this.sensor.fetchSample(sampleSensor, 0);
		// on change l'échelle de 0 à 1 en 0 à 100
		float value = sampleSensor[0] * 100;
		return value;
	}
}
