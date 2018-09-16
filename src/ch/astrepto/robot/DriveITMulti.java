package ch.astrepto.robot;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.utility.Delay;

public class DriveITMulti {

	public static void main(String[] args) {

		// à faire avant de déposer le robot sur la piste
		RobotECB rob = new RobotECB();
		System.out.println("Placer le robot sur la piste et presser ENTER\n");
		System.out.println("A gauche, sur le bleu");
		System.out.println("A droite, sur le blanc");
		Sound.twoBeeps();
		rob.touch.waitForPressAndRelease();
		Track.updateTrackInfos(rob.colorDroite.getValue());
		System.out.println("Analyse track ok, appuyez pour demarrer");
		rob.touch.waitForPressAndRelease();
		
		//rob.robotStart();
		float intensity;
		double speed;
		double time1 = 0;
		double time2 = 0;
		double time = 0;
		
		do {
			
			if (!Track.inCrossroads) {
				intensity = rob.updateLightIntensity();
				// Détection du carrefour
				if (intensity <= Track.crossLineValue + 1)
					Track.crossroads = true;
			}
			
			rob.updateDistance();

			if (!Track.inCrossroads && !Track.crossroads) 
				rob.updateDirection(true);

			speed = rob.updateSpeed();
		
			// si immobilisation de plus de 10sec				
			if(speed == 0) {
				if(time2 == 0)
					time1 = System.currentTimeMillis();
				time2 = System.currentTimeMillis();
		//		Sound.playNote(Sound.PIANO, 100, 1000);
				time = time2-time1;
				Sound.playTone((int)(100+time/8), 100, 100);
				if(time2-time1 > 5000)
					rob.depassement();
			}else {
				time2 = 0;
			}
			

			// entrée dans le croisement
			if (Track.crossroads && !Track.inCrossroads)
				rob.crossroads();

			// sortie du croisement
			if (Track.inCrossroads) 
				rob.crossroadsEnd();

		} while (!Button.UP.isDown());

		rob.robotStop();
	}
	
	
	
}