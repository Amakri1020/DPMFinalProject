import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

public class Robot {

	public static double[] goalArea = {90, 90};
	public static final double[] FIELD_SIZE = {240, 240};
	
	public static final double WHEEL_BASE = 12.48, US_OFFSET = 6.0;
	public static final double WHEEL_RADIUS = 2.12;
	
	public static final NXTRegulatedMotor LEFT_WHEEL = Motor.A;
	public static final NXTRegulatedMotor RIGHT_WHEEL = Motor.B;
	public static final NXTRegulatedMotor LAUNCHER = Motor.C;
	
	public static final UltrasonicSensor usSensor = new UltrasonicSensor(SensorPort.S1);
	public static final UltrasonicSensor usSensorLeft = new UltrasonicSensor(SensorPort.S3);
	public static final UltrasonicSensor usSensorRight = new UltrasonicSensor(SensorPort.S2);
	public static final ColorSensor ls = new ColorSensor(SensorPort.S4);
	
	public static double FWD_SPEED = 300, TURN_SPEED = 40;
	
	public static Odometer odo;
	public static Navigation navigator;
	public static USLocalizer usLoc;
	public static ObstacleAvoidance obAvoid;
	public static LightLocalizer lLoc;
	
	/**
	 * @param args
	 * Initializes threads for all constant autonomous functions
	 */
	public static void main(String[] args) {
		odo = new Odometer();
		odo.start();
		navigator = new Navigation(odo);
		obAvoid = new ObstacleAvoidance();
		
		
		Button.waitForAnyPress();
		usLoc = new USLocalizer(odo, usSensor, navigator);
		lLoc = new LightLocalizer(odo, ls);
		
		/*navigator.travelTo(0, 60);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {}
		navigator.travelTo(60, 0);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {}
		navigator.travelTo(60, 60);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {}
		navigator.travelTo(0, 0);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {}
		navigator.turnTo(0);
		Robot.setSpeeds(0, 0);*/
		
		//Button.waitForAnyPress();
		//Launcher launcher = new Launcher(3);
		
//		while (odo.getX() < goalArea[0] && odo.getY() < goalArea[1]){
//			debugSet("AGAIN", 0, 5, true);
//			navigator.travelTo(goalArea[0], goalArea[1]);
//		}
		//usLoc.doLocalization();
		
		// RConsole.open();
		int count = 72;
		int arc = 360/count;
		
		int[] dists = usLoc.sweepFull(count);
		int[] yx = usLoc.findLocalMinima(dists);
  		odo.setY(dists[yx[0]] + US_OFFSET-30);
		odo.setX(dists[yx[1]] + US_OFFSET-30);
		odo.setTheta(Math.toRadians((180 - yx[0]*arc)));
		navigator.travelTo(4, 4);
		navigator.turnTo(60);
		odo.setY(0);
		odo.setX(0);
		odo.setTheta(0);
		lLoc.doLocalization();
		navigator.travelTo(0,0);
		navigator.turnTo(0);
		navigator.travelTo(-Navigation.tile/2 , Navigation.tile); //Nav shoot at point test
		navigator.travelTo(-Navigation.tile/2, 5*Navigation.tile);
		navigator.turnTo(45);
		navigator.travelTo(0*Navigation.tile, 5.5*Navigation.tile);
		navigator.turnTo(90);
		navigator.travelTo(1*Navigation.tile, 5.5*Navigation.tile);
		navigator.turnTo(45);
		navigator.travelTo(2*Navigation.tile, 6.5*Navigation.tile);
		navigator.turnTo(90);
 		navigator.travelTo(4.5*Navigation.tile, 6.5*Navigation.tile);
		//Go to shooting point(DEMO target)
		// Algorithm for X and Y  position to shoot =
		//4*T + (  T*3-     (   shootingDist(cos(theta) or sin(theta)) -  T*2)   )
		//4*T = distance from shooting in x and y
		// 3*T = dimensions of shooting zone
		//T*2 distance from top right corner of shooting zone to target in X and Y 
		navigator.travelTo(145, 145);
		navigator.turnTo(45);
		

		
		Launcher launcher = new Launcher(3);
//		for (int i = 0; i < 72; i++){
//			RConsole.println(i+": "+ dists[i] + ", ");
//		}
		//RConsole.println("setting y from " + odo.getY() + "to "+ dists[yx[0]]);
		//RConsole.println("setting x from " + odo.getX() + "to "+ dists[yx[1]]);
		//RConsole.println("setting theta from " + odo.getTheta() + "to "+ ((yx[1]*arc - 90)));
		//process();
	}
	
	


	/**
	 * Contains behaviour functionality for robot
	 */
	public static void process(){
		while(true){
			Button.waitForAnyPress();
			double[][] positions = {{0, 60},{60, 60}, {60, 0}, {0, 0}};
			for (int i = 0; i < positions.length; i++){
				navigator.travelTo(positions[i][0], positions[i][1]);
				try{
					Thread.sleep(500);
				} catch (InterruptedException e){}
			}
			navigator.turnTo(0);
			Robot.setSpeeds(0, 0);
		}
	}
	
	/**
	 * @param info
	 * @param x
	 * @param y
	 * @param clearRowOnly
	 * Function to display debug information on the NXT LCD Display
	 */
	public static void debugSet(String info, int x, int y, boolean clearRowOnly){
		if (clearRowOnly)
			LCD.clear(y);
		else 
			LCD.clear();
		
		LCD.drawString(info, x, y);
	}
	
	/**
	 * @param forwardSpeed
	 * @param rotationalSpeed
	 * Sets the speed of the robot
	 */
	public static void setSpeeds(double forwardSpeed, double rotationalSpeed) {
		double leftSpeed, rightSpeed; 

		//the method has been reworked to function with degrees per seconds speeds
		leftSpeed = forwardSpeed+rotationalSpeed;
		rightSpeed = forwardSpeed-rotationalSpeed;
		
		// set motor speeds
		if (leftSpeed > 900.0)
			LEFT_WHEEL.setSpeed(900);
		else
			LEFT_WHEEL.setSpeed((int)leftSpeed);
		
		if (rightSpeed > 900.0)
			RIGHT_WHEEL.setSpeed(900);
		else
			RIGHT_WHEEL.setSpeed((int)rightSpeed);
		
		// set motor directions
		if (leftSpeed > 0.0){
			LEFT_WHEEL.forward();
		} else if (leftSpeed == 0){
			//do nothing at speed = 0
		} else {
			LEFT_WHEEL.backward();
			leftSpeed = -leftSpeed;
		}
		
		if (rightSpeed > 0.0){
			RIGHT_WHEEL.forward();
		} else if (rightSpeed == 0){
			//do nothing at speed = 0
		} else {
			RIGHT_WHEEL.backward();
			rightSpeed = -rightSpeed;
		}
	}
}
