
public class StateManager {
	private static final int CAPACITY = 20;
	
	private final Adapter adapter;
	private final Sender sender;
	private final StateListener listener;
	
	public StateManager(Adapter adapter){
		this.adapter = adapter;
		this.listener = new StateListener(this);
		this.sender = new Sender(CAPACITY, listener.getSocket());
	}
	
	/**
	 * State message is sent when the number of jobs goes below threshold
	 * @param state
	 */
	public void receivedState(State state){
		int numJobs = state.numJobs;
		if(adapter.needLoadBalancing(numJobs)){
			
		}
	}
	
	/**
	 * Adds a new state message to message queue. 
	 * Does not involve system call.
	 * @param numJobs
	 * @param throttle
	 */
	public void sendState(int numJobs, int throttle){
		State state = new State();
		state.numJobs = numJobs;
		state.throttle = throttle;
		sender.addToMessageQueue(state);
	}
	
	class State{
		int numJobs;
		int throttle;
	}
}
