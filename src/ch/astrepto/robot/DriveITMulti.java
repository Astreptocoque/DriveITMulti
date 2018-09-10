package ch.astrepto.robot;

import lejos.hardware.Button;

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
	
		do {
			if (!Track.inCrossroads && !Track.overtaking) {
				float intensity = rob.updateLightIntensity();
				// D�tection du carrefour
			/*	if (intensity <= Track.crossLineValue + 1)
					Track.crossroads = true;*/
			}
			
			if(!Track.verifiyFreeWay && !Track.crossroads) 
				rob.updateDistance();

			if (!Track.inCrossroads && !Track.crossroads && !Track.overtaking && Track.hangOnTrack) 
				rob.updateDirection(true);

			if (!Track.overtaking && !Track.verifiyFreeWay && !Track.ultrasonicRepositioning) 
				rob.updateSpeed();
/*
			// entr�e dans le croisement
			if (Track.crossroads && !Track.inCrossroads)
				crossroads(rob);

			// sortie du croisement
			if (Track.inCrossroads) 
				crossroadsEnd(rob);
*/
	/*		if (!Track.crossroads && !Track.verifiyFreeWay && Track.hangOnTrack
					&& !Track.ultrasonicRepositioning) {
				rob.isThereAnOvertaking();
			}

			// GESTION DE LA VERIFICATION POUR PASSER SUR L'AUTRE VOIE (VOIE LIBRE)
			// Est maj si "il faut v�rifier le chemin"
			if (Track.verifiyFreeWay) {
				rob.freeWay();
			}

			// GESTION DES DEPASSEMENTS
			// Est maj si "initialisation d'un d�passement"
			if (Track.overtaking) {
				rob.overtaking();
			}

			// GESTION DE LA FIN DES DEPASSEMENTS
			// Est maj si pas "accroch� � la piste"
			if (!Track.hangOnTrack) {
				rob.overtakingEnd();
			}
			*/

		} while (!Button.UP.isDown());

		rob.robotStop();
	}
	
	/**
	 * Gestion du carrefour Une fois le carrefour d�tect�, cette section r�agit en fonction du
	 * c�t� du croisement
	 */
	public static void crossroads(RobotECB rob) {
		// n'est pas mis � la m�me condition juste en dessous pour acc�l�rer le
		// freinage (sinon lent � cause de goTo)
		if (Track.part == -1)
			// arr�te le robot
			rob.tractionMotor.move(false);

		// indique qu'on est en train de passer le croisement
		Track.inCrossroads = true;
		rob.tractionMotor.resetTachoCount();

		rob.ultrasonicMotor.goTo(0);
		rob.directionMotor.goTo(0);

		// si on est au croisement � priorit�
		if (Track.part == -1)
			waitRightPriorityOk(rob);
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
	 * Gestion de la fin du d�passement. Cette fin comprend 2 parties : la fin du virage pour
	 * rejoind l'autre c�t� et la fin du bout droit jusqu'� l'autre c�t�
	 * 
	 * @param part
	 *                partie de la fin du croisement. Vrai s'il faut fini le virage, faut s'il
	 *                faut rejoindre l'autre c�t�. La valeur de part est la valeur de
	 *                Track.overtaking
	 */
/*	public void overtakingEnd() {
		if (Track.overtakingPart == 1) {
			if (tractionMotor.getTachoCount() >= previousTachoCount
					+ (Track.smallRadius + Track.gradientWidth / 2) * 2 * Math.PI / 4
							/ TractionMotor.cmInDegres) {
				ultrasonicMotor.goTo(0, true);
				directionMotor.goTo(0);
				Track.overtakingPart = 2;
			}
		} else {
			// s�curit� pour ne pas d�tecter le c�t� actuel
			if (tractionMotor.getTachoCount() >= previousTachoCount + 720) {
				if (intensity <= (ColorSensor.trackMaxValue - 10)) {
					Track.hangOnTrack = true;
					// on change les donn�es de piste
					Track.changeSide();
				}
			}
		}
	}
*/
	/**
	 * Gestion des d�passements s'occupe de faire tourner le robot � la bonne "inclinaison" pour
	 * lui faire rejoindre l'autre c�t� de la piste ATTENTION : le d�passement sous-entend
	 * uniquement le virage effectu� pour d�crocher la piste et pouvoir ensuite rejoindre
	 * l'autre c�t�. Du moment que le virage est fait, la variabe "d�passement" est fausse, mais
	 * "hangOnTrack" reste fausse jusqu'� qu'on est � nouveau rejoint la piste
	 */
/*	public void overtaking() {

		Track.hangOnTrack = false;
		Track.overtaking = false;

		// r�gle l'angle que les roues doivent prendre pour changer de c�t�
		int angle;
		if (Track.side == -1) {
			angle = 0;
			Track.overtakingPart = 2;
		} else {
			Track.overtakingPart = 2;
			// angle des roues en fonction du rayon
			if (Track.part == 1) {
				// - arcsin(empatement / petit rayon)
				angle = -(int) (Math
						.asin(DirectionMotor.wheelBase
								/ (Track.smallRadius + Track.gradientWidth / 2))
						* 180d / Math.PI);
			} else {
				// arcsin(empatement / petit rayon)
				angle = (int) (Math
						.asin(DirectionMotor.wheelBase
								/ (Track.smallRadius + Track.gradientWidth / 2))
						* 180d / Math.PI);
			}

			angle = DirectionMotor.maxDegree / DirectionMotor.maxAngle * angle;
		}

		ultrasonicMotor.goTo(-angle, true);
		directionMotor.goTo(angle);
		previousTachoCount = tractionMotor.getTachoCount();
		tractionMotor.setSpeed(TractionMotor.currentSpeed);
	}
/*
	/**
	 * Gestion de l'ultrason pour v�rifier si l'autre c�t� de la piste est libre
	 */
/*	public void freeWay() {

		if (rob.ultrasonicMotor.isPreviousMoveComplete()) {
			// si la voie est libre (sup�rieur � la largeur de la piste - la largeur du
			// robot - la moiti� du d�grad� (suivi)
			if (rob.ultrasonic.getDistance() > Track.crossroadsLength - TractionMotor.rob.wheelSpacing
					- Track.gradientWidth / 2) {
				// si la distance restante est toujours ok
				float remainingDistance = Track.trackPartLength - rob.tractionMotor.getTachoCount();
				if (remainingDistance > Track.overtakingLength)
					Track.overtaking = true;
			} else {
				Track.ultrasonicRepositioning = true;
				rob.ultrasonicMotor.goTo(-rob.directionMotor.determineAngle(intensity), true);
			}
			Track.verifiyFreeWay = false;
			// pour emp�cher le robot de v�rifier s'il peut d�passer apr�s une
			// v�rification pendant qu'il r�acc�l�re
			rob.previousSpeed = TractionMotor.maxSpeed;
		}
	}*/
/*	
	public void isThereAnOvertaking() {
		// analyse de la vitesse pour �v. commencer un d�passsement
		// si la vitesse pr�c�dente est plus petite, c'est qu'on r�acc�l�re, donc qu'on a
		// atteint la vitesse de l'autre v�hicul

		if (previousSpeed < TractionMotor.currentSpeed && ultrasonic.getDistance() < TractionMotor.firstLimit) {
			float remainingDistance = Track.trackPartLength - tractionMotor.getTachoCount();
			if (remainingDistance > Track.overtakingLength) {
				Track.verifiyFreeWay = true;
				// si on doit tourner l'ultrason � droite
				if ((Track.part == 1 && Track.side == 1)
						|| (Track.part == -1 && Track.side == -1))
					ultrasonicMotor.goTo(UltrasonicMotor.maxDegree, false);
				// sinon � gauche
				else
					ultrasonicMotor.goTo(-UltrasonicMotor.maxDegree, false);

			}
		}
		previousSpeed = TractionMotor.currentSpeed;
	}

	*/
	/**
	 * Gestion de la priorit� de droite laisse continuer le robot seulement si aucun v�hicule
	 * devant avoir la priorit� n'est d�tect�
	 */
	private static void waitRightPriorityOk(RobotECB rob) {
		
		double distanceDetectBeforeCrossLine; // cm
		double zoneFirstAngle; //degr�s
		double zoneLastAngle; // degr�s
		
		if(Track.side == 1) {
			distanceDetectBeforeCrossLine = 30;
			zoneFirstAngle = 5;
			zoneLastAngle = 50;
		}else {
			distanceDetectBeforeCrossLine = 50;
			zoneFirstAngle = 5;
			zoneLastAngle = 40;
		}
		
		// l'ultrason se rend au d�but de son trac� de mesure
		rob.ultrasonicMotor.goTo(RobotAttributs.degresCourbureToDegresUltrason(90-zoneFirstAngle));
		rob.ultrasonicMotor.waitComplete();

		// on commence la detection
		boolean blockedTrack = true;
		int sens = 1;
		boolean vehicle = false;
		double distanceMesured;
		double distanceCalculated;

		// on r�p�te tant que la piste n'est pas libre
		while (blockedTrack) {

			if (sens == 1)
				rob.ultrasonicMotor.goTo(RobotAttributs.degresCourbureToDegresUltrason(90-zoneLastAngle));
			else
				rob.ultrasonicMotor.goTo(RobotAttributs.degresCourbureToDegresUltrason(90-zoneFirstAngle));

			while (!rob.ultrasonicMotor.isPreviousMoveComplete()) {
				distanceMesured = rob.ultrasonic.getValue();
				distanceCalculated = distanceDetectBeforeCrossLine/Math.cos((Math.toRadians(90 - RobotAttributs.degresUltrasonToDegresCourbure(rob.ultrasonicMotor.getCurrentDegres()))));

				// si on d�tecte un v�hicule
				if (distanceMesured <= distanceCalculated)
					vehicle = true;
			}
			
			// � la fin de la d�tection, on regarde si un v�hicule a �t� d�tect�
			if (vehicle) {
				vehicle = false;
				sens *= -1;
			}
			else 
				blockedTrack = false;
		}
		
		rob.ultrasonicMotor.goTo(0);
		rob.tractionMotor.move(true);
	}
}
