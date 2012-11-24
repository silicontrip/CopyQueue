import java.util.ArrayList;
import java.util.Iterator;

public class CopyJobList extends ArrayList<CopyJob>
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
		
		
		Iterator<CopyJob> it =  this.iterator();
		while (it.hasNext()) {
			CopyJob cj = it.next();
			if (cj.isComplete()) 
				completedNumber++;
			if (cj.isErrored())
				errorNumber ++;
			
			completedBytes += cj.getCompletedBytes();
			remainingBytes += cj.getRemainingBytes();
			totalBytes += cj.getSize();
			
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
		Iterator<CopyJob> it =  this.iterator();
		while (it.hasNext()) {
			CopyJob cj = it.next();
			if (cj.isWaiting()) 
				return cj; 
		}
		return null;
	}

	boolean hasFirstWaiting() {
		Iterator<CopyJob> it =  this.iterator();
		while (it.hasNext()) {
			if (it.next().isWaiting()) 
				return true; 
		}
		return false;
	}
	
	CopyJob getCopying() {
		Iterator<CopyJob> it =  this.iterator();
		while (it.hasNext()) {
			CopyJob cj = it.next();
			if (cj.isCopying()) 
				return cj; 
		}
		return null;
	}
	
	
	boolean isCopying() {
		Iterator<CopyJob> it =  this.iterator();
		while (it.hasNext()) {
			if (it.next().isCopying()) 
				return true; 
		}
		return false;
	}
	
	
}