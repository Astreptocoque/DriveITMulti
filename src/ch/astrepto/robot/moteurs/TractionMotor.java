package ch.astrepto.robot.moteurs;

import ch.astrepto.robot.RobotAttributs;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

public class TractionMotor {

	private Moteur motorLeft;
	private Moteur motorRight;
	private RegulatedMotor[] synchro;
	
	private static boolean isMoving = false;
	private double speed = RobotAttributs.maxSpeed;

	public TractionMotor(MoteursTypes type, Port portLeft, Port portRight) {
		motorLeft = new Moteur(type, portLeft);
		motorRight = new Moteur(type, portRight);	

		synchro = new EV3LargeRegulatedMotor[1];
		synchro[0] = motorRight.motor;
		motorLeft.motor.synchronizeWith(synchro);
		motorLeft.motor.setAcceleration(2000);
		motorRight.motor.setAcceleration(2000);

		motorLeft.motor.setSpeed((int) speed);
		motorRight.motor.setSpeed((int) speed);
	}

	/**
	 * Règle la vitesse et l'ajuste pour chaque roue de traction en fonction du virage Chaque
	 * partie de la piste a ses réglages. Si le robot va tout droit, quelques soit les réglages,
	 * la vitesse de chaque moteur sera égale
	 * 
	 */
	public double setSpeed(double angleCourbure, double distance) {
		
		double speedLeft;
		double speedRight;
		double speedAtFirst = RobotAttributs.maxSpeed;
		double speedAtLast = 0;
		
		if(distance > RobotAttributs.firstLimit)
			distance = RobotAttributs.firstLimit;
		if(distance < RobotAttributs.lastLimit)
			distance = RobotAttributs.lastLimit;
		
		double a = (speedAtLast - speedAtFirst)/(RobotAttributs.lastLimit - RobotAttributs.firstLimit);
		double b = speedAtFirst - (speedAtLast-speedAtFirst)/(RobotAttributs.lastLimit-RobotAttributs.firstLimit)*RobotAttributs.firstLimit;
		double speed = a*distance +b;
		
		if(angleCourbure == 0 || (int) speed == 0) {
			speedLeft = speed;
			speedRight = speed;
		}else{
			double radius = RobotAttributs.baseLength/Math.tan(Math.toRadians(angleCourbure));
			double rotationSpeed = speed/radius;
			
			speedLeft = rotationSpeed*(radius- RobotAttributs.wheelSpacing/2);
			speedRight = rotationSpeed*(radius+ RobotAttributs.wheelSpacing/2);
		}
		
		// set la vitesse
		motorLeft.motor.startSynchronization();
		motorLeft.motor.setSpeed((int) speedLeft);
		motorRight.motor.setSpeed((int) speedRight);
		motorLeft.motor.endSynchronization();
		this.speed = speed;
		
		if (speed == 0) {
			move(false);
		}else {
			move(true);
		}
		
		return(speed);
	}

	public double setSpeed(double angleCourbure, int speed) {
		
		double speedLeft;
		double speedRight;
		
		if(angleCourbure == 0 || (int) speed == 0) {
			speedLeft = speed;
			speedRight = speed;
		}else{
			double radius = RobotAttributs.baseLength/Math.tan(Math.toRadians(angleCourbure));
			double rotationSpeed = speed/radius;
			
			speedLeft = rotationSpeed*(radius- RobotAttributs.wheelSpacing/2);
			speedRight = rotationSpeed*(radius+ RobotAttributs.wheelSpacing/2);
		}
		
		motorLeft.motor.setSpeed((int) speedLeft);
		motorRight.motor.setSpeed((int) speedRight);
		this.speed = speed;
		
		return speed;
	}
	
	public void goTo(double nbCentimetres) {
		
		double degresZero = getCurrentDegres();
		double degresToDo = RobotAttributs.centimetresToDegresTraction(nbCentimetres);

		move(true);
		
		while(getCurrentDegres() - degresZero < degresToDo)
			Delay.msDelay(5);

		move(false);
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
