import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;

public class USLocalizer {
	public static double ROTATION_SPEED = 30;

	private Odometer odo;
	private UltrasonicSensor us;
	private Navigation nav;
	private static int THETA_OFFSET = 0, ANG_THRSH = 2;
	
	public static int clipDistance = 60;
	
	public USLocalizer(Odometer odo, UltrasonicSensor us, Navigation nav) {
		this.odo = odo;
		this.us = us;
		this.nav = nav;
		
		// switch off the ultrasonic sensor 
		us.off();
	}
	
	//the only filter used clips large values down to 50. The sleeps in the routine are used to remove areas where the readings fluctuate across the threshold
	private int getFilteredData() {
		int distance;
		
		// do a ping
		us.ping();
		
		// wait for the ping to complete
		try { Thread.sleep(50); } catch (InterruptedException e) {}
		
		// there will be a delay here
		distance = us.getDistance();
		if (distance > clipDistance){
			distance = clipDistance;
		}
		
		return distance;
	}

	public int[] sweepFull(int count){
		
		//rotate until no walls
		Robot.setSpeeds(0, Robot.TURN_SPEED);
		while ((getFilteredData() < clipDistance));
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		odo.setTheta(0);
		Sound.beep();

		
		int[] distances = new int[count];
		double arc = 360/count;
		double start, target;
		start = odo.getTheta();
		Robot.setSpeeds(0,Robot.TURN_SPEED);
		//rotate until he doesnt see a wall
				
		for (int i = 0; i < count; i++){
			target = start + (i*arc);
			Robot.setSpeeds(0, Robot.TURN_SPEED);
			while (Math.abs(odo.getTheta() - target) > ANG_THRSH){
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
			}
			distances[i] = getFilteredData();
		}
		Robot.setSpeeds(0, 0);
		return distances;
	}
	
	public int[] findLocalMinima(int[] dists){
		int[] result = new int[2];
		result[0] = 0; 
		result[1] = 0;
		int firstUp=0, secondUp=0;
		
		//iterate through distances until first local min
		for(int i = 0; i < dists.length; i++){
			
			if(dists[i] == clipDistance){
				continue;
			} else {
				
				if(dists[i] < dists[result[0]] && (dists[i-1] != clipDistance) && (dists[i] == dists[i-1])){
					result[0] = i;
				}
				//break when distance values start rising again
				if(i != 0 && dists[i] > dists[i-1]){
					firstUp = i-1;
					break;
				}
			}
		}
		
		
		//RConsole.println("first indices: " + result[0] + ", " + firstUp);
		result[0] = ((result[0] + firstUp)/2);
		
		/*int marker = firstUp;
		int pass = 0;
		
		//
		while(dists[marker] >= dists[marker-1] && pass != 2){
			marker++;
			if (dists[marker] <= dists[marker-3]){
				pass++;
				RConsole.println("pass++");
			} else {
				pass = 0;
			}
		}
		
		//start from where the last loop ended
		for(int i = marker; i < dists.length; i++){
			//read until values start decreasing again (past the corner)
			
			if(dists[i] == clipDistance){
				continue;
			} else {
				if(dists[i] < dists[result[1]] && (dists[i-1] != clipDistance) && (dists[i] == dists[i-1])){
					result[1] = i;
				}
				if( i != 0 && dists[i] > dists[i-1]){
					secondUp = i-1;
					break;
				}
			}
		}
		*/
		result[1] = result[0] + 18;

		/*RConsole.println("second indices: " + result[1] + ", " + secondUp);
		result[1] = ((result[1] + secondUp)/2);
		RConsole.println("x facing wall = " + result[1]);*/
		return result;
	}
}