package lab5;

import java.util.HashMap;

import lejos.hardware.lcd.LCD;
import lejos.robotics.SampleProvider;

public class Scanner {

	SampleProvider usValue = Main.usSensor.getMode("Distance");
	float[] usData = new float[usValue.sampleSize()];
	int distance;
	double angle, startAng, finalHeading, errorHigh, errorLow;
	Integer shortestDist = 255;
	HashMap<Integer, Double> distAtAng = new HashMap<Integer, Double>();
	
	public Scanner() {
		
	}
	
	public void chooseDirection(int turnAmnt) {
		startAng = Main.odometer.getAng();
		Main.nav.turnAmount(turnAmnt);
		
		if ((startAng - turnAmnt) < 0) {
			finalHeading = 360 + (startAng - turnAmnt);
		} else {
			finalHeading = (startAng - turnAmnt);
		}
		
		errorHigh = finalHeading + 2;
		errorLow = finalHeading - 2;
		
//		System.out.println("turnAmnt: " + turnAmnt + " errorHigh: " + errorHigh + " errorLow: " + errorLow);
				
		while (Main.odometer.getAng() > errorHigh || Main.odometer.getAng() < errorLow) {	
			
			usValue.fetchSample(usData, 0);
			distance = (int)(usData[0]*100.0);
			
			distAtAng.put((Integer)distance, (Double)Main.odometer.getAng());
//			System.out.println("Key: " + distance + " Value: " + distAtAng.get(distance));
		}
		
//		System.out.println("Angle: " + Main.odometer.getAng());
		
		for(Integer key : distAtAng.keySet()) {
			if (key < shortestDist) {
				shortestDist = key;
			}
		}
		
		angle = (double)distAtAng.get(shortestDist);
//		System.out.println("");
//		System.out.println("ShortDist: " + shortestDist + " Angle: " + angle);
		
//		return angle;
	}
}
