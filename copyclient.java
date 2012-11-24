import java.io.*;
import java.net.*;


public  class  copyclient {
	
	final static int PORT = 2679;
	final static String HOST = "127.0.0.1";
	
	
	
	public static void main(String[] args) {
		
		
		Socket comm;
		
		
		
		
		if (args.length >= 2) {
			
			//	try {
			
			
			
			File destinationFile =new File(args[args.length-1]);
			
			if (destinationFile.isDirectory()) {
				
				// need to recurse directories
				for (int i=0; i < args.length-1; i++) {
					
					File sourcefile =new File(args[i]);
					
					if (sourcefile.isFile()) {
						try {
							
							comm = new Socket(HOST,PORT);
							try {
								ObjectOutputStream oos = new ObjectOutputStream(comm.getOutputStream());
								
								
								
								
								CopyJob copy = new CopyJob(sourcefile,destinationFile);
								
								//System.out.println("Copy: " + copy);
								
								if (copy.getSourceFileName() != null && copy.getDestinationPathName()!=null) {
									oos.writeObject(copy);
								} 
								
								
							}
							catch (IOException ioe) {
								System.out.println ("Source or network: IOException occured: " + ioe.getMessage());
							}
							
							
						} catch (IOException ioe) {
							System.out.println ("Network connect: IOException occured: " + ioe);
						}
					} else if ( sourcefile.isDirectory()) {
						System.out.println ("Source Directories not handled.");

					} else {
						System.out.println ("Source is not a file.");
					}
				}
			} else {
				System.out.println ("Destination is not a directory.");
			}
		}	
	}
}