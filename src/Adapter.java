import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


public class Adapter {

	private static final double UNDERLOAD_THRESHOLD = .5;
	
	BlockingQueue<Integer> workQueue; //thread-safe
	private final int[][] leftMatrix;
	private final int[][] rightMatrix;
	private final int[][] resultMatrix;
	private final TransferManager transferManager;
	private final StateManager stateManager;
	private final HardwareMonitor hardwareMonitor;
	
	private Thread worker;
	public volatile boolean finished = false;
	private int throttle; 
	public int threshold;
	
	Adapter(int [][] left, int[][] right){
		this.leftMatrix = left;
		this.rightMatrix = right;
		this.workQueue = new ArrayBlockingQueue<Integer>(leftMatrix.length);
		this.threshold = (int) (leftMatrix.length * UNDERLOAD_THRESHOLD);
		this.resultMatrix = new int[leftMatrix.length][leftMatrix[0].length];
		dispatchWorkThread();
		makeJobs();		
		
		this.transferManager = new TransferManager(this);
		this.stateManager = new StateManager(this);
		this.hardwareMonitor = new HardwareMonitor(this);
	}
	
	private void makeJobs(){
		for(int i=0; i < leftMatrix.length; i++){
			workQueue.add(i);
		}
	}
	
	private void dispatchWorkThread(){
		worker = new Thread(new WorkThread(this));
		worker.start();
	}
	
	/**
	 * Transfer half the difference between number of our work and their work.
	 * @param numJobs
	 */
	public void transferLoad(int numJobs){
		int ourNumJobs = workQueue.size();
		int numLoadToSend = (numJobs - ourNumJobs) /2;
		if(numLoadToSend <= 0) return;
		for(int i=0; i < numLoadToSend; i++){
			transferManager.transferData(pollFromWorkQueue());
		}
	}
	
	/**
	 * Checks whether we need load balancing (send our load).
	 * We need load balancing if our number of pending jobs is over threshold
	 * and if their number of jobs is less than ours.
	 * @param numJobs
	 * @return
	 */
	public boolean needLoadBalancing(int numJobs){
		int ourNumJobs = workQueue.size();
		return (numJobs < ourNumJobs && ourNumJobs > threshold);  
	}
	
	public int getNumberOfWork(){
		return workQueue.size();
	}
	
	/**
	 * Adds a new work to work queue.
	 * Should only be called by TransferManager.
	 * @param row
	 */
	public void pushWorkToQueue(Integer row){
		try{
		workQueue.add(row);
		}catch(IllegalStateException e){
			// queue is full. Do some state load balancing
			
		}
	}
	
	/**
	 * Gets a work from work queue.
	 * @return
	 * @throws InterruptedException
	 */
	public Integer pollFromWorkQueue(){
		Integer work = null;
		try {
			work = workQueue.poll(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int workNum = workQueue.size();
		// went below the threshold, send the state
		if(workNum <= threshold){
			stateManager.sendState(workNum, this.throttle);
		}
		
		return work;
	}
	
	public int[][] getResultMatrix(){
		return resultMatrix;
	}
	
	/**
	 * Should be called only by hardware monitor
	 * @param t new throttle value set by user
	 */
	public void setThrottle(int t){
		this.throttle = t;
	}
	
/*********************************************************/
	/*
	 * Call back functions for worker thread to utilize
	 */
	public int getValue(int r, int c){
		return leftMatrix[r][c] + rightMatrix[r][c];
	}
	
	public void storeValue(int r, int c, int value){
		resultMatrix[r][c] = value;
	}
	
	public int getRowSize(int r){
		return leftMatrix[r].length;
	}
	
	static class WorkThread implements Runnable{

		Adapter adapter;
		
		public WorkThread(Adapter adapter){
			this.adapter = adapter;
		}
		
		@Override
		public void run() {
			while (true) {
				Integer row;
				row = adapter.pollFromWorkQueue();
			
				if (row == null) {
					adapter.finished = true;
					return;
				}
				int sum;
				for (int i = 0; i < adapter.getRowSize(row); i++) {
					sum = adapter.getValue(row, i);
					adapter.storeValue(row, i, sum);
				}
			}
		}
		
	}
}
