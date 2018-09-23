package application;

import java.util.Vector;

/*This class holds client's threads. 
 *It extends Vector class to enable to add and access each thread.
 */
public class ThreadsArray extends Vector{
	
	public ThreadsArray() {
		super();
	}
	
	public void add(ChatServerThread obj) {
		this.addElement(obj);
	}
	
	public ChatServerThread getThread(int index) {
		ChatServerThread result = null;
		
		try {
			result = (ChatServerThread) this.elementAt(index);
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("Exception in ThreadsArray: " + e);
		} catch(Exception e) {
			System.out.println("Exception in ThreadsArray: " + e);
		}
		
		return result;
	}
}
