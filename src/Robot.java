import lejos.nxt.*;

public class Robot {

	public static final double WHEEL_BASE = 11.2;
	public static final double WHEEL_RADIUS = 2.15;
	public static final NXTRegulatedMotor LEFT_WHEEL = Motor.A;
	public static final NXTRegulatedMotor RIGHT_WHEEL = Motor.B;
	public static final NXTRegulatedMotor LAUNCHER = Motor.C;
	
	public static double FWD_SPEED = 300, TURN_SPEED = 100;
	
	public static Odometer odo;
	public static Navigation navigator;
	public static DriveController driver;
	public static Display disp;
	
	/**
	 * @param args
	 * Initializes threads for all constant autonomous functions
	 */
	public static void main(String[] args) {
		odo = new Odometer(); 
		odo.start();
		
		navigator = new Navigation(odo);
		
		disp = new Display(odo);
		disp.start();
		
		driver = new DriveController();
		
		//driver.straightDrive(400, 5000);
		driver.turnDrive(-400, 5000);
		
		process();
	}
	
	
	/**
	 * Contains behaviour functionality for robot
	 */
	public static void process(){
		
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
	
	public static void setAcceleration(int a){
		LEFT_WHEEL.setAcceleration(a);
		RIGHT_WHEEL.setAcceleration(a);
	}
	
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
