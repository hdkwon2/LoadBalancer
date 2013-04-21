
public class Bootstrap extends Listener{

	private static final int PORT_NUM = 10100;
	Adapter adapter;
	
	public Bootstrap() {
		super(PORT_NUM);
	}

	@Override
	void doJob() {
		double left[][] = (double [][]) readObject();
		double right[][] = (double [][]) readObject();
		
		adapter = new Adapter(left, right);
	}

	public Adapter getAdapter(){
		return adapter;
	}

	public static void main(String [] args){
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.run();
		Adapter adapter = bootstrap.getAdapter();
		adapter.waitForLocalWorks();
		// wait for the remote to finish its job
		while(!adapter.isRemoteDone()){
			adapter.waitForRemoteWorks();
		}
		
		double result [][] =adapter.getResultMatrix();
		for(int i=0; i < result.length; i++){
			for(int j=0; j < result[i].length; j++){
				System.out.print(result[i][j] + " ");
			}
			System.out.println();
		}
		
	}
}
