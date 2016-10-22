package logTutorial;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Log {

	static PrintStream writer = System.out;

	public static enum Sender {
		odometer, Navigator, usSensor, avoidance, lightSensor
	}

	static boolean printOdometer;
	static boolean printNavigator;
	static boolean printUsSensor;
	static boolean printAvoidance;
	static boolean printLightSensor;

	public static void log(Sender sender, String message) {
		long timestamp = System.currentTimeMillis() % 100000;

		if (sender == Sender.Navigator && printNavigator) {
			writer.println("NAV::" + timestamp + ": " + message);
		}
		if (sender == Sender.odometer && printOdometer) {
			writer.println("ODO::" + timestamp + ": " + message);
		}
		if (sender == Sender.usSensor && printUsSensor) {
			writer.println("US::" + timestamp + ": " + message);
		}
		if (sender == Sender.avoidance && printAvoidance){
			writer.println("OA::" + timestamp + ": " + message);
		}
		if (sender == Sender.lightSensor && printLightSensor){
			writer.println("LS::" + timestamp + ": " + message);
		}

	}

	public static void setLogging(boolean nav, boolean odom, boolean us,boolean avoid, boolean ls) {
		printNavigator = nav;
		printOdometer = odom;
		printUsSensor = us;
		printAvoidance = avoid;
		printLightSensor = ls;
	}

	public static void setLogWriter(String filename) throws FileNotFoundException {
		writer = new PrintStream(new File(filename));
	}

}
