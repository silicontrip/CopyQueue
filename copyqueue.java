import java.io.File;
import java.util.Iterator;


public  class  copyqueue {
	
	
	public static void main(String[] args) {
		
		CopyJobList jobList = CopyJobList.getInstance();
		
		// set up listener
		
		try { 
			Thread listenThread = new Thread(new CopyListener());
			listenThread.start();
			
			Display display = new Display();
			
			while (true) {
				
				display.updateList(jobList);
				
				while (jobList.hasFirstWaiting()) {
					
					CopyJob cj = jobList.getFirstWaiting();
					
					Thread thread = new Thread(cj);	
					
					thread.start();
					
					while (thread.isAlive()) {
						display.updateList(jobList);
						display.updateProgress(jobList);
						
						//	System.out.println(" " + copy.getPercent() +"% : " + copy.getRemainingBytes() + " : " + copy.getBPS());
						
						try {
							Thread.sleep(250);
						} catch (java.lang.InterruptedException e) {
							;
							// but I don't want to be interrupted.
						}
					}
					display.updateProgress(jobList);
					display.updateList(jobList);
					try {
						thread.join();
					} catch (java.lang.InterruptedException e) {
						;
						//  I don't care if I'm interrupted here.
					}
					
				}
				
				try {
					Thread.sleep(250);
				} catch (java.lang.InterruptedException e) {
					;
					// but I don't want to be interrupted.
				}
				
			}
			// want to allow ctrl-c exit. 
			// or some form of interface quit
			// never called
			// display.close();
		} catch (java.io.IOException e) {
			System.out.println ("Could not create the listening socket: " + e.getMessage());
			e.printStackTrace();
		}
	}
}