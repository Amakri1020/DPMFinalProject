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
		
		double idealAngle = Math.atan((Robot.goalArea[0] - currentPosition[0]) / (Robot.goalArea[1] - currentPosition[1]));
		
		//Which way to rotate
		if (currentPosition[2] > idealAngle && currentPosition[2] <= idealAngle) {
			Robot.usSensorLeft.ping();
			try{
				Thread.sleep(200);
			} catch (InterruptedException e){}
			distanceLeft = Robot.usSensorLeft.getDistance();
			
			Robot.debugSet("LD: " + distanceLeft, 0, 5, true);
			Button.waitForAnyPress();
			if (distanceLeft > 60){
				
				double turnAngle = currentPosition[2] - 45;
				if (turnAngle < 0)
					turnAngle += 360;
				
				Robot.navigator.turnTo(turnAngle);
				odo.getPosition(currentPosition, new boolean[] {true, true, true});
				
				Robot.navigator.travelTo(currentPosition[0] + 30*Math.sin(Math.toRadians(currentPosition[2])), 
						currentPosition[1] + 30*Math.cos(currentPosition[2]));
				
				odo.getPosition(currentPosition, new boolean[] {true, true, true});
				
				idealAngle = Math.atan((Robot.goalArea[0] - currentPosition[0]) / (Robot.goalArea[1] - currentPosition[1]));
				Robot.navigator.turnTo(idealAngle);
				
				return;
			}
			turnSpeed = -Robot.TURN_SPEED;
		} else {
			Robot.usSensorRight.ping();
			try{
				Thread.sleep(200);
			} catch (InterruptedException e){}
			distanceRight = Robot.usSensorRight.getDistance();
			
			Robot.debugSet("RD: " + distanceRight, 0, 5, true);
			Button.waitForAnyPress();
			if (distanceRight > 60){
				
				double turnAngle = currentPosition[2] + 45;
				if (turnAngle > 360)
					turnAngle -= 360;
				
				Robot.navigator.turnTo(currentPosition[2] - 45);
				Button.waitForAnyPress();
				odo.getPosition(currentPosition, new boolean[] {true, true, true});
				
				Robot.navigator.travelTo(currentPosition[0] + 30*Math.sin(Math.toRadians(currentPosition[2])), 
						currentPosition[1] + 30*Math.cos(Math.toRadians(currentPosition[2])));
				
				odo.getPosition(currentPosition, new boolean[] {true, true, true});
				
				idealAngle = Math.atan((Robot.goalArea[0] - currentPosition[0]) / (Robot.goalArea[1] - currentPosition[1]));
				Robot.navigator.turnTo(idealAngle);
				
				return;
			}
			turnSpeed = Robot.TURN_SPEED;
		}
		
		Robot.debugSet("OUTSIDE", 0, 5, true);
		
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
