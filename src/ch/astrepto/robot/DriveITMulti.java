package ch.astrepto.robot;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.utility.Delay;

public class DriveITMulti {

	public static void main(String[] args) {

		// � faire avant de d�poser le robot sur la piste
		RobotECB rob = new RobotECB();
		System.out.println("Placer le robot sur la piste et presser ENTER\n");
		System.out.println("A gauche, sur le bleu");
		System.out.println("A droite, sur le blanc");
		Button.ENTER.waitForPressAndRelease();
		Track.updateTrackInfos(rob.colorDroite.getValue());
		System.out.println("Appuyer pour demarrer");
		Button.ENTER.waitForPressAndRelease();
		rob.robotStart();
	
		double speed = 0;
		float intensity;
		double time1 = 0;
		double time2 = 0;
		
		do {
			if (!Track.inCrossroads && !Track.overtaking) {
				intensity = rob.updateLightIntensity();
				// D�tection du carrefour
				if (intensity <= Track.crossLineValue + 1)
					Track.crossroads = true;
			}
			
			rob.updateDistance();

			if (!Track.inCrossroads && !Track.crossroads && !Track.overtaking && Track.hangOnTrack) 
				rob.updateDirection(true);

			if (!Track.overtaking && !Track.ultrasonicRepositioning) {
				speed = rob.updateSpeed();
			/*	
				// si immobilisation de plus de 10sec
				if(speed == 0) {
					if(time2 == 0)
						time1 = System.currentTimeMillis();
					time2 = System.currentTimeMillis();
					if(time2-time1 > 10000)
						depassement(rob);
				}else {
					time2 = 0;
				}*/
			}

			// entr�e dans le croisement
			if (Track.crossroads && !Track.inCrossroads)
				crossroads(rob);

			// sortie du croisement
			if (Track.inCrossroads) 
				crossroadsEnd(rob);

			
				
			

		} while (!Button.UP.isDown());

		rob.robotStop();
	}
	
	private static void depassement(RobotECB rob) {

		int directionOtherSide = Track.getPart() * Track.getSide();
		int speed = 100;
		double teta = 60; //degr�s du cercle
		double tetaRad = Math.toRadians(teta);
		double rayon = RobotAttributs.wheelSpacing / Math.tan(RobotAttributs.wheelCourbureMax);
		
		rob.tractionMotor.setSpeed(RobotAttributs.wheelCourbureMax*directionOtherSide, speed);
		
		// attente que l'autre cot� soit libre
		rob.ultrasonicMotor.goTo(90 * directionOtherSide);
		while(rob.ultrasonic.getValue() > 45)
			Delay.msDelay(100);
		rob.ultrasonicMotor.goTo(0);		
		
		//1er virage
		rob.directionMotor.goTo(RobotAttributs.wheelCourbureMax*directionOtherSide);
		rob.directionMotor.waitComplete();
		rob.tractionMotor.goTo(tetaRad*rayon);
		
		//analyseTerrain
		
		//bout droit
		rob.directionMotor.goTo(0);
		double cm = (RobotAttributs.distHorizontalBetweenRobots - 2*(rayon - tetaRad*rayon))/Math.cos(Math.PI/2-teta);
		rob.tractionMotor.goTo(cm);
		
		//analyseTerrain
		
		//2eme virage
		rob.directionMotor.goTo(RobotAttributs.wheelCourbureMax*directionOtherSide*-1);
		rob.directionMotor.waitComplete();
		rob.tractionMotor.goTo(tetaRad*rayon);
		rob.directionMotor.goTo(0);
	}
	
	
	/**
	 * Gestion du carrefour Une fois le carrefour d�tect�, cette section r�agit en fonction du
	 * c�t� du croisement
	 */
	public static void crossroads(RobotECB rob) {
		// n'est pas mis � la m�me condition juste en dessous pour acc�l�rer le
		// freinage (sinon lent � cause de goTo)
//		if (Track.part == -1)
			// arr�te le robot
			rob.tractionMotor.move(false);

		// indique qu'on est en train de passer le croisement
		Track.inCrossroads = true;
		rob.tractionMotor.resetTachoCount();

		rob.ultrasonicMotor.goTo(0);
	//	rob.directionMotor.goTo(0);
		if (Track.getPart() == 1 && Track.getSide() == 1) 
			rob.directionMotor.goTo(-5); 
		 else if(Track.getPart() == -1 && Track.getSide() == 1)
			 rob. directionMotor.goTo(0); 
		else 
			rob.directionMotor.goTo(0);
		
		rob.directionMotor.waitComplete();

		// si on est au croisement � priorit�
		if (Track.part == -1)
			waitRightPriorityOk(rob);

		rob.tractionMotor.move(true);

	}

	/**
	 * Gestion de la d�tection de la fin du carrefour D�tecte la fin du carrefour et maj les
	 * indications de piste
	 */
	public static void crossroadsEnd(RobotECB rob) {
		// on attends de l'avoir pass� pour red�marrer les fonctions de direction
		// on attends de l'avoir pass� pour red�marrer les fonctions de direction
		if (rob.tractionMotor.getCurrentDegres() >= Track.crossroadsLength / RobotAttributs.cmInDegres) {

			int intensityGauche = (int) rob.colorGauche.getValue();
			int intensityDroite = (int) rob.colorDroite.getValue();

			int diff = intensityGauche - intensityDroite;
			
			if(Math.abs(diff) > 5) {
				Track.inCrossroads = false;
				Track.crossroads = false;
				Track.justAfterCrossroads = true;
			
				if((diff > 5 && ((Track.getPart() == 1 && Track.getSide() == -1)
					|| (Track.getPart() == -1 && Track.getSide() == 1)))
					|| (diff < 5 && ((Track.getPart() == 1 && Track.getSide() == 1)
						|| (Track.getPart() == -1 && Track.getSide() == -1)))) {
					Track.changeSide();
				}
				Track.changePart();
				rob.tractionMotor.resetTachoCount();
			}
		}
	}

	/**
	 * Gestion de la priorit� de droite laisse continuer le robot seulement si aucun v�hicule
	 * devant avoir la priorit� n'est d�tect�
	 */
	private static void waitRightPriorityOk(RobotECB rob) {
		
		double distanceDetectBeforeCrossLine; // cm
		double zoneFirstAngle; //degr�s
		double zoneLastAngle; // degr�s
		
		if(Track.side == 1) {
			distanceDetectBeforeCrossLine = 50;
			zoneFirstAngle = 20;
			zoneLastAngle = 70;
		}else {
			distanceDetectBeforeCrossLine = 60;
			zoneFirstAngle = 40;
			zoneLastAngle = 80;
		}
		
		// l'ultrason se rend au d�but de son trac� de mesure
		rob.ultrasonicMotor.waitComplete();
		rob.ultrasonicMotor.goTo(zoneFirstAngle);
		rob.ultrasonicMotor.waitComplete();

		// on commence la detection
		boolean blockedTrack = true;
		int sens = 1;
		boolean vehicle = false;
		double distanceMesured;

		// on r�p�te tant que la piste n'est pas libre
		double time1 = System.currentTimeMillis();
		double time2;
		
		while (blockedTrack) {

			if (sens == 1)
				rob.ultrasonicMotor.goTo(zoneLastAngle);
			else
				rob.ultrasonicMotor.goTo(zoneFirstAngle);

			do{
				distanceMesured = rob.ultrasonic.getValue();
				// si on d�tecte un v�hicule
				if (distanceMesured <= distanceDetectBeforeCrossLine) 
					vehicle = true;
				
			}while (!rob.ultrasonicMotor.isPreviousMoveComplete());
			
			time2 = System.currentTimeMillis();
			//stop la detection apr�s 15 seconde et on force le passage
			if (time2-time1 > 25000) 
				blockedTrack = false;
			
			// � la fin de la d�tection, on regarde si un v�hicule a �t� d�tect�
			if (vehicle) {
				vehicle = false;
				sens *= -1;
			}
			else 
				blockedTrack = false;
		}
		
		rob.ultrasonicMotor.goTo(0);
		rob.ultrasonicMotor.waitComplete();
	//	rob.tractionMotor.setSpeed(0, RobotAttributs.maxSpeed);
	}
}
