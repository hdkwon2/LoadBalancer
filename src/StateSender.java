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
			
			send(obj);
			
			if(obj instanceof PoisonPill){
				System.err.println("StateSender exiting");
				break;
			}
		}
	}

}
