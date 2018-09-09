package ch.astrepto.robot;

public class Track {

	// VARIABLES POUR LA SITUATION SUR LA PISTE
	public static int side; // 1 si grand, -1 si petit
	public static int part; // 1 c�t� avec priorit� de droite, -1 c�t� prioritaire
	public final static float smallRadius = 15;
	public final static float largeRadius = 55;
	public final static float gradientWidth = 12;

	// VARIABLES POUR LE CARREFOUR
	public static boolean crossroads = false; // si arriv� au carrrefour
	public static boolean inCrossroads = false; // si en train de passer le carrefour
	// var permettant d'att�nuer l'angle d�tect� juste apr�s le carrefour et au d�marrage
	public static boolean justAfterCrossroads = true;
	public static final float maxValue = 60; // blanc
	public static final float minValue = 4; // bleu fonc�
	public static final float crossLineValue = 2; // ligne noire
	
	// VARIABLES POUR LES DEPASSEMENTS
	public static boolean verifiyFreeWay = false; // true si on lance la proc�dure de v�rification
	public static boolean overtaking = false; // si en train de d�passer
	public static int overtakingPart = 0;
	public static boolean hangOnTrack = true; // si en train de suivre la piste (avec le
							// d�grad�)
	public static boolean ultrasonicRepositioning = false;
	public final static float crossroadsLength = 50; // en cm
	// longueur minimal d'un c�t� de la piste
	public final static float trackPartLength = (float) (((smallRadius + gradientWidth) * 2 * Math.PI)/RobotAttributs.cmInDegres);
	// le bout droit + le 1/4 du petit virage de la piste + une marge de 10, en degr�s
	public final static float overtakingLength = (float) ((crossroadsLength
			+ ((smallRadius + gradientWidth / 2) * 2 * Math.PI) / 4) + 10) / RobotAttributs.cmInDegres;


	public static void changeSide() {
		side *= -1;
	}

	public static void changePart() {
		part *= -1;
	}

	public static int getSide() {
		return side;
	}
	
	public static int getPart() {
		return part;
	}
	
	/**
	 * Gestion des informations de la piste Appel�e en d�but de programme, cette m�thode permet
	 * au robot de se situer sur la piste. Le robot doit �tre plac� sur le bleu ext�rieur si sur
	 * la partie 1 de la piste, sur le blanc si sur la partie -1 de la piste
	 * 
	 * @param intensity
	 *                l'intensit� lumineuse mesur�e
	 */
	public static void updateTrackInfos(float intensity) {
		// valeur 0 = partieHuit, valeur 1 = cotePiste

		// on rel�ve la couleur du sol
		if (intensity >= Track.maxValue - 15)
			// si c'est le blanc, partie -1
			Track.part = -1;
		else
			// sinon, partie 1
			Track.part = 1;

		// on commence toujours sur le grand c�t�
		Track.side = 1;
	}

}