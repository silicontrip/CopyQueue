
public  class  copyqueue {

	public static void main(String[] args) {
		
		CopyJobList jobList = CopyJobList.getInstance();
		
		// set up listener
		Thread listenThread=null;
		Display display=null;
		
		try { 
			listenThread = new Thread(new CopyListener());
			listenThread.setDaemon(true);
			listenThread.setName("CopyListener");
			listenThread.start();
			
			 display = new Display();
			
			while (true) {
				
				display.updateList(jobList);
				
				while (jobList.hasFirstWaiting()) {
					
					CopyJob cj = jobList.getFirstWaiting();
					
					Thread thread = new Thread(cj);	
					thread.setName("CopyJob");

					thread.start();
					
					while (thread.isAlive()) {
						display.updateList(jobList);
						display.updateProgress(jobList);
						
						//	System.out.println(" " + copy.getPercent() +"% : " + copy.getRemainingBytes() + " : " + copy.getBPS());
						
						try {
							Thread.sleep(250);
						} catch (java.lang.InterruptedException e) {
							// but I don't want to be interrupted.
						}
					}
					display.updateProgress(jobList);
					display.updateList(jobList);
					try {
						thread.join();
					} catch (java.lang.InterruptedException e) {
						//  I don't care if I'm interrupted here.
					}
					
				}
				
				try {
					Thread.sleep(250);
				} catch (java.lang.InterruptedException e) {
					// but I don't want to be interrupted.
				}
				
			}
			// want to allow ctrl-c exit. 
			// or some form of interface quit
			// never called
		} catch (com.googlecode.lanterna.LanternaException le) {
			if (listenThread != null) {				
				listenThread.interrupt();
			}
			if (display != null) {
				display.close();
			}
		} catch (java.io.IOException e) {
			System.out.println ("Could not create the listening socket: " + e.getMessage());
			e.printStackTrace();
		}

	}
}