
public class StateListener extends Listener{

	private final StateManager manager;
	
	public StateListener(StateManager manager){
		super();
		
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
