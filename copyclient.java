import java.io.*;
import java.net.*;


public  class  copyclient {
	
	final static int PORT = 2679;
	final static String HOST = "127.0.0.1";
	
	
	public static void addFile (File src, File dst) throws IOException {
		
		Socket comm = new Socket(HOST,PORT);
		
		ObjectOutputStream oos = new ObjectOutputStream(comm.getOutputStream());
		CopyJob copy = new CopyJob(src,dst);
		
		System.out.println("Copy: " + copy);
		oos.writeObject(copy);
	}
	
	public static void handleDirectory(File dir, File dst)  throws IOException {
		
		
		File destinationFile = new File(dst.getCanonicalPath() +  File.separator + dir.getName());
		
		addFile(null,destinationFile);
		
		File filelist[] = dir.listFiles();
		
		
		
		for (File sourcefile: filelist) {
			
			if (sourcefile.isFile()) {
				addFile(sourcefile,destinationFile);
			} else if ( sourcefile.isDirectory()) {
				handleDirectory(sourcefile,	destinationFile);						
			} else {
				System.out.println ("Source is not a file.");
			}
			
			
		}
	}
	
	public static void main(String[] args) {
		
		
		
		if (args.length >= 2) {
			
			File destinationFile =new File(args[args.length-1]);
			
			
			if (destinationFile.isDirectory() || !destinationFile.exists() ) {
				
				// create target directory
				try {
					if (!destinationFile.exists()) { addFile(null,destinationFile); }
				
				
				// need to recurse directories
				for (int i=0; i < args.length-1; i++) {
					
					try {
						
						File sourcefile =new File(args[i]);
						
						if (sourcefile.isFile()) {
							addFile(sourcefile,destinationFile);
						} else if ( sourcefile.isDirectory()) {
							handleDirectory(sourcefile,	destinationFile);						
						} else {
							System.out.println ("Source is not a file.");
						}
					} catch (IOException ioe) {
						System.out.println ("IOException: " + ioe.getMessage());
					}
					
				}
				} catch (IOException ioe) {
					System.out.println ("Directory IOException: " + ioe.getMessage());
				}
				
				
			} else {
				System.out.println ("Destination is not a directory.");
			}
		}	
	}
}