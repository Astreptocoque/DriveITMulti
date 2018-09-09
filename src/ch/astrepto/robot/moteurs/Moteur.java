package ch.astrepto.robot.moteurs;

import ch.astrepto.robot.RobotAttributs;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.robotics.RegulatedMotor;

public  class Moteur {

	protected RegulatedMotor motor;
	protected int maxSpeed = 200;
	protected int destinationDegres;
	
	public Moteur(MoteursTypes type, Port port) {
		
		switch(type) {
			case EV3Motor:
				motor = new EV3LargeRegulatedMotor(port);
				break;
			case EV3MediumMotor:
				motor = new EV3MediumRegulatedMotor(port);
				break;
			case NXTMotor:
				motor = new NXTRegulatedMotor(port);
				break;
		}
	}
	
	void goTo(double angle) {
		motor.rotate(RobotAttributs.degresCourbureToDegresRoue(angle));
	}
	
	public double getCurrentDegres() {
		return this.motor.getTachoCount();
	}
	
	public double getCurrentRadian() {
		return Math.toRadians(this.motor.getTachoCount());
	}
	
	public boolean isPreviousMoveComplete() {
		return ! this.motor.isMoving();
	}
}
