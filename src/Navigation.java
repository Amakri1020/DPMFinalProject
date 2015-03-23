


public class Navigation extends Thread {
	
	private Odometer odo;
	private static double ANGLE_ERROR = 10;
	private static double TURNING_ANGLE_ERROR = 1;
	private static long TURN_TIME = 20;
	private static final double COORD_ERROR = 2;
	public boolean isNavigating;
	public boolean isRotating;
	
	/**
	 * @param odo
	 * Default Constructor
	 */
	public Navigation(Odometer odo) {
		this.odo = odo;
		Robot.LEFT_WHEEL.setAcceleration(300);
		Robot.RIGHT_WHEEL.setAcceleration(300);
	}
	
	/**
	 * @param angle
	 * Turns the robot by a number of degrees corresponding to the angle
	 */
	public void turnTo(double angle) {
		while(angle < 0)
			angle += 360;
		
		double aPos = odo.getTheta();
		double dAngle = aPos - angle;
		if ( (dAngle > 180) || ((dAngle < 0) && (dAngle > -180))){
			Robot.setSpeeds(0, Robot.TURN_SPEED);
			while (Math.abs(dAngle) > TURNING_ANGLE_ERROR){
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
			while (Math.abs(dAngle) > TURNING_ANGLE_ERROR){
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
		double[] currentPosition = new double[3];
		double hypotenuse;
		double xNow, yNow, thetaNow;
		double dx, dy;
		double thetaGo = 0;
		isNavigating = true;
		odo.getPosition(currentPosition, new boolean [] {true, true, true});
	    hypotenuse = Math.sqrt(Math.pow((x-currentPosition[0]),2) + Math.pow((y-currentPosition[1]), 2));
		
	    //Continue while robot is not at desired position
		while(hypotenuse >= COORD_ERROR)
		{	
			odo.getPosition(currentPosition, new boolean [] {true, true, true});
			
			xNow = currentPosition[0];
			yNow = currentPosition[1];
			hypotenuse = Math.sqrt(Math.pow(x-xNow,2) + Math.pow(y-yNow,2));
			dx = x-xNow;
			dy = y-yNow;
			thetaNow = currentPosition[2]*Math.PI/180;
			
			
			//Desired angle calculation
			if (dx == 0 && dy > 0)
				thetaGo = 0;
			else if (dx == 0 && dy < 0)
				thetaGo = Math.PI;
			else if(dx < 0 && dy > 0)  
				thetaGo = 3*Math.PI/2 - ( Math.atan(dy/dx) ); 
			else if(dx < 0 && dy < 0)
				thetaGo = 3*Math.PI/2 - (Math.atan(dy/dx));
			else 
				thetaGo = Math.PI/2 - Math.atan(dy/dx);
			
			if (thetaNow*180/Math.PI >= 360)
				thetaNow = 0;
			
			//If angle is not within reasonable limits, rotate robot
			//else move forward
			if(Math.abs(thetaGo - thetaNow) >= Math.toRadians(ANGLE_ERROR) && Math.abs(thetaGo - thetaNow) <= 2*Math.PI-Math.toRadians(ANGLE_ERROR)){
				isRotating = true;
				turnTo(Math.toDegrees(thetaGo));
				Robot.setSpeeds(0, 0);
				try{
					Thread.sleep(500);
				} catch (InterruptedException e){}
			}
			else{
				Robot.setSpeeds(Robot.FWD_SPEED, 0);
			}
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
			theta = Math.atan(dX/dY);
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
	/*private boolean isOffTarget(double x, double y){
		return (Math.abs(x - odo.getX()) > COORD_ERROR || Math.abs(y - odo.getY()) > COORD_ERROR);
	}*/

}
