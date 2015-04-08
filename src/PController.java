import lejos.nxt.*;

public class PController{
	
	public final int FILTER_OUT = 5;
	public final int ANGLE_DIST = 25 ;	
	public final int FOCUS_WHEEL_DIST = 13;
	public final int AVOID_SPEED_STRAIGHT = 200;
	public final int AVOID_SPEED_HIGH = 400;
	public final int AVOID_SPEED_LOW = 100;
	public final int BAND_WIDTH = 20;
	private NXTRegulatedMotor focusMotor;
	private int distance;
	private int filterControl;
	
	public PController(int bandCenter, int bandwith, NXTRegulatedMotor focus) {
		//Default Constructor
		Robot.setSpeeds(0, 0);
		
		try {Thread.sleep(100);}catch (InterruptedException e) {}
		
		this.distance = 255;
		
		focusMotor = focus;
		
		Robot.RIGHT_WHEEL.setSpeed(AVOID_SPEED_STRAIGHT);
		Robot.LEFT_WHEEL.setSpeed(AVOID_SPEED_STRAIGHT);
		Robot.RIGHT_WHEEL.forward();
		Robot.LEFT_WHEEL.forward();
		filterControl = 0;
	}
	
	public void processUSData(int distance) {
		boolean focusMotorForward = true;
		// rudimentary filter
		if (distance >= 50 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the filter value
			filterControl++;
		} else if (distance >= 50){
			// true > 60, therefore set distance to > 60
			this.distance = distance;
			
			if (focusMotorForward == false){
				this.distance = 25;
				filterControl = 0;
			}
		} else {
			// distance went below 60, therefore reset everything.
			filterControl = 0;
			this.distance = distance;
		}

		int angleDistance = ANGLE_DIST;
		int wheelLowDistance = FOCUS_WHEEL_DIST; //Distance from the wall at which the focus wheel speed is AVOID_SPEED_STRAIGHT
		if( angleDistance * Math.cos(Math.PI/4) < wheelLowDistance ){
			angleDistance = (int)((wheelLowDistance + 1.0)/(Math.cos(Math.PI/4)));
		}
		
		//Equation for the speed of the robot (y = ax + b)
		double focusMotorSpeedSlope = (AVOID_SPEED_STRAIGHT - AVOID_SPEED_LOW)/((double)angleDistance*Math.cos(Math.PI/4) - wheelLowDistance); //a
		double yInt = AVOID_SPEED_STRAIGHT - focusMotorSpeedSlope*((double)angleDistance*Math.cos(Math.PI/4)); //b
		
		//y = ax + b
		double focusMotorSpeed = focusMotorSpeedSlope*(double)(this.distance)*Math.cos(Math.PI/4) + yInt;
		
		if (Math.abs(focusMotorSpeed - AVOID_SPEED_STRAIGHT) < BAND_WIDTH)
			focusMotorSpeed = AVOID_SPEED_STRAIGHT;
			
		
		//Upper Limit on the turning of the focus wheel
		if (focusMotorSpeed > (AVOID_SPEED_HIGH)){
			focusMotorSpeed = AVOID_SPEED_HIGH;
		}
		
		//Lower limit on the turning of the focus wheel
		if (focusMotorSpeed <= 0.0){
			focusMotorSpeed = 1.0;
		}
		
		if (distance < 8){
			focusMotor.backward();
			focusMotorForward = false;
			focusMotorSpeed = AVOID_SPEED_STRAIGHT;
		} else {
			focusMotor.forward();
			focusMotorForward = true;
		}
		focusMotor.setSpeed( (int)focusMotorSpeed );
	}
}
