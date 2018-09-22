package ch.astrepto.robot;

public abstract class RobotAttributs {
	
	public final static float cmInDegres = 0.037699112f; // pas touche (en fct des roues)
	public final static float wheelSpacing = 9.5f;
	public final static float baseLength = 13f;
	public final static double diametreEngrenage = 1.25f;
	public final static double essieu = 1.7f;
	public final static int ultrasonicMaxDegree = 2450; // de droit � un bord
	public final static int wheelCourbureMax = 50;
	public final static int wheelMaxDegree = degresCourbureToDegresRoue(wheelCourbureMax); // de droit � un bord
	// LIMITES DE DETECTION D'UN VEHICULE
	public final static float lastLimit = 20f; // en dessous, le robot stop
	public final static float firstLimit = 30f; // pass� cette limite, le robot est � plein r�gime
	//public final static float crossingLimit = 50f; // limite de d�gagement pour le croisement � priorit�
	
	public final static float distHorizontalBetweenRobots = 18;
	public final static int maxSpeed = 300;
	public final static int maxSpeedDepassement = 200;
	public final static double angleDepassement = Math.toRadians(60);

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
