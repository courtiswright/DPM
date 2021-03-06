package navigation;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter = 30;			//distance robot is to stay away from the block
	private final int bandwidth = 2;			//amount off the bandCetner the robot can be
	private final int FILTER_OUT = 30;
	private final int motorLow, motorHigh;
	private int distance;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int filterControl;
	private boolean avoid;
	static double Xstart = 0;
	static double Ystart = 0;
	static double Tstart = 0;
	private Odometer odometer;
	private boolean firstAdjust = true;			//checks if this is the original encounter of the block
	private static double OGtheta = 0.0;		//stores angle robot is traveling at before it avoids the block
	private Navigation navigation;
	private static Boolean Avoiding = true;

	/*edited constructor that now accepts odometer and navigation inputs
	this is called from the Lab3 class to check if the sensor is closer than the band Center */
	public BangBangController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
							  Odometer odometer, Navigation navigation, int motorLow, int motorHigh) {
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.odometer = odometer;
		leftMotor.setSpeed(motorHigh);				// Start robot moving forward
		rightMotor.setSpeed(motorHigh);
		leftMotor.forward();
		rightMotor.forward();
		filterControl = 0;
		this.navigation = navigation;

	}
	
	@Override
	public void processUSData(int distance) {
		this.distance = distance;
		int error = distance - bandCenter;
		
		/* checks if the robot is closer to a block than we would like
		 * if it is, boolean avoid becomes true and next loop beings
		 * as the robot tries to avoid the block
		 */
		if (distance <= bandCenter) {
			avoid = true;
			Xstart = odometer.getX();
			Ystart = odometer.getY();
			Tstart = odometer.getTheta();
		}
		
		/* only goes into this loop if the us sensor polls a distance closer
		 * than the bandCenter. This loop is starts the BangBang process to avoid the block
		 * and (tries to) kick out when the robot fully passes the block
		 */
		if (avoid) {
			double thetaEnd  = (OGtheta + 3*Math.PI/2) % (2*Math.PI);
			
			//TEST
				int degOG = (int)(OGtheta * 180/Math.PI);
				int ED = (int)(thetaEnd * 180/Math.PI);
				LCD.drawString("OG: " + degOG, 10, 3);
				LCD.drawString("ED: " + ED, 10, 5);
			//END TEST	
				
				// rudimentary filter - toss out invalid samples corresponding to null signal.		
				if (distance >= 255 && filterControl < FILTER_OUT) {
					// bad value, do not set the distance var, however do increment the filter value
					filterControl ++;
				} else if (distance == 255){
					// true 255, therefore set distance to 255
					this.distance = distance;
				} else {
					// distance went below 255, therefore reset everything.
					filterControl = 0;
					this.distance = distance;
				}
				
				//same as Lab1, moving forward
				if (Math.abs(error) <= bandwidth) {
					leftMotor.setSpeed(motorLow);
					rightMotor.setSpeed(motorLow);
			
					leftMotor.forward();
					rightMotor.forward();
				}
				
				/* the robot will travel around the block to the right, so if the robot
				 * has made it all the way around the block, the angle read by the
				 * odometer should be 90 degrees less than the angle that the robot was
				 * traveling at before it started navigating around the block. At this 
				 * point, the moveTo method in navigate will (hopefully) be called to
				 * move the robot from its current position to its original destination
				 */
				
				else if (odometer.getTheta() <= ( thetaEnd + Math.PI/36)  && odometer.getTheta() >= (  thetaEnd - Math.PI/36)  ) {	
					
					/* if the original angle at which the robot was traveling is zero
					 * taking into account error, send the robot from where it is after
					 * moving around the block, to the first point, (60,0). If the original
					 * angle at which the robot was traveling is anything other than that,
					 * send it to the second point, (0,60).
					 */
					if (Math.toDegrees(OGtheta) > -5 && Math.toDegrees(OGtheta) < 5){
						LCD.drawString("INSIDE1", 10, 2);
						navigation.travelTo(0, 60);
						Avoiding = false;
					} else {
						LCD.drawString("INSIDE2", 10, 2);
						navigation.travelTo(60, 0);
						Avoiding = false;
					}
				} 
				
				else if (error < 0) {
					
					//keeps the robot a bit closer to the block so it goes around properly
					if (distance < bandCenter-5) {
						
						/* Checks to see if this is the original encounter of the block using boolean
						 * "firstAdjust". If it is, it sets "OGtheta" to the
						 * angle the robot is currently traveling at and sets
						 * the boolean to false so that "OGtheta" is never reset.
						 */				
						if (firstAdjust) {	
							LCD.drawString("INSIDE", 10, 2);		 
							OGtheta = odometer.getTheta();			
							firstAdjust = false;					
						}	
						
						//same as Lab1, turns robot to the left to avoid the block
						leftMotor.setSpeed(motorHigh);
						rightMotor.setSpeed(0);
				
						leftMotor.forward();
						rightMotor.forward();
					}
			
				} 
				
				/* same as Lab1 except for "firstAdjust" condition. This statement
				 * turns the robot to the left to go back around to the other side
				 * of the block. The difference is that it checks to make sure that
				 * this is not the first encounter with the block as a double check
				 * to make sure that the robot will never go closer to an object
				 * that it was not trying to avoid (example: drive toward wall).
				 */
				else if (error > 0 && !firstAdjust) {
					
					
					leftMotor.setSpeed(motorLow+15);
					rightMotor.setSpeed(motorHigh);
			
					leftMotor.forward();
					rightMotor.forward();
				}
		}
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
	public static boolean isAvoiding() {
		return Avoiding;
	}
}
