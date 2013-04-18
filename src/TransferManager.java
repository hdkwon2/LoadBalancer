
public class TransferManager {

	private static final int CAPACITY = 20;
	
	private final Adapter adapter;
	private final DataListener listener;
	private final Sender sender;
	
	public TransferManager(Adapter adapter){
		
		this.adapter = adapter;
		listener = new DataListener(this); // blocks until a connection is made
		sender = new Sender(CAPACITY, listener.getSocket());
		
		new Thread(listener).start();
		new Thread(sender).start();
	}
	
	/**
	 * Puts data into data queue
	 * @param job
	 */
	public void transferData(Integer job){
		sender.addToMessageQueue(job);
	}
	
	public void receivedData(Integer job){
		adapter.pushWorkToQueue(job);
	}
	
	/**
	 * Called when the receiver receives a poison pill indicating the other side terminated the sender
	 * Aggregate data into our local matrix.
	 * @param matrix
	 * @param rows
	 */
	public void listenerFinished(int [][] matrix, int [] rows){
		for(int i=0; i < rows.length; i++){
			int row = rows[i];
			for(int j=0; j < matrix[row].length; j++){
				// copy the result over to our local result
				adapter.storeValue(row, j, matrix[row][j]);
			}
		}
	}
}
