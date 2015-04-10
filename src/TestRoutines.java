import lejos.nxt.Button;

public class TestRoutines {
	
	public static void turnTest(){
		Robot.navigator.turnTo(90);
		try{
			Thread.sleep(100);
		} catch(InterruptedException e){}
		Robot.navigator.turnTo(180);
		try{
			Thread.sleep(100);
		} catch(InterruptedException e){}
		Robot.navigator.turnTo(270);
		try{
			Thread.sleep(100);
		} catch(InterruptedException e){}
		Robot.navigator.turnTo(0);
		try{
			Thread.sleep(100);
		} catch(InterruptedException e){}
		
		Button.waitForAnyPress();
	}
	
	public static void travelTest(){
		Robot.navigator.travelTo(0, 60);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {}
		Robot.navigator.travelTo(60, 0);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {}
		Robot.navigator.travelTo(60, 60);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {}
		Robot.navigator.travelTo(0, 0);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {}
		Robot.navigator.turnTo(0);
		
		Button.waitForAnyPress();
	}
	
	public static void betaDemoRoutine(){
		Robot.navigator.travelTo(-Navigation.tile/2 , Navigation.tile); //Nav shoot at point test
		Robot.navigator.travelTo(-Navigation.tile/2, 5.5*Navigation.tile);
		Robot.navigator.turnTo(90);
		Robot.navigator.travelTo(1.5*Navigation.tile, 5.5*Navigation.tile);
		Robot.navigator.turnTo(0);
		Robot.navigator.travelTo(1.5*Navigation.tile, 6.5*Navigation.tile);
		Robot.navigator.turnTo(90);
		Robot.navigator.travelTo(5*Navigation.tile, 6.5*Navigation.tile);
		
		//Go to shooting point(DEMO target)
		// Algorithm for X and Y  position to shoot =
		//4*T + (  T*3-     (   shootingDist(cos(theta) or sin(theta)) -  T*2)   )
		//4*T = distance from shooting in x and y
		// 3*T = dimensions of shooting zone
		//T*2 distance from top right corner of shooting zone to target in X and Y 
		
		Robot.navigator.travelTo(145, 145);
		Robot.navigator.turnTo(45);

		Launcher launcher = new Launcher();
		launcher.fire(3);
	}
	public static void launchDistanceTest(){
		for (int i = 0; i <6; i++){
			Robot.launcher.fire(1);
			Button.waitForAnyPress();
		}
	}
	
	public static void launchTest(int t1x, int t1y){
		Robot.odo.setX(10*Navigation.tile);
		Robot.odo.setY(10*Navigation.tile);
		
		int[] launch = Launcher.launchPosition(t1x,t1y);
		Robot.navigator.travelTo(launch[0],launch[1]);
		Robot.navigator.turnTo(launch[2]);
		Robot.launcher.fire(3);
	}
	
	public static void mapThreeBottom(){
		Robot.navigator.travelToBlind(3*Navigation.tile, -.5*Navigation.tile);
		Robot.navigator.travelToBlind(4*Navigation.tile, -.5*Navigation.tile);
		Robot.navigator.travelToBlind(6*Navigation.tile, 1.5*Navigation.tile);
		Robot.navigator.travelToBlind(7.5*Navigation.tile, 1.5*Navigation.tile);
		Robot.navigator.travelToBlind(7.5*Navigation.tile, -.5*Navigation.tile);
		Robot.navigator.travelToBlind(9*Navigation.tile, -.5*Navigation.tile);
		Robot.navigator.travelToBlind(10.5*Navigation.tile, 1*Navigation.tile);
		Robot.navigator.travelToBlind(10.5*Navigation.tile, 2.5*Navigation.tile);
	}
	public static void mapTwoBottom(){
		Robot.navigator.travelToBlind(4*Navigation.tile, 2.5*Navigation.tile);
		Robot.navigator.travelToBlind(5*Navigation.tile, 2.5*Navigation.tile);
		Robot.navigator.travelToBlind(7*Navigation.tile, 1.5*Navigation.tile);
		Robot.navigator.travelToBlind(9*Navigation.tile, -.5*Navigation.tile);
		Robot.navigator.travelToBlind(10.5*Navigation.tile, -.5*Navigation.tile);
		Robot.navigator.travelToBlind(10.5*Navigation.tile, 2.5*Navigation.tile);
	}
	public static void mapOneBottom(){
		Robot.navigator.travelToBlind(3*Navigation.tile, 1.5*Navigation.tile);
		Robot.navigator.travelToBlind(4*Navigation.tile, 1.5*Navigation.tile);
		Robot.navigator.travelToBlind(6*Navigation.tile, -.5*Navigation.tile);
		Robot.navigator.travelToBlind(7*Navigation.tile, -.5*Navigation.tile);
		Robot.navigator.travelToBlind(10*Navigation.tile, 2.5*Navigation.tile);
		Robot.navigator.travelToBlind(10.5*Navigation.tile, 2.5*Navigation.tile);
	}
}
