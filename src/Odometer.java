/*
 * Odometer.java
 */

public class Odometer extends Thread {
	
	//Robot's position
	private double x, y, theta;
	
    public static final int SINTERVAL = 50;  /* A 20Hz sampling rate */
    public static final int SLEEPINT = 500;  /* Display update 2Hz */
    
    public static int lastTachoL;          /* Tacho L at last sample */
    public static int lastTachoR;          /* Tacho R at last sample */
    public static int nowTachoL;           /* Current tacho L */
    public static int nowTachoR;           /* Current tacho R */

	//Odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	//Lock object for mutual exclusion
	private Object lock;

	/**
	 *Default Constructor 
	 */
	public Odometer() {
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		lastTachoL = 0;
		lastTachoR = 0;
		lock = new Object();
	}

	//Run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;

		while (true) {
			updateStart = System.currentTimeMillis();
			
			//Update odometer based on tachometer count
			double distL, distR, deltaD, deltaT, dX, dY = 0;
			
			synchronized (lock) {
				//Get the Tacho count of the Motor
				nowTachoL = Robot.LEFT_WHEEL.getTachoCount();
				nowTachoR = Robot.RIGHT_WHEEL.getTachoCount();
				
				//Get Distance traveled based on the movement of the left and right wheels
				distL = Math.PI * Robot.WHEEL_RADIUS * (nowTachoL - lastTachoL) / 180;
				distR = Math.PI * Robot.WHEEL_RADIUS * (nowTachoR - lastTachoR) / 180;
				
				//Store previous Tacho Count
				lastTachoL = nowTachoL;
				lastTachoR = nowTachoR;
				
				//Distance traveled by the center of the wheel base
				deltaD = 0.5*(distL + distR);
				
				//Difference in heading from previous reading
				deltaT = (distL - distR) / Robot.WHEEL_BASE;
			
				
				//Update heading
				theta += deltaT;
				
				//Keep theta within the range of 0 - 2pi
				if (theta > 2*Math.PI){
					theta -= 2*Math.PI;
				}
				if (theta < 0){
					theta += 2*Math.PI;
				}
				
			    dX = deltaD * Math.sin(theta);
				dY = deltaD * Math.cos(theta);
				
				//Add distance traveled in X and Y to the previous values
				x = x + dX;
				y = y + dY;
			}
			
			Robot.debugSet("X: " + (int)x, 0, 0, true);
			Robot.debugSet("Y: " + (int)y, 0, 1, true);
			Robot.debugSet("T: " + (int)(theta*180/Math.PI), 0, 2, true);

			//This ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {	}
			}
		}
	}
	
	/**
	 * @param position
	 * @param update
	 * Accessor Method for robot x, y, theta data
	 */
	public void getPosition(double[] position, boolean[] update) {
		//Ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta * 180 / Math.PI;
		}
	}

	/**
	 * @return
	 * Accessor Method for robot x position
	 */
	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	/**
	 * @return
	 * Accessor Method for robot y position
	 */
	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	/**
	 * @return
	 * Accessor Method for robot heading value
	 */
	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta*180/Math.PI;
		}

		return result;
	}

	/**
	 * @param position
	 * @param update
	 * Mutator Method for robot position data
	 */
	public void setPosition(double[] position, boolean[] update) {
		//Ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	/**
	 * @param x
	 * Mutator Method for robot's x value
	 */
	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	/**
	 * @param y
	 * Mutator Method for robot's y value
	 */
	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	/**
	 * @param theta
	 * Mutator Method for robot's value
	 */
	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}