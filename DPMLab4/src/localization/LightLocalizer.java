/* Group 2
 * Nareg Torikian:	260633071
 * Tamim Sujat:		260551583
 */
package localization;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;


public class LightLocalizer extends Thread{
	private Odometer odo;
	private static SampleProvider colorSensor;
	private static float[] colorData;	
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private Navigation navigator;
	//private float[] sample = new float[sampleSize];
	private int FORWARD_SPEED = 100;
	private int ROTATION_SPEED = 50;
	private int extraDist = 4;
	private double distance = 13.0;
	double x, y, delta_theta;
	
	public LightLocalizer(Odometer odo, SampleProvider colorSensor, float[] colorData, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, Navigation navigator) {
		this.odo = odo;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.navigator = navigator;
	}
	
	public void doLocalization() {
		
		
	}

}
