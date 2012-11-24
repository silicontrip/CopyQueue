import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.terminal.text.UnixTerminal;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.Terminal.SGR;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.util.Iterator;

import com.googlecode.lanterna.input.Key;

public class Display {
	
	UnixTerminal terminal;
	TerminalSize screenSize;
	
	final static int PROGRESS_SIZE = 5;
	
	int files;
	int cursorPosition;
	int displayPosition;
	
	final static int base = 1000;
	final static char suffix[] = {' ','k','M','G','T','P','E','Z','Y'};
	
	public Display() {
		
		terminal = TerminalFacade.createUnixTerminal();
		terminal.enterPrivateMode();
		
		terminal.clearScreen();
		screenSize = terminal.getTerminalSize();		
		
		cursorPosition = 0;
		displayPosition = 0;
		
		// divider
		
		
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
		return String.format("%7.3f%c",sci,suffix[log]);
		
	}
	
	private void clearLine(int line) {
		terminal.moveCursor(0,line); 
		spaces(screenSize.getColumns()-1);	
		terminal.moveCursor(0,line); 
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
	
	private void spaces (int number) {
		for (int i=0; i<number;i++)
			terminal.putCharacter(' ');
	}
	
	public void keyboardInput(CopyJobList list) {
		//  have to wait until this is called.
		// there is no callback
		
		Key keyboard;
		while ((keyboard = terminal.readInput()) != null) 
		{
			if (list.size() > 0) {
				if (keyboard.getKind() == Key.Kind.ArrowUp) {
					cursorPosition = cursorPosition > 0 ? cursorPosition-1:0;
					// may need to update displayPosition
					if (cursorPosition < displayPosition)
						displayPosition = cursorPosition;
				}
				if (keyboard.getKind() == Key.Kind.ArrowDown) {
					cursorPosition = cursorPosition < list.size() ? cursorPosition+1:list.size()-1;
					// may need to update displayPosition
					
					// bottom of displayed list
					
					int displayableRows = (screenSize.getRows() - PROGRESS_SIZE)-1;
					int displayBottom = displayableRows + displayPosition ;
					
					if (cursorPosition > displayBottom) {
						displayPosition = cursorPosition - displayableRows;
					}
					
				}
				if (keyboard.getKind() == Key.Kind.Delete || keyboard.getKind() == Key.Kind.Backspace ) {
					// remove from list.
					list.removeElementAt(cursorPosition);
					if(cursorPosition >= list.size()) {
						cursorPosition = list.size()-1;
					}
				}
				if (keyboard.getKind() == Key.Kind.Enter) {
					// retry errored list.
					list.elementAt(cursorPosition).resetStatus();
				}
			}
			if (keyboard.getKind() == Key.Kind.Escape || 
				(keyboard.getKind() == Key.Kind.NormalKey && keyboard.getCharacter() == 3)) {
				
				// exit
				
			}
			
		}
	}		
	
	public void updateList (CopyJobList list)
	{
		
		int cursor = PROGRESS_SIZE;
		// ERRORS have highest display Priority
		// WAITING files have next highest
		// COMPLETE have lowest display priority
		
		
		keyboardInput(list);
		
		int bottom = screenSize.getRows() - PROGRESS_SIZE + displayPosition;
		
		for (int iterate = displayPosition ; iterate < list.size() && iterate < bottom; iterate ++) {
			CopyJob cj = list.elementAt(iterate);
			terminal.moveCursor(0,cursor);
			
			if (iterate == cursorPosition) {
				terminal.applySGR(SGR.ENTER_REVERSE);
			}
			terminal.applyForegroundColor(Color.BLACK);
			if (cj.isComplete()) { terminal.applyForegroundColor(Color.GREEN); }
			if (cj.isErrored()) { 
				clearLine(cursor); 
				
				
				terminal.applyForegroundColor(Color.RED);
				String disp = cj.getSourceFileName() + ": " + cj.getStatusException().getMessage();
				// truncate if greater than 
				System.out.print(disp);
				
			} else {
				clearLine(cursor);
				
				System.out.print (cj.getSourceFileName());
				if (screenSize.getColumns()-(11+cj.getDestinationPathName().length())  > 0) {
					terminal.moveCursor(screenSize.getColumns()-(11+cj.getDestinationPathName().length()),cursor);
					System.out.print( cj.getDestinationPathName());
				}
				if (screenSize.getColumns()-9 > 0) {
					terminal.moveCursor(screenSize.getColumns()-9,cursor);
					System.out.print(humanRead(cj.getSize()));
				}
				
			}
			if (iterate == cursorPosition) {
				terminal.applySGR(SGR.EXIT_REVERSE);
			}
			cursor ++;
		}
		
		
		terminal.flush();
	}
	
	public void updateProgress(CopyJobList list) 
	{
		
		terminal.applyForegroundColor(Color.BLACK);
		
		if (list.isCopying()) {
			updateProgress(list.getCopying());
		} else {
			clearLine(0); clearLine(1);
		}
		
		updateTotals(list);
		
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
		
	}
	
	public void updateTotals (CopyJobList list) {
		// update totals.
		
		if (list != null)  {
			
			
			if (list.size() != files) {
				files = list.size();
				clearLine(2); clearLine(3);
			}
			
			list.updateTotals();
			terminal.moveCursor(0, 2);
			
			
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
	
	public void close() {
		terminal.exitPrivateMode();
	}
	
}