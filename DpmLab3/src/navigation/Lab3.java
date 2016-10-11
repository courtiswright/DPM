// Lab3.java
package navigation;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;


public class Lab3{
	

	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	public static EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	public static Port usPort = LocalEV3.get().getPort("S1");

	// Constants
	public static final double LEFT_RADIUS = 2.1;
	public static final double RIGHT_RADIUS = 2.12;		//accounts for variance in motor strengths
	public static final double WHEEL_RADIUS = 2.1;
	public static final double WIDTH = 15.2;
	private static final int motorLow = 100;			// Speed of slower rotating wheel (deg/sec)
	private static final int motorHigh = 200;			// Speed of the faster rotating wheel (deg/seec)
	
	public static void main(String [] args){
		int buttonChoice;
		
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);
		SampleProvider usDistance = usSensor.getMode("Distance");
		float[] usData = new float[usDistance.sampleSize()];
		
		
		// some objects that need to be instantiated
		final TextLCD t = LocalEV3.get().getTextLCD();
		Odometer odometer = new Odometer(leftMotor, rightMotor);
		Navigation navigator = new Navigation(odometer, leftMotor, rightMotor, WIDTH, LEFT_RADIUS, RIGHT_RADIUS);
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, navigator, t);
		
		
		do {
			// clear the display
			LCD.clear();

			// ask the user whether the motors should Avoid Block or Go to locations
			LCD.drawString("< Left | Right >", 0, 0);
			LCD.drawString("       |        ", 0, 1);
			LCD.drawString(" Avoid | Drive  ", 0, 2);
			LCD.drawString(" Block |		", 0, 3);
			LCD.drawString("       |		", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		//if left button is pressed, start second demo
		if (buttonChoice == Button.ID_LEFT) {
			
			//call UltrasonicPoller and BangBangController constructors to initialize
			BangBangController bangbang = new BangBangController(leftMotor, rightMotor,
					 odometer, navigator, motorLow, motorHigh);
			UltrasonicPoller usPoller = new UltrasonicPoller(usDistance, usData, bangbang);
			
			//start odometer, display, and sensor
			odometer.start();
			odometryDisplay.start();
			usPoller.start();
//			navigator.start();			//not actually a thread so nothing to start
			navigator.travelTo(0, 60);
			navigator.travelTo(60, 0);
			
//THIS DOES NOT WORK BECAUSE IT CREATES A TON OF INTERFERENCE FOR PART 2
//IT IS ESSENTIALLY A CHEAT INSTEAD OF CREATING A THREAD IN THE CLASS ITSELF
			//create thread to run second demo
//			(new Thread() {
//				public void run() {
//					//instantiated everything again - short fix
//					final TextLCD t = LocalEV3.get().getTextLCD();
//					Odometer odometer = new Odometer(leftMotor, rightMotor);
//					Navigation navigator = new Navigation(odometer, leftMotor, rightMotor, WIDTH, LEFT_RADIUS, RIGHT_RADIUS);
//					OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, navigator, t);
//										
//					odometer.start();
//					odometryDisplay.start();
//					navigator.start();
//					
//					//calls travelTo method in Navigation class, which then uses BangBang
//					navigator.travelTo(0, 60);
//					navigator.travelTo(60, 0);
//				}
//			}).start();
			
			
			//TODO: write code for second part
		}
		else {			
			//have to put in thread so you can exit program early
			(new Thread() {
				public void run() {
					//instantiated everything again - short fix
					final TextLCD t = LocalEV3.get().getTextLCD();
					Odometer odometer = new Odometer(leftMotor, rightMotor);
					Navigation navigator = new Navigation(odometer, leftMotor, rightMotor, WIDTH, LEFT_RADIUS, RIGHT_RADIUS);
					OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, navigator, t);
					
					odometer.start();
					odometryDisplay.start();
					navigator.start();
					
					//calls travelTo method in Navigation class to move to each of the points
					navigator.travelTo(60, 30);
					navigator.travelTo(30, 30);
					navigator.travelTo(30, 60);
					navigator.travelTo(60, 0);
				}
			}).start();

		}

		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
		
	}
}
