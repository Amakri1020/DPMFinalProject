public class Launcher {
	public Launcher(){
	}
	
	public void fire(int shotCount){
		Robot.LAUNCHER.setSpeed(40);
		Robot.LAUNCHER.rotate(-shotCount * 360);
	}
	
	public static int[] launchPosition(double x, double y) {
		int[] result = new int[3];
		int shot_length = 153;
		
		double xprime = x - 10*Navigation.tile;
		double yprime = y - 10*Navigation.tile;
		double dTheta = Math.atan(xprime/yprime);
		if(y <= 10){
			dTheta += Math.PI;
		}
		result[0] = (int) (x - shot_length*Math.sin(dTheta));
		result[1] = (int) (y - shot_length*Math.cos(dTheta));
		result[2] = (int) Math.toDegrees(dTheta);
		
		return result;
	}
}