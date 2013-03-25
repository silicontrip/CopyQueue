import java.util.Vector;

public class CopyJobList extends Vector<CopyJob>
{

	static CopyJobList instance = null;

	int completedNumber;
	int errorNumber;
	long completedBytes;
	long remainingBytes;
	long totalBytes;
	
	public static CopyJobList getInstance() {
		if (instance == null) {
			instance = new CopyJobList();
		}
		return instance;
	}
	
	void updateTotals() {

		completedNumber = 0;
		completedBytes = 0;
		remainingBytes = 0;
		totalBytes = 0;
		errorNumber = 0;

		for (CopyJob cj : this) {
			
			if (cj.isErrored()) {
				errorNumber ++;
			} else { // Don't count it if it errors.
				if (cj.isComplete()) 
					completedNumber++;
			
				completedBytes += cj.getCompletedBytes();
				remainingBytes += cj.getRemainingBytes();
				totalBytes += cj.getSize();
			}			
		}
	}
	
	
	int getCompletedNumber() { return completedNumber;}
	int getErrorNumber() { return errorNumber; }
	long getCompletedBytes() { return completedBytes; }
	long getRemainingBytes() { return remainingBytes; }
	long getTotalBytes() { return totalBytes; }
	double getPercent() { 
		if (totalBytes >0) {
			return 100.0 * completedBytes / totalBytes; 
		}
		return 100.0;
	}
	
	double getBPS() {
		CopyJob cj = getCopying();
		if (cj != null) {
			return cj.getBPS();
		}
		return 0;
	}
	
	double getETA() { 
		double bps = getBPS();
		if (bps > 0) {
			return getRemainingBytes() / bps;
		}
		return -1;
	}
		
	
	CopyJob getFirstWaiting() {

		for (CopyJob cj : this)
        {
			if (cj.isWaiting()) 
				return cj; 
		}
		return null;
	}

	boolean hasFirstWaiting() {
        return this.getFirstWaiting()!=null;
	}
	
	CopyJob getCopying() {
		// for (Iterator<CopyJob> it =  this.iterator(); it.hasNext();)
	 //	{
		for (CopyJob cj : this)
        {
			if (cj.isCopying()) 
				return cj; 
		}
		return null;
	}
	
	
	boolean isCopying() {
        return this.getCopying() != null;
	}
	
	
}