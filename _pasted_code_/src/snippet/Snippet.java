package snippet;

public class Snippet {
	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	public static EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	public static Port usPort = LocalEV3.get().getPort("S1");
}

