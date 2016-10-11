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
		
		//constructs an instance of Navigation which is called from other classes
		//in order to move the robot from one point to the next
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
		
		//moves the robot from its current position to the desired position
		public void travelTo(double xPos, double yPos){
			
			//gets the position that the robot is currently at
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
			
			//calls method to calculate angle to be at before moving forward
			thetaD = calcAngle(dX,dY);
			//thetaD = Math.atan(dX/dY);	
			
			//angle robot needs to turn is difference between angle it
			//should be at and angle it is currently at
			theta = thetaD - thetaR;
			
			
			distance = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
			
			//if the angle to turn is greater than Pi (180deg), or less than -2*Pi
			//the angle is changed to be the same angle-2*Pi or +2*Pi, respectively
			//this ensures the smallest angle possible is turned
			if (theta > Math.PI) {
				theta = theta - 2*Math.PI;
//				LCD.drawString("T>180", 10, 2);
			}
			else if (theta < -(Math.PI) ) { 
				theta = theta + 2*Math.PI;
//				LCD.drawString("T<-180", 10, 2);
			}
			
			//calls turnTo method to physically turn to desired angle
			turnTo(theta);
			
			//once orientation is proper, moves the desired amount forward
			travelForward(distance);
			//turnTo(-theta);
		
			navigating = false;
		}	
		
		//physically turns robot to desired angle
		public void turnTo(double angle) {
			
			leftMotor.setSpeed(ROTATE_SPEED);
			rightMotor.setSpeed(ROTATE_SPEED);
			
			//convert to degrees
			angle = angle * 180 / Math.PI;
			
			//turns to calculated angle
			navigating = true;
			leftMotor.rotate(convertAngle(leftRadius, WIDTH, angle), true);
			rightMotor.rotate(-convertAngle(rightRadius, WIDTH, angle), false);
			navigating = false;
			
			leftMotor.stop();
			rightMotor.stop();
		}	
		
		//physically moves robot desired distance forward
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
		
		//checks to see if the robot is busy navigating
		public boolean isNavigating() {
			return navigating;
		}
		
		//gets the X coordinate when checked
		public double getX(){
			double result = xPos;
			return result;
		}
		
		//gets the Y coordinate when checked
		public double getY(){
			double result = yPos;
			return result;
		}
		
		//gets Theta when checked
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
		//calculates the angle the robot should be facing in order to move
		//forward to the desired location (deltaX,deltaY)
		private double calcAngle(double deltaX, double deltaY) {
			
			theta = 0.0;
			int deltX = (int) deltaX;
			int deltY = (int) deltaY;
			
			//special conditions
			//tan^-1 of 0 is 0 
			if (deltX == 0 && deltY > 0) {
				theta = 0;
//				LCD.drawString("IN1", 10, 2);
			}
			else if (deltX == 0 && deltY > 0){
				theta = Math.PI;
//				LCD.drawString("IN2", 10, 2);
			}
			//can't divide by 0
			else if (deltY == 0 && deltX > 0) {
				theta = Math.PI/2;
//				LCD.drawString("IN3", 10, 2);
			}
			else if (deltY == 0 && deltX < 0) {
				theta = - Math.PI/2;
//				LCD.drawString("IN4", 10, 2);
			}	
			
			//if none of the special conditions are met, calculates angle using arctan
			//we know tan = opposite/adjacent so set calculate using distance of each side of triangle
			else {
//				LCD.drawString("ELSE", 10, 2);
				
				theta = Math.atan(deltaX/deltaY);				
			
				//checks special conditions 
				if (deltaX < 0 && deltaY < 0)  { 
					theta = theta + Math.PI;
//					LCD.drawString("IN5", 10, 2);
				}
				else if (deltaX > 0 && deltaY < 0) {
					theta = theta + Math.PI;
//					LCD.drawString("IN6", 10, 2);
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
