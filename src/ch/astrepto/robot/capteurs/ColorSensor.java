package ch.astrepto.robot.capteurs;

import ch.astrepto.robot.Track;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;

public class ColorSensor extends Capteur{
	
	private EV3ColorSensor color;
	
	public ColorSensor(Port port){
		this.port = port;
		this.color= new EV3ColorSensor(port);
		this.sensor = this.color.getRedMode();
		this.sampleSensor = new float[this.sensor.sampleSize()];
	}
	
	public float getValue() {
		float intensity = super.getValue();
		
		// ajustement de la valeur et détection év. du croisement
		if (intensity > Track.maxValue)
			intensity = Track.maxValue;
		
		return intensity;
	}
	
	public void close() {
		color.close();
	}
}
