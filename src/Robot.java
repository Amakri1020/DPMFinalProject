import lejos.nxt.*;

public class Robot {

	public static final double WHEEL_BASE = 0;
	public static final double WHEEL_RADIUS = 0;
	public static final NXTRegulatedMotor LEFT_WHEEL = Motor.A;
	public static final NXTRegulatedMotor RIGHT_WHEEL = Motor.B;
	public static final NXTRegulatedMotor LAUNCHER = Motor.C;
	public static Odometer odo;
	
	
	/**
	 * @param args
	 * Initializes threads for all constant autonomous functions
	 */
	public static void main(String[] args) {
		odo = new Odometer(); 
		odo.start();
		
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
	};
}
