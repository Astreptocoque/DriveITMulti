package ch.astrepto.robot;

import lejos.hardware.Sound;

public class Track {

	// VARIABLES POUR LA SITUATION SUR LA PISTE
	public static int side; // 1 si grand, -1 si petit
	public static int part; // 1 côté avec priorité de droite, -1 côté prioritaire
	public final static float smallRadius = 15;
	public final static float largeRadius = 55;
	public final static float gradientWidth = 12;

	// VARIABLES POUR LE CARREFOUR
	public static boolean crossroads = false; // si arrivé au carrrefour
	public static boolean inCrossroads = false; // si en train de passer le carrefour
	// var permettant d'atténuer l'angle détecté juste après le carrefour et au démarrage
	public static boolean justAfterCrossroads = true;
	public static final float maxValue = 60; // blanc
	public static final float minValue = 4; // bleu foncé
	public static final float crossLineValue = 2; // ligne noire
	public final static float crossroadsLength = 60; // en cm
	public final static double littleSideLength = 3/4*2*Math.PI*(smallRadius+gradientWidth/2); //3/4 du cercle est la piste effective
	public final static double largeSideLength = 3/4*2*Math.PI*(largeRadius-gradientWidth/2);

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

}
