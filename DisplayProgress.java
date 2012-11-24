import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.terminal.text.UnixTerminal;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal.Color;

public class DisplayProgress {

	UnixTerminal term;
	Screen scr;
	
	final static int base = 1000;
	final static char suffix[] = {' ','k','M','G','T','P','E','Z','Y'};
	
	public DisplayProgress() {
	
		term = TerminalFacade.createUnixTerminal();
		scr = new Screen(term);
		
	}
	
	private String padBytes(long bytes)
	{
		return String.format("%12d",bytes);
	}
	
	private String formatPercent(double per)
	{
		return String.format("%3.0f%%",per);
	}
	
	private String humanRead (double num) 
	{
		int log=0;
		double sci=num;
		while ( sci> base) {
			sci /= base;
			log++;
		}
		return String.format("%3.3f%c",sci,suffix[log]);

	}
	
	private String formatSeconds (int time)
	{
	
		
		if (time >= 0) {
		
			int hour = time / 3600;
			int minute = (time%3600) / 60;
			int second = (time%60);
		
			return String.format("%2d:%02d:%02d",hour,minute,second);
		}
		return "--:--:--";
	}
	
	public void update(CopyJobList list) 
	{
	
		if (list.isCopying()) {
			CopyJob current = list.getCopying();
			// want to make these positions dynamic.
			scr.putString(0,1,current.getSourceFileName(),Color.BLACK,Color.WHITE);
			scr.putString(41,1,padBytes(current.getCompletedBytes()),Color.BLACK,Color.WHITE);
			scr.putString(54,1,formatPercent(current.getPercent()),Color.BLACK,Color.WHITE);
			scr.putString(59,1,humanRead(current.getBPS()),Color.BLACK,Color.WHITE);
			scr.putString(72,1,formatSeconds((int)current.getETA()),Color.BLACK,Color.WHITE);
		}
	}

}