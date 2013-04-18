
public class StateListener extends Listener{

	private static final int PORT_NUM = 10101;
	private final StateManager manager;
	
	public StateListener(StateManager manager){
		super(PORT_NUM);
		
		this.manager = manager;
	}
	
	@Override
	void doJob() {
		Object obj;
		//start listening on the data channel
		while(true){
			obj = readObject();
			if(obj instanceof PoisonPill){
				break;
			}
			// add work
			manager.receivedState((StateManager.State) obj);
		}
	}

}
