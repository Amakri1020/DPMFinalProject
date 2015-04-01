import lejos.nxt.*;

/* 
 * OdometryCorrection.java
 */

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private static final int UNSAFE_CORRECTION_ERROR = 5;
	
	private double[] currentPosition = new double[3];
	private double[] sensorPosition = new double[2]; //{x, y}
	
	private Odometer odometer;
	
	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;
		
		ColorSensor light = Robot.ls;
		light.setFloodlight(ColorSensor.Color.RED); //Turn floodlight on
		int lightColor = 1000;
		
		while (true) {
			correctionStart = System.currentTimeMillis();

			//Get current light reading
			lightColor = light.getRawLightValue();
			
			if (lightColor <= Robot.LIGHT_THRESH){
				//Beep to indicate a grid line has been crossed
				Sound.beep();
				
				//Get position of sensor based on distance from wheel base, heading, and current wheel base
				//coordinates
				odometer.getPosition(currentPosition, new boolean[] { true, true, true });
				currentPosition[2] = currentPosition[2] * Math.PI / 180;
				sensorPosition[0] = currentPosition[0] - Robot.LSENSOR_DIST*Math.sin(currentPosition[2]);
				sensorPosition[1] = currentPosition[1] - Robot.LSENSOR_DIST*Math.cos(currentPosition[2]);
				
				//Get the distance of the sensor from the nearest grid line in x
				int currX = (int) Math.round((sensorPosition[0]/Navigation.tile));
				double errorX = (sensorPosition[0] - (currX*Navigation.tile));
				
				//Get the distance of the sensor from the nearest grid line in y
				int currY = (int) Math.round((sensorPosition[1]/Navigation.tile));
				double errorY = (sensorPosition[1] - (currY*Navigation.tile));
				
				//adjust odometer readings based on the closest line to the sensor
				if((Math.abs(errorX) < UNSAFE_CORRECTION_ERROR) ^ (Math.abs(errorY) < UNSAFE_CORRECTION_ERROR)){
					if (Math.abs(errorX) < Math.abs(errorY)){
						double newX = currentPosition[0] + errorX;
						odometer.setPosition(new double[] {newX, 0, 0}, new boolean[] {true, false, false});
						//Robot.debugSet("FIRST X: " + errorX, 0, 5, true);
					} else {
						double newY = currentPosition[1] + errorY;
						odometer.setPosition(new double[] {0, newY, 0}, new boolean[] {false, true, false});
						//Robot.debugSet("FIRST Y: " + errorY, 0, 5, true);
					}
					//Robot.setSpeeds(0, 0);
					//Button.waitForAnyPress();
					//Robot.navigator.travelTo(10, 100);
				}
			}

			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
}