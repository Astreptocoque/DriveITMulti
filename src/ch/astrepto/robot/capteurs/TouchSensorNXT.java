package ch.astrepto.robot.capteurs;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.NXTTouchSensor;
import lejos.utility.Delay;

public class TouchSensorNXT extends Capteur {

	private NXTTouchSensor touch;

	public TouchSensorNXT(Port port){
		super();
		this.port = port;
		this.touch= new NXTTouchSensor(port);
		this.sensor = this.touch.getTouchMode();
		this.sampleSensor = new float[this.sensor.sampleSize()];
	}
	
public void waitForPressAndRelease() {
		
		boolean boucle = true;
		do {
			if(getValue() == 100)
				boucle = false;
			Delay.msDelay(5);
		}while(boucle);
		
		boucle = true;
		
		do {
			if(getValue() == 0)
				boucle = false;
			Delay.msDelay(5);
		}while(boucle);
	}
	
	public void close() {
		touch.close();
	}
}
