import lejos.nxt.*;

public class Robot {

	public static double[] goalArea = {60, 60};
	public static final double[] FIELD_SIZE = {120, 120};
	
	public static final double WHEEL_BASE = 11.2;
	public static final double WHEEL_RADIUS = 2.15;
	
	public static final NXTRegulatedMotor LEFT_WHEEL = Motor.A;
	public static final NXTRegulatedMotor RIGHT_WHEEL = Motor.B;
	public static final NXTRegulatedMotor LAUNCHER = Motor.C;
	
	public static final UltrasonicSensor usSensor = new UltrasonicSensor(SensorPort.S1);
	
	public static double FWD_SPEED = 300, TURN_SPEED = 40;
	
	public static Odometer odo;
	public static Navigation navigator;
	
	/**
	 * @param args
	 * Initializes threads for all constant autonomous functions
	 */
	public static void main(String[] args) {
		Button.waitForAnyPress();
		
		odo = new Odometer();
		odo.start();
		
		navigator = new Navigation(odo);
				
		process();
	}
	
	
	/**
	 * Contains behaviour functionality for robot
	 */
	public static void process(){
		navigator.travelToSimple(0,75);
		while(true){}
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
