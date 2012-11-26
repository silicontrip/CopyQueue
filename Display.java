import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.terminal.text.UnixTerminal;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.Terminal.SGR;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.LanternaException;

import java.io.IOException;

public class Display {
	
	UnixTerminal terminal;
	TerminalSize screenSize;
	
	final static int PROGRESS_SIZE = 5;
	
	int files;
	int cursorPosition;
	int displayPosition;
	
	
	int listFiles;
	int listComplete;
	int listError;
	
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
		fillLine(4,'-');		
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
	
	
	private void drawBar (int line, double per) {
		terminal.moveCursor(0,line); terminal.putCharacter('[');
		terminal.moveCursor(screenSize.getColumns(),line); terminal.putCharacter(']');
		
		int totalLength = screenSize.getColumns()-2;
		
		int bar =(int)(totalLength * per / 100);
		
		terminal.moveCursor(1,line);
		fillChar(bar,'=');
		fillChar(totalLength-bar,' ');
		
	}
	
	private void fillLine(int line,char ch) {
		terminal.moveCursor(0,line); 
		fillChar(screenSize.getColumns(),ch);	
		terminal.moveCursor(0,line); 
	}
	
	private void clearLine(int line) {
		fillLine(line,' ');
	}
	
	private void fillChar (int number,char ch ) {
		for (int i=0; i<number;i++)
			terminal.putCharacter(ch);
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
	
	
	public boolean keyboardInput(CopyJobList list) throws LanternaException {
		//  have to wait until this is called.
		// there is no callback
		
		boolean keypress = false;
		
		Key keyboard;
		while ((keyboard = terminal.readInput()) != null) 
		{
			if (list.size() > 0) {
				if (keyboard.getKind() == Key.Kind.ArrowUp) {
					keypress = true;
					cursorPosition = cursorPosition > 0 ? cursorPosition-1:0;
					// may need to update displayPosition
					if (cursorPosition < displayPosition)
						displayPosition = cursorPosition;
				}
				if (keyboard.getKind() == Key.Kind.ArrowDown) {
					keypress = true;

					cursorPosition = cursorPosition < list.size()-1 ? cursorPosition+1:list.size()-1;
					// may need to update displayPosition
					
					// bottom of displayed list
					
					int displayableRows = (screenSize.getRows() - PROGRESS_SIZE)-1;
					int displayBottom = displayableRows + displayPosition ;
					
					if (cursorPosition > displayBottom) {
						displayPosition = cursorPosition - displayableRows;
					}
					
				}
				if (keyboard.getKind() == Key.Kind.PageUp)
				{
					keypress = true;

					int scroll = (screenSize.getRows() - PROGRESS_SIZE) -1;
					
					cursorPosition = cursorPosition-scroll  >= 0 ? cursorPosition-scroll:0;
					// may need to update displayPosition
					if (cursorPosition < displayPosition)
						displayPosition = cursorPosition;
					
					
				}
				if (keyboard.getKind() == Key.Kind.Home)
				{		
					keypress = true;

					cursorPosition = 0;
					// may need to update displayPosition
					if (cursorPosition < displayPosition)
						displayPosition = cursorPosition;
				}
				
				if (keyboard.getKind() == Key.Kind.PageDown) {
					keypress = true;
					
					int scroll = (screenSize.getRows() - PROGRESS_SIZE)-1;

					cursorPosition = cursorPosition+scroll < list.size() ? cursorPosition+scroll:list.size()-1;
					// may need to update displayPosition
					
					// bottom of displayed list
					
					int displayBottom = scroll + displayPosition ;
					
					if (cursorPosition > displayBottom) {
						displayPosition = cursorPosition - scroll;
					}
					
				}
				if (keyboard.getKind() == Key.Kind.End)
				{		
					keypress = true;
					
					cursorPosition = list.size()-1;
					int displayableRows = (screenSize.getRows() - PROGRESS_SIZE)-1;
					int displayBottom = displayableRows + displayPosition ;
					
					if (cursorPosition > displayBottom) {
						displayPosition = cursorPosition - displayableRows;
					}
				}
				
				
				if (keyboard.getKind() == Key.Kind.Delete || keyboard.getKind() == Key.Kind.Backspace ) {
					keypress = true;

					// remove from list.
					list.removeElementAt(cursorPosition);
					if(cursorPosition >= list.size()) {
						cursorPosition = list.size()-1;
					}
				}
				if (keyboard.getKind() == Key.Kind.Enter) {
					keypress = true;

					// retry errored list.
					list.elementAt(cursorPosition).resetStatus();
				}
			}
			if (keyboard.getKind() == Key.Kind.Escape || 
				(keyboard.getKind() == Key.Kind.NormalKey && keyboard.getCharacter() == 3)) {
				
				throw new LanternaException(new IOException("End of Terminal Input"));
				// exit
				
			}
			
		}
		return keypress;
	}		
	
	public void updateList (CopyJobList list) throws LanternaException
	{
		
		int cursor = PROGRESS_SIZE;
		// ERRORS have highest display Priority
		// WAITING files have next highest
		// COMPLETE have lowest display priority
		
		list.updateTotals();
		
		if (keyboardInput(list) ||
			list.getCompletedNumber() != listComplete ||
			list.getErrorNumber() != listError ||
			list.size() != listFiles) {
			
			listComplete = list.getCompletedNumber();
			listError = list.getErrorNumber();
			listFiles = list.size();
			
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
					terminal.applyForegroundColor(Color.RED);

					clearLine(cursor); 
										
					String disp = cj.getSourceFileName() + ": " + cj.getStatusException().getMessage();
					// truncate if greater than 
					if (disp.length() > screenSize.getColumns()) {
						disp = disp.substring(0,screenSize.getColumns()-1);
					}
					System.out.print(disp);
					
				} else {
					clearLine(cursor);
					
					System.out.print (cj.getSourceFileName());

//					System.out.print (cj.getSourcePath());
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
			
			if (list.size() < bottom) {
				clearLine(cursor); 
		}
			
			terminal.flush();
		}
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
		
		drawBar(1,current.getPercent());
		
		
	}
	
	public void updateTotals (CopyJobList list) {
		// update totals.
		
		if (list != null)  {
			
			list.updateTotals();
			terminal.moveCursor(0, 2);
			
			
			System.out.print("TOTAL: " + list.getCompletedNumber() + " of " + list.size() + "   " +
							 humanRead(list.getCompletedBytes()) + " of " + humanRead(list.getTotalBytes()) + "   " +
							 humanRead(list.getRemainingBytes()) + " remain   " + list.getErrorNumber() + " Error" );
			
			if (list.getErrorNumber() != 1) {
				System.out.print("s ");
			} else {
				System.out.print("  ");
			}
			
			if (screenSize.getColumns()-14 > 0) {
				terminal.moveCursor(screenSize.getColumns()-14,2);
				System.out.print(formatPercent(list.getPercent()));
			}
			
			
			if (screenSize.getColumns()-8 > 0) {
				terminal.moveCursor(screenSize.getColumns()-8,2);
				System.out.print(formatSeconds((int)list.getETA() ));
			}
			
			
			
			// percent bar
			
			drawBar(3,list.getPercent());
		}
		
	}
	
	public void close() {
		terminal.exitPrivateMode();
	}
	
}