package ch.astrepto.robot;

import ch.astrepto.robot.capteurs.ColorSensor;
import ch.astrepto.robot.capteurs.ColorSensorRemote;
import ch.astrepto.robot.capteurs.UltrasonicSensor;
import ch.astrepto.robot.moteurs.DirectionMotor;
import ch.astrepto.robot.moteurs.MoteursTypes;
import ch.astrepto.robot.moteurs.TractionMotor;
import ch.astrepto.robot.moteurs.UltrasonicMotor;
import lejos.hardware.Button;
import lejos.hardware.Sound;
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
		Sound.beep();
		ultrasonicMotor = new UltrasonicMotor(MoteursTypes.NXTMotor, MotorPort.D);
		Sound.beep();
	}

	public boolean updateDirection(boolean ultrasonicConnected) {
		
		if(ultrasonicConnected) {
			if (ultrasonicMotor.isPreviousMoveComplete() && directionMotor.isPreviousMoveComplete()) {
				currentDestination = directionMotor.angleFunctionOfIntensity(intensity);
				directionMotor.goTo(currentDestination);
				ultrasonicMotor.goTo(currentDestination);
				return true;
			}
			else {
				return false;
			}
		}else {
			if (directionMotor.isPreviousMoveComplete()) {
				currentDestination = directionMotor.angleFunctionOfIntensity(intensity);
				directionMotor.goTo(currentDestination);
				return true;
			}else {
				return false;
			}
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
		tractionMotor.move(false);
		directionMotor.goTo(0);
		ultrasonicMotor.goTo(0);
		colorDroite.close();
		colorGauche.close();
		directionMotor.close();
		ultrasonicMotor.close();
		ultrasonic.close();
		coffre.disConnect();
	}

	public void robotStart() {
		tractionMotor.move(true);
	}
}
