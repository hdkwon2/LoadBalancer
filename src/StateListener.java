
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
			if(obj instanceof PoisonPill || obj == null){
				System.out.println("Remote is done");
				manager.remoteDone();
				break;
			}
			// add work
			manager.receivedState((State) obj);
		}
	}

}
