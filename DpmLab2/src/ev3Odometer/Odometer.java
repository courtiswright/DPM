/*
 * Odometer.java
 */

package ev3Odometer;

import lejos.hardware.motor.EV3LargeRegulatedMotor;  

public class Odometer extends Thread {
	// robot position
	private double x, y, theta;

	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;
	
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	
	private double WR = 2.1;
	private double WB = 10.5;
	private double lastTachoR = 0;	
	private double lastTachoL = 0;
//	private double [] oldData = new double[2];
//	private double [] data = new double[2];

	// default constructor
	public Odometer(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) {
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		lock = new Object();
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
	}

	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;
		
		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();

		while (true) {
			updateStart = System.currentTimeMillis();
			
			//MY CODE
			int leftTach = leftMotor.getTachoCount();
			int rightTach = rightMotor.getTachoCount();
			
//			//distance
//			data[0] = ( leftTach * WR + rightTach* WR) * Math.PI/ 360;
//			//direction
//			data[1] = ( leftTach * WR - rightTach* WR) / WB;
			
			//END MY CODE
			
			double distL, distR, deltaD, deltaT, dX, dY;
			distL= Math.PI*WR*(leftTach-lastTachoL)/180;		// compute wheel
			distR= Math.PI*WR*(rightTach-lastTachoR)/180;		// displacements
			lastTachoL=leftTach;								// save tachometer counts for next iteration
			lastTachoR=rightTach;
			deltaD= 0.5*(distL+distR);							// compute vehicle displacement
			deltaT= (distL-distR)/WB;							// compute change in heading
			
			//store previous values
//			oldData[0] = data[0];
//			oldData[1] = data[1];
			
			
			synchronized (lock) {
				// don't use the variables x, y, or theta anywhere but here!
				//theta = -0.7376;
				theta += deltaT;					// update heading 
				dX = deltaD * Math.sin(theta);		// compute X component of displacement 
				dY = deltaD * Math.cos(theta);		// compute Y component of displacement
				x = x + dX;            
				y = y + dY;
			}
			
			leftTach = leftMotor.getTachoCount();
			rightTach = rightMotor.getTachoCount();

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
					
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta;
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}