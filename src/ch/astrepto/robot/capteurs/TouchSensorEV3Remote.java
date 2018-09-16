package ch.astrepto.robot.capteurs;

import ch.astrepto.robot.RobotRemote;

public class TouchSensorEV3Remote extends CapteurRemote {
	
	public TouchSensorEV3Remote(RobotRemote coffre, String port){
		    
		sensor  = coffre.brique.createSampleProvider(port, "lejos.hardware.sensor.EV3TouchSensor", "Touch");
	}
}
