// Lab3.java
package navigation;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;


public class Lab3{
	
	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

	// Constants
	public static final double LEFT_RADIUS = 2.1;
	public static final double RIGHT_RADIUS = 2.12;
	public static final double WHEEL_RADIUS = 2.1;
	public static final double WIDTH = 15.2;
	
	public static void main(String [] args){
		int buttonChoice;
		
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

		if (buttonChoice == Button.ID_LEFT) {
			
			odometer.start();
			odometryDisplay.start();
			
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
