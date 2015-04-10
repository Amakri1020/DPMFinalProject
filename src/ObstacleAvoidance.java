import lejos.nxt.Button;
import lejos.nxt.UltrasonicSensor;


public class ObstacleAvoidance {

	public static final int COUNTER_TIME = 50;
	public static final double ERROR_CORR = 5;
	private static double[] currentPosition = new double[3];
	public static boolean avoiding;

	private Odometer odo;
	
	public ObstacleAvoidance(Odometer odometer){
		this.odo = odometer;
		avoiding = false;
	}
	
	public void startAvoidance(){
		avoiding = true;
		odo.getPosition(currentPosition, new boolean[] {true, true, true});
		
		int rightDistance;
		int leftDistance;
		
		Robot.usSensorRight.ping();
		try{Thread.sleep(200);}catch (InterruptedException e){}
		rightDistance = Robot.usSensorRight.getDistance();
		if (rightDistance > 60){
			avoidRight(Robot.usSensorLeft, 90);
			avoiding = false;
			return;
		}
		
		Robot.usSensorLeft.ping();
		try{Thread.sleep(200);}catch (InterruptedException e){}
		leftDistance = Robot.usSensorLeft.getDistance();
		if (leftDistance > 60){
			avoidLeft(Robot.usSensorRight, 90);
			avoiding = false;
			return;
		}
		
		odo.getPosition(currentPosition, new boolean[]{true, true, true});
		if (currentPosition[0] > currentPosition[1])
			avoidLeft(Robot.usSensorRight, 90);
		else
			avoidRight(Robot.usSensorLeft, 90);
		
		avoiding = false;
	}
	
	public boolean avoidRight(UltrasonicSensor wallSensor, double turn)
	{
		Robot.navigator.turnTo(safeAddToAngle(odo.getTheta(),turn));
		
		//Initiate PController
		PController pCont = new PController(20, 3, Robot.RIGHT_WHEEL);
		
		//Calculate the linear equation for the current trajectory so it can find the path later on
		odo.getPosition(currentPosition, new boolean[]{true, true, true});
		double slope = (Robot.goalArea[1] - currentPosition[1])/(Robot.goalArea[0] - currentPosition[0]);
		double b0 = Robot.goalArea[1] - slope*Robot.goalArea[0];
		double b = Double.MAX_VALUE; //Initial value out of range
		int counter = 0;
		int distance;
		
		//PController while loop
		while ((Math.abs(b - b0) > ERROR_CORR/*/Math.abs(Math.sin(Math.toRadians(currentPosition[2])))*/) || counter < COUNTER_TIME){
			wallSensor.ping();
			try{Thread.sleep(75);}catch (InterruptedException e){}
			distance = wallSensor.getDistance();
			pCont.processUSData(distance);
			odo.getPosition(currentPosition, new boolean[] {true, true, true});
			b = currentPosition[1] - slope*currentPosition[0];
			counter += 1;
		}
		
		Robot.setSpeeds(0, 0);
		
		return true;
	}
	
	public boolean avoidRightToWall(UltrasonicSensor wallSensor, double turn)
	{
		Robot.navigator.turnTo(safeAddToAngle(odo.getTheta(),turn));
		
		//Initiate PController
		PController pCont = new PController(20, 3, Robot.RIGHT_WHEEL);
		int distance = 40;
		
		//PController while loop
		while (distance >= 25){
			wallSensor.ping();
			try{Thread.sleep(75);}catch (InterruptedException e){}
			distance = wallSensor.getDistance();
			pCont.processUSData(distance);
			odo.getPosition(currentPosition, new boolean[] {true, true, true});
			Robot.usSensor.ping();
			try{Thread.sleep(75);}catch (InterruptedException e){}
			distance = Robot.usSensor.getDistance();
		}
		
		Robot.setSpeeds(0, 0);
		
		return true;
	}
	
	public boolean avoidLeft(UltrasonicSensor wallSensor, double turn)
	{
		Robot.navigator.turnTo(safeAddToAngle(odo.getTheta(), -turn));
		
		//Initiate PController
		PController pCont = new PController(20, 3, Robot.LEFT_WHEEL);
		
		//Calculate the linear equation for the current trajectory so it can find the path later on
		odo.getPosition(currentPosition, new boolean[]{true, true, true});
		double slope = (Robot.goalArea[1] - currentPosition[1])/(Robot.goalArea[0] - currentPosition[0]);
		double b0 = Robot.goalArea[1] - slope*Robot.goalArea[0];
		double b = Double.MAX_VALUE; //Initial value out of range
		int counter = 0;
		int distance;
		
		//PController while loop
		while ((Math.abs(b - b0) > ERROR_CORR/*/Math.abs(Math.sin(Math.toRadians(currentPosition[2])))*/) || counter < COUNTER_TIME){
			wallSensor.ping();
			try{Thread.sleep(75);}catch (InterruptedException e){}
			distance = wallSensor.getDistance();
			Robot.debugSet("Right Dist: " + distance, 0, 5, true);
			pCont.processUSData(distance);
			odo.getPosition(currentPosition, new boolean[] {true, true, true});
			b = currentPosition[1] - slope*currentPosition[0];
			counter += 1;
		}
		
		Robot.setSpeeds(0, 0);
		
		return true;
	}
	

	public boolean avoidLeftToWall(UltrasonicSensor wallSensor, double turn)
	{
		Robot.navigator.turnTo(safeAddToAngle(odo.getTheta(),-turn));
		
		//Initiate PController
		PController pCont = new PController(20, 3, Robot.RIGHT_WHEEL);
		int counter = 0;
		int distance = 40;
		
		//PController while loop
		while (distance >= 25){
			wallSensor.ping();
			try{Thread.sleep(75);}catch (InterruptedException e){}
			distance = wallSensor.getDistance();
			pCont.processUSData(distance);
			odo.getPosition(currentPosition, new boolean[] {true, true, true});
			Robot.usSensor.ping();
			try{Thread.sleep(75);}catch (InterruptedException e){}
			distance = Robot.usSensor.getDistance();
		}
		
		Robot.setSpeeds(0, 0);
		
		return true;
	}
	
	public static double safeAddToAngle(double angle, double add)
	{
		angle += add;
		if (angle < 0)
			angle += 360;
		if (angle > 360)
			angle -= 360;
		return angle;
	}
}