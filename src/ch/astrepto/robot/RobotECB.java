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
import lejos.utility.Delay;

public class RobotECB {

	public DirectionMotor directionMotor;
	public TractionMotor tractionMotor;
	public UltrasonicMotor ultrasonicMotor;
	public ColorSensor colorGauche;
	public ColorSensorRemote colorDroite;
	public UltrasonicSensor ultrasonic;
	public RobotRemote coffre;

	private float intensity = 0;
	private float distance = 0;
	private double  speed = 0;
	private double currentDestination;

	public RobotECB() {

		coffre = new RobotRemote();
		colorGauche = new ColorSensor(SensorPort.S4);
		colorDroite = new  ColorSensorRemote(coffre, "S1");
		ultrasonic = new UltrasonicSensor(SensorPort.S3);
		directionMotor = new DirectionMotor(MoteursTypes.EV3MediumMotor, MotorPort.A);
		tractionMotor = new TractionMotor(MoteursTypes.EV3Motor, MotorPort.C, MotorPort.B);
		Sound.setVolume(30);
		Sound.beep();
		ultrasonicMotor = new UltrasonicMotor(MoteursTypes.NXTMotor, MotorPort.D);
		Sound.beep();
		Sound.setVolume(100);
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
	
	public double  updateSpeed() {
		double angle = RobotAttributs.degresRoueToDegresCourbure(directionMotor.getCurrentDegres());
		speed = tractionMotor.setSpeed(angle, distance);
	
		return speed;
	}

	public void depassement() {

		int directionOtherSide = Track.getPart() * Track.getSide();
		int speed = 100;
		double rayon = RobotAttributs.wheelSpacing / Math.tan(Math.toRadians(RobotAttributs.wheelCourbureMax));
		double mouvements[] = analyseDepassement(rayon);
		
		// attente que l'autre coté soit libre
		ultrasonicMotor.goTo(90 * directionOtherSide);
		ultrasonicMotor.waitComplete();
		while(ultrasonic.getValue() < 45)
			Delay.msDelay(100);
		ultrasonicMotor.goTo(0);		
		
		//1er virage
		tractionMotor.setSpeed(RobotAttributs.wheelCourbureMax*directionOtherSide*-1, speed);
		directionMotor.goTo(RobotAttributs.wheelCourbureMax*directionOtherSide);
		directionMotor.waitComplete();
		goToCentimetres(mouvements[0]);
				
		//bout droit
		tractionMotor.setSpeed(0, speed);
		directionMotor.goTo(0);
		directionMotor.waitComplete();
		goToCentimetres(mouvements[1]);
				
		//2eme virage
		tractionMotor.setSpeed(RobotAttributs.wheelCourbureMax*directionOtherSide, speed);
		directionMotor.goTo(RobotAttributs.wheelCourbureMax*directionOtherSide*-1);
		directionMotor.waitComplete();
		goToCentimetres(mouvements[2]);
		directionMotor.goTo(0);
		directionMotor.waitComplete();
	}
	
	private void goToCentimetres(double cm) {
		
		double intensity;
		double degresZero = tractionMotor.getCurrentDegres();
		double degresToDo = RobotAttributs.centimetresToDegresTraction(cm);
		tractionMotor.move(true);
		while(tractionMotor.getCurrentDegres() - degresZero < degresToDo) {
			intensity  = updateLightIntensity();
			if (intensity <= Track.crossLineValue + 1)
				tractionMotor.resetTachoCount();
		}		
		tractionMotor.move(false);
	
	}

	private double[] analyseDepassement(double rayon) {
		//longueur moyenne, car on ne sait pas à l'avance les angles exacts
		double mouvements[] = new double[3]; //les 3 distances à parcourir
		double angles[] = new double[2];
		
		double emplacement = RobotAttributs.degresTractionToCentrimetres(tractionMotor.getCurrentDegres());
		
		//virage 1
		if(emplacement < Track.crossroadsLength)
			angles[0] = 50;
		else if (Track.getSide() == 1)
			angles[0] = 70;
		else
			angles[0] = 10;
		mouvements[0] = angles[0] * rayon;
		
		//bout droit
		mouvements[1] = RobotAttributs.distHorizontalBetweenRobots;
		
		//virage 2
		double longueurVirage1 = Math.sin(angles[0])*rayon;
		double longueurBoutDroit = RobotAttributs.distHorizontalBetweenRobots/Math.sin(Math.PI/4); //pi/4 car on essaye avec le virage 1 de toujours mettre le robot à 45 degrés par rapport à la piste
		double longueurPiste;
		// nouvel emplacement
		emplacement = emplacement +  longueurVirage1 + longueurBoutDroit;
		if(Track.getSide() == 1) 
			longueurPiste = Track.crossroadsLength + Track.largeSideLength;
		else
			longueurPiste = Track.crossroadsLength + Track.littleSideLength;
		if(emplacement > longueurPiste)
			emplacement = emplacement - longueurPiste;
		if(emplacement < Track.crossroadsLength)
			angles[1] = 50;
		else if(Track.getSide() == 1)
			angles[1] = 30;
		else
			angles[1] = 80;
		mouvements[2] = angles[1] * rayon;
		
		return mouvements;
	}
	
	/**
	 * Gestion du carrefour Une fois le carrefour détecté, cette section réagit en fonction du
	 * côté du croisement
	 */
	public void crossroads() {

		tractionMotor.move(false);

		// indique qu'on est en train de passer le croisement
		Track.inCrossroads = true;
		tractionMotor.resetTachoCount();

		ultrasonicMotor.goTo(0);

		//correction
		if (Track.getPart() == 1 && Track.getSide() == 1) 
			directionMotor.goTo(-5); 
		 else if(Track.getPart() == -1 && Track.getSide() == 1)
			  directionMotor.goTo(0); 
		else 
			directionMotor.goTo(0);
		
		directionMotor.waitComplete();

		// si on est au croisement à priorité
		if (Track.part == -1)
			waitRightPriorityOk();

		tractionMotor.move(true);

	}

	/**
	 * Gestion de la détection de la fin du carrefour Détecte la fin du carrefour et maj les
	 * indications de piste
	 */
	public void crossroadsEnd() {
		// on attends de l'avoir passé pour redémarrer les fonctions de direction
		// on attends de l'avoir passé pour redémarrer les fonctions de direction
		if (tractionMotor.getCurrentDegres() >= Track.crossroadsLength / RobotAttributs.cmInDegres) {

			int intensityGauche = (int) colorGauche.getValue();
			int intensityDroite = (int) colorDroite.getValue();

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
				tractionMotor.resetTachoCount();
			}
		}
	}

	/**
	 * Gestion de la priorité de droite laisse continuer le robot seulement si aucun véhicule
	 * devant avoir la priorité n'est détecté
	 */
	private void waitRightPriorityOk() {
		
		double distanceDetectBeforeCrossLine; // cm
		double zoneFirstAngle; //degrés
		double zoneLastAngle; // degrés
		
		if(Track.side == 1) {
			distanceDetectBeforeCrossLine = 50;
			zoneFirstAngle = 20;
			zoneLastAngle = 70;
		}else {
			distanceDetectBeforeCrossLine = 60;
			zoneFirstAngle = 40;
			zoneLastAngle = 80;
		}
		
		// l'ultrason se rend au début de son tracé de mesure
		ultrasonicMotor.waitComplete();
		ultrasonicMotor.goTo(zoneFirstAngle);
		ultrasonicMotor.waitComplete();

		// on commence la detection
		boolean blockedTrack = true;
		int sens = 1;
		boolean vehicle = false;
		double distanceMesured;

		// on répète tant que la piste n'est pas libre
		double time1 = System.currentTimeMillis();
		double time2;
		
		while (blockedTrack) {

			if (sens == 1)
				ultrasonicMotor.goTo(zoneLastAngle);
			else
				ultrasonicMotor.goTo(zoneFirstAngle);

			do{
				distanceMesured = ultrasonic.getValue();
				// si on détecte un véhicule
				if (distanceMesured <= distanceDetectBeforeCrossLine) 
					vehicle = true;
				
			}while (!ultrasonicMotor.isPreviousMoveComplete());
			
			time2 = System.currentTimeMillis();
			//stop la detection après 15 seconde et on force le passage
			if (time2-time1 > 15000) 
				blockedTrack = false;
			
			// à la fin de la détection, on regarde si un véhicule a été détecté
			if (vehicle) {
				vehicle = false;
				sens *= -1;
			}
			else 
				blockedTrack = false;
		}
		
		ultrasonicMotor.goTo(0);
		ultrasonicMotor.waitComplete();
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
