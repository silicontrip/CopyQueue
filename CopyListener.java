
import java.io.*;
import java.net.*;

public class CopyListener  implements Runnable {
	
	final int PORT = 2679;
	final String HOST = "127.0.0.1";
	ObjectInputStream ois;
//	ObjectOutputStream oos;
	ServerSocket listen;
	Socket comm;
	
	
	public CopyListener () throws IOException {
		InetAddress inet = InetAddress.getByName(HOST);
		listen = new ServerSocket(PORT,0,inet);
	}
	
	public void run() {
		
		CopyJobList jobList = CopyJobList.getInstance();


            do {
                try {
                    comm = listen.accept();
                    ois = new ObjectInputStream(comm.getInputStream());

                    CopyJob copy = (CopyJob) ois.readObject();
                    jobList.add(copy);

                } catch (Exception e) {
                    // not sure what to do with these.
                    // need a log...
                    System.out.println("input: " + e.getMessage());
                    e.printStackTrace();

                }

                try {
                    if (ois != null)
                        ois.close();
                    comm.close();
                } catch (Exception e) {
                    // not sure what to do with these.
                    System.out.println("close: " + e.getMessage());
                    e.printStackTrace();

                }

            } while (true);

	}
	
}