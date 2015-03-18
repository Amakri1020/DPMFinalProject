
public class Navigation extends Thread {
	
	private Odometer odo;
	private static double ANGLE_ERROR = 10;
	private static long TURN_TIME = 20;
	private static final double COORD_ERROR = 1.5;

	
	/**
	 * @param odo
	 * Default Constructor
	 */
	public Navigation(Odometer odo) {
		this.odo = odo;
	}
	
	/**
	 * @param angle
	 * Turns the robot by a number of degrees corresponding to the angle
	 */
	public void turnTo(double angle) {
		double aPos = odo.getTheta();
		double dAngle = aPos - angle;
		if ( (dAngle > 180) || ((dAngle < 0) && (dAngle > -180))){
			Robot.setSpeeds(0, Robot.TURN_SPEED);
			while (Math.abs(dAngle) > ANGLE_ERROR){
				try {
					Thread.sleep(TURN_TIME);
				} catch (InterruptedException e) {
				}
				aPos = odo.getTheta();
				dAngle = aPos - angle;
				Robot.debugSet("A: " + dAngle, 0, 3, true);
			}

		} else {
			Robot.setSpeeds(0, -Robot.TURN_SPEED);
			while (Math.abs(dAngle) > ANGLE_ERROR){
				try {
					Thread.sleep(TURN_TIME);
				} catch (InterruptedException e) {
				}
				aPos = odo.getTheta();
				dAngle = aPos - angle;
				Robot.debugSet("A: " + dAngle, 0, 3, true);
			}
		}
	}
	
	
	/**
	 * @param x
	 * @param y
	 * Makes the robot travel to the given point
	 */
	public void travelTo(double x, double y) {
		double angle;
		Robot.debugSet("NOPE", 0, 3, true);
		while (isOffTarget(x,y)) {
			angle = (Math.atan2(y - odo.getY(), x - odo.getX())) * (180.0 / Math.PI);
			if (angle < 0)
				angle += 360.0;
			if (Math.abs(odo.getTheta()-angle) > ANGLE_ERROR){
				this.turnTo(angle);
				Robot.debugSet("A: " + angle, 0, 3, true);
			}

			Robot.setSpeeds(Robot.FWD_SPEED, 0);
		}
	}
	
	
	//No correction, just turns and moves forward the distance
	/**
	 * @param x
	 * @param y
	 * travelTo without correction
	 */
	public void travelToSimple(double x, double y){
		double dX = x - odo.getX();
		double dY = y - odo.getY();
		double theta;
		
		if (dY > 0){
			theta = Math.atan(dX/dY);
		} else if (dY < 0 && dX > 0){
			theta = Math.atan(dX/dY) + Math.PI;
		} else {
			theta = Math.atan(dX/dY) - Math.PI;
		}
		
		theta = Math.toDegrees(theta);
		Robot.debugSet("R: " + theta, 0, 5, true);
		
		if (theta != odo.getTheta()){
			turnTo(theta);
		}
		
		double distance = Math.sqrt(dX*dX + dY*dY);
		Robot.setSpeeds(Robot.FWD_SPEED,0);
		Robot.LEFT_WHEEL.forward();
		Robot.RIGHT_WHEEL.forward();
		Robot.LEFT_WHEEL.rotate(convertDistance(Robot.WHEEL_RADIUS, distance),true);
		Robot.RIGHT_WHEEL.rotate(convertDistance(Robot.WHEEL_RADIUS, distance),false);
	}
	
	private static int convertDistance(double radius, double distance){
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	private boolean isOffTarget(double x, double y){
		return (Math.abs(x - odo.getX()) > COORD_ERROR || Math.abs(y - odo.getY()) > COORD_ERROR);
	}

}
