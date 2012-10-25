package chat.server;
/**
 * An http server listening on port 2880. 
 *
 * This services each request in a separate thread.
 *
 *
 * @author - Michael Ballantyne
 * Modified from EchoServer by Greg Gagne.
 */

import java.net.*;
import java.io.*;
import java.util.concurrent.*;

/**
 * A chat server listening on port 3520. 
 *
 * This services each chat client in a separate thread.
 *
 *
 * @author - Michael Ballantyne and Matthew Castillo
 * Modified from EchoServer by Greg Gagne.
 */
public class ChatServer
{
	public static final int DEFAULT_PORT = 3520;
	
	private static final Executor exec = Executors.newCachedThreadPool();
	
	public static void main(String[] args) throws IOException {
		ServerSocket sock = null;
				
		try {
			sock = new ServerSocket(DEFAULT_PORT);
			
			// map of usernames to their corresponding client connection (or the at least the writer)
			ConcurrentMap<String, Writer> clients = new ConcurrentHashMap<String, Writer>();
			
			// concurrent queue used to pass messages from client threads to the broadcast thread
			BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<Message>();
			
			BroadcastThread broadcast = new BroadcastThread(clients, messageQueue);	
			Thread broadcastThread = new Thread(broadcast);
			broadcastThread.start();
			
			while (true) {
				/**
				 * now listen for connections
				 * and service the connection in a separate thread.
				 */
				Runnable task = new ClientThread(sock.accept(), clients, messageQueue);
				exec.execute(task);
			}
		}
		catch (IOException ioe) { 
			ioe.printStackTrace(System.err);
		}
		finally {
			if (sock != null)
				sock.close();
		}
	}
}
