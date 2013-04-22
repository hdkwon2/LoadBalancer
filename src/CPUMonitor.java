import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CPUMonitor implements Runnable{

	static long lastSystemTime      = 0;
	static long lastProcessCpuTime  = 0;
	public static int  availableProcessors = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
	/**
	 * returns how much of the cpu is getting used by the current process.
	 */
	volatile public boolean keepRunning = true;
	public boolean isRunning = false;
	private int millis = 10;
	private AtomicInteger cpuUsage = new AtomicInteger(0);

	public synchronized double getCpuUsage(){
		ManagementFactory.getThreadMXBean().setThreadCpuTimeEnabled(true);
		if ( lastSystemTime == 0 ){
			baselineCounters();
		}

		long systemTime = System.nanoTime();
		long processCpuTime = 0;

		processCpuTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
		double cpuUsage = (double) (processCpuTime - lastProcessCpuTime ) / ( systemTime - lastSystemTime )*100.0;

		lastSystemTime = systemTime;
		lastProcessCpuTime = processCpuTime;

		return cpuUsage/availableProcessors;
	}
	/**
	 * used if this is the first time getCpuUsage is being called
	 */
	 private void baselineCounters(){
		 lastSystemTime = System.nanoTime();
		 lastProcessCpuTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
	 }

	 @Override
	 public void run() {
		 isRunning = true;
		 while(this.keepRunning){
			 try {
				 Thread.sleep(millis);
			 } catch (InterruptedException e) {
				 e.printStackTrace();
			 }
			 cpuUsage.set((int) getCpuUsage()); 
		 }
		 isRunning = false;
		 System.err.println("CPUMonitor exiting");
	 }
	 public void stopRunning(){
		 this.keepRunning = false;
	 }
	 public int getCurrentCpuUsage(){
		 return (int)cpuUsage.get();
	 }
	 public void setTime(int millis){
		 this.millis = millis;
	 }
}