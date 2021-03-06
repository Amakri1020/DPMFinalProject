import lejos.nxt.ColorSensor;
import lejos.nxt.Sound;
import lejos.nxt.LCD;

public class LightLocalizer {
	private final int ANGLE_OFFSET = 8;
	
	private Odometer odo;
	private ColorSensor ls;
	
	/**
	 * @param odo
	 * @param ls
	 * Constructor Method
	 */
	public LightLocalizer(Odometer odo, ColorSensor ls) {
		this.odo = odo;
		this.ls = ls;		
		//Turn on the floodlight
		ls.setFloodlight(true);
	}
	
	/**
	 * Initiate the Light Localization Routine
	 */
	public void doLocalization() {
		//Begin rotating
		Robot.TURN_SPEED = 40;
		Robot.setSpeeds(0,Robot.TURN_SPEED);
		
		int currentLightVal = 0;
		boolean onLine = false;
		double[] angles = new double[4];
		int i = 0;
		
		//Runs until 4 angles have been stored
		while (i < 4){

				currentLightVal = ls.getNormalizedLightValue();
				LCD.drawString("Light: " + currentLightVal, 0, 6);
				//While the threshold is not met
				if(currentLightVal < Robot.LIGHT_THRESH)
					onLine = true;
				else
					onLine = false;
				//If line gets crossed, store angle, increment i
				if(onLine){
					Sound.beep();

					//For the first 3 lines, the robot sleeps after detection to prevent re-reads. 
					//The sleep is short enough not to affect reading close lines
					if (i < 3) 
						try {Thread.sleep(1000);} catch (InterruptedException e) {}
					
					angles[i] = odo.getTheta();
					i++;
					onLine=false;
					continue;
				}
		}
		
		Robot.TURN_SPEED = 80;
		
		Robot.debugSet(""+(int)angles[0]+" "+(int)angles[1]+" "+(int)angles[2]+" "+(int)angles[3], 0, 5, true);
		//The angle from the US is assumed to be reasonably correct, and only position is corrected.
		double dX = Math.toRadians(angles[0]-angles[2]);
		double dY = Math.toRadians(angles[1]-angles[3]);
		double dTheta = angles[3]+Math.toDegrees(dY/2)-270; //Error in theta
		odo.setX(-(Robot.LSENSOR_DIST*Math.cos(dY / 2.0)));
		odo.setY(-(Robot.LSENSOR_DIST*Math.cos(dX / 2.0)));
		odo.setTheta(Math.toRadians(odo.getTheta() - dTheta + ANGLE_OFFSET));

		Robot.setSpeeds(0, 0);
	}
}