package eu.vital.TrustManager.taskExecutor;

/**
 * @author adminuser
 * 
 * this class is used to schedule periodic tasks
 * it creates a new thread to perform the task.
 * classes performing the task has to implement Tasker interface (method doTask).
 *
 */
public class Scheduler extends Thread{

	Tasker tasker;
	int period;
	boolean execute = true;
	
	public Scheduler (Tasker tasker, int period){
		this.tasker = tasker;
		this.period = period;
	}
	
	public void run(){
		while (execute){
			tasker.doTask();
			
			try {
				Thread.sleep(period);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stop_while(){
		execute = false;
	}
}
