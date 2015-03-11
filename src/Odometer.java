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

	//Default constructor
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
			synchronized (lock) {
				
				//Update odometer based on tachometer count
				double distL, distR, deltaD, deltaT, dX, dY;
				
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
				
				Robot.debugSet("X: " + x, 0, 0, true);
				Robot.debugSet("Y: " + y, 0, 1, true);
			}

			//This ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {	}
			}
		}
	}
	
	//Accessor methods
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

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	//Mutator Methods
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

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}