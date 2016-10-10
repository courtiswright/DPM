package navigation;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigation extends Thread{
	
		private Odometer odometer;
		private EV3LargeRegulatedMotor leftMotor, rightMotor;
		boolean navigating = false;
		
		//CONSTANTS
		private int FORWARD_SPEED = 200; 	//(250) in squareDriver
		private int ROTATE_SPEED = 100; 	//(150) in squareDriver
		private double WIDTH, leftRadius, rightRadius;
		private double thetaR, xR, yR;		//current heading
		private double xPos, yPos, theta;	//distance and heading
			
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
		
		 	synchronized (odometer.lock) {
				thetaR = odometer.getTheta();
				xR = odometer.getX();
				yR = odometer.getY();
			}
		 	double thetaD, distance;
			double dX = xPos - xR;
			double dY = yPos - yR;
			
			thetaD = calcAngle(dX,dY);
			//thetaD = Math.atan(dX/dY);	
			theta = thetaD - thetaR;
			
			distance = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
			
			if (theta > Math.PI) {
				theta = theta - 2*Math.PI;
				LCD.drawString("T>180", 10, 2);
			}
			else if (theta < -(Math.PI) ) { 
				theta = theta + 2*Math.PI;
				LCD.drawString("T<-180", 10, 2);
			}
			
			turnTo(theta);
			travelForward(distance);
			//turnTo(-theta);
		
			navigating = false;
		}	
		public void turnTo(double angle) {
			
			leftMotor.setSpeed(ROTATE_SPEED);
			rightMotor.setSpeed(ROTATE_SPEED);
			
			//convert to degrees
			angle = angle * 180 / Math.PI;
			
			navigating = true;
			leftMotor.rotate(convertAngle(leftRadius, WIDTH, angle), true);
			rightMotor.rotate(-convertAngle(rightRadius, WIDTH, angle), false);
			navigating = false;
			
			leftMotor.stop();
			rightMotor.stop();
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
			double result = theta * 180/Math.PI;
			return result;
		}
		/**
		 * 
		 * @param deltaX displacement in x position
		 * @param deltaY displacement in y position
		 * @return angle in radians that describes the amount needed to turn
		 * to face the hypotenuse of the x and y displacement vectors
		 */
		private double calcAngle(double deltaX, double deltaY) {
			theta = 0.0;
			int deltX = (int) deltaX;
			int deltY = (int) deltaY;
			//special conditions
			//tan^-1 of 0 is 0 
			if (deltX == 0 && deltY > 0) {
				theta = 0;
				LCD.drawString("IN1", 10, 2);
			}
			else if (deltX == 0 && deltY > 0){
				theta = Math.PI;
				LCD.drawString("IN2", 10, 2);
			}
			//can't divide by 0
			else if (deltY == 0 && deltX > 0) {
				theta = Math.PI/2;
				LCD.drawString("IN3", 10, 2);
			}
			else if (deltY == 0 && deltX < 0) {
				theta = - Math.PI/2;
				LCD.drawString("IN4", 10, 2);
			}		
			else {
				LCD.drawString("ELSE", 10, 2);
				
				theta = Math.atan(deltaX/deltaY);				
			
				if (deltaX < 0 && deltaY < 0)  { 
					theta = theta + Math.PI;
					LCD.drawString("IN5", 10, 2);
				}
				else if (deltaX > 0 && deltaY < 0) {
					theta = theta + Math.PI;
					LCD.drawString("IN6", 10, 2);
				}				
			}
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
