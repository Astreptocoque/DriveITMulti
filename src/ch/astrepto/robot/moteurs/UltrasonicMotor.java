package ch.astrepto.robot.moteurs;

import ch.astrepto.robot.RobotAttributs;
import ch.astrepto.robot.capteurs.TouchSensorNXT;
import lejos.hardware.Sound;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;

public class UltrasonicMotor extends Moteur{
	
	private TouchSensorNXT ultrasonicTouchSensor;
	
	public UltrasonicMotor(MoteursTypes type, Port port, TouchSensorNXT ultrasonicTouchSensor) {
		super(type, port);
		this.maxSpeed = 740;
		this.motor.setSpeed(this.maxSpeed);
		this.ultrasonicTouchSensor = ultrasonicTouchSensor;
		System.out.println("ultrason");
		Sound.beepSequence();
		initPosition();
	}

	private void initPosition() {
		motor.forward();
		boolean boucle = true;
	
		while (boucle) {
			float touch = ultrasonicTouchSensor.getValue();

			if (touch > 0) {
				System.out.println("value " + touch);
				motor.stop();
				motor.rotate(-(RobotAttributs.ultrasonicMaxDegree+50));
				boucle = false;
			}
		}
		motor.resetTachoCount();
	}

	/**
	 * 
	 * @param angleP
	 *                où l'on veut se rendre
	 * @param boundWithWheels
	 *                si l'ultrason est lié au degré de la direction
	 */
	@Override
	public void goTo(double angleCourbature) {
		
		// arrête le moteur s'il est en train de bouger
	/*	if (motor.isMoving())
			motor.stop();*/
		double angle = RobotAttributs.degresCourbureToDegresUltrason(angleCourbature);
		double currentDegres = super.getCurrentDegres();
		double angleToDo;
		int max = RobotAttributs.ultrasonicMaxDegree;
		// si l'angle dépasse les bornes
		if(angle  > max) {
			angleToDo = max - currentDegres;
			destinationDegres = max;
		}else if(angle < -max) {
			angleToDo = max-currentDegres;
			destinationDegres = -max;
		}else {
			angleToDo = angle - currentDegres;
			destinationDegres = (int) angle;
		}
		motor.rotate((int)angleToDo, true);
	}

	public void waitComplete() {
		motor.waitComplete();
	}

	public boolean isPreviousMoveComplete() {
		return !motor.isMoving();
	}
}
