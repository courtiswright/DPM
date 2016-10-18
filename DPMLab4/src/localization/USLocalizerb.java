//package lab4Localization;
//
//
//import java.util.SortedSet;
//import java.util.TreeSet;
//
//import lejos.hardware.Sound;
//import lejos.hardware.ev3.LocalEV3;
//import lejos.hardware.motor.EV3LargeRegulatedMotor;
//import lejos.robotics.SampleProvider;
//
//public class USLocalizerb{
//	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
//	public static double ROTATION_SPEED = 30;
//	//private static float ROTATE_SPEED = 150;
//	
//	
//	private EV3LargeRegulatedMotor leftMotor;
//	private EV3LargeRegulatedMotor rightMotor;
//	
//	//Minimal distance to consider the wall as being detected
//	private static int WALL_DETECT = 30;
//	private SortedSet<Float> data = new TreeSet<>();
//	//Max distance that we consider as out of range
//	private static int MAX_DISTANCE = 50;
//	
//	
//
//	private Odometer odo;
//	private SampleProvider usSensor;
//	private float[] usData;
//	private LocalizationType locType;
//	private int filterControl;
//	
//	
//	
//	
//	public USLocalizerb(Odometer odo,  SampleProvider usSensor, float[] usData, LocalizationType locType, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) {
//		this.odo = odo;
//		this.usSensor = usSensor;
//		this.usData = usData;
//		this.locType = locType;
//		this.leftMotor = leftMotor;
//		this.rightMotor = rightMotor;
//	}
//	
//	public void doLocalization() {
//		double [] pos = new double [3];
//		
//		//values for storing the first angle of interest and the second
//		double angleA, angleB;
//		float hundred = 100;
//		//get 5 readings into a sorted set. 
//		for(int i = 0; i < 5; i++){
//			data.add(hundred);
//		}
//		
//		if (locType == LocalizationType.FALLING_EDGE) {
//			// rotate the robot until it sees no wall
//
//			//seeing a wall, need to turn away from it.
//			if(getFilteredData() <= 30 ){
//			
//				
//				leftMotor.setSpeed(ROTATE_SPEED);
//				rightMotor.setSpeed(ROTATE_SPEED);
//				rightMotor.forward();
//				leftMotor.backward();
//				
//				//rotate the robot until the distance is the max. As long as the distance is not 50 or greater, rotate
//				for(;;){
//					if(getFilteredData() >= MAX_DISTANCE){
//						leftMotor.setSpeed(0);
//						rightMotor.setSpeed(0);
//						leftMotor.forward();
//						rightMotor.forward();
//						//break;
//					}
//				}
//			}
//			
//			//Rotate to get back to seeing the wall
//			leftMotor.setSpeed(ROTATE_SPEED);
//			rightMotor.setSpeed(ROTATE_SPEED);
//			rightMotor.backward();
//			leftMotor.forward();
//			
//			for(;;){
//				if(getFilteredData() <= WALL_DETECT){
//					leftMotor.setSpeed(0);
//					rightMotor.setSpeed(0);
//					leftMotor.forward();
//					rightMotor.forward();
//					angleA = odo.getAng();
//					//break;
//				}
//			}
//		}
//		
//		odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
//		
//	}	
//	//	Sound.beep();
//		//sort that list
//		
//		//
//		
//			
//			//Once it is certain that the robot is not facing a wall. Start to localize, detect the falling edge
//			
//			
//			
//			
//			
//		//	Sound.buzz();
//		
//		
//			//Detected the first falling edge. Turn the other way and detect the other one
//			//check if it is facing a wall. if true, the robot should rotate until it sees the MAX_DISTANCE
//		
//			
//			// keep rotating until the robot sees a wall, then latch the angle
//			
//			// switch direction and wait until it sees no wall
//			
//			// keep rotating until the robot sees a wall, then latch the angle
//			
//			// angleA is clockwise from angleB, so assume the average of the
//			// angles to the right of angleB is 45 degrees past 'north'
//			
//			// update the odometer position (example to follow:)
//			
////		} else {
////			/*
////			 * The robot should turn until it sees the wall, then look for the
////			 * "rising edges:" the points where it no longer sees the wall.
////			 * This is very similar to the FALLING_EDGE routine, but the robot
////			 * will face toward the wall for most of it.
////			 */
////			
////			//
////			// FILL THIS IN
////			//
////		}
//	
//	
//	//This method polls the sensor several times to get a distance, it then fitlers the values received and then uses that to implement the code.
//	// The fitler used is a median filter, which polls the sensor a certain amount of times and then takes the median. This was effective, because
//	// taking the median effectively ignores the values that are too large and the values that are too low.
//	
//	//TODO this code should also take into account the fact that the sensor returns infinity if it detects and object too close to it
//	
//	public float getFilteredData() {
//		usSensor.fetchSample(usData, 0);
//		
//		//Multiply the value returned in order to work with cm.
//		float distance = usData[0]*100;	
//		
//
//		
//		
////		if (distance == 255 && filterControl < FILTER_OUT) {
////			// bad value, do not set the distance var, however do increment the filter value
////			filterControl ++;
////		} else if (distance == 255){
////			// true 255, therefore set distance to 255
////			this.distance = distance;
////		} else {
////			// distance went below 255, therefore reset everything.
////			filterControl = 0;
////			this.distance = distance;
////		}
////		
//		return distance;
//	}
//
//}
