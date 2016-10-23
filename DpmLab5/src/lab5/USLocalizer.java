package lab5;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class USLocalizer {

	public static int ROTATION_SPEED = 50;
	private Odometer odo;
	private SampleProvider usSensor;
	private Navigator nav;
	private float[] usData;
	private int threshDist = 25;
		
	
	public USLocalizer(Odometer odo,  SampleProvider usSensor, float[] usData, Navigator navigation) {
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.nav = navigation;
	}
	
	public void doLocalization() {
		double angleA, angleB, angleAvg;
		
		nav.setSpeeds(ROTATION_SPEED, ROTATION_SPEED);
					
		/* if the robot starts facing a wall, turn it 180 degrees so it has passed
		 * the appointed threshold distance of 35cm, then start rising edge procedure
		*/
		if (getFilteredData() <= threshDist) {
			nav.turnAmount(180);
		}
			
		//start falling edge procedure by turning robot
		nav.rotateCW();
			
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
		nav.turnAmount(-90);

		//start normal turning Counter-clock wise
		nav.rotateCCW();
			
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
		Sound.beep();
		moveToOrigin();
		
	}
	private void moveToOrigin(){
		double x, y, dx, dy;
		nav.setSpeeds(ROTATION_SPEED, ROTATION_SPEED);
		
		//turn to face perpendicular to bottom wall
		nav.turnTo(270, true);
		Sound.beep();
		y = getFilteredData();
		
		//turn to face perpendicular to left wall
		nav.turnTo(180, true);
		Sound.beep();
		x = getFilteredData();
		
		//navigate to origin
		dx = 23-x;
		dy = 25-y;
		
		nav.turnTo(0, true);
		Sound.beep();
		nav.goForward(dx);
		nav.turnTo(90, true);
		Sound.beep();
		nav.goForward(dy);
		odo.setPosition(new double [] {0.0, 0.0, 1}, new boolean []{true, true, true});
		Sound.beep();
	}
	
	public float getFilteredData() {
		
		usSensor.fetchSample(usData, 0);
		float distance = 100*usData[0];
				
		return distance;
	}



}
