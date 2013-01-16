

import java.io.*;

public class CopyJob  implements Runnable, Serializable {
	
	public final static String statusCode[] = {"Success","Pending","Copying","Error"};
	public final static int OK = 0;
	public final static int WAIT = 1;
	public final static int COPY = 2;
	public final static int ERROR = 3;
	
	
	private File Source;
	private File Destination;
	private int status;
	
	transient private Exception failReason;
	private int bufferSize;
	transient private long startTime;
	transient private long currentBytes;
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
	
	public CopyJob (String src, String dst) 
	{
		this();
		
		setSource(src);
		setDestination(dst);
	}
	
	
	public void run() {
		this.copy();
	}
	
	
	public void copy() {
		
		
		if ( getSourceFileName() != null) {
			InputStream inStream = null;
			OutputStream outStream = null;
			
			
			try{
				
				//          System.out.println ("copy: " + getSourceFileName());
				//			System.out.println ("to: " + getDestinationPathName() + File.separator + getSourceFileName());
				
				inStream = new FileInputStream(Source);
				
				File output  = new File (getDestinationPathName() + File.separator + getSourceFileName());
				
				outStream = new FileOutputStream(output);
				
				byte[] buffer = new byte[bufferSize];
				
				
				int length;
				//copy the file content in bytes 
				
				startTime = java.lang.System.currentTimeMillis();
				status = COPY;
				while ((length = inStream.read(buffer)) > 0){
					
					outStream.write(buffer, 0, length);
					currentBytes += length;
				}
				
				
				status = OK;
				
			} catch (Exception e) {
				failReason = e;
				status = ERROR;
				
			} 
			finally {
				if (inStream != null) {
					inStream.close();
				}
				if (outStream != null) {
					outStream.close();
				}
			}
		} else if (!Destination.exists()) {
			// make it
			try {
				Destination.mkdir();
				status = OK;
			} catch (Exception e) {
				failReason = e;
				status = ERROR;
			}
		
		}
	}
	
	public void resetStatus () {
		if (!isCopying()) {
			status = WAIT;
			currentBytes = 0;
		}
	}
	
	public boolean isCopying() { return status == COPY; }
	public boolean isWaiting() { return status == WAIT; }
	public boolean isComplete() { return status == OK; }
	public boolean isErrored() { return status == ERROR; }
	
	
	public int getStatus() { return status; }
	
	public Exception getStatusException() { return failReason; }
	
	public String getSourceFileName() {
		if (Source != null) {
			return Source.getName();
		}
		return null;
	}
	
	public String getSourcePath() {
		if (Source != null) {
			try {
				return Source.getCanonicalPath();
			} catch (IOException e) {
				return e.getMessage();
			}
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
		if (src == null) {
			Source = null;
		} else if (src.isFile()) {
			fileLength = src.length();
//if (fileLength > 0) {
				Source = src;
			//}
		}
		
	}
	
	public void setSource (String src) 
	{
		if (src != null) {
			File f = new File (src);
			if (f == null) {
				Source = null;
			} else if (f.isFile()) {
				fileLength = f.length();
				Source = f;
			}
		} else {
			Source = null;
		}
	}
	
	public void setDestination (String dst) 
	{
		File f = new File (dst);
		if (f == null) {
			Destination = null;
		} else {
			Destination = f;
		}
	}
	
	
	public void setDestination (File dst)
	{
		Destination = dst;
	}
	
	public File getSource() { return Source; }
	public File getDestination() { return Destination; }
	
	public long getFileLength() { return fileLength; }
	public void setFileLength(long dummy) { fileLength = Source.length(); }
	
	
	public long getSize() { return fileLength; }
	public long getCompletedBytes() { return currentBytes; }
	public double getPercent() { return 100.0 * currentBytes / fileLength; }
	public long getRemainingBytes() { return fileLength - currentBytes; }
	
	public double getETA() { 
		
		double bps = getBPS();
		if (bps > 0) {
			return getRemainingBytes() / bps;
		}
		return -1;
	}
	
	public double getBPS() { 
		
		long now = java.lang.System.currentTimeMillis();
		
		if (now > startTime) {
			return 1000.0 * currentBytes / (now - startTime); 
		}
		return 0 ;
	}
	
	public void setBufferSize(int bs) { 
		if (bs > 0) {
			bufferSize = bs; 
		}
	}
	
	public String toString () {
			return ( "" + getStatus() +"|"+ getSourcePath() +"|" + getDestinationPathName() +"|" + getSize() + "|" + bufferSize);
	}
	
}