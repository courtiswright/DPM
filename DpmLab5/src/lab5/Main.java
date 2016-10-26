package lab5;


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
	public static Navigator nav;
	public static Odometer odometer;
	public static ObstacleAvoidance obstacleAvoider;
	private static int threshDist = 3;
	static double blockAng;
	
	public static void main(String[] args) throws FileNotFoundException {
		
		
		//Code for logging from tutorial
		// boolean odometer,navigation,UltrasonicSensor, Avoidance, LightSensor 
		Log.setLogging(false,false,false,false, false);
	
		//Prints log info to a file
		//Log.setLogWriter(System.currentTimeMillis() + ".log");
		
		// some objects that need to be instantiated	
		odometer = new Odometer(leftMotor, rightMotor, 30, true);
		UltrasonicPoller usPoller = new UltrasonicPoller(usSensor);
		nav = new Navigator(odometer,usPoller);
		LCDInfo lcd = new LCDInfo(odometer);
		obstacleAvoider = new ObstacleAvoidance(nav);
			
		//Asks which part to run
		lcd.startScreen();
		int buttonChoice = lcd.getButtonChoice();	

		//Part 1
		if (buttonChoice == Button.ID_LEFT) {		
			
			//Instantiate SampleProviders and buffer arrays
			SampleProvider usValue = usSensor.getMode("Distance");
			SampleProvider colorValue = colorSensor.getMode("RGB");
			BlockChecker BC = new BlockChecker();
			float[] colorData = new float[colorValue.sampleSize()];
			float[] usData = new float[usValue.sampleSize()];
			int distance;
			
			//Always searching for block
			while(true){
				//stores sample into buffer array
//				colorValue.fetchSample(colorData, 0);
				usValue.fetchSample(usData, 0);
				distance = (int)(usData[0]*100.0);
				
				Log.log(Log.Sender.lightSensor, "Color: " + Arrays.toString(colorData));
				LCD.clear();
//				LCD.drawString("id:" + Float.toString(colorData[0]), 0, 1);
//				LCD.drawString("id:" + Float.toString(colorData[1]), 0, 2);
//				LCD.drawString("id:" + Float.toString(colorData[2]), 0, 3);
				
				if(distance<=threshDist) {
					LCD.drawString("Object detected", 0, 4);
					
					if(BC.blockCheck()) {
						LCD.drawString("Block", 0, 5);
					}
					else {
						LCD.drawString("No block", 0, 5);
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
			lcd.startTimer();
					
//			localize();
			//TODO complete course, stopping after every waypoint to look for blocks in 180deg direction. 
			//If block found move close to it (within 4cm)
			//Detect if styrofoam
			//stab it with fast moving adhesive arm
			//pick up block
			
			SampleProvider usValue = usSensor.getMode("Distance");
			Scanner SC = new Scanner();
			float[] usData = new float[usValue.sampleSize()];
			
			nav.turnTo(15, false);
			SC.chooseDirection(120);
			blockAng = SC.angle;
			System.out.println("blockAng: " + blockAng);
			nav.turnTo(blockAng, true);
			
			SampleProvider colorValue = colorSensor.getRGBMode();
			float[] colorData = new float[colorValue.sampleSize()];
			BlockChecker BC = new BlockChecker();
			obstacleAvoider.start();
			int distance;
			
			if (SC.shortestDist > threshDist) {
				nav.goForward(SC.shortestDist-threshDist, false);
			} 
			
			usValue.fetchSample(usData, 0);
			distance = (int)(usData[0]*100.0);
			
			if (distance > threshDist) {
				nav.goForward(distance - threshDist, false);
			}
			
			if(distance<=threshDist) {
				LCD.drawString("Object detected", 0, 4);
				
				if(BC.blockCheck()) {
					LCD.drawString("Block", 0, 5);
				}
				else {
					LCD.drawString("No block", 0, 5);
				}
				
				try{
					Thread.sleep(1000);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
		
		
	}
	private static void completeCourse(){
		int[][] waypoints = {{0,30},{0,60},{30,60},{60,60}};
		
		for(int[] point : waypoints){
			nav.travelTo(point[0],point[1], true);
			while(nav.isTravelling()){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private static void localize() {
		
		//set up sample provider and buffer array
		SampleProvider usValue = usSensor.getMode("Distance");			// usValue provides samples from this instance
		float[] usData = new float[usValue.sampleSize()];				// usData is the buffer in which data are returned
		
		
		//instantiate localizer
		USLocalizer usl = new USLocalizer(odometer, usValue, usData, nav);
		
		usl.doLocalization();
		
	}
	
}