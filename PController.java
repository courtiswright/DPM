package wallFollower;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwidth;
	private final int motorStraight = 200, FILTER_OUT = 20;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int distance;
	private int filterControl;
	private int Kprop = 6;
	private int prev = 0; 
	
	public PController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
					   int bandCenter, int bandwidth) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(motorStraight);					// Initialize motor rolling forward
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		filterControl = 0;
	}
	
	@Override
	public void processUSData(int distance) {
		int error = distance - bandCenter;
		int correction = 0;
		LCD.drawString("Error: " + error, 0, 9);
		
		// rudimentary filter - toss out invalid samples corresponding to null
		// signal.
		// (n.b. this was not included in the Bang-bang controller, but easily
		// could have).
		//
		if ((distance >= 255 || Math.abs(prev-distance) > 20 ) && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the
			// filter value
			filterControl++;
		} else if (distance >= 255) {
			// We have repeated large values, so there must actually be nothing
			// there: leave the distance alone
			this.distance = distance;
		} else {
			// distance went below 255: reset filter and leave
			// distance alone.
			filterControl = 0;
			this.distance = distance;
		}
		prev = distance;


		// TODO: process a movement based on the us distance passed in (P style)
		
		//appropriate distance from wall, set motors to same speed
		if (Math.abs(error) <= bandwidth) {
			LCD.drawString("Working Well", 0, 5);
			leftMotor.setSpeed(motorStraight);
			rightMotor.setSpeed(motorStraight);
			leftMotor.forward();
			rightMotor.forward();
		}
		//large distance readings, increase speed of outer wheel by constant
/*		else if (distance >= 255) {
			leftMotor.setSpeed(130);
			rightMotor.setSpeed(motorStraight);
			leftMotor.forward();
			rightMotor.forward();
		}
*/		//too close to wall, increase speed of inner wheel	
		else if (error < 0) {
			correction = calcCorrection(error);
			LCD.drawString("Too close", 0, 5);
			leftMotor.setSpeed(motorStraight + correction);
			rightMotor.setSpeed(motorStraight - correction);
			leftMotor.forward();
			rightMotor.forward();
		}	
		
		//too far from wall, increase speed of outer wheel	
		else {
			correction = calcCorrection(error);
			LCD.drawString("Too far", 0, 5);
			leftMotor.setSpeed(motorStraight - correction);
			rightMotor.setSpeed(motorStraight + correction);
			
//			leftMotor.setSpeed(200 - correction);
//			rightMotor.setSpeed(200 + correction);
			
//			leftMotor.setSpeed(115);
//			rightMotor.setSpeed(200);
			leftMotor.forward();
			rightMotor.forward();
		}
	}
	public int calcCorrection(int error){
		int correction = 0;
		if(error < 0) {
			error = Math.abs(error);
		}
		correction = (int)(Kprop * error);
	
		//if correction is too large set to half of motor speed
		if(correction > motorStraight && distance < 255) 
			correction = motorStraight/4;
		return correction;
	}
	
	@Override
	public int readUSDistance() {
		return this.distance;
	}

}
