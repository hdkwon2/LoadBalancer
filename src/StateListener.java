
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
				/* give some time for the other node to get the poisonPill */
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				manager.jobDone(new PoisonPill(PoisonPill.WORK_DONE));
				System.err.println("StateListener exiting");
				break;
			}
			// add work
			manager.receivedState((State) obj);
		}
	}

}
