import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.terminal.text.UnixTerminal;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.TerminalSize;

public class DisplayProgress {

	UnixTerminal terminal;
	TerminalSize screenSize;
	int width;
	int height;
	// Screen scr;
	
	final static int base = 1000;
	final static char suffix[] = {' ','k','M','G','T','P','E','Z','Y'};
	
	public DisplayProgress() {
	
		terminal = TerminalFacade.createUnixTerminal();
		terminal.enterPrivateMode();

		terminal.clearScreen();
		screenSize = terminal.getTerminalSize();		
		
		
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
			update(list.getCopying());
		}
		// update totals.
	}
	
	public void update(CopyJob current)
	{
		// want to make these positions dynamic.
		terminal.moveCursor(0, 0);
		System.out.print(current.getSourceFileName());
		
		if (screenSize.getColumns()-8 > 0) {
			terminal.moveCursor(screenSize.getColumns()-8,0);
			System.out.print(formatSeconds((int)current.getETA()));
		}

		if (screenSize.getColumns()-21 > 0) {
			terminal.moveCursor(screenSize.getColumns()-21,0);
			System.out.print(humanRead(current.getBPS()) + "B/s "); 
		}
		
		if (screenSize.getColumns()-26 > 0) {
			terminal.moveCursor(screenSize.getColumns()-26,0);
			System.out.print(formatPercent(current.getPercent()));
		}
		
		
		if (screenSize.getColumns()-39 > 0) {
			terminal.moveCursor(screenSize.getColumns()-39,0);
			System.out.print( padBytes(current.getCompletedBytes()));
		}

		// percent bar
		
		terminal.moveCursor(0,1); terminal.putCharacter('[');
		terminal.moveCursor(screenSize.getColumns(),1); terminal.putCharacter(']');
		
		int bar =(int)( (screenSize.getColumns()-2) * current.getPercent() / 100);
		
		for (int i=0; i < bar; i++) {
			terminal.moveCursor(i+1,1); terminal.putCharacter('=');
		}
		
		/*
		scr.putString(0,1,current.getSourceFileName(),Color.BLACK,Color.WHITE);
		scr.putString(41,1,padBytes(current.getCompletedBytes()),Color.BLACK,Color.WHITE);
		scr.putString(54,1,formatPercent(current.getPercent()),Color.BLACK,Color.WHITE);
		scr.putString(59,1,humanRead(current.getBPS()),Color.BLACK,Color.WHITE);
		scr.putString(72,1,formatSeconds((int)current.getETA()),Color.BLACK,Color.WHITE);
		*/
		
	}
	
	public void close() {
		terminal.exitPrivateMode();
	}

}