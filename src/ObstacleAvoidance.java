
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
		double turnSpeed;
		odo.getPosition(currentPosition, new boolean[] {true, true, true});
		
		//Which way to rotate
		if(currentPosition[0] > currentPosition[1])
			turnSpeed = -Robot.TURN_SPEED;
		else
			turnSpeed = Robot.TURN_SPEED;
		
		Robot.setSpeeds(0, turnSpeed);
		
		while(avoiding){
			distance = Robot.usSensor.getDistance();
			if (distance > 100){
				odo.getPosition(currentPosition, new boolean[] {true, true, true});
				if (!isWall(currentPosition, distance)){
					avoiding = false;
				} else {
					Robot.setSpeeds(0, -turnSpeed);
				}
			}
		}
		Robot.setSpeeds(Robot.FWD_SPEED, 0);
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
