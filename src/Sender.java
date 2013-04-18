import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


public class Sender implements Runnable{

	
	private static final String LOCAL_NODE_IP = "130.126.31.48";
	private static final int LOCAL_NODE_PORT = 10102;
	
	private Socket sock;
	protected ObjectOutputStream os;
	private final BlockingQueue queue;
	
	
	public Sender(int capacity){
		queue = new ArrayBlockingQueue(capacity);
	}
	
	private void openSocket(){
		try {
			sock = new Socket(LOCAL_NODE_IP, LOCAL_NODE_PORT);
			os = new ObjectOutputStream(sock.getOutputStream());
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void send(Object obj){
		try {
			os.writeObject(obj);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addToQueue(Object obj){
		try {
			queue.put(obj);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void closeSocket(){
		try {
			os.close();
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		openSocket();
		
		Object obj = null;
		while(true){
			try {
				obj = queue.poll(10, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(obj instanceof PoisonPill){
				send(new PoisonPill());
				break;
			}
			
			send(obj);
		}
		closeSocket();
	}

}
