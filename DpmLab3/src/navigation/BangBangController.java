package navigation;
import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter = 40;
	private final int bandwidth = 2;
	private final int FILTER_OUT = 30;
	private final int motorLow, motorHigh;
	private int distance;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int filterControl;
	private boolean avoid;
	static double Xstart = 0;
	static double Ystart = 0;
	static double Tstart = 0;
	private static Odometer odometer;
	private boolean firstAdjust = true;
	private static double OGtheta;
//	private double endX;
//	private double endY;
	private Navigation navigator1;

	//edited constructor that now accepts odometer and navigation inputs
	//this is called from the Lab3 class to check if the sensor is closer than the band Center
	public BangBangController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
							  Odometer odometer, Navigation navigation, int motorLow, int motorHigh) {
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.odometer = odometer;
		leftMotor.setSpeed(motorHigh);				// Start robot moving forward
		rightMotor.setSpeed(motorHigh);
		leftMotor.forward();
		rightMotor.forward();
		filterControl = 0;
		navigator1 = new Navigation(odometer, leftMotor, rightMotor, Lab3.WIDTH, Lab3.LEFT_RADIUS, Lab3.RIGHT_RADIUS);

	}
	
	@Override
	public void processUSData(int distance) {
		this.distance = distance;
		int error = distance - bandCenter;
		
		//checks if the robot is closer to a block than we would like
		//if it is, boolean avoid becomes true and next loop beings
		//as the robot tries to avoid the block
		if (distance <= bandCenter) {
			avoid = true;
			Xstart = odometer.getX();
			Ystart = odometer.getY();
			Tstart = odometer.getTheta();
		}
		
		if (avoid) {
		// rudimentary filter - toss out invalid samples corresponding to null signal.
				// (n.b. this was not included in the Bang-bang controller, but easily could have).
		
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
				
				
				if (Math.abs(error) <= bandwidth) {
					leftMotor.setSpeed(motorLow);
					rightMotor.setSpeed(motorLow);
			
					leftMotor.forward();
					rightMotor.forward();
			
				} else if (odometer.getTheta() == OGtheta + (Math.PI/2)) {
					
//					endX = odometer.getX();
//					endY = odometer.getY();
					
					if (Math.toDegrees(OGtheta) > -5 && Math.toDegrees(OGtheta) < 5){
						navigator1.travelTo(60,0);
					} else {
						navigator1.travelTo(0, 60);
					}
					
				} else if (error < 0) {
					
					if (distance < bandCenter-5) {
						if (firstAdjust) {
							OGtheta = odometer.getTheta();
							firstAdjust = false;
						}
						
						leftMotor.setSpeed(motorHigh);
						rightMotor.setSpeed(0);
				
						leftMotor.forward();
						rightMotor.forward();
					}
			
				} else if (error > 0 && !firstAdjust) {
					
					
					leftMotor.setSpeed(motorLow+15);
					rightMotor.setSpeed(motorHigh);
			
					leftMotor.forward();
					rightMotor.forward();
				}
		}
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
