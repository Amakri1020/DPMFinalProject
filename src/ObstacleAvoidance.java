import lejos.nxt.Button;


public class ObstacleAvoidance {

	public static Odometer odo;
	public static boolean avoiding;
	public static final double ERROR_CORR = 3;
	
	public ObstacleAvoidance(){
		odo = Robot.odo;
		avoiding = true;
	}
	
	public static void startAvoidance(){
		double[] currentPosition = new double[3];
		int distance;
		int distanceLeft;
		int distanceRight;
		double turnSpeed;
		boolean quickTurn = false;
		odo.getPosition(currentPosition, new boolean[] {true, true, true});
		
		//Which way to rotate
		if (currentPosition[0] > currentPosition[1]) {
			distanceLeft = Robot.usSensorLeft.getDistance();
			if (distanceLeft > 60){
				Robot.navigator.turnTo(currentPosition[2] - 45);
				Button.waitForAnyPress();
				odo.getPosition(currentPosition, new boolean[] {true, true, true});
				Robot.navigator.travelTo(currentPosition[0] + 30*Math.sin(Math.toRadians(currentPosition[2])), 
						currentPosition[1] + 30*Math.cos(currentPosition[2]));
				return;
			}
			turnSpeed = -Robot.TURN_SPEED;
		} else {
			distanceRight = Robot.usSensorRight.getDistance();
			if (distanceRight > 60){
				Robot.navigator.turnTo(currentPosition[2] - 45);
				Button.waitForAnyPress();
				odo.getPosition(currentPosition, new boolean[] {true, true, true});
				Robot.navigator.travelTo(currentPosition[0] + 30*Math.sin(Math.toRadians(currentPosition[2])), 
						currentPosition[1] + 30*Math.cos(Math.toRadians(currentPosition[2])));
				return;
			}
			turnSpeed = Robot.TURN_SPEED;
		}
		
		Robot.setSpeeds(0, turnSpeed);
		
		while(avoiding){
			distance = Robot.usSensor.getDistance();
			if (distance > 60){
				odo.getPosition(currentPosition, new boolean[] {true, true, true});
				if (!isWall(currentPosition, distance)){
					avoiding = false;
				} else {
					Robot.setSpeeds(0, -turnSpeed);
				}
			}
		}
		
		odo.getPosition(currentPosition, new boolean[] {true, true, true});
		Robot.navigator.travelTo(currentPosition[0] + 30*Math.sin(Math.toRadians(currentPosition[2])),
				currentPosition[1] + 30*Math.cos(Math.toRadians(currentPosition[2])));
		return;
	}
	
	public static boolean isWall(double[] currentPosition, int distance){
		boolean wall = false;
		
		if (currentPosition[2] < 180){
			if (distance + ERROR_CORR <= Math.abs(currentPosition[0] / Math.sin(Math.toRadians(currentPosition[2]))))
				wall = true;
		} else {
			if (distance + ERROR_CORR <= Math.abs((Robot.FIELD_SIZE[0] - currentPosition[0]) / Math.sin(Math.toRadians(currentPosition[2]))))
				wall = true;
		}
		
		if (currentPosition[2] < 90 || currentPosition[2] > 270){
			if (distance + ERROR_CORR <= Math.abs((Robot.FIELD_SIZE[1] - currentPosition[1]) / Math.cos(Math.toRadians(currentPosition[2]))))
				wall = true;
		} else {
			if (distance + ERROR_CORR <= Math.abs(currentPosition[1] / Math.cos(Math.toRadians(currentPosition[2]))))
				wall = true;
		}
		
		return wall;
	}
}
