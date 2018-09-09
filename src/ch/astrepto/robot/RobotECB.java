package ch.astrepto.robot;

import ch.astrepto.robot.capteurs.ColorSensor;
import ch.astrepto.robot.capteurs.ColorSensorRemote;
import ch.astrepto.robot.capteurs.UltrasonicSensor;
import ch.astrepto.robot.moteurs.DirectionMotor;
import ch.astrepto.robot.moteurs.MoteursTypes;
import ch.astrepto.robot.moteurs.TractionMotor;
import ch.astrepto.robot.moteurs.UltrasonicMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;

public class RobotECB {

	public DirectionMotor directionMotor;
	public TractionMotor tractionMotor;
	public UltrasonicMotor ultrasonicMotor;
	public ColorSensor colorGauche;
	public ColorSensorRemote colorDroite;
	public UltrasonicSensor ultrasonic;
	public RobotRemote coffre;

	private  float intensity = 0;
	private float distance = 0;
	private double currentDestination;

	public RobotECB() {

		coffre = new RobotRemote();
		colorGauche = new ColorSensor(SensorPort.S4);
		colorDroite = new  ColorSensorRemote(coffre, "S1");
		ultrasonic = new UltrasonicSensor(SensorPort.S3);
		directionMotor = new DirectionMotor(MoteursTypes.EV3MediumMotor, MotorPort.A);
		tractionMotor = new TractionMotor(MoteursTypes.EV3Motor, MotorPort.C, MotorPort.B);
	}

	public boolean updateDirection(boolean ultrasonicConnected) {
		
		if(ultrasonicConnected) {
			if(!ultrasonicMotor.isPreviousMoveComplete())
				return false;
		}

		if (directionMotor.isPreviousMoveComplete()) {
			currentDestination = directionMotor.angleFunctionOfIntensity(intensity);
			directionMotor.goTo(currentDestination);
			return true;
		}else {
			return false;
		}
	}
	
	public boolean updateUltrasonicDirection() {
		
		if (ultrasonicMotor.previousMoveComplete()) {
			ultrasonicMotor.goTo(currentDestination);
			return true;
		}else {
			return false;
		}
	}

	public float updateDistance() {
		distance = ultrasonic.getValue();
		return distance;
	}
	
	public float updateLightIntensity() {
		intensity = colorDroite.getValue();
		return intensity;
	}
	
	public void updateSpeed() {
		double angle = RobotAttributs.degresRoueToDegresCourbure(directionMotor.getCurrentDegres());
		tractionMotor.setSpeed(angle, distance);
	}

	public void robotStop() {
		// arret du robot
		tractionMotor.move(false);
		// remet les roues droites
		directionMotor.goTo(0);
		// remet l'ultrason droit
		ultrasonicMotor.goTo(0);
	}

	public void robotStart() {
		tractionMotor.move(true);
	}
}
