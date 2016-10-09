package navigation;
import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwidth;
	private final int FILTER_OUT = 30;
	private final int motorLow, motorHigh;
	private int distance;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int filterControl;
	
	public BangBangController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
							  int bandCenter, int bandwidth, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(motorHigh);				// Start robot moving forward
		rightMotor.setSpeed(motorHigh);
		leftMotor.forward();
		rightMotor.forward();
		filterControl = 0;
	}
	
	@Override
	public void processUSData(int distance) {
		this.distance = distance;
		int error = distance - bandCenter;
		
		// rudimentary filter - toss out invalid samples corresponding to null signal.
				// (n.b. this was not included in the Bang-bang controller, but easily could have).
				//
				if (distance >= 255 && filterControl < FILTER_OUT) {
					// bad value, do not set the distance var, however do increment the filter value
					filterControl ++;
				} else if (distance == 255){
					// true 255, therefore set distance to 255
					this.distance = distance;
				} else {
					// distance went below 255, therefore reset everything.
					filterControl = 0;
					this.distance = distance;
				}
				
		// TODO: process a movement based on the us distance passed in (BANG-BANG style)
		
		if (Math.abs(error) <= bandwidth) {
			leftMotor.setSpeed(motorLow);
			rightMotor.setSpeed(motorLow);
			
			leftMotor.forward();
			rightMotor.forward();
			
		} else if (error < 0) {
			leftMotor.setSpeed(motorHigh);
			rightMotor.setSpeed(0);
			
			leftMotor.forward();
			rightMotor.forward();
			
		} else {
			leftMotor.setSpeed(motorLow+15);
			rightMotor.setSpeed(motorHigh);
			
			leftMotor.forward();
			rightMotor.forward();
		}
		
//		else if (error >= 0 && error < 3) {
//			leftMotor.setSpeed(motorLow+20);
//			rightMotor.setSpeed(motorHigh);
//			
//			leftMotor.forward();
//			rightMotor.forward();
//		} else  if (error >= 3) {
//			leftMotor.setSpeed(motorLow);
//			rightMotor.setSpeed(motorHigh);
//			
//			leftMotor.forward();
//			rightMotor.forward();
//		}
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
