package ch.astrepto.robot.moteurs;

import ch.astrepto.robot.RobotAttributs;
import ch.astrepto.robot.capteurs.TouchSensorNXT;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;

public class UltrasonicMotor extends Moteur{
	
	private TouchSensorNXT ultrasonicTouchSensor;
	
	public UltrasonicMotor(MoteursTypes type, Port port) {
		super(type, port);
		this.maxSpeed = 800;
		this.motor.setSpeed(this.maxSpeed);
		this.ultrasonicTouchSensor = new TouchSensorNXT(SensorPort.S1);
		
		initPosition();
	}

	private void initPosition() {
		motor.forward();
		boolean boucle = true;
	
		while (boucle) {
			float touch = ultrasonicTouchSensor.getValue();

			if (touch > 0) {
				motor.stop();
				motor.rotate(-RobotAttributs.ultrasonicMaxDegree);
				boucle = false;
			}
		}
		motor.resetTachoCount();
	}

	/**
	 * 
	 * @param angleP
	 *                o� l'on veut se rendre
	 * @param boundWithWheels
	 *                si l'ultrason est li� au degr� de la direction
	 */
	@Override
	public void goTo(double angleCourbature) {
		
		// arr�te le moteur s'il est en train de bouger
		if (motor.isMoving())
			motor.stop();

		double angle = RobotAttributs.degresCourbureToDegresUltrason(angleCourbature);
		double currentDegres = super.getCurrentDegres();
		double angleToDo;
		
		int max = RobotAttributs.ultrasonicMaxDegree;
		// si l'angle d�passe les bornes
		if(angle  > max) {
			angleToDo = max - currentDegres;
			destinationDegres = max;
		}else if(angle < -max) {
			angleToDo = max-currentDegres;
			destinationDegres = max;
		}else {
			angleToDo = angle - currentDegres;
			destinationDegres = (int) angle;
		}
			
		motor.rotate((int)-angleToDo, true);
	}

	public void waitComplete() {
		motor.waitComplete();
	}

	public boolean previousMoveComplete() {
		return !motor.isMoving();
	}
}
