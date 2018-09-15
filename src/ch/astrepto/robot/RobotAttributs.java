package ch.astrepto.robot;

public abstract class RobotAttributs {
	
	public final static float cmInDegres = 0.037699112f; // pas touche (en fct des roues)
	public final static float wheelSpacing = 9.5f;
	public final static float baseLength = 13f;
	public final static double diametreEngrenage = 1.25f;
	public final static double essieu = 1.7f;
	public final static int ultrasonicMaxDegree = 2500; // de droit à un bord
	public final static int wheelCourbureMax = 50;
	public final static int wheelMaxDegree = degresCourbureToDegresRoue(wheelCourbureMax); // de droit à un bord
	// LIMITES DE DETECTION D'UN VEHICULE
	public final static float lastLimit = 15f; // en dessous, le robot stop
	public final static float firstLimit = 30f; // passé cette limite, le robot est à plein régime
	//public final static float crossingLimit = 50f; // limite de dégagement pour le croisement à priorité
	
	public final static float distHorizontalBetweenRobots = 23;
	public final static int maxSpeed = 200;

	public static int degresCourbureToDegresRoue(double angle) {
		
		angle = 	Math.sin(Math.toRadians((angle)))*360*essieu/(diametreEngrenage*Math.PI);
		return (int) angle;
	}
	
	public static double degresRoueToDegresCourbure(double angle) {
		
		angle = Math.toDegrees((Math.asin(((diametreEngrenage * Math.toRadians(angle)) / ( essieu * 2)))));
		return angle;
	}

	public static double degresCourbureToDegresUltrason(double angle) {
		
		angle = RobotAttributs.ultrasonicMaxDegree/90*angle;
		return (int) angle;
	}
	
	public static double degresUltrasonToDegresCourbure(double angle) {
		
		angle = angle*90/RobotAttributs.ultrasonicMaxDegree;
		return (int) angle;
	}

	public static double centimetresToDegresTraction(double cm) {
		
		double degres = 360/(4.32*Math.PI)*cm;
		return degres;
	}
	
	public static double degresTractionToCentrimetres(double degres) {
	
		double cm = (4.32*Math.PI)/360*degres;
		return cm;
	}
}
