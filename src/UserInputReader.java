import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class UserInputReader implements Runnable {

	private final HardwareMonitor monitor;
	
	public UserInputReader(HardwareMonitor monitor){
		this.monitor = monitor;
	}
	
	@Override
	public void run() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input = null;
		Integer value = null;
		while(true){
			System.out.print("Throttling value: ");
			try {
				input = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(input.equals("quit")){
				System.err.println("User input reader exiting");
				break;
			}
			monitor.setThrottleValue(Integer.parseInt(input));
		}	
	}

}
