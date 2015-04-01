public class Launcher {
	public Launcher(){
	}
	
	public void fire(int shotCount){
		Robot.LAUNCHER.setSpeed(40);
		Robot.LAUNCHER.rotate(-shotCount * 360);
	}
}