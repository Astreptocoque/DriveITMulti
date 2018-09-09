package ch.astrepto.robot;

import ch.astrepto.robot.capteurs.UltrasonicSensor;
import lejos.hardware.Button;

public class DriveITMulti {

	public static void main(String[] args) {

		// à faire avant de déposer le robot sur la piste
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
				// Détection du carrefour (+3 pour les variations lumineuses)
				if (intensity <= Track.crossLineValue + 1)
					// Indique qu'on est arrivé au carrefour
					Track.crossroads = true;
			}

			if (!Track.inCrossroads && !Track.crossroads && !Track.overtaking && Track.hangOnTrack) {
				rob.updateDirection(true);
				rob.updateUltrasonicDirection();

			}
			// GESTION DE LA VITESSE AUTOMATIQUE
			// Est maj si pas "intialisation d'un dépassement" et si pas "vérification
			// peut dépasser")
			if (!Track.overtaking && !Track.verifiyFreeWay && !Track.ultrasonicRepositioning) {
				rob.updateSpeed();
			}

			// GESTION DE L'ARRIVEE AU CROISEMENT
			// Est maj si "arrivé au crossroads" mais pas "en train de passer le
			// crossroads"
			if (Track.crossroads && !Track.inCrossroads) {
				crossroads(rob);
			}

			// GESTION A L'INTERIEUR DU CROISEMENT
			// Est maj si "en train de passer le crossroads"
			if (Track.inCrossroads) {
				// on attends de l'avoir passé pour redémarrer les fonctions de
				// direction
				crossroadsEnd(rob);
			}

	/*		if (!Track.crossroads && !Track.verifiyFreeWay && Track.hangOnTrack
					&& !Track.ultrasonicRepositioning) {
				rob.isThereAnOvertaking();
			}

			// GESTION DE LA VERIFICATION POUR PASSER SUR L'AUTRE VOIE (VOIE LIBRE)
			// Est maj si "il faut vérifier le chemin"
			if (Track.verifiyFreeWay) {
				rob.freeWay();
			}

			// GESTION DES DEPASSEMENTS
			// Est maj si "initialisation d'un dépassement"
			if (Track.overtaking) {
				rob.overtaking();
			}

			// GESTION DE LA FIN DES DEPASSEMENTS
			// Est maj si pas "accroché à la piste"
			if (!Track.hangOnTrack) {
				rob.overtakingEnd();
			}
			*/

		} while (!Button.ESCAPE.isDown());

		rob.robotStop();

	}
	
	/**
	 * Gestion du carrefour Une fois le carrefour détecté, cette section réagit en fonction du
	 * côté du croisement
	 */
	public static void crossroads(RobotECB rob) {
		// n'est pas mis à la même condition juste en dessous pour accélérer le
		// freinage (sinon lent à cause de goTo)
		if (Track.part == -1)
			// arrête le robot
			rob.tractionMotor.move(false);

		// indique qu'on est en train de passer le croisement
		Track.inCrossroads = true;
		rob.tractionMotor.resetTachoCount();
		// les roues se remettent droites
		int angle = 0;
		rob.ultrasonicMotor.goTo(-angle);
		rob.directionMotor.goTo(angle);

		// si on est au croisement à priorité
		if (Track.part == -1) {
			// lance le balayage de priorité
			waitRightPriorityOk(rob);
			rob.ultrasonicMotor.goTo(0);
			rob.tractionMotor.move(true);
		}
	}

	/**
	 * Gestion de la détection de la fin du carrefour Détecte la fin du carrefour et maj les
	 * indications de piste
	 */
	public static void crossroadsEnd(RobotECB rob) {
		// on attends de l'avoir passé pour redémarrer les fonctions de direction
		if (rob.tractionMotor.getCurrentDegres() >= Track.crossroadsLength / RobotAttributs.cmInDegres) {
			Track.inCrossroads = false;
			Track.crossroads = false;
			Track.justAfterCrossroads = true;
			Track.changePart();
			Track.changeSide();
			rob.tractionMotor.resetTachoCount();
		}
	}

	/**
	 * Gestion de la fin du dépassement. Cette fin comprend 2 parties : la fin du virage pour
	 * rejoind l'autre côté et la fin du bout droit jusqu'à l'autre côté
	 * 
	 * @param part
	 *                partie de la fin du croisement. Vrai s'il faut fini le virage, faut s'il
	 *                faut rejoindre l'autre côté. La valeur de part est la valeur de
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
			// sécurité pour ne pas détecter le côté actuel
			if (tractionMotor.getTachoCount() >= previousTachoCount + 720) {
				if (intensity <= (ColorSensor.trackMaxValue - 10)) {
					Track.hangOnTrack = true;
					// on change les données de piste
					Track.changeSide();
				}
			}
		}
	}
*/
	/**
	 * Gestion des dépassements s'occupe de faire tourner le robot à la bonne "inclinaison" pour
	 * lui faire rejoindre l'autre côté de la piste ATTENTION : le dépassement sous-entend
	 * uniquement le virage effectué pour décrocher la piste et pouvoir ensuite rejoindre
	 * l'autre côté. Du moment que le virage est fait, la variabe "dépassement" est fausse, mais
	 * "hangOnTrack" reste fausse jusqu'à qu'on est à nouveau rejoint la piste
	 */
/*	public void overtaking() {

		Track.hangOnTrack = false;
		Track.overtaking = false;

		// règle l'angle que les roues doivent prendre pour changer de côté
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
	 * Gestion de l'ultrason pour vérifier si l'autre côté de la piste est libre
	 */
/*	public void freeWay() {

		if (rob.ultrasonicMotor.isPreviousMoveComplete()) {
			// si la voie est libre (supérieur à la largeur de la piste - la largeur du
			// robot - la moitié du dégradé (suivi)
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
			// pour empêcher le robot de vérifier s'il peut dépasser après une
			// vérification pendant qu'il réaccèlère
			rob.previousSpeed = TractionMotor.maxSpeed;
		}
	}*/
/*	
	public void isThereAnOvertaking() {
		// analyse de la vitesse pour év. commencer un dépasssement
		// si la vitesse précédente est plus petite, c'est qu'on réaccélère, donc qu'on a
		// atteint la vitesse de l'autre véhicul

		if (previousSpeed < TractionMotor.currentSpeed && ultrasonic.getDistance() < TractionMotor.firstLimit) {
			float remainingDistance = Track.trackPartLength - tractionMotor.getTachoCount();
			if (remainingDistance > Track.overtakingLength) {
				Track.verifiyFreeWay = true;
				// si on doit tourner l'ultrason à droite
				if ((Track.part == 1 && Track.side == 1)
						|| (Track.part == -1 && Track.side == -1))
					ultrasonicMotor.goTo(UltrasonicMotor.maxDegree, false);
				// sinon à gauche
				else
					ultrasonicMotor.goTo(-UltrasonicMotor.maxDegree, false);

			}
		}
		previousSpeed = TractionMotor.currentSpeed;
	}

	*/
	/**
	 * Gestion de la priorité de droite laisse continuer le robot seulement si aucun véhicule
	 * devant avoir la priorité n'est détecté
	 */
	private static void waitRightPriorityOk(RobotECB rob) {
		double startDetectionAngle;
		double endDetectionAngle;
		// si on est du grand côté
		if (Track.side == 1) {
			// ArcTan de opposé (6cm) sur adjacent (long.Piste + 8d, la
			// profondeur du capteur dans le robot. Le tout *180/pi car
			// la atan renvoi un radian
			startDetectionAngle = Math.atan(6d / (Track.crossroadsLength + 8d)) * 180d / Math.PI;
			endDetectionAngle = Math.atan(40d / 8d) * 180d / Math.PI;
		}
		// si on est du petit côté
		else {
			startDetectionAngle = Math.atan((Track.crossroadsLength - 6d) / (Track.crossroadsLength + 8d))
					* 180d / Math.PI;
			endDetectionAngle = Math.atan((Track.crossroadsLength - 6d + 40d) / 8d) * 180d / Math.PI;
		}

		// on transforme au préalable les ° du cercle en ° de l'ultrason
		startDetectionAngle = RobotAttributs.ultrasonicMaxDegree / 90 * startDetectionAngle;
		endDetectionAngle = RobotAttributs.ultrasonicMaxDegree / 90 * endDetectionAngle;

		// l'ultrason se rend au début de son tracé de mesure
		rob.ultrasonicMotor.goTo((int) startDetectionAngle);
		rob.ultrasonicMotor.waitComplete();

		// on commence la detection
		boolean blockedTrack = true;
		int sens = 1;
		float distance;
		boolean vehicle = false;

		// on répète tant que la piste n'est pas libre
		while (blockedTrack) {

			// l'ultrason boug
			if (sens == 1)
				rob.ultrasonicMotor.goTo((int) endDetectionAngle);
			else
				rob.ultrasonicMotor.goTo((int) startDetectionAngle);

			while (!rob.ultrasonicMotor.isPreviousMoveComplete()) {
				distance = rob.ultrasonic.getValue();
				// si on détecte un véhicule
				if (distance <= UltrasonicSensor.maxDetectedDistance -1)
					vehicle = true;
			}
			// à la fin de la détection, on regarde si un véhicule a été détecté
			if (vehicle) {
				vehicle = false;
				sens *= -1;
			}
			// sinon on sort de la boucle blocked track
			else {
				blockedTrack = false;
			}
		}
	}
	private static void end(RobotECB rob) {
		rob.colorDroite.close();
		rob.colorGauche.close();
		rob.ultrasonic.close();
		rob.coffre.disConnect();
	}
}
