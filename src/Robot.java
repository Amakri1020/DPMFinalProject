import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

public class Robot {

	public static double[] goalArea = {300, 300};
	public static final double[] FIELD_SIZE = {240, 240};
	
	public static final double WHEEL_BASE = 11.8;
	public static final double US_OFFSET = 6.0;
	public static final double WHEEL_RADIUS = 2.12;
	public static final double LSENSOR_DIST = 13.5;
	public static final double FWD_SPEED = 300;
	public static double TURN_SPEED = 80;
	public static final double LIGHT_THRESH = 440;
	
	public static final double ANGLE_CORRECTION = 1.013;
	
	public static final NXTRegulatedMotor LEFT_WHEEL = Motor.A;
	public static final NXTRegulatedMotor RIGHT_WHEEL = Motor.B;
	public static final NXTRegulatedMotor LAUNCHER = Motor.C;
	
	public static final UltrasonicSensor usSensor = new UltrasonicSensor(SensorPort.S1);
	public static final UltrasonicSensor usSensorRight = new UltrasonicSensor(SensorPort.S3);
	public static final UltrasonicSensor usSensorLeft = new UltrasonicSensor(SensorPort.S2);
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
		odoCorr = new OdometryCorrection(odo);
		odoCorr.start();
		navigator = new Navigation(odo);
		obAvoid = new ObstacleAvoidance(odo);
		launcher = new Launcher(); 
		
		goalArea[0] = Navigation.tile*10;
		goalArea[1] = Navigation.tile*10;
		
		usLoc = new USLocalizer(odo, usSensor);
		lLoc = new LightLocalizer(odo, ls);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TestRoutines.mapOneBottom();
		process(1,13*Navigation.tile,13*Navigation.tile,7*Navigation.tile,14*Navigation.tile);
		//lLoc.doLocalization();
		/*navigator.turnTo(0);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TestRoutines.turnTest();
	*/}
	
	


	/**
	 * Contains behaviour functionality for final routine
	 */
	public static void process(int map, double t1x, double t1y, double t2x, double t2y){
		int count = 72;
		int arc = 360/count;
		
		int[] dists = usLoc.sweepFull(count);
		int[] yx = usLoc.findLocalMinima(dists);
  		odo.setY(dists[yx[0]] + US_OFFSET-30);
		odo.setX(dists[yx[1]] + US_OFFSET-30);
		odo.setTheta(Math.toRadians((180 - yx[0]*arc)));
		navigator.travelTo(4, 4);
		navigator.turnTo(60);
		odo.setPosition(new double[]{0, 0, 0}, new boolean[]{true, true, true});
		lLoc.doLocalization();
		
		//Button.waitForAnyPress();
		
		//odoCorr.correct = true;
		navigator.travelTo(Navigation.tile*2, Navigation.tile*2);
		navigator.travelTo(Navigation.tile*3.5, Navigation.tile*2.5);
		navigator.travelTo(goalArea[0], goalArea[1]);
		//odoCorr.correct = false;
		
		odo.setPosition(new double[]{0, 0, 0}, new boolean[]{true, true, true});
		dists = usLoc.sweepFull(count);
		yx = usLoc.findLocalMinima(dists);
  		odo.setY(dists[yx[0]] + US_OFFSET-30);
		odo.setX(dists[yx[1]] + US_OFFSET-30);
		odo.setTheta(Math.toRadians((180 - yx[0]*arc)));
		navigator.travelTo(4, 4);
		navigator.turnTo(60);
		odo.setPosition(new double[]{0, 0, 0}, new boolean[]{true, true, true});
		lLoc.doLocalization();
		
		odo.setTheta(Math.toRadians(ObstacleAvoidance.safeAddToAngle(odo.getTheta(), 180)));
		odo.setX(10*Navigation.tile - odo.getX());
		odo.setY(10*Navigation.tile - odo.getY());
		
		int[] launch = Launcher.launchPosition(t1x,t1y);
		navigator.travelTo(launch[0],launch[1]);
		navigator.turnTo(launch[2]);
		launcher.fire(3);
		
		launch = Launcher.launchPosition(t2x,t2y);
		navigator.travelTo(launch[0],launch[1]);
		navigator.turnTo(launch[2]);
		launcher.fire(3);
		
		//odoCorr.correct = true;
		navigator.travelTo(0, 0);
		//odoCorr.correct = false;
		
		odo.setPosition(new double[]{0, 0, 0}, new boolean[]{true, true, true});
		dists = usLoc.sweepFull(count);
		yx = usLoc.findLocalMinima(dists);
  		odo.setY(dists[yx[0]] + US_OFFSET-30);
		odo.setX(dists[yx[1]] + US_OFFSET-30);
		odo.setTheta(Math.toRadians((180 - yx[0]*arc)));
		navigator.travelTo(4, 4);
		navigator.turnTo(60);
		odo.setPosition(new double[]{0, 0, 0}, new boolean[]{true, true, true});
		lLoc.doLocalization();
		
		navigator.travelTo(0, 0);
		navigator.turnTo(0);
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
