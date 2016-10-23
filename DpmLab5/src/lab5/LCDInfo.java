package lab5;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class LCDInfo implements TimerListener{
	public static final int LCD_REFRESH = 100;
	private Odometer odo;
	private Timer lcdTimer;
	private TextLCD LCD = LocalEV3.get().getTextLCD();;
	
	// arrays for displaying data
	private double [] pos;
	private int buttonChoice;
	
	public LCDInfo(Odometer odo) {
		this.odo = odo;
		this.lcdTimer = new Timer(LCD_REFRESH, this);

		// initialise the arrays for displaying data
		pos = new double [3];
	}
	public void startScreen(){
		do{
			LCD.clear();
	
			// ask the user whether the motors should Avoid Block or Go to locations
			LCD.drawString("< Left | Right >", 0, 0);
			LCD.drawString("       |        ", 0, 1);
			LCD.drawString(" Detect| Pt.2   ", 0, 2);
			LCD.drawString(" Block |		", 0, 3);
			LCD.drawString("       |		", 0, 4);
	
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
	}
	public int getButtonChoice(){
		return buttonChoice;
	}	
	public void startTimer(){
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
	}
}
