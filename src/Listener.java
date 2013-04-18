import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;


public abstract class Listener implements Runnable{

	
	private static final int PORT_NUM = 10102;
	private ServerSocket service;
	private Socket sock;
	private ObjectInputStream is;
	
	
	private Socket openSocket(){
		try {
			service = new ServerSocket(PORT_NUM);
			sock = service.accept();
			is = new ObjectInputStream( sock.getInputStream());
			System.out.println("Socket opened at " + PORT_NUM);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sock;
	}
	
	/**
	 * Called only once during bootstrap phase
	 * @return Initial workload to be divided
	 */
	protected Object readObject(){
		Object obj = null;
		try {
			obj = is.readObject();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}
	
	private void closeSocket(){
		try {
			is.close();
			sock.close();
			service.close();
			System.out.println("Socket closed");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	abstract void doJob();
	
	@Override
	public void run() {
		openSocket();
		doJob();
		closeSocket();
		
	}
}
