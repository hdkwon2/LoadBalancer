
public class Bootstrap extends Listener{

	private static final int PORT_NUM = 10100;
	Adapter adapter;
	
	public Bootstrap() {
		super(PORT_NUM);
	}

	@Override
	void doJob() {
		int left[][] = (int [][]) readObject();
		int right[][] = (int [][]) readObject();
		
		adapter = new Adapter(left, right);
		int startRow = left.length/2;
		for(int i=startRow; i< left.length; i++){
			adapter.pushWorkToQueue(i);
		}
	}

	
}
