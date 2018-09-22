package ch.astrepto.robot.moteurs;

import ch.astrepto.robot.RobotAttributs;
import ch.astrepto.robot.Track;
import ch.astrepto.robot.capteurs.ColorSensor;
import ch.astrepto.robot.capteurs.TouchSensorEV3Remote;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;

public class DirectionMotor extends Moteur{
	
	private TouchSensorEV3Remote directionTouchSensor;

	public DirectionMotor(MoteursTypes type, Port port, TouchSensorEV3Remote directionTouchSensor) {
		super(type, port);
		this.maxSpeed = RobotAttributs.speedDirection;
		this.motor.setSpeed(this.maxSpeed);
		this.directionTouchSensor = directionTouchSensor;

		initPosition();
	}
	
	/**
	 * Gestion de la direction des roues avants
	 * @param angleP
	 *                angle auquel on veut se rendre
	 */
	@Override
	public void goTo(double angleCourbure) {
		// arrête le moteur s'il est en train de bouger
		if (motor.isMoving())
			motor.stop();
		
		double angle =  -RobotAttributs.degresCourbureToDegresRoue(angleCourbure);
		double currentDegres = super.getCurrentDegres();

		// transformation de l'angle final en nombre de ° que doit faire le robot
		double angleToDo;
		int max = RobotAttributs.wheelMaxDegree;

		// si l'angle est supérieure au maximum à gauche
		if (angle < -max) {
			angleToDo = -max - currentDegres;
			destinationDegres = -max;
			// si l'angle est supérieur au max à droite
		} else if (angle > max) {
			angleToDo = max - currentDegres;
			destinationDegres = max;
			// sinon
		} else {
			angleToDo = angle - currentDegres;
			destinationDegres = (int) angle;
		}

		motor.rotate((int) (angleToDo), true);

	}

	public double angleFunctionOfIntensity(float intensity) {

		double angleCourbure;
		double angleForMaxLum;
		double angleForMinLum;
		int angleCourbureContreDirection = 10;
		double maxDegreCourbureDegres = RobotAttributs.degresRoueToDegresCourbure(RobotAttributs.wheelMaxDegree);

		//en fonction du coté de la piste
		if(Track.getPart() == 1 && Track.getSide() == 1) {
			angleForMaxLum = -angleCourbureContreDirection;
			angleForMinLum = maxDegreCourbureDegres;
		}else if (Track.getPart() == 1 && Track.getSide() == -1) {
			angleForMaxLum = maxDegreCourbureDegres;
			angleForMinLum = -angleCourbureContreDirection;
		}else if (Track.getPart() == -1 && Track.getSide() == 1) {
			angleForMaxLum = angleCourbureContreDirection;
			angleForMinLum =  -maxDegreCourbureDegres;
		}else {
			angleForMaxLum = -maxDegreCourbureDegres;
			angleForMinLum =  angleCourbureContreDirection;
		}
		
		double a = (angleForMinLum - angleForMaxLum) / (Track.minValue - Track.maxValue);
		double b = angleForMaxLum - (angleForMinLum - angleForMaxLum) / (Track.minValue - Track.maxValue)
				* Track.maxValue;

		angleCourbure = a * intensity + b;

		return angleCourbure;
	}

	/**
	 * comme le capteur tactile n'est pas pressé exactement au centre, mais un peu avant et de
	 * manière décalée si on vient de par la gauche ou par la droite, il faut ajouter un petit
	 * nbr de rotation pour etre bien au centre. Varie si on vient de la gauche ou la droite.
	 * 
	 * @param sens
	 *                1 ou -1, à gauche ou à droite du centre droit des roues
	 * @return
	 */
	private int positioningAdjustment(int sens) {
		int angle;
		if (sens == 1) {  // vient de la droite
			angle = 55;
		} else {
			angle = -20; // vient de la gauche
		}
		return angle;
	}

	public boolean isPreviousMoveComplete() {
		return !motor.isMoving();
	}
	
	private void initPosition() {
		motor.backward();
		// cadrage du moteur, où qu'il soit
		boolean boucle = true;
		int sens = -1;
		boolean firstIteration = true;

		while (boucle) {
			int touch = (int) directionTouchSensor.getValue();
			// si le capteur est pressé
			if (touch > 0 && firstIteration) {
				motor.rotate(80);
				motor.backward();
			}

			else if (touch > 0) {
				// si la roue vient de la gauche ou de la droite
				motor.rotate(positioningAdjustment(sens));
				boucle = false;

			} else if (motor.isStalled()) {
				motor.forward();
				sens = 1;
			}
			firstIteration = false;
		}
		motor.resetTachoCount();
	}
	
	public void waitComplete() {
		motor.waitComplete();
	}
}
