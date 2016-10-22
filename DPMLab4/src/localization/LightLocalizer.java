package localization;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;


public class LightLocalizer extends Thread{
	private Odometer odo;
	private static SampleProvider colorSensor;
	private static float[] colorData;	
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private Navigation navigator;
	//private float[] sample = new float[sampleSize];
	private int FORWARD_SPEED = 100;
	private int ROTATION_SPEED = 55;
	private int extraDist = 4;
	private double distance = 11.5;
	double x, y, angleX, angleY;
	
	public LightLocalizer(Odometer odo, SampleProvider colorSensor, float[] colorData, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, Navigation navigator) {
		this.odo = odo;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.navigator = navigator;
	}
	
	public void doLocalization() {

		//turns robot to 45 degrees, which can happen because the angle is already oriented
		navigator.turnTo(45, true);
		
		//moves robot forward 15cm to properly place sensor to check lines
		navigator.goForward(15);
		
		//turns robot 180 degrees so that it can check lines by turning clockwise
		navigator.turnTo(180, true);
		
		//instantiate array that will hold angle at each line and index of each line
		double angles[] = new double[4];
		int lineNumber = 0;
		
		//sets rotation speed
		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);
		
		//starts turning to check lines
		leftMotor.forward();
		rightMotor.backward();
		
		/* polls for data by checking lineCrossed() method at bottom of code. If a line is encountered,
		 * robot beeps and stores angle it is at in angles array at position of line (line 0, then line
		 * 1, then line 2, then line 3). Then sleeps for 500ms so that the line is not detected twice.
		 */
		while (lineNumber < 4) {
			if (lineCrossed()) {
				Sound.beep();
				angles[lineNumber] = odo.getAng();
				lineNumber +=1;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} 
			
		}
		
		//stops both motors
		leftMotor.stop();
		rightMotor.stop();
		
		//formula from the slides, angles are those taken when passing line
		angleY = (angles[3] - angles[1])/2;
		angleX = (angles[2] - angles[0])/2;
		
		/* formula came from slides, however it was determined experimentally that there was a mistake in the
		 * calculation for x and it was returned as the absolute value of what it should have been when calculated
		 * with a negative, so the formula was changed to exclude the negative
		 */
		x = (distance)*Math.cos(Math.toRadians(angleY));
		y = (-distance)*Math.cos(Math.toRadians(angleX));
		
		//turns to theta=0 to make it easier to set odometer position
		navigator.turnTo(0, true);
		
		//sets x and y knowing that theta is 0
		odo.setPosition(new double [] {x, y, 0.0}, new boolean []{true, true, true});
		
		// navigation method to move the robot to its final position based on calculation
		navigator.travelTo(0,0);
		
		//stops the robot before it moves to its final position
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// navigation method to turn the robot to its final position based on calculation
		navigator.turnTo(0, true);
		
		//set odometer to confirm that it is now at 0,0,0
		odo.setPosition(new double [] {0, 0, 0}, new boolean [] {true, true, true});
	}
	
	/* method that polls color sensor to get the data to see if it passes over a line.
	 * returns a boolean true if it does cross a line.
	 */
	private static boolean lineCrossed(){
		colorSensor.fetchSample(colorData, 0);
//		System.out.println("Sensor reading: " + colorData[0]);
		if(colorData[0] < 0.23){
			return true;
		}
		else{
			return false;
		}
	}

}
