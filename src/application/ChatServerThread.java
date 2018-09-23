package application;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatServerThread extends Thread{

	protected Socket socket;
	protected DataOutputStream out;
	protected BufferedReader input;
	protected ChatServer chatServer;
	protected ThreadsArray threadsArray;
	
	public String nick = null;
	protected PrintWriter outLog = null;
	private boolean stopped = false;
	private String name;
	
	public ChatServerThread(Socket socket, PrintWriter outLog, ChatServer chatServer) {
		super();
		
		this.socket = socket;
		this.chatServer = chatServer;
		this.threadsArray = ChatServer.threadsArray;
		this.outLog = outLog;
		name = this.getName();
	}
	
	public void run() {
		try {
			try {
				out = new DataOutputStream(socket.getOutputStream());
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch(IOException e) {
				ChatServer.writeLog(name + "Error - stream creation: " + e);
				stopped = true;
				return;
			}
		
			String line;
		
			while(!stopped) {
				try {
					line = input.readLine();
					ChatServer.writeLog(name + "String line received: " + line);
				
					if(line == null) {
						ChatServer.writeLog(name + "Thread \"" + this + "\"" + "stopped. Null message received");
						return;
					}
				
					processMessage(line);
				
				} catch(IOException e) {
					ChatServer.writeLog(name + "Error while reading string line: " + e);
					return;
				}
			}
		} finally {
			quit();
		}
	}
	
	public String getInfo() {
		String info;
		
		info = "IP" + socket.getInetAddress().getHostAddress() + " ";
		info += "Port" + socket.getPort() + " ";
		info += name + " nick: " + nick;
		
		return info;
	}
	
	public void send(String line) {
		try {
			out.writeBytes(line + "\n");
			ChatServer.writeLog(name + "Sent: " + line);
		} catch (IOException e) {
			ChatServer.writeLog(name + "IO error: " +e);
			quit();
		}
	}
	
	public void sendToAll(String line) {
		synchronized(threadsArray) {
			for(int i = 0; i < threadsArray.size(); i++) {
				ChatServerThread serverThread = ChatServer.threadsArray.getThread(i);
				if(serverThread.nick != null)
					serverThread.send(line);
			}
		}
	}
	
	public void processMessage(String line) {
		ChatServer.writeLog(name + "Line is being processed: " + line);
		
		if(line.length() < 5) {
			if(nick != null) {
				sendToAll(nick + "> " + line);
			} else {
				send("/nonk");
			}
			return;
		}
		
		String command = line.substring(0, 5);
		
		if(command.equals("/quit")) {
			send("/quit");
			stopped = true;
			//quit();
		} else if(command.equals("/nick")) {
			
			if(line.length() < 7) {
				send("/nonk");
				return;
			}
			
			String nick = line.substring(6, line.length());
			
			if(!nickExists(nick)) {
				send("/nkok " + nick);
				
				if(this.nick != null) {
					sendToAll("/nkrm " + this.nick);
				}
				
				this.nick = nick;
				sendAllNicks();
				sendToAll("/nick " + nick);	
			} else {
				send("/nkex");
			}
			
		} else {
				
			if(nick != null) {
				sendToAll(nick + "> " + line);
			} else {
				send("/nonk");
			}
				
		}
	}	
	
	
	public boolean nickExists(String nick) {
		synchronized(threadsArray) {
			for(int i = 0; i < threadsArray.size(); i++) {
				ChatServerThread serverThread = threadsArray.getThread(i);
				if((serverThread.nick != null) && serverThread.nick.equals(nick)) {
					return true;
				}
			}
			return false;
		}
	}
	
	public void quit() {
		synchronized(threadsArray) {
			threadsArray.removeElement(this);
		}
		sendToAll("/nkrm " + this.nick);
		ChatServer.writeLog(name + "Thread stopped");
	}
	
	public void sendAllNicks() {
		synchronized(threadsArray) {
			for(int i = 0; i < threadsArray.size(); i++) {
				String nick = threadsArray.getThread(i).nick;
				if((nick != null) && !nick.equals(this.nick)) {
					send("/nick " + nick);
				}
			}
		}
	}
}
