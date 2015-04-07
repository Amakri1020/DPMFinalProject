import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

public class Robot {

	public static double[] goalArea = {30, 150};
	public static final double[] FIELD_SIZE = {240, 240};
	
	public static final double WHEEL_BASE = 11.4;
	public static final double US_OFFSET = 6.0;
	public static final double WHEEL_RADIUS = 2.12;
	public static final double LSENSOR_DIST = 13.5;
	public static final double FWD_SPEED = 300;
	public static final double TURN_SPEED = 40;
	public static final double LIGHT_THRESH = 440;
	
	public static final double ANGLE_CORRECTION = 1.013;
	
	public static final NXTRegulatedMotor LEFT_WHEEL = Motor.A;
	public static final NXTRegulatedMotor RIGHT_WHEEL = Motor.B;
	public static final NXTRegulatedMotor LAUNCHER = Motor.C;
	
	public static final UltrasonicSensor usSensor = new UltrasonicSensor(SensorPort.S1);
	public static final UltrasonicSensor usSensorLeft = new UltrasonicSensor(SensorPort.S3);
	public static final UltrasonicSensor usSensorRight = new UltrasonicSensor(SensorPort.S2);
	public static final ColorSensor ls = new ColorSensor(SensorPort.S4);
	
	public static Odometer odo;
	public static OdometryCorrection odoCorr;
	public static Navigation navigator;
	public static USLocalizer usLoc;
	public static ObstacleAvoidance obAvoid;
	public static LightLocalizer lLoc;
	public static Launcher launcher;
	
	/**
	 * @param args
	 * Initializes threads for all constant autonomous functions
	 */
	public static void main(String[] args) {
		
		Button.waitForAnyPress();
		
		odo = new Odometer();
		odo.start();
		//odoCorr = new OdometryCorrection(odo);
		//odoCorr.start();
		navigator = new Navigation(odo);
		obAvoid = new ObstacleAvoidance(odo);
		
		usLoc = new USLocalizer(odo, usSensor);
		lLoc = new LightLocalizer(odo, ls);
		process(1,1,1,1,1);
		
		/*navigator.travelTo(goalArea[0], goalArea[1]);
		
		while (odo.getX() < goalArea[0] - 5 && odo.getY() < goalArea[1] - 5){
			navigator.travelTo(goalArea[0], goalArea[1]);
		}
	
		Button.waitForAnyPress();
//		usLoc.doLocalization();
		
//		RConsole.open();
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
		
//		for (int i = 0; i < 72; i++){
//			RConsole.println(i+": "+ dists[i] + ", ");
//		}
//		RConsole.println("setting y from " + odo.getY() + "to "+ dists[yx[0]]);
//		RConsole.println("setting x from " + odo.getX() + "to "+ dists[yx[1]]);
//		RConsole.println("setting theta from " + odo.getTheta() + "to "+ ((yx[1]*arc - 90)));
		*/
		process(1,1,1,1,1);
	}
	
	


	/**
	 * Contains behaviour functionality for robot
	 */
	public static void process(int map, int t1x, int t1y, int t2x, int t2y){
		int count = 72;
		int arc = 360/count;
		
		int[] dists = usLoc.sweepFull(count);
		int[] yx = usLoc.findLocalMinima(dists);
  		odo.setY(dists[yx[0]] + US_OFFSET-30);
		odo.setX(dists[yx[1]] + US_OFFSET-30);
		odo.setTheta(Math.toRadians((180 - yx[0]*arc)));
		navigator.travelTo(4, 4);
		navigator.turnTo(60);
		lLoc.doLocalization();
		//navigator.travelTo(0,0);
		//navigator.turnTo(0);
		
		if(map == 1){
			navigateMapOne();
		} else if(map == 2){
			navigateMapTwo();
		} else if(map == 3){
			navigateMapThree();
		}
		
		navigator.travelTo(11*Navigation.tile, 11*Navigation.tile);
		
		dists = usLoc.sweepFull(count);
		yx = usLoc.findLocalMinima(dists);
  		odo.setY(dists[yx[0]] + US_OFFSET-30);
		odo.setX(dists[yx[1]] + US_OFFSET-30);
		odo.setTheta(Math.toRadians((180 - yx[0]*arc)));
		navigator.travelTo(4, 4);
		navigator.turnTo(60);
		lLoc.doLocalization();
		odo.setTheta(odo.getTheta() + 180);
		odo.setX(10*Navigation.tile - odo.getX());
		odo.setY(10*Navigation.tile - odo.getY());
		
		//navigator.travelTo(t1x*Navigation.tile - LAUNCH_OFFSET,t1y*Navigation.tile - LAUNCH_OFFSET);
		//launcher.fire(3);
		//navigator.travelTo(t2x*Navigation.tile - LAUNCH_OFFSET,t2y*Navigation.tile - LAUNCH_OFFSET);
		//launcher.fire(3);
		
		if(map == 1){
			navigateBackMapOne();
		} else if(map == 2){
			navigateBackMapTwo();
		} else if(map == 3){
			navigateBackMapThree();
		}
		
		dists = usLoc.sweepFull(count);
		yx = usLoc.findLocalMinima(dists);
  		odo.setY(dists[yx[0]] + US_OFFSET-30);
		odo.setX(dists[yx[1]] + US_OFFSET-30);
		odo.setTheta(Math.toRadians((180 - yx[0]*arc)));
		navigator.travelTo(4, 4);
		navigator.turnTo(60);
		lLoc.doLocalization();

	}
	
	private static void navigateBackMapThree() {
		// TODO Auto-generated method stub
		
	}




	private static void navigateBackMapTwo() {
		// TODO Auto-generated method stub
		
	}




	private static void navigateBackMapOne() {
		// TODO Auto-generated method stub
		
	}




	private static void navigateMapThree() {
		// TODO Auto-generated method stub
		
	}




	private static void navigateMapTwo() {
		// TODO Auto-generated method stub
		
	}




	private static void navigateMapOne() {
		// TODO Auto-generated method stub
		
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
