/* Group 2
 * Nareg Torikian:	260633071
 * Tamim Sujat:		260551583
 */
package localization;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static int ROTATION_SPEED = 50;
	private Odometer odo;
	private SampleProvider usSensor;
	private Navigation navigation;
	private float[] usData;
	private LocalizationType locType;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	
	//private SortedSet<Float> data = new TreeSet<>();
	float[] data = new float[5];
	
	
	public USLocalizer(Odometer odo,  SampleProvider usSensor, float[] usData, LocalizationType locType,
			EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, Navigation navigation) {
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.navigation = navigation;
	}
	
	public void doLocalization() {
		double angleA, angleB, angleAvg;
		
		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);
		
		//for falling edge localization, which is chosen in Lab4 class
		if (locType == LocalizationType.FALLING_EDGE) {
			
			/* if the robot starts facing a wall, turn it 180 degrees so it has passed
			 * the appointed threshold distance of 35cm, then start rising edge procedure
			 */
			if (getFilteredData() <= 35) {
				leftMotor.rotate(convertAngle(Lab4.WHEEL_RADIUS, Lab4.WIDTH, 180), true);
				rightMotor.rotate(-convertAngle(Lab4.WHEEL_RADIUS, Lab4.WIDTH, 180), false);
			
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
				if(getFilteredData() <= 35){
					Sound.beep();
					leftMotor.stop();
					rightMotor.stop();
					angleA = odo.getAng();
					break;
				}
			}
			
			
			/* stop checking for distance from wall while the robot turns 90 degrees 
			 * to ensure that the sensor does not stop rotation too early
			 */
			leftMotor.rotate(-convertAngle(Lab4.WHEEL_RADIUS, Lab4.WIDTH, 90), true);
			rightMotor.rotate(convertAngle(Lab4.WHEEL_RADIUS, Lab4.WIDTH, 90), false);

			//start normal turning clockwise
			rightMotor.forward();
			leftMotor.backward();
			
			/* continuously check for a wall while turning, if one is found, beep,
			 * then stop the robot and record the angle it is at in angleB,
			 * the second angle used for angular positioning
			 */
			while (true){
				if(getFilteredData() <= 35){
					Sound.beep();
					leftMotor.stop();
					rightMotor.stop();
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
			
			//turn robot to face along the X-axis
			navigation.turnTo(0, true);
			
		} 
		//for rising edge localization, which is chosen in Lab4 class
		else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			
			rightMotor.setSpeed(ROTATION_SPEED);
			leftMotor.setSpeed(ROTATION_SPEED);
			
			/* if the robot starts facing a wall, turn it until it has passed
			 * the appointed threshold distance of 35cm, then start falling edge procedure
			 */			
			if(getFilteredData() >= 50){
				//turn the robot clockwise
				leftMotor.forward();
				rightMotor.backward();
				
				while (true){
					if(getFilteredData() <= 35){
						leftMotor.stop();
						rightMotor.stop();
						break;
					}
				}
			}
			
			/* stop checking for distance from wall while the robot turns 45 degrees 
			 * to ensure that the sensor does not stop rotation too early
			 */
			leftMotor.rotate(convertAngle(Lab4.WHEEL_RADIUS, Lab4.WIDTH, 45), true);
			rightMotor.rotate(-convertAngle(Lab4.WHEEL_RADIUS, Lab4.WIDTH, 45), false);
			
			//start normal turning clockwise
			leftMotor.forward();
			rightMotor.backward();
			
			/* continuously check for a distance farther than the threshold of
			 * 35cm while turning, if one is found, beep, then stop the robot
			 * and record the angle it is at in angleA, the first angle used for
			 * angular positioning
			 */
			while (true){
				if(getFilteredData() > 35){
					Sound.beep();
					leftMotor.stop();
					rightMotor.stop();
					angleA = odo.getAng();
					break;
				}
			}
			
			/* Turn toward the wall a bit to ensure the sensor detects the wall
			 * properly and does not accidentally detect it is farther than the
			 * threshold
			 */
			leftMotor.rotate(-convertAngle(Lab4.WHEEL_RADIUS, Lab4.WIDTH, 45), true);
			rightMotor.rotate(convertAngle(Lab4.WHEEL_RADIUS, Lab4.WIDTH, 45), false);
			
			//start normal turning counterclockwise
			leftMotor.backward();
			rightMotor.forward();
			
			/* continuously check for a distance farther than the threshold of
			 * 35cm while turning, if one is found, beep, then stop the robot
			 * and record the angle it is at in angleB, the second angle used for
			 * angular positioning
			 */			
			while (true){
				if(getFilteredData() > 35){
					Sound.beep();
					rightMotor.stop();
					leftMotor.stop();
					angleB = odo.getAng();
					break;
				}
			}
			
			//calculations for average angle based on formulas given in slides
			if(angleA > angleB){
				angleAvg = 225 - (angleA + angleB)/2;
			}
			else{
				angleAvg =  45 - (angleA + angleB)/2;
			}
			
			odo.setPosition(new double [] {0.0, 0.0, angleA + angleAvg}, new boolean []{true, true, true});
			
			//turn robot to face along the X-axis
			navigation.turnTo(0, true);
			
		}
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
