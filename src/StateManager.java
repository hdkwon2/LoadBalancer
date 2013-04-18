
public class StateManager {
	private static final int CAPACITY = 20;
	
	private final Adapter adapter;
	private final Sender sender;
	private final StateListener listener;
	
	public StateManager(Adapter adapter){
		this.adapter = adapter;
		this.sender = new Sender(CAPACITY);
		this.listener = new StateListener(this);
	}
	
	public void receivedState(State state){
		
	}
	
	public void sendState(Integer state){
		
	}
	
	class State{
		int numJobs;
		int throttle;
		
	}
}
