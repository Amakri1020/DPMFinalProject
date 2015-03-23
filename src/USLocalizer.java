import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static double ROTATION_SPEED = 30;

	private Odometer odo;
	private UltrasonicSensor us;
	private LocalizationType locType;
	private Navigation nav;
	private static int DETECTION_LIMIT = 55, THETA_OFFSET = 0;
	
	private int clipDistance = 70;
	
	public USLocalizer(Odometer odo, UltrasonicSensor us, LocalizationType locType) {
		this.odo = odo;
		this.us = us;
		this.locType = locType;
		
		// switch off the ultrasonic sensor
		us.off();
	}
	
	public void doLocalization() {
		double angleA, angleB;
		
		if (locType == LocalizationType.FALLING_EDGE) {
			Robot.setSpeeds(0, Robot.TURN_SPEED);
			
			//rotate until no walls
			while ((getFilteredData() < DETECTION_LIMIT));
			Sound.beep();
			
			//the sleeps effectively filter out any readings that are marginal near the wall/no wall cutoff
			try {
				Thread.sleep(300);
			} catch (InterruptedException e1) {

			}
			
			//rotate until wall, store angle
			while ((getFilteredData() > DETECTION_LIMIT));
			angleA = odo.getTheta();
			Sound.beep();
			
			//switch directions, rotate until no walls
			Robot.setSpeeds(0,-Robot.TURN_SPEED);
			while ((getFilteredData() < DETECTION_LIMIT));
			Sound.beep();
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {

			}
			
			//rotate until wall, store angle
			while((getFilteredData() > DETECTION_LIMIT));
			angleB = odo.getTheta();
			double correctionAngle = ((angleA + angleB)/2);
			Sound.beep();
			
			double dTheta = 0;
			
			//The correction angle is calculated and applied to the odometer
			if (angleA < angleB){
				dTheta = (225-correctionAngle);
			} else {
				dTheta = (45-correctionAngle);
			}
			
			odo.setTheta(odo.getTheta() + dTheta + THETA_OFFSET);
			nav.turnTo(0);

		} else {
			Robot.setSpeeds(0,Robot.TURN_SPEED);
			//ensure the robot is facing a wall
			while (getFilteredData() > DETECTION_LIMIT);
			Sound.beep();
			
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {

			}
			//rotate until wall is gone, save angle
			while (this.getFilteredData() < DETECTION_LIMIT);
			angleA = odo.getTheta();
			Sound.beep();
			//change direction
			Robot.setSpeeds(0,-Robot.TURN_SPEED);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {

			}
			//rotate until the next wall is gone
			while (this.getFilteredData() < DETECTION_LIMIT);
			Sound.beep();

			angleB = odo.getTheta();
			Sound.beep();
			
			//correct angle
			double dTheta;
			double correctionAngle = ((angleA + angleB) / 2);
			if (angleA < angleB){
				dTheta = (45-correctionAngle);
			} else {
				dTheta = (225-correctionAngle);
			}
			
			odo.setTheta(odo.getTheta() + dTheta + THETA_OFFSET);
			nav.turnTo(0);

			//set up for LS correction
			//nav.travelTo(7, 7);

		}
	}
	//the only filter used clips large values down to 50. The sleeps in the routine are used to remove areas where the readings fluctuate across the threshold
	private int getFilteredData() {
		int distance;
		
		// do a ping
		us.ping();
		
		// wait for the ping to complete
		try { Thread.sleep(50); } catch (InterruptedException e) {}
		
		// there will be a delay here
		distance = us.getDistance();
		if (distance > clipDistance){
			distance = clipDistance;
		}
		
		return distance;
	}

}