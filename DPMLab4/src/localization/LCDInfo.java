package localization;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class LCDInfo implements TimerListener{
	public static final int LCD_REFRESH = 100;
	private Odometer odo;
	private Timer lcdTimer;
	private TextLCD LCD;
	private USLocalizer usLocalizer;
	
	// arrays for displaying data
	private double [] pos;
	
	public LCDInfo(Odometer odo, TextLCD LCD, USLocalizer usLocalizer) {
		this.odo = odo;
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		this.LCD = LCD;
		this.usLocalizer = usLocalizer;
		// initialise the arrays for displaying data
		pos = new double [3];
		
		// start the timer
		lcdTimer.start();
	}
	
	public void timedOut() { 
		odo.getPosition(pos);
		LCD.clear();
		LCD.drawString("X: ", 0, 0);
		LCD.drawString("Y: ", 0, 1);
		LCD.drawString("H: ", 0, 2);
		LCD.drawInt((int)(pos[0] * 10), 3, 0);
		LCD.drawInt((int)(pos[1] * 10), 3, 1);
		LCD.drawInt((int)pos[2], 3, 2);
		LCD.drawString("S: ", 0, 3);
		
		//Display the value detected by the sensor, convert the meters to cm.
		LCD.drawString("" + usLocalizer.getFilteredData(), 3, 3);
	}
}
