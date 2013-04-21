import java.net.Socket;
import java.util.concurrent.TimeUnit;


public class DataSender extends Sender{

	public DataSender(int capacity, Socket sock) {
		super(capacity, sock);
		// TODO Auto-generated constructor stub
	}

	@Override
	void doJob() {
		Object obj = null;
		
		while(true){
			try {
				obj = queue.poll(Integer.MAX_VALUE, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(obj instanceof PoisonPill){
				PoisonPill pill = (PoisonPill) obj;
				
				if(pill.getState() == PoisonPill.TRIGGER_AGGREGATE){
					// Wait until receiver receives results from the remote
					send(obj);
					continue;
				}
				 
				System.err.println("DataSender exiting");
				break;
			}
			
			send(obj);
		}
	}

}
