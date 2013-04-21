import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;


public abstract class Listener implements Runnable{

	
	private ServerSocket service;
	private Socket sock;
	private ObjectInputStream is;
	private final int portNum;
	
	public Listener(int portNum){
		this.portNum = portNum;
		openSocket();
	}
	
	private void openSocket(){
		try {
			service = new ServerSocket(portNum);
			sock = service.accept();
			is = new ObjectInputStream( sock.getInputStream());
			System.out.println("Socket opened at " + portNum);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
	

	public Socket getSocket(){
		return sock;
	}
	
	abstract void doJob();
	
	@Override
	public void run() {
		doJob();
		closeSocket();
	}
}
