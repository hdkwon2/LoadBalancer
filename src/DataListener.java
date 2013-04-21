import java.util.ArrayList;


/**
 * Listens for data transmission.
 * Also responsible for bootstrap and aggregation phases.
 * @author cs423
 *
 */
public class DataListener extends Listener{

	private static final int PORT_NUM = 10102;
	private final TransferManager manager;
	
	public DataListener(TransferManager manager){
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
				
//				// All jobs done
//				double result[][] = (double [][])readObject();
//				ArrayList rows =  (ArrayList)readObject();
//				manager.listenerFinished(result, rows);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				manager.jobDone(new PoisonPill(PoisonPill.WORK_DONE));
				System.err.println("DataListener exiting");
				break;
			}
			// add work
			manager.receivedData((Integer) obj);
		}

	}
	
}
