/* 
 * OdometryCorrection.java
 */
package ev3Odometer;

import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	private SensorModes lightSensor;
	
	private double odometerX = 0;
	private double odometerY = 0;

	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;
		
		SampleProvider sensor = lightSensor.getMode("Red");
		int dataSize = sensor.sampleSize();

		while (true) {
			correctionStart = System.currentTimeMillis();

			// put your correction code here
			float sensorData[] = new float[dataSize];
			sensor.fetchSample(sensorData, 0);
			
			if(odometer.getTheta() < 1.5 && sensorData[0] < 0.2) {
				
			} else if ((odometer.getTheta() >= 1.5 && odometer.getTheta() < 3) && sensorData[0] < 0.2) {
				
			} else if ((odometer.getTheta() >= 3 && odometer.getTheta() < 6.2) && sensorData[0] < 0.2) {
				
			}
			

			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
}