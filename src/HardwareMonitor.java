
public class HardwareMonitor {

	private final Adapter adapter;
	private final UserInputReader reader;
	private final CPUMonitor cpuMonitor;
	
	public HardwareMonitor(Adapter adapter){
		this.adapter = adapter;
		this.reader = new UserInputReader(this);
		this.cpuMonitor = new CPUMonitor();
		new Thread(reader).start();
		new Thread(cpuMonitor).start();
	}
	
	public void setThrottleValue(int value){
		adapter.setThrottle(value);
	}
	
	public int getCPUUsage(){
		return cpuMonitor.getCurrentCpuUsage();
	}
	
	public void jobDone(){
		cpuMonitor.stopRunning();
	}
}
