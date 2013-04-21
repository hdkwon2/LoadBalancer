import java.net.Socket;
import java.util.concurrent.TimeUnit;


public class StateSender extends Sender{

	public StateSender(int capacity, Socket sock) {
		super(capacity, sock);
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
				if(pill.getState() == PoisonPill.DONT_KILL){
					send(obj);
					continue;
				}
				System.err.println("StateSender exiting");
				break;
			}
			send(obj);
		}
	}

}
