import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


public abstract class Sender implements Runnable{


	protected ObjectOutputStream os;
	protected final BlockingQueue queue;
	
	
	public Sender(int capacity, Socket sock){
		queue = new ArrayBlockingQueue(capacity);
		try {
			os = new ObjectOutputStream(sock.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void send(Object obj){
		try {
			os.writeObject(obj);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addToMessageQueue(Object obj){
		try {
			queue.put(obj);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void closeSocket(){
		try {
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	abstract void doJob();
	
	@Override
	public void run() {
		doJob();
		closeSocket();
	}
}
