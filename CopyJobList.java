import java.util.ArrayList;
import java.util.Iterator;

public class CopyJobList extends ArrayList<CopyJob>
{

	static CopyJobList instance = null;

	public static CopyJobList getInstance() {
		if (instance == null) {
			instance = new CopyJobList();
		}
		return instance;
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