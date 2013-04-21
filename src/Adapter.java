import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class Adapter {

	private static final double UNDERLOAD_THRESHOLD = .5;
	
	BlockingQueue workQueue; //thread-safe
	private final double[][] leftMatrix;
	private final double[][] rightMatrix;
	private final double[][] resultMatrix;
	private final TransferManager transferManager;
	private final StateManager stateManager;
	private final HardwareMonitor hardwareMonitor;
	
	private final Object lock;
	
	private AtomicBoolean remoteDone = new AtomicBoolean(false);
	private WorkThread worker;
	private int throttle; 
	public int threshold;
	
	
	Adapter(double [][] left, double[][] right){
		this.leftMatrix = left;
		this.rightMatrix = right;
		this.workQueue = new ArrayBlockingQueue(leftMatrix.length);
		this.threshold = (int) (leftMatrix.length * UNDERLOAD_THRESHOLD);
		this.resultMatrix = new double[leftMatrix.length][leftMatrix[0].length];
		
		this.transferManager = new TransferManager(this);
		this.stateManager = new StateManager(this);
		this.hardwareMonitor = new HardwareMonitor(this);
		lock = new Object();
		
		makeJobs();		
		dispatchWorkThread();		
	}
	
	private void makeJobs(){
		int myHalf = leftMatrix.length/2;
		for(int i=myHalf; i < leftMatrix.length; i++){
			workQueue.add(i);
		}
	}
	
	private void dispatchWorkThread(){
		worker = new WorkThread(this);
		new Thread(worker).start();
	}
	
	/**
	 * Remote node is done, and we did not send more work to them.
	 * Can safely kill work thread.
	 */
	public void setRemoteDone(){
		remoteDone.set(true);
		try {
			workQueue.put(new PoisonPill(PoisonPill.WORK_DONE));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isRemoteDone(){
		return remoteDone.get();
	}
	
	public void waitForLocalWorks(){
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * At this state, remote node is done, our job is done.
	 * 
	 * Signals the main thread, and initiates close handshake. 
	 */
	public void signalDone(){
		
		stateManager.jobDone(new PoisonPill(PoisonPill.DONT_KILL));
		transferManager.jobDone(new PoisonPill(PoisonPill.TRIGGER_AGGREGATE));
		
		synchronized (lock) {
			lock.notify();
		}
	}
	
	/**
	 * Transfer half the difference between number of our work and their work.
	 * @param numJobs
	 */
	public int transferLoad(int numJobs){
		int ourNumJobs = workQueue.size();
		int numLoadToSend = (numJobs - ourNumJobs) /2;
		if(numLoadToSend <= 0 || isRemoteDone()) return 0;
		for(int i=0; i < numLoadToSend; i++){
			transferManager.transferData((Integer) pollFromWorkQueue());
		}
		
		return numLoadToSend;
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
		return (numJobs < ourNumJobs/* && ourNumJobs > threshold*/);  
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
	public Object pollFromWorkQueue(){
		Object work = null;
		try {
			work = workQueue.poll(Integer.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(work instanceof PoisonPill) return work;
		
		int workNum = workQueue.size();
		// went below the threshold, send the state
		if(workNum <= threshold){
			stateManager.sendState(workNum, this.throttle);
		}
		
		return work;
	}
	
	public double[][] getResultMatrix(){
		return resultMatrix;
	}
	
	/**
	 * Should be called only by hardware monitor
	 * @param t new throttle value set by user
	 */
	public void setThrottle(int t){
		worker.setThrottle(t);
	}
	
/*********************************************************/
	/*
	 * Call back functions for worker thread to utilize
	 */
	public double doWork(int r, int c){
		double sum = 0;
		for(int i=0; i < leftMatrix[r].length; i++){
			for(int j=0; j < rightMatrix.length; j++){
				sum +=leftMatrix[r][i] * rightMatrix[j][c];
			}
		}
		return sum;
	}
	
	public void storeValue(int r, int c, double value){
		resultMatrix[r][c] = value;
	}
	
	public int getRowSize(int r){
		return leftMatrix[r].length;
	}
	
	static class WorkThread implements Runnable{

		Adapter adapter;
		int throttle;
		long runTime;
		
		public WorkThread(Adapter adapter){
			this.adapter = adapter;
			throttle = 0;
		}
		
		public void setThrottle(int v){
			throttle = v;
		}
		
		@Override
		public void run() {
			while (true) {
				long begin = System.nanoTime();
				Object work;
				work = adapter.pollFromWorkQueue();
			
				/* Work is done */
				if (work instanceof PoisonPill) {
					adapter.signalDone();
					System.err.println("Worker exiting");
					return;
				}
				
				Integer row = (Integer) work;
				double sum;
				for (int i = 0; i < adapter.getRowSize(row); i++) {
					sum = adapter.doWork(row, i);
					adapter.storeValue(row, i, sum);
				}
				runTime = (System.nanoTime() - begin) / 1000000;
				try {
					Thread.sleep(runTime * throttle / 100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
