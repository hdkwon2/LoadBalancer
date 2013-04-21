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
			if(obj instanceof PoisonPill | obj == null){
				if(obj instanceof PoisonPill) System.out.println("Received poison");
				// All jobs done
				double result[][] = (double [][])readObject();
				ArrayList rows =  (ArrayList)readObject();
				manager.listenerFinished(result, rows);
				break;
			}
			// add work
			manager.receivedData((Integer) obj);
		}

	}
	
}
