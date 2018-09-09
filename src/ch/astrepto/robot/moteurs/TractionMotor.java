package ch.astrepto.robot.moteurs;

import ch.astrepto.robot.RobotAttributs;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.robotics.RegulatedMotor;

public class TractionMotor {

	private Moteur motorLeft;
	private Moteur motorRight;
	private RegulatedMotor[] synchro;
	
	private static boolean isMoving = false;
	public final static int maxSpeed = 400;
	private int speed = maxSpeed;

	public TractionMotor(MoteursTypes type, Port portLeft, Port portRight) {
		motorLeft = new Moteur(type, portLeft);
		motorRight = new Moteur(type, portRight);	

		synchro = new EV3LargeRegulatedMotor[1];
		synchro[0] = motorRight.motor;
		motorLeft.motor.synchronizeWith(synchro);
		motorLeft.motor.setAcceleration(2000);
		motorRight.motor.setAcceleration(2000);

		motorLeft.motor.setSpeed(speed);
		motorRight.motor.setSpeed(speed);
	}

	/**
	 * Règle la vitesse et l'ajuste pour chaque roue de traction en fonction du virage Chaque
	 * partie de la piste a ses réglages. Si le robot va tout droit, quelques soit les réglages,
	 * la vitesse de chaque moteur sera égale
	 * 
	 */
	public void setSpeed(double angleCourbure, double distance) {
		
		double speedLeft;
		double speedRight;
		double speedAtFirst = maxSpeed;
		
		double a = -speedAtFirst/(RobotAttributs.lastLimit - RobotAttributs.firstLimit);
		double b = speedAtFirst / (RobotAttributs.lastLimit - RobotAttributs.firstLimit) *RobotAttributs.lastLimit;
		double speed = a*distance +b;
		
		if(angleCourbure == 0 || speed == 0) {
			speedLeft = speed;
			speedRight = speed;
		}else{
			double radius = RobotAttributs.baseLength/Math.tan(Math.toRadians(angleCourbure));
			double rotationSpeed = speed/radius;
			
			speedLeft = rotationSpeed*(radius- RobotAttributs.wheelSpacing/2);
			speedRight = rotationSpeed*(radius+ RobotAttributs.wheelSpacing/2);
		}
		
		// set la vitesse
		motorLeft.motor.setSpeed((int) speedLeft);
		motorRight.motor.setSpeed((int) speedRight);

		if (speed == 0)
			isMoving = false;
		else if(isMoving == false) {
			isMoving = true;
			move(true);
		}
	}


	/**
	 * Gestion du mouvement du véhicule (en marche et à l'arret)
	 * 
	 * @param move
	 *                true pour démarrer, false pour arrêter
	 */
	public void move(boolean move) {
		motorLeft.motor.startSynchronization();

		if (move) {
			motorLeft.motor.backward();
			motorRight.motor.backward();
			isMoving = true;
		} else {
			motorLeft.motor.stop();
			motorRight.motor.stop();
			isMoving = false;
		}
		motorLeft.motor.endSynchronization();
	}

	public void resetTachoCount() {
		motorLeft.motor.resetTachoCount();
		motorRight.motor.resetTachoCount();
	}
	
	public float getCurrentDegres() {
		return (float) ((motorLeft.getCurrentDegres()+ motorRight.getCurrentDegres())/ 2 * -1);
	}
}
