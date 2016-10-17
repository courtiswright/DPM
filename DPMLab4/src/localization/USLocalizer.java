package localization;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static int ROTATION_SPEED = 30;

	private Odometer odo;
	private SampleProvider usSensor;
	private float[] usData;
	private LocalizationType locType;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private Navigation navigator;
	
	public USLocalizer(Odometer odo,  SampleProvider usSensor, float[] usData, LocalizationType locType, 
			EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, Navigation navigator) {
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
		this.rightMotor = rightMotor;
		this.leftMotor = leftMotor;
		this.navigator = navigator;
	}
	
	public void doLocalization() {
		double [] pos = new double [3];
		double angleA, angleB, angleAvg;
		
		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);
		
		if (locType == LocalizationType.FALLING_EDGE) {
			// rotate the robot until it sees no wall
			while(true) {
				if(getFilteredData() <= 30) {
					rightMotor.forward();
					leftMotor.backward();
//					System.out.println("Start: " + odo.getAng());
//					System.out.println("<=30 " + getFilteredData());
				} else {
					rightMotor.stop();
					leftMotor.stop();
//					System.out.println("After turn at start: " + odo.getAng());
					break;
				}
			}
			
			// keep rotating until the robot sees a wall, then latch the angle
			rightMotor.forward();
			leftMotor.backward();
			
			while (true) {
				if(getFilteredData() <= 30) {
					rightMotor.stop();
					leftMotor.stop();
					angleA = odo.getAng();
//					System.out.println("angleA: " + angleA);
					break;
				}
			}
			
			// switch direction and wait until it sees no wall
			leftMotor.forward();
			rightMotor.backward();
			
			while (true) {
				if (getFilteredData() > 30) {
					break;
				}
			}
			
			// keep rotating until the robot sees a wall, then latch the angle
			while (true) {
				if (getFilteredData() <= 30) {
					rightMotor.stop();
					leftMotor.stop();
					angleB = odo.getAng();
//					System.out.println("angleB: " + angleB);
					break;
				}
			}

			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			if (angleA > angleB) {
				angleAvg = 45-((angleA+angleB)/2);
			} else {
				angleAvg = 225-((angleA+angleB)/2);
			}
//			System.out.println("angleAvg: " + angleAvg);
			
			// update the odometer position (example to follow:)
			
			odo.setPosition(new double [] {0.0, 0.0, angleB+angleAvg}, new boolean [] {true, true, true});
			navigator.turnTo(0, true);
		} else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			
			while(true) {
				if(getFilteredData() > 30) {
					rightMotor.forward();
					leftMotor.backward();
				} else {
					rightMotor.stop();
					leftMotor.stop();
					break;
				}
			}
			
			rightMotor.forward();
			leftMotor.backward();
			
			while (true) {
				if(getFilteredData() > 30) {
					rightMotor.stop();
					leftMotor.stop();
					angleA = odo.getAng();
					break;
				}
			}
			
			leftMotor.forward();
			rightMotor.backward();
			
			while (true) {
				if (getFilteredData() <= 30) {
					break;
				}
			}
			
			while (true) {
				if (getFilteredData() > 30) {
					rightMotor.stop();
					leftMotor.stop();
					angleB = odo.getAng();
					break;
				}
			}
			
			if (angleA <= angleB) {
				angleAvg = 45-((angleA+angleB)/2);
			} else {
				angleAvg = 225-((angleA+angleB)/2);
			}
			
			odo.setPosition(new double [] {0.0, 0.0, angleA+angleAvg}, new boolean [] {true, true, true});
			navigator.turnTo(0, true);
			
		}
	}
	
	private float getFilteredData() {
		usSensor.fetchSample(usData, 0);
		float distance = 100*usData[0];
				
		return distance;
	}

}
