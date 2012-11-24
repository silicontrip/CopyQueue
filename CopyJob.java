
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class CopyJob  implements Runnable {

	public final static String statusCode[] = {"Success","Pending","Copying","Error"};
	public final static int OK = 0;
	public final static int WAIT = 1;
	public final static int COPY = 2;
	public final static int ERROR = 3;

	
	private File Source;
	private File Destination;
	private int status;

	private Exception failReason;
	private int bufferSize;
	private long startTime;
	private long currentBytes;
	private long fileLength;

	public CopyJob () {
		status = WAIT;
		Source = null;
		Destination = null;
		bufferSize = 1048576;
		failReason = null;
		currentBytes = 0;
		fileLength = 0;
	}

	public CopyJob (File src, File dst) 
	{
		this();
		
		setSource(src);
		setDestination(dst);
	}
	
	public void run() {
		this.copy();
	}
	
	
	public void copy() {

		InputStream inStream = null;
		OutputStream outStream = null;

		
		try{
			
			System.out.println ("copy: " + getSourceFileName());
			System.out.println ("to: " + getDestinationPathName() + File.separator + getSourceFileName());

			inStream = new FileInputStream(Source);
			
			File output  = new File (getDestinationPathName() + File.separator + getSourceFileName());
			
    	    outStream = new FileOutputStream(output);
			
			byte[] buffer = new byte[bufferSize];
			
			
			int length;
    	    //copy the file content in bytes 
			
			startTime = java.lang.System.currentTimeMillis();
			
    	    while ((length = inStream.read(buffer)) > 0){
				
    	    	outStream.write(buffer, 0, length);
				currentBytes += length;
    	    }
			
    	    inStream.close();
    	    outStream.close();
			
			
		} catch (Exception e) {
			failReason = e;
			status = ERROR;
		} 
		status = OK;
	}
	
	public void resetStatus () {
		if (status != COPY)
			status = WAIT;
	}

	public boolean isWaiting() {
		return status == WAIT;
	}
	
	public int getStatus() {
		return status;
	}

	public Exception getStatusException() {
		return failReason;
	}

	public String getSourceFileName() {
		if (Source != null) {
			return Source.getName();
		}
		return null;
	}
	public String getDestinationFileName() {
		if (Destination != null) {
			return Destination.getName();
		}
		return null;
	}

	public String getDestinationPathName() {
		if (Destination != null) {
			return Destination.getAbsolutePath();
		}
		return null;
	}
	
	
	public void setSource (File src)
	{
		// will have to handle directories.
		if (src.isFile()) {
			fileLength = src.length();
			if (fileLength > 0) {
				Source = src;
			}
		}
		
	}

	public void setDestination (File dst)
	{
		if (dst.isDirectory()) {
			Destination = dst;
		}
	}

	public long getSize() { return fileLength; }
	public long getCompletedBytes() { return currentBytes; }
	public double getPercent() { return 100.0 * currentBytes / fileLength; }
	public long getRemainingBytes() { return fileLength - currentBytes; }
	public double getBPS() { 
		
		long now = java.lang.System.currentTimeMillis();
		
		if (now > startTime) {
			return 1.0 * currentBytes / (now - startTime); 
		}
		return 0 ;
	}
	
	public void setBufferSize(int bs) { 
		if (bs > 0) {
			bufferSize = bs; 
		}
	}
}