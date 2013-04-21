import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


public class Sender implements Runnable{


	protected ObjectOutputStream os;
	private final BlockingQueue queue;
	
	
	public Sender(int capacity, Socket sock){
		queue = new ArrayBlockingQueue(capacity);
		try {
			os = new ObjectOutputStream(sock.getOutputStream());
		} catch (IOException e) {
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
	
	@Override
	public void run() {
		
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
		System.out.println("Sender quitting");
	}

}
