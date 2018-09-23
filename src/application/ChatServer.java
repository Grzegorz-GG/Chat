package application;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;


/*Server has two threads:
 *(a) console - available from a system console and designed to 
 * manipulate and access some data about the client threads,
 *(b) network - providing service for chat clients.
 */
public class ChatServer implements Runnable {

	protected static PrintWriter outLog = null;
	protected static PrintWriter outp = null;
	protected static ThreadsArray threadsArray = null;
	protected String threadType;
	
	public ChatServer(String threadType) {
		this.threadType = threadType;
	}
	
	//Console thread is used to get the status of all connections from a system console.
	public void console() {
		BufferedReader bufferedInput = null;
		String line;
		
		//Define output stream and a default charset for a console.
		try {
			outp = new PrintWriter(new OutputStreamWriter(System.out, "Cp852"), true);	
		} catch(UnsupportedEncodingException e) {
			System.out.println("Error - coding Cp852");
			outp = new PrintWriter(new OutputStreamWriter(System.out), true);
		}
		
		//Create a server log file.
		try {
			outLog = new PrintWriter(new FileOutputStream("Server--Log.txt"), true);	
		} catch(FileNotFoundException e) {
			outp.println("Not able to create log file");
			System.exit(-1);
		}
		
		//Define input stream for a console.
		try {
			bufferedInput = new BufferedReader(new InputStreamReader(System.in));
		} catch(Exception e) {
			outp.println("Error - input stream creation: " + e);
			System.exit(-1);
		}
		
		/*In this simplified version only two console commands are possible:
		 * (a) status - show the status of all threads in threadsArray,
		 * (b) quit - close the server.
		 */
		while(true) {
			try {
				line = bufferedInput.readLine();
				if(line.equals("quit")) {
					writeLog("Ending...");
					quit();
				}
				else if(line.equals("status")) {
					showStatus();
				}
				else {
					outp.println("Command not found");
				}
			} catch(IOException e) {
				writeLog("I/O error: " + e);
				System.exit(-1);
			}
		}
	}
	
	//Network thread is used to establish connection with clients.
	public void network() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		//InputStream input;
		//BufferedReader bufferedInput;
		//DataOutputStream out;
		threadsArray = new ThreadsArray();
		ChatServerThread tempThread;
		
		//Create server socket.
		try {
			serverSocket = new ServerSocket(7997);
		} catch(IOException e) {
			writeLog("Server socket error");
			System.exit(-1);
		}
		
		writeLog("Server socket initialized");
		writeLog("Server socket parameters: " + serverSocket);
		
		/*Loop waiting for connection.
		 *Each client will be placed in a separate thread
		 *(threadsArray will be populated with them).
		 */
		while(true) {
			
			try {
				socket = serverSocket.accept();
			} catch(IOException e) {
				writeLog("I/O error: " + e);
				System.exit(-1);
			}
			
			writeLog("Connection arrived...");
			writeLog("Socket parameters: " + socket);
			
			tempThread = new ChatServerThread(socket, outLog, this);
			threadsArray.add(tempThread);
			tempThread.start();
		} 
	}
	
	@Override //Run suitable method for console or network thread.
	public void run() {
		if(threadType.equals("console")) {
			console();
		}
		else if(threadType.equals("network")) {
			network();
		}
		else {
			System.out.println("Internal error");
			System.exit(-1);
		}
	}
	
	//Close server.
	protected void quit() {
		System.exit(0);
	}
	
	//Write a message in a server log.
	public static void writeLog(String line) {
		synchronized(outLog) {
			outLog.println(line);
		}
	}
	
	//Show the status of all threads in threadsArray.
	public void showStatus() {
		ChatServerThread temp;
		
		synchronized(threadsArray) {
			for(int i = 0; i < threadsArray.size(); i++) {
				temp = threadsArray.getThread(i);
				outp.println(temp.getInfo());
			}
		}
		
		if(threadsArray.size() < 1) {
			outp.println("No connections available");
		}
	}
	
	//Remove thread from threadsArray.
	public void remove(Object o) {
		synchronized(threadsArray) {
			threadsArray.removeElement(o);
		}
	}
	
	public static void main(String args[]) {
		ChatServer console = new ChatServer("console");
		ChatServer network = new ChatServer("network");
		
		new Thread(console).start();
		new Thread(network).start();
	}

}
