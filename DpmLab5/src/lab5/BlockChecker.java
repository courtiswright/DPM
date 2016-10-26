package lab5;

import lejos.robotics.SampleProvider;

public class BlockChecker {
	
	SampleProvider colorValue = Main.colorSensor.getMode("RGB");
	float[] colorData = new float[colorValue.sampleSize()];	
	
	public BlockChecker() {
		
	}
	
	public boolean blockCheck() {
		colorValue.fetchSample(colorData, 0);
		if (colorData[1] > colorData[0] && colorData[0] > 0.01) {
			if (colorData[2] > colorData[0]) {
				return true;
			}
		}
		return false;
	}
}
