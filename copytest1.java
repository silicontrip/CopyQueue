import java.io.File;


public  class  copytest1 {


public static void main(String[] args) {

	
	File afile =new File(args[0]);
	File bfile =new File(args[1]);

	CopyJob copy = new CopyJob(afile,bfile);
	
	Thread thread = new Thread(copy);	
	
	thread.start();
	
	while (thread.isAlive()) {
		
		System.out.println(" " + copy.getPercent() +"% : " + copy.getRemainingBytes() + " : " + copy.getBPS());

		try {
		
			Thread.sleep(1000);

		} catch (java.lang.InterruptedException e) {
			;
			// but I don't want to be interrupted.
		}
	}

	System.out.println("Status: " + CopyJob.statusCode[copy.getStatus()]);

	if (copy.getStatus() == CopyJob.ERROR) {
		copy.getStatusException().printStackTrace();
	}
	
	try {
	thread.join();
	} catch (java.lang.InterruptedException e) {
	;
	//  I don't care if I'm interrupted here.
}
	
}

}