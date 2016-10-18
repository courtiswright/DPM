/* Group 2
 * Nareg Torikian:	260633071
 * Tamim Sujat:		260551583
 * 
 * Odometer and Navigation classes are the same ones that were provided with the assignment
 */
package localization;

import lejos.hardware.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;

public class Lab4 {

	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	// Ultrasonic sensor port connected to input S1
	// Color sensor port connected to input S2
	
	
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final Port usPort = LocalEV3.get().getPort("S1");		
	private static final Port colorPort = LocalEV3.get().getPort("S2");	
	public static final double WHEEL_RADIUS = 2.1;
	public static final double WIDTH = 15.2;

	
	public static void main(String[] args) {
		final TextLCD display = LocalEV3.get().getTextLCD();
		int buttonChoice;
		//Setup ultrasonic sensor
		// 1. Create a port object attached to a physical port (done above)
		// 2. Create a sensor instance and attach to port
		// 3. Create a sample provider instance for the above and initialize operating mode
		// 4. Create a buffer for the sensor data
		@SuppressWarnings("resource")							    	// Because we don't bother to close this resource
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);
		SampleProvider usValue = usSensor.getMode("Distance");			// colorValue provides samples from this instance
		float[] usData = new float[usValue.sampleSize()];				// colorData is the buffer in which data are returned
		
		//Setup color sensor
		// 1. Create a port object attached to a physical port (done above)
		// 2. Create a sensor instance and attach to port
		// 3. Create a sample provider instance for the above and initialize operating mode
		// 4. Create a buffer for the sensor data
		SensorModes colorSensor = new EV3ColorSensor(colorPort);
		SampleProvider colorValue = colorSensor.getMode("Red");			// colorValue provides samples from this instance
		float[] colorData = new float[colorValue.sampleSize()];			// colorData is the buffer in which data are returned
		
		
		// setup the odometer and display
		Odometer odo = new Odometer(leftMotor, rightMotor, 30, true);
		
		Navigation navigation = new Navigation(odo);
				
		USLocalizer usl = new USLocalizer(odo, usValue, usData, USLocalizer.LocalizationType.FALLING_EDGE, leftMotor, rightMotor, navigation);
		LightLocalizer lsl = new LightLocalizer(odo, colorValue, colorData, leftMotor, rightMotor, navigation);
		
		LCDInfo lcd = new LCDInfo(odo, display, usl);
		
		usl.doLocalization();
		while(Button.waitForAnyPress() != 1){
			lsl.doLocalization();
		}
		
		
		//This is not necessary as there is only one task to do here.
		
		//Simply call the first method, execute everything and then call the second one. The message on the screen could change
		//	depending on which stage the robot is at in execution.
		
//		do {
//			// clear the display
//			display.clear();
//
//			// ask the user whether they would like to work on the course without the obstacle or with it.
//			display.drawString("< Left | Right >", 0, 0);
//			display.drawString("       |        ", 0, 1);
//			display.drawString(" US    | Light	", 0, 2);
//			display.drawString(" Local | Local  ", 0, 3);
//			display.drawString(" ize   | ize    ", 0, 4);
//
//			buttonChoice = Button.waitForAnyPress();
//		} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
//		
//		//if right side is chosen, the robot uses the light sensor to localize
//		if(buttonChoice == Button.ID_RIGHT){
//			LCDInfo lcd = new LCDInfo(odo, display, usl);
//			usl.doLocalization();
//			
//		}
//		
//		//By choosing the left side, the robot uses the ultrasonic sensor to localize
//		else{
//			LCDInfo lcd = new LCDInfo(odo, display, usl);
//			lsl.doLocalization();
//			
//		}
//		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);	
		
	}

}
