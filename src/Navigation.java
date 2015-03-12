
public class Navigation extends Thread {
	
	private Odometer odo;
	private static double ANGLE_ERROR = 1;
	private static long TURN_TIME = 20;
	private static final double COORD_ERROR = .5;

	
	public Navigation(Odometer odo) {
		this.odo = odo;
	}
	
	public void turnTo(double angle) {
		angle = Math.toDegrees(angle);
		double aPos = odo.getTheta();
		double dAngle = aPos - angle;
		if ( (dAngle > 180) || ((dAngle < 0) && (dAngle > -180))){
			Robot.setSpeeds(0, 100);
			while (Math.abs(dAngle) > ANGLE_ERROR){
				try {
					Thread.sleep(TURN_TIME);
				} catch (InterruptedException e) {
				}
				aPos = odo.getTheta();
				dAngle = aPos - angle;
			}

		} else {
			Robot.setSpeeds(0, -100);
			while (Math.abs(dAngle) > ANGLE_ERROR){
				try {
					Thread.sleep(TURN_TIME);
				} catch (InterruptedException e) {
				}
				aPos = odo.getTheta();
				dAngle = aPos - angle;
			}
		}
	}
	
	//TravelTo that corrects while traveling
	public void travelTo(double x, double y) {
		double angle;
		while (isOffTarget(x,y)) {
			angle = (Math.atan2(y - odo.getY(), x - odo.getX())) * (180.0 / Math.PI);
			if (angle < 0)
				angle += 360.0;
			if (Math.abs(odo.getTheta()-angle) > 4){
				this.turnTo(angle);
			}			

			Robot.setSpeeds(100, 0);
		}
	}
	
	
	//No correction, just turns and moves forward the distance
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
