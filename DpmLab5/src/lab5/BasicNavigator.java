package lab5;
/*
 * File: Navigation.java
 * Written by: Sean Lawlor
 * ECSE 211 - Design Principles and Methods, Head TA
 * Fall 2011
 * Ported to EV3 by: Francois Ouellet Delorme
 * Fall 2015
 * Helper methods and extend Thread - Jonah Caplan
 * 2015
 * 
 * Movement control class (turnTo, travelTo, flt, localize)
 */
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class BasicNavigator extends Thread{
	
	final static int FAST = 200, SLOW = 100, ACCELERATION = 50;
	final static double DEG_ERR = 3.0, CM_ERR = 1.0;
	Odometer odometer;
	EV3LargeRegulatedMotor leftMotor, rightMotor;

	public BasicNavigator(Odometer odo) {
		this.odometer = odo;

		EV3LargeRegulatedMotor[] motors = this.odometer.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];

		// set acceleration
		this.leftMotor.setAcceleration(ACCELERATION);
		this.rightMotor.setAcceleration(ACCELERATION);
	}

	/*
	 * Functions to set the motor speeds jointly
	 */
	public void setSpeeds(float lSpd, float rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	public void setSpeeds(int lSpd, int rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	/*
	 * Float the two motors jointly
	 */
	public void setFloat() {
		this.leftMotor.stop();
		this.rightMotor.stop();
		this.leftMotor.flt(true);
		this.rightMotor.flt(true);
	}

	/*
	 * TravelTo function which takes as arguments the x and y position in cm Will travel to designated position, while
	 * constantly updating it's heading
	 */
	public void travelTo(double x, double y) {
		double minAng;
		while (!checkIfDone(x,y)) {
			minAng = getDestAngle(x,y);
			this.turnTo(minAng, false);
			this.setSpeeds(FAST, FAST);
		}
		this.setSpeeds(0, 0);
	}

	protected boolean checkIfDone(double x, double y) {
		return Math.abs(x - odometer.getX()) < CM_ERR
				&& Math.abs(y - odometer.getY()) < CM_ERR;
	}

	protected boolean facingDest(double angle) {
		return Math.abs(angle - odometer.getAng()) < DEG_ERR;
	}

	protected double getDestAngle(double x, double y) {
		double minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX()))
				* (180.0 / Math.PI);
		if (minAng < 0) {
			minAng += 360.0;
		}
		return minAng;
	}
	
	/*
	 * TurnTo function which takes an angle and boolean as arguments The boolean controls whether or not to stop the
	 * motors when the turn is completed
	 */
	public void turnTo(double angle, boolean stop) {

		double error = angle - this.odometer.getAng();

		while (Math.abs(error) > DEG_ERR) {

			error = angle - this.odometer.getAng();

			if (error < -180.0) {
				this.setSpeeds(-SLOW, SLOW);
			} else if (error < 0.0) {
				this.setSpeeds(SLOW, -SLOW);
			} else if (error > 180.0) {
				this.setSpeeds(SLOW, -SLOW);
			} else {
				this.setSpeeds(-SLOW, SLOW);
			}
		}

		if (stop) {
			this.setSpeeds(0, 0);
		}
	}
	
	/*
	 * Go foward a set distance in cm
	 */
	public void goForward(double distance) {
		this.travelTo(
				odometer.getX()
						+ Math.cos(Math.toRadians(this.odometer.getAng()))
						* distance,
				odometer.getY()
						+ Math.sin(Math.toRadians(this.odometer.getAng()))
						* distance);

	}
	public void turnAmount(double amount, boolean cont){
		if(amount>=0) {
			leftMotor.rotate(convertAngle(Main.WHEEL_RADIUS, Main.TRACK, amount), true);
			rightMotor.rotate(-convertAngle(Main.WHEEL_RADIUS, Main.TRACK, amount), cont);
		}
		if(amount<0){
			amount = Math.abs(amount);
			leftMotor.rotate(-convertAngle(Main.WHEEL_RADIUS, Main.TRACK, amount), true);
			rightMotor.rotate(convertAngle(Main.WHEEL_RADIUS, Main.TRACK, amount), cont);
		}	
	}
	public void rotateCCW() {
		rightMotor.forward();
		leftMotor.backward();
	}
	public void rotateCW(){
		leftMotor.forward();
		rightMotor.backward();
	}
	
	//methods originally from SquareDriver class in Lab2
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
}
