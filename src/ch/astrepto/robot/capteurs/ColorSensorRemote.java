package ch.astrepto.robot.capteurs;

import ch.astrepto.robot.RobotRemote;

public class ColorSensorRemote extends CapteurRemote {
		
	public ColorSensorRemote(RobotRemote coffre, String port){

		sensor = coffre.brique.createSampleProvider(port, "lejos.hardware.sensor.EV3ColorSensor", "Red");
	}
}
