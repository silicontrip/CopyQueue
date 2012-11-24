import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.terminal.text.UnixTerminal;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.TerminalSize;

public class Display {
	
	UnixTerminal terminal;
	TerminalSize screenSize;
	int width;
	int height;
	// Screen scr;
	
	final static int base = 1000;
	final static char suffix[] = {' ','k','M','G','T','P','E','Z','Y'};
	
	public Display() {
		
		terminal = TerminalFacade.createUnixTerminal();
		terminal.enterPrivateMode();
		
		terminal.clearScreen();
		screenSize = terminal.getTerminalSize();		
		
		// divider
		
		// percent bar
		
		
		
		for (int i=0; i < screenSize.getColumns(); i++) {
			terminal.moveCursor(i,4); terminal.putCharacter('-');
		}
		
		
		
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
	
	private void clearLine(int line) {
		for (int i=0; i < screenSize.getColumns(); i++) {
			terminal.moveCursor(i,line); terminal.putCharacter(' ');
		}
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
	
	public void updateProgress(CopyJobList list) 
	{
		
		if (list.isCopying()) {
			updateProgress(list.getCopying());
		} else {
			clearLine(0); clearLine(1);
		}
		// update totals.
		
		list.updateTotals();
		terminal.moveCursor(0, 2);
		
		if (list != null)  {
			
			System.out.print("TOTAL: " + list.getCompletedNumber() + " of " + list.size() + " " +
							 humanRead(list.getCompletedBytes()) + " of " + humanRead(list.getTotalBytes()) + " " +
							 humanRead(list.getRemainingBytes()) + " remain " + list.getErrorNumber() + " Errors " );
			
			
			if (screenSize.getColumns()-14 > 0) {
				terminal.moveCursor(screenSize.getColumns()-14,2);
				System.out.print(formatPercent(list.getPercent()));
			}
			
			
			if (screenSize.getColumns()-8 > 0) {
				terminal.moveCursor(screenSize.getColumns()-8,2);
				System.out.print(formatSeconds((int)list.getETA() ));
			}
			
			
			
			// percent bar
			
			terminal.moveCursor(0,3); terminal.putCharacter('[');
			terminal.moveCursor(screenSize.getColumns(),3); terminal.putCharacter(']');
			
			int bar =(int)( (screenSize.getColumns()-2) * list.getPercent() / 100);
			
			for (int i=0; i < bar; i++) {
				terminal.moveCursor(i+1,3); terminal.putCharacter('=');
			}
		}
		
	}
	
	public void updateProgress(CopyJob current)
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