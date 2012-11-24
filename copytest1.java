import java.io.File;
import java.util.Iterator;


public  class  copytest1 {
	
	
	public static void main(String[] args) {
		
		CopyJobList jobList = CopyJobList.getInstance();

		
		if (args.length >= 2) {
			
			File destinationFile =new File(args[args.length-1]);

			for (int i=0; i < args.length-1; i++) {
				File sourcefile =new File(args[i]);
			
				CopyJob copy = new CopyJob(sourcefile,destinationFile);
			
				jobList.add(copy);
			}
				
			Display display = new Display();
			
			display.updateList(jobList);

			Iterator<CopyJob> it =  jobList.iterator();
			while (it.hasNext()) {
				
				CopyJob cj = it.next();
				
				Thread thread = new Thread(cj);	
				
				thread.start();
				
				while (thread.isAlive()) {
					
					display.updateProgress(jobList);
					
					//	System.out.println(" " + copy.getPercent() +"% : " + copy.getRemainingBytes() + " : " + copy.getBPS());
					
					try {
						
						Thread.sleep(1000);
						
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
			display.close();
		}
	}
}