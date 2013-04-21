import java.io.Serializable;


public class PoisonPill implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int WORK_DONE = 0;
	public static final int TRIGGER_AGGREGATE =1;
	public static final int DONT_KILL = 2;
	
	private final int state;
	
	public PoisonPill(int state){
		this.state = state;
	}
	
	public int getState(){
		return state;
	}
}
