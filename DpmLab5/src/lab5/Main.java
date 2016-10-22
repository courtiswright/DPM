package logTutorial;


import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.Arrays;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;


public class Main {
	
	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	public static final EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S1);
	public static final EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S2);
	public static final Port usPort = LocalEV3.get().getPort("S1");		
	public static final Port colorPort = LocalEV3.get().getPort("S2");	
	
	// Constants
	public static final double WHEEL_RADIUS = 2.130;
	public static final double TRACK = 15.2;
	private static Navigator nav;
	private static Odometer odometer;
	
	public static void main(String[] args) throws FileNotFoundException {
		
		Log.setLogging(false,false,false,false, true);
		Log.setLogWriter("Data.csv");
		int buttonChoice;
		//Uncomment this line to print to a file
//		Log.setLogWriter(System.currentTimeMillis() + ".log");
		
		// some objects that need to be instantiated	
		
		
		odometer = new Odometer(leftMotor, rightMotor);
		UltrasonicPoller usPoller = new UltrasonicPoller(usSensor);
		nav = new Navigator(odometer,usPoller);
		LCDInfo lcd = new LCDInfo(odometer);
			


		do {
			// clear the display
			LCD.clear();

			// ask the user whether the motors should Avoid Block or Go to locations
			LCD.drawString("< Left | Right >", 0, 0);
			LCD.drawString("       |        ", 0, 1);
			LCD.drawString(" Detect| Pt.2   ", 0, 2);
			LCD.drawString(" Block |		", 0, 3);
			LCD.drawString("       |		", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);

		//if left button is pressed, start first demo
		if (buttonChoice == Button.ID_LEFT) {		
			SampleProvider usValue = usSensor.getMode("Distance");
			SampleProvider colorValue = colorSensor.getMode("RGB");
			float[] colorData = new float[colorValue.sampleSize()];
			float[] usData = new float[usValue.sampleSize()];
			int distance;
			
			while(true){
				
				colorValue.fetchSample(colorData, 0);
				usValue.fetchSample(usData, 0);
				distance = (int)(usData[0]*100.0);
				
				Log.log(Log.Sender.lightSensor, "Color: " + Arrays.toString(colorData));
				LCD.clear();
				LCD.drawString("id:" + Float.toString(colorData[0]), 0, 1);
				LCD.drawString("id:" + Float.toString(colorData[1]), 0, 2);
				LCD.drawString("id:" + Float.toString(colorData[2]), 0, 3);
				
				if(distance<=3) {
					LCD.drawString("Object detected", 0, 4);
					
					//Color Blue has a higher Green rating, lower red rating
					if(colorData[1] > colorData[0] && colorData[0] > 0.01){
						if(colorData[2] > colorData[0])
							LCD.drawString("Block", 0, 5);
					}
					else {
						LCD.drawString("No block", 0, 6);
					}
					
					try{
						Thread.sleep(1000);
					}catch(Exception e){
						e.printStackTrace();
					}
				}	
			}
		}
		//Part 2
		else {
			odometer.start();
			usPoller.start();
			nav.start();
			lcd.start();
			
			localize();
		}
	
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
		
		
	}

	private static void localize() {
		

		//set up sample provider and buffer array
		SampleProvider usValue = usSensor.getMode("Distance");			// colorValue provides samples from this instance
		float[] usData = new float[usValue.sampleSize()];				// colorData is the buffer in which data are returned
		
		
		//instantiate localizer
		USLocalizer usl = new USLocalizer(odometer, usValue, usData, nav);
		
		usl.doLocalization();
		
	}
	
}