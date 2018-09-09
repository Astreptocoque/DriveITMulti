package ch.astrepto.robot.capteurs;

import ch.astrepto.robot.RobotRemote;
import ch.astrepto.robot.Track;
import lejos.hardware.sensor.EV3ColorSensor;

public class ColorSensorRemote extends Capteur{
	
	private EV3ColorSensor color;
	
	public ColorSensorRemote(RobotRemote coffre, String port){
		this.color = new EV3ColorSensor(coffre.brique.getPort(port));
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
