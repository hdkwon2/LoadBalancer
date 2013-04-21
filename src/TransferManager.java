import java.util.ArrayList;


public class TransferManager {

	private static final int CAPACITY = 20;
	
	private final Adapter adapter;
	private final DataListener listener;
	private final DataSender sender;
	private final Object lock;
	
	public TransferManager(Adapter adapter){
		
		this.adapter = adapter;
		this.lock = new Object();
		listener = new DataListener(this); // blocks until a connection is made
		sender = new DataSender(CAPACITY, listener.getSocket());
		
		new Thread(listener).start();
		new Thread(sender).start();
	}
	
	/**
	 * Puts data into data queue
	 * @param job
	 */
	public void transferData(Integer job){
		System.err.println("Transfering " + job);
		sender.addToMessageQueue(job);
	}
	
	public void receivedData(Integer job){
		System.out.println("Received " + job +" from remote");
		adapter.pushWorkToQueue(job);
	}
	
	public void jobDone(PoisonPill pill){
		// Triggers aggregate phase
		sender.addToMessageQueue(pill);
	}
	
	/**
	 * Called when the receiver receives a poison pill indicating the other side terminated the sender
	 * Aggregate data into our local matrix.
	 * @param matrix
	 * @param rows
	 */
	public void listenerFinished(double [][] matrix, ArrayList rows){
//		sender.addToMessageQueue(new PoisonPill());
//		for(int i=0; i < rows.size() - 1; i++){
//			int row = (int) rows.get(i);
//			for(int j=0; j < matrix[row].length; j++){
//				// copy the result over to our local result
//				adapter.storeValue(row, j, matrix[row][j]);
//			}
//		}
	}
}
