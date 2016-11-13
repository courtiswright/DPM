package lab5;

import java.util.HashMap;

import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class Scanner {

	//Instantiate objects
	EV3UltrasonicSensor usSensor;
	int distance;
	double angle, startAng, finalHeading, errorHigh, errorLow;
	Integer shortestDist = 255;
	HashMap<Integer, Double> distAtAng = new HashMap<Integer, Double>();
	Navigator nav;
	Odometer odo;
	
	//constructor
	public Scanner(Navigator nav, Odometer odo, EV3UltrasonicSensor usSensor) {
		this.nav = nav;
		this.odo = odo;
		this.usSensor = usSensor;
	}
	
	//Instantiate US sensor stuff 
	SampleProvider usValue = Main.usSensor.getMode("Distance");
	float[] usData = new float[usValue.sampleSize()];
	
	/* chooses direction in which closest block is located by scanning and polling while
	 * turning. 
	 * 
	 */
	public void chooseDirection(int turnAmnt) {
		startAng = odo.getAng();
		nav.turnAmount(turnAmnt, true);
		
		if ((startAng - turnAmnt) < 0) {
			finalHeading = 360 + (startAng - turnAmnt);
		} else {
			finalHeading = (startAng - turnAmnt);
		}
		
		errorHigh = finalHeading + 2;
		errorLow = finalHeading - 2;
						
		while (odo.getAng() > errorHigh || odo.getAng() < errorLow) {	
			
			usValue.fetchSample(usData, 0);
			distance = (int)(usData[0]*100.0);
			
			distAtAng.put((Integer)distance, (Double)odo.getAng());
		}
		
		for(Integer key : distAtAng.keySet()) {
			if (key < shortestDist) {
				shortestDist = key;
			}
		}
		
		angle = (double)distAtAng.get(shortestDist);
	}
}
