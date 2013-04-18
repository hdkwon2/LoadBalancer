import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


public class Adapter {

	BlockingQueue<Integer> workQueue;
	private final int[][] leftMatrix;
	private final int[][] rightMatrix;
	private final int[][] resultMatrix;
	Thread worker;
	public volatile boolean finished = false;
	
	Adapter(int [][] left, int[][] right){
		this.leftMatrix = left;
		this.rightMatrix = right;
		this.workQueue = new ArrayBlockingQueue<Integer>(leftMatrix.length);
		this.resultMatrix = new int[leftMatrix.length][leftMatrix[0].length];
		dispatchWorkThread();
		makeJobs();		
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
	
	public int getValue(int r, int c){
		return leftMatrix[r][c] + rightMatrix[r][c];
	}
	
	public void setValue(int r, int c, int value){
		resultMatrix[r][c] = value;
	}
	
	public int getRowSize(int r){
		return leftMatrix[r].length;
	}
	
	public void pushWorkToQueue(Integer row){
		try{
		workQueue.add(row);
		}catch(IllegalStateException e){
			// queue is full. Do some state load balancing
			
		}
	}
	
	public Integer pollFromWorkQueue() throws InterruptedException{
		return workQueue.poll(5, TimeUnit.SECONDS);
	}
	
	public int[][] getResultMatrix(){
		return resultMatrix;
	}
	static class WorkThread implements Runnable{

		Adapter adapter;
		
		public WorkThread(Adapter adapter){
			this.adapter = adapter;
		}
		
		@Override
		public void run() {
			while (true) {
				Integer row = null;
				try {
					row = adapter.pollFromWorkQueue();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (row == null) {
					adapter.finished = true;
					System.out.println("here");
					return;
				}
				int sum;
				for (int i = 0; i < adapter.getRowSize(row); i++) {
					sum = adapter.getValue(row, i);
					adapter.setValue(row, i, sum);
				}
			}
		}
		
	}
}
