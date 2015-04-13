import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;

public class USLocalizer {
	private static int THETA_OFFSET = 0, ANG_THRSH = 2;
	public static int clipDistance = 60;

	private Odometer odo;
	private UltrasonicSensor us;

	/**
	 * @param odo
	 * @param us
	 *            Default Constructor
	 */
	public USLocalizer(Odometer odo, UltrasonicSensor us) {
		this.odo = odo;
		this.us = us;

		// Switch off the ultrasonic sensor
		us.off();
	}

	/**
	 * @return Get distance data with a clipping filter at 60
	 */
	private int getFilteredData() {
		int distance;

		//Do a ping
		us.ping();

		//Wait for the ping to complete
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
		}

		//There will be a delay here
		distance = us.getDistance();

		if (distance > clipDistance)
			distance = clipDistance;

		return distance;
	}

	/**
	 * @param count
	 * @return Ultrasonic Localization Routine
	 */
	public int[] sweepFull(int count) {
		//Rotate until no walls
		Robot.setSpeeds(0, Robot.TURN_SPEED);
		while ((getFilteredData() < clipDistance))
			;
		try {Thread.sleep(750);} catch (InterruptedException e1) {}
		odo.setPosition(new double[] { 0, 0, 0 }, new boolean[] { true, true, true });
		Sound.beep();

		int[] distances = new int[count];
		double arc = 360 / count;
		double start, target;
		start = odo.getTheta();
		Robot.setSpeeds(0, Robot.TURN_SPEED);

		for (int i = 0; i < count; i++) {
			target = start + (i * arc);
			Robot.setSpeeds(0, Robot.TURN_SPEED);
			while (Math.abs(odo.getTheta() - target) > ANG_THRSH) {
				try {Thread.sleep(50);} catch (InterruptedException e) {}
			}
			distances[i] = getFilteredData();
		}
		Robot.setSpeeds(0, 0);
		return distances;
	}

	/**
	 * @param dists
	 * @return Finds the Local Minima of the 2 walls
	 */
	public int[] findLocalMinima(int[] dists) {
		int[] result = new int[2];
		result[0] = 0;
		result[1] = 0;
		int firstUp = 0, secondUp = 0;

		// Iterate through distances until first local minima
		for (int i = 0; i < dists.length; i++) {
			if (dists[i] == clipDistance) {
				continue;
			} else {
				if (dists[i] < dists[result[0]]
						&& (dists[i - 1] != clipDistance)
						&& (dists[i] == dists[i - 1]))
					result[0] = i;
				// Break when distance values start rising again
				if (i != 0 && dists[i] > dists[i - 1]) {
					firstUp = i - 1;
					break;
				}
			}
		}

		result[0] = ((result[0] + firstUp) / 2);
		result[1] = result[0] + 18;

		return result;
	}
}