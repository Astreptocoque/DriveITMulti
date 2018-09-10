package ch.astrepto.robot.capteurs;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.NXTTouchSensor;

public class TouchSensorNXT extends Capteur {

	private NXTTouchSensor touch;

	public TouchSensorNXT(Port port){
		super();
		this.port = port;
		this.touch= new NXTTouchSensor(port);
		this.sensor = this.touch.getTouchMode();
		this.sampleSensor = new float[this.sensor.sampleSize()];
	}
	
	public void close() {
		touch.close();
	}
}
