public class Launcher {
	public Launcher(){
	}
	
	/**
	 * @param shotCount
	 * Method for shooting shotCount number of balls
	 */
	public void fire(int shotCount){
		//Launching Routine
		Robot.LAUNCHER.setSpeed(40);
		Robot.LAUNCHER.rotate(-shotCount * 360);
	}
	
	/**
	 * @param x
	 * @param y
	 * @return
	 * Calculates position and rotation for launching
	 */
	public static int[] launchPosition(double x, double y) {
		int[] result = new int[3];
		double shot_length = 5*Navigation.tile + 18;
		
		double xprime = x - 9.5*Navigation.tile;
		double yprime = y - 9.5*Navigation.tile;
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