package navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigation extends Thread{
	
		private Odometer odometer;
		private EV3LargeRegulatedMotor leftMotor, rightMotor;
		boolean navigating = false;
		
		//CONSTANTS
		private int FORWARD_SPEED = 200; 	//(250) in squareDriver
		private int ROTATE_SPEED = 100; 	//(150) in squareDriver
		private double WIDTH, leftRadius, rightRadius;
		//private double thetaR, xR, yR;	//comment out for now may change
		private double xPos, yPos;			//The heading
		private double theta;				//error in theta
		
		public Navigation (Odometer odometer, EV3LargeRegulatedMotor leftMotor,EV3LargeRegulatedMotor rightMotor,
							double width, double leftRadius, double rightRadius){
			this.odometer = odometer;
			this.leftMotor = leftMotor;
			this.rightMotor = rightMotor;
			this.WIDTH = width;
			this.leftRadius = leftRadius;
			this.rightRadius = rightRadius;
			leftMotor.setAcceleration(4000);
			rightMotor.setAcceleration(4000);
			navigating = false;
		}
		
		public void travelTo(double xPos, double yPos){
			this.xPos = xPos;
			this.yPos = yPos;
			
		/*
		 	synchronized (odometer.lock) {
				thetaR = odometer.getTheta() * 180 / Math.PI;
				xR = odometer.getX();
				yR = odometer.getY();
			}
		*/	
			turnTo(calcAngle(xPos,yPos));
			travelForward(calcDistance(xPos,yPos));
		}
		
		public void turnTo(double thetaD) {
			
			double angle = thetaD - odometer.getTheta();
			
			if (angle > 180)
				angle = angle - 360;
			if (angle < -180)
				angle = angle + 360;
			
			leftMotor.setSpeed(ROTATE_SPEED);
			rightMotor.setSpeed(ROTATE_SPEED);
			
			navigating = true;
			leftMotor.rotate(convertAngle(leftRadius, WIDTH, 90.0), true);
			rightMotor.rotate(-convertAngle(rightRadius, WIDTH, 90.0), false);
			navigating = false;
		}
		
		public void travelForward(double distance) {
			
			
			leftMotor.setAcceleration(2000);
			rightMotor.setAcceleration(2000); //(3000) in odometer
			
			leftMotor.setSpeed(FORWARD_SPEED);
			rightMotor.setSpeed(FORWARD_SPEED);
			
			navigating = true;
			leftMotor.rotate(convertDistance(leftRadius,distance), true);
			rightMotor.rotate(convertDistance(rightRadius,distance), false);
			
			leftMotor.forward();
			rightMotor.forward();
	
			leftMotor.stop();
			rightMotor.stop();	
			
			navigating = false;
		}
		
		public boolean isNavigating() {
			return navigating;
		}
		public double getX(){
			double result = xPos;
			return result;
		}

		public double getY(){
			double result = yPos;
			return result;
		}
		public double getTheta(){
			double result = theta * 180 / Math.PI;
			return result;
		}

		private double calcDistance(double xPos, double yPos){
			double dX = xPos - odometer.getX();
			double dY = yPos - odometer.getY();
			return Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
		}
		private double calcAngle(double xPos, double yPos) {
			theta = 0.0;
			double dX = xPos - odometer.getX();
			double dY = yPos - odometer.getY();
			
			//tan^-1 of 0 is 0			
			if (dX == 0 && dY < 0) 
				theta = Math.PI;
			if (dX == 0 && dY > 0)
				theta = 0;
			
			//can't divide by 0
			if (dY == 0 && dX < 0)
				theta = -(Math.PI/2);
			if (dY == 0 && dX > 0)
				theta = Math.PI;
			
			
			theta = Math.atan(dY/dX);
			if (dX < 0 && dY < 0) 
				theta = theta + Math.PI;
			else if (dX > 0 && dY < 0)
				theta = theta + Math.PI;
				
			return theta;
		}


		//Methods from square driver
		
		private static int convertDistance(double radius, double distance) {
			return (int) ((180.0 * distance) / (Math.PI * radius));
		}
	
		private static int convertAngle(double radius, double width, double angle) {
			return convertDistance(radius, Math.PI * width * angle / 360.0);
		}
		
	
}
