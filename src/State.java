import java.io.Serializable;


public class State implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int numJobs;
		int throttle;
		int cpuUsage;
}