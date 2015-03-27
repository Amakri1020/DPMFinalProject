import lejos.nxt.Button;
import lejos.nxt.UltrasonicSensor;


public class ObstacleAvoidance {

	public static Odometer odo;
	public static boolean avoiding;
	public static final double ERROR_CORR = 3;
	public static double[] currentPosition = new double[3];
	
	public ObstacleAvoidance(){
		odo = Robot.odo;
		avoiding = true;
	}
	
	public static void startAvoidance(){
		int distance;
		int distanceLeft;
		int distanceRight;
		double turnSpeed = Robot.TURN_SPEED;
		double previousAngle;
		
		avoiding = true;
		odo.getPosition(currentPosition, new boolean[] {true, true, true});
		
		previousAngle = currentPosition[2];
		
		double idealAngle = Math.toDegrees(Math.atan((Robot.goalArea[0] - currentPosition[0]) / (Robot.goalArea[1] - currentPosition[1])));
		
		Robot.debugSet("Avoidance: Mid", 0, 5, true);
		
		double leftLimit;
		double rightLimit;
		double dAngle;
		boolean aroundObstacle = false;
		
		Robot.setSpeeds(0, turnSpeed);
		
		while(avoiding){
			Robot.usSensor.ping();
			try{
				Thread.sleep(200);
			} catch (InterruptedException e){}
			distance = Robot.usSensor.getDistance();
			
			odo.getPosition(currentPosition, new boolean[] {true, true, true});
			
			idealAngle = Math.toDegrees(Math.atan((Robot.goalArea[0] - currentPosition[0]) / (Robot.goalArea[1] - currentPosition[1])));
			if (idealAngle < 0)
				idealAngle += 360;
			
			leftLimit = safeAddToAngle(idealAngle, -90);
			rightLimit = safeAddToAngle(idealAngle, 90);
			
			if(turnSpeed > 0){
				dAngle = currentPosition[2] - rightLimit;
				if (!(dAngle > 180 || (dAngle < 0 && dAngle > -180))){
					turnSpeed = -turnSpeed;
					Robot.setSpeeds(0, turnSpeed);
					try{
						Thread.sleep(1000);
					} catch(InterruptedException e){}
				}
			} else {
				dAngle = currentPosition[2] - leftLimit;
				if (!((dAngle < 180 && dAngle > 0) || dAngle < -180)){
					turnSpeed = -turnSpeed;
					Robot.setSpeeds(0, turnSpeed);
					try{
						Thread.sleep(1000);
					} catch(InterruptedException e){}
				}
			}
			
			if (distance > 60){
				aroundObstacle = avoidObstacle(turnSpeed, previousAngle);
				if(aroundObstacle)
					avoiding = false;
			}
		}
	}
	
	public static boolean avoidObstacle(double previousTurnSpeed, double previousAngle){
		Robot.setSpeeds(0, 0);
		
		try{
			Thread.sleep(100);
		} catch(InterruptedException e){}
		
		UltrasonicSensor wallSensor;
		int preDistance = 0;
		int distance = 0;
		int filterControl = 0;
		
		if(previousTurnSpeed > 0)
			wallSensor = Robot.usSensorLeft;
		else 
			wallSensor = Robot.usSensorRight;
		
		Robot.setSpeeds(Robot.FWD_SPEED, 0);
		
		if (!travelUntilClear(wallSensor, 60, 0, 12))
			return false;
		
		Robot.setSpeeds(0, 0);
		
		Robot.odo.getPosition(currentPosition, new boolean[] {true, true, true});
		if(previousTurnSpeed > 0)
			Robot.navigator.turnTo(previousAngle/*safeAddToAngle(currentPosition[2], -45)*/);
		else
			Robot.navigator.turnTo(previousAngle/*safeAddToAngle(currentPosition[2], 45)*/);
		
		Robot.setSpeeds(0, 0);
		
		try{
			Thread.sleep(500);
		} catch(InterruptedException e){}
		
		Robot.setSpeeds(Robot.FWD_SPEED, 0);
		
		if(!travelUntilClear(wallSensor, 80, 0, 5))
			return false;
		
		return true;
	}
	
	public static boolean travelUntilClear(UltrasonicSensor wallSensor, int minDistance, double closestAllowable, int filterCount){
		int preDistance = 0;
		int distance = 0;
		int filterControl = 0;
		
		while (distance < minDistance){
			wallSensor.ping();
			try{
				Thread.sleep(50);
			} catch (InterruptedException e){}
			preDistance = wallSensor.getDistance();
			
			if (preDistance >= 60 && filterControl < filterCount) {
				// bad value, do not set the distance var, however do increment the filter value
				filterControl ++;
			} else if (preDistance >= 60){
				// true 255, therefore set distance to 255
				distance = preDistance;
			} else {
				// distance went below 255, therefore reset everything.
				filterControl = 0;
				distance = preDistance;
			}
			
			if (distance < closestAllowable){
				return false;
			}
		}
		return true;
	}
	
	public static double safeAddToAngle(double angle, double add){
		angle += add;
		if (angle < 0)
			angle += 360;
		if (angle > 360)
			angle -= 360;
		return angle;
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

//if (idealAngle < 0)
//idealAngle += 360;
//
////Which way to rotate
//if (currentPosition[2] > safeAddToAngle(idealAngle, -10) && currentPosition[2] < safeAddToAngle(idealAngle, 10)) {
//Robot.usSensorLeft.ping();
//try{
//	Thread.sleep(200);
//} catch (InterruptedException e){}
//distanceLeft = Robot.usSensorLeft.getDistance();
//
//Robot.debugSet("LD: " + distanceLeft, 0, 5, true);
//if (distanceLeft > 60){
//	double turnAngle = safeAddToAngle(currentPosition[2], -60);
//	
//	Robot.navigator.turnTo(turnAngle);
//	odo.getPosition(currentPosition, new boolean[] {true, true, true});
//
//	Robot.navigator.travelTo(currentPosition[0] + 35*Math.sin(Math.toRadians(currentPosition[2])), 
//			currentPosition[1] + 35*Math.cos(currentPosition[2]));
//	
//	Robot.navigator.turnTo(previousAngle);
//	
//	odo.getPosition(currentPosition, new boolean[] {true, true, true});
//	
//	Robot.debugSet("X: " + (currentPosition[0] + 30*Math.sin(Math.toRadians(previousAngle))), 0, 5, true);
//	Robot.debugSet("Y: " + (currentPosition[1] + 30*Math.cos(Math.toRadians(previousAngle))), 0, 6, true);
//	
//	Button.waitForAnyPress();
//	
//	Robot.navigator.travelTo(currentPosition[0] + 30*Math.sin(Math.toRadians(previousAngle)), 
//			currentPosition[1] + 30*Math.cos(previousAngle));
//	
//	return;
//}
//}
//
//if (currentPosition[2] < safeAddToAngle(idealAngle, 10) && currentPosition[2] >= safeAddToAngle(idealAngle, -10)) {
//Robot.usSensorRight.ping();
//try{
//	Thread.sleep(200);
//} catch (InterruptedException e){}
//distanceRight = Robot.usSensorRight.getDistance();
//
//Robot.debugSet("RD: " + distanceRight, 0, 5, true);
//if (distanceRight > 60){
//	
//	double turnAngle = safeAddToAngle(currentPosition[2], 60);				
//	
//	Robot.navigator.turnTo(turnAngle);
//
//	odo.getPosition(currentPosition, new boolean[] {true, true, true});
//	
//	Robot.navigator.travelTo(currentPosition[0] + 35*Math.sin(Math.toRadians(currentPosition[2])), 
//			currentPosition[1] + 35*Math.cos(Math.toRadians(currentPosition[2])));
//	
//	Robot.navigator.turnTo(previousAngle);
//	
//	odo.getPosition(currentPosition, new boolean[] {true, true, true});
//
//	Robot.debugSet("X: " + (currentPosition[0] + 30*Math.sin(Math.toRadians(previousAngle))), 0, 5, true);
//	Robot.debugSet("Y: " + (currentPosition[1] + 30*Math.cos(Math.toRadians(previousAngle))), 0, 6, true);
//	
//	Button.waitForAnyPress();
//	
//	Robot.navigator.travelTo(currentPosition[0] + 30*Math.sin(Math.toRadians(previousAngle)), 
//			currentPosition[1] + 30*Math.cos(previousAngle));
//	
//	return;
//}
//}
