package ch.astrepto.robot.capteurs;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3TouchSensor;

public class TouchSensorEV3 extends Capteur {

	private EV3TouchSensor touch;

	public TouchSensorEV3(Port port){
		super();
		this.port = port;
		this.touch= new EV3TouchSensor(port);
		this.sensor = this.touch.getTouchMode();
		this.sampleSensor = new float[this.sensor.sampleSize()];
	}
	
	public void close() {
		touch.close();
	}
}
