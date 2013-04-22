import java.io.FileWriter;
import java.io.IOException;


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

	public static void writeToFile(double [][] result){
		FileWriter fw = null;
		try {
			fw = new FileWriter("out/result.txt");
			for(int i=0; i < result.length; i++){
				for(int j=0; j < result[i].length; j++){
					fw.write(result[i][j] + " ");
				}
				fw.write("\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void main(String [] args){
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.run();
		Adapter adapter = bootstrap.getAdapter();
		adapter.waitForLocalWorks();
		
		double result [][] =adapter.getResultMatrix();
		Bootstrap.writeToFile(result);		
	}
}
