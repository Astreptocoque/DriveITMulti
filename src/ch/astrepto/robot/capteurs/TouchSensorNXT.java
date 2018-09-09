package ch.astrepto.robot.capteurs;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.NXTTouchSensor;

public class TouchSensorNXT extends Capteur {

	private NXTTouchSensor color;

	public TouchSensorNXT(Port port){
		super();
		this.port = port;
		this.color= new NXTTouchSensor(port);
		this.sensor = this.color.getTouchMode();
		this.sampleSensor = new float[this.sensor.sampleSize()];
	}
}
