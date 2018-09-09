package ch.astrepto.robot;

public abstract class RobotAttributs {
	
	public final static float cmInDegres = 0.037699112f; // pas touche (en fct des roues)
	public final static float wheelSpacing = 9.5f;
	public final static float baseLength = 13f;
	public final static double diametreEngrenage = 1.25f;
	public final static double essieu = 1.7f;
	public final static int ultrasonicMaxDegree = 2500; // de droit à un bord
	public final static int wheelMaxDegree = 120; // de droit à un bord
	// LIMITES DE DETECTION D'UN VEHICULE
	public final static float lastLimit = 15f; // en dessous, le robot stop
	public final static float firstLimit = 40f; // passé cette limite, le robot est à plein régime
	
	public static int degresCourbureToDegresRoue(double angle) {
		
		angle = 	Math.sin(Math.toRadians((angle)))*360*essieu/(diametreEngrenage*Math.PI);
		return (int) angle;
	}
	
	public static double degresRoueToDegresCourbure(double angle) {
		
		angle = Math.toDegrees((Math.asin(((diametreEngrenage * Math.toRadians(angle)) / ( essieu * 2)))));
		return angle;
	}

	public static double degresCourbureToDegresUltrason(double angle) {
		
		angle = 90/2500*angle;
		return (int) angle;
	}
	
	public static double degresUltrasonToDegresCourbure(double angle) {
		
		angle = 0.036*angle;
		return (int) angle;
	}
}
