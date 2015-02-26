public class Launcher {
	public Launcher(int shotCount){
		Robot.LAUNCHER.setSpeed(40);
		Robot.LAUNCHER.rotate(-shotCount * 360);
	}
}