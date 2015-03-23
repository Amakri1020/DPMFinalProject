import lejos.nxt.ColorSensor;
import lejos.nxt.Sound;
import lejos.nxt.LCD;

public class LightLocalizer {
	private static final double LSENSOR_DISTANCE = 10; //same as below
	private Odometer odo;
	private ColorSensor ls;
	private Navigation nav;
	private final int ANGLE_OFFSET = 3;	//chosen based on experimental observations
	
	public LightLocalizer(Odometer odo, ColorSensor ls) {
		this.odo = odo;
		this.ls = ls;		
		// turn on the light
		ls.setFloodlight(true);
	}
	
	public void doLocalization() {
		//begin rotating
		nav.turnTo(45);
		Robot.setSpeeds(Robot.TURN_SPEED, 0);
		while(ls.getNormalizedLightValue() > 300){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		Robot.setSpeeds(-Robot.TURN_SPEED,0);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
		}
		Robot.setSpeeds(0,Robot.TURN_SPEED);
		
		int currentLightVal = 0;
		boolean onLine = false;
		double[] angles = new double[4];
		int i = 0;
		
		//runs until 4 angles have been stored
		while (i < 4){

				currentLightVal = ls.getNormalizedLightValue();
				LCD.drawString("Light: " + currentLightVal, 0, 6);
				//while the threshold is not met
				if(currentLightVal < 300){
					onLine = true;
				} else {
					onLine = false;
				}
				
				//if line gets crossed, store angle, increment i
				if(onLine){
					//for the first 3 lines, the robot sleeps after detection to prevent re-reads. The sleep is short enough not to affect reading close lines
					if (i < 3) {
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {

						}
					}
					
					Sound.beep();
					angles[i] = odo.getTheta();
					i++;
					onLine=false;
					continue;
				}
			
		}
		
		//LCD.drawString(""+(int)angles[0]+" "+(int)angles[1]+" "+(int)angles[2]+" "+(int)angles[3], 0, 4);
		//the angle from the US is assumed to be reasonably correct, and only position is corrected.
		double dX = Math.toRadians(angles[0]-angles[2]);
		double dY = Math.toRadians(angles[1]-angles[3]);
		double dTheta = angles[3]+Math.toDegrees(dY/2)-270;//error in theta
		odo.setX(-(LSENSOR_DISTANCE*Math.cos(dY / 2.0)));
		odo.setY(-(LSENSOR_DISTANCE*Math.cos(dX / 2.0)));
		odo.setTheta(odo.getTheta() - dTheta + ANGLE_OFFSET);

		nav.travelTo(0,0);
		nav.turnTo(0);
		Robot.setSpeeds(0, 0);
	}

}