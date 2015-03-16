
public class DriveController extends Thread {
	public void straightDrive(int speed, int time){
		Robot.setSpeeds(speed, 0);
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
		Robot.setSpeeds(0, 0);
	}
	
	public void floatMotors(){
		Robot.LEFT_WHEEL.flt();
		Robot.RIGHT_WHEEL.flt();
	}
	
	public void turnDrive(int speed, int time){
		Robot.setSpeeds(0, speed);
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
		Robot.setSpeeds(0,0);
	}
	
	public void arcDrive(int forward, int turn, int time){
		Robot.setSpeeds(forward, turn);
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
		Robot.setSpeeds(0, 0);
	}
	
	public void driveToPoint(double x, double y){
		Robot.navigator.travelTo(x, y);
	}
	
	public void driveArrayPoints(double[][] P, int n){
		for (int i = 0; i < n; i++){
			driveToPoint(P[i][0], P[i][1]);
		}
	}

}
