package logTutorial;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

public class USLocalizer {

	public static int ROTATION_SPEED = 50;
	private Odometer odo;
	private SampleProvider usSensor;
	private Navigator nav;
	private float[] usData;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int threshDist = 25;
		
	
	public USLocalizer(Odometer odo,  SampleProvider usSensor, float[] usData, Navigator navigation) {
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		EV3LargeRegulatedMotor[] motors = this.odo.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];
		this.nav = navigation;
	}
	
	public void doLocalization() {
		double angleA, angleB, angleAvg;
		
		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);
		
			
		/* if the robot starts facing a wall, turn it 180 degrees so it has passed
		 * the appointed threshold distance of 35cm, then start rising edge procedure
		*/
		if (getFilteredData() <= threshDist) {
			leftMotor.rotate(convertAngle(Main.WHEEL_RADIUS, Main.TRACK, 180), true);
			rightMotor.rotate(-convertAngle(Main.WHEEL_RADIUS, Main.TRACK, 180), false);
			
		}
			
		//start rising edge procedure by turning robot
		leftMotor.forward();
		rightMotor.backward();
			
		/* continuously check for a wall while turning, if one is found, 
		 * beep, then stop the robot and record the angle it is at in 
		 * angleA, the first angle used for angular positioning, then 
		 * continue the procedure
		 */
		while (true) {
			if(getFilteredData() <= threshDist){
				Sound.beep();
				nav.setFloat();
				angleA = odo.getAng();
				break;
			}
		}
			
			
		/* stop checking for distance from wall while the robot turns 90 degrees 
		 * to ensure that the sensor does not stop rotation too early
		 */
		leftMotor.rotate(-convertAngle(Main.WHEEL_RADIUS, Main.TRACK, 90), true);
		rightMotor.rotate(convertAngle(Main.WHEEL_RADIUS, Main.TRACK, 90), false);

		//start normal turning clockwise
		rightMotor.forward();
		leftMotor.backward();
			
		/* continuously check for a wall while turning, if one is found, beep,
		 * then stop the robot and record the angle it is at in angleB,
		 * the second angle used for angular positioning
		 */
		while (true){
			if(getFilteredData() <= threshDist){
				Sound.beep();
				nav.setFloat();
				angleB = odo.getAng();
				break;
			}
		}
			
		//calculations for average angle based on formulas given in slides
		if (angleA > angleB){
			angleAvg = 225 - (angleA + angleB)/2;
		}
		else{
			angleAvg =  45 - (angleA + angleB)/2;
		}
			
		odo.setPosition(new double [] {0.0, 0.0, angleB + angleAvg}, new boolean []{true, true, true});
			
		//turn robot to face along the Y-axis
		nav.turnTo(0, true);
		moveToOrigin();
		
	}
	private void moveToOrigin(){
		double x, y;
		
		//turn to face perpendicular to bottom wall
		nav.turnTo(270, true);
		Sound.beep();
		y = getFilteredData();
		
		//turn to face perpendicular to left wall
		nav.turnTo(180, true);
		Sound.beep();
		x = getFilteredData();
		
		//navigate to origin
		nav.turnTo(0, true);
		Sound.beep();
		nav.goForward(30-x);
		nav.turnTo(270, true);
		Sound.beep();
		nav.goForward(30-y);
	}
	
	public float getFilteredData() {
		
		usSensor.fetchSample(usData, 0);
		float distance = 100*usData[0];
				
		return distance;
	}

	//methods originally from SquareDriver class in Lab2
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

}
