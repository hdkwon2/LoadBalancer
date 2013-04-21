
public class HardwareMonitor {

	private final Adapter adapter;
	private final UserInputReader reader;
	
	public HardwareMonitor(Adapter adapter){
		this.adapter = adapter;
		this.reader = new UserInputReader(this);
		new Thread(reader).start();
	}
	
	public void setThrottleValue(int value){
		adapter.setThrottle(value);
	}
}
