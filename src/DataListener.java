import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Listens for data transmission.
 * Also responsible for bootstrap and aggregation phases.
 * @author cs423
 *
 */
public class DataListener extends Listener{

	private final TransferManager manager;
	
	public DataListener(TransferManager manager){
		super();
		
		this.manager = manager;
	}
	
	private void bootstrap(){
		int left[][] = (int [][]) readObject();
		int right[][] = (int [][])readObject();		
	}

	@Override
	void doJob() {
		
		bootstrap();
		
		Object obj;
		//start listening on the data channel
		while(true){
			obj = readObject();
			if(obj instanceof PoisonPill){
				// All jobs done
				int result[][] = (int [][])readObject();
				int rows[] = (int [])readObject();
				
				manager.listenerFinished(result, rows);
				break;
			}
			// add work
			manager.receivedData((Integer) obj);
		}

	}
	
}
