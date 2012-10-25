package chat.server;

import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.io.*;

import chat.ChatConstants;
import chat.Validator;

/**
 * This is the code run in a separate thread for each incoming chat client req.
 * It is responsible for handling the input and output streams and dispatching work to
 * the broadcast, validator and response classes.
 *  
 * @author Michael Ballantyne and Matthew Castillo
 * 
 * based in the Connection and Handler classes for EchoServer by Greg Gagne 
 */
public class ClientThread implements Runnable
{
	public static final int TIMEOUT_SECONDS = 15;
	private Socket client;

	BufferedReader fromClient;;
	BufferedWriter toClient;
	
	ConcurrentMap<String, Writer> clients;
	BlockingQueue<Message> messageQueue;
	
	String username = null;

	public ClientThread(Socket client, ConcurrentMap<String, Writer> clients, BlockingQueue<Message> messageQueue) {
		this.client = client;
		this.clients = clients;
		this.messageQueue = messageQueue;

	}

	// Adds the line terminator to messages from the server and outputs them to the client
	private void writeLine(String line) throws IOException {
		toClient.write(line + ChatConstants.LINE_TERMINATOR);
		toClient.flush();
	}

	@Override
	public void run() { 
		try {
			fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
			toClient = new BufferedWriter(new PrintWriter(client.getOutputStream()));

			client.setSoTimeout(TIMEOUT_SECONDS * 1000);
			
			
			try {
				writeLine("Enter username:");
			
				int usernameValidationCode = -1;
				do {
					username = fromClient.readLine();
					usernameValidationCode = Validator.validateUsername(username);
					if (usernameValidationCode == 0) {
						if (clients.get(username) != null) {
							usernameValidationCode = 1;
						}
					}
					writeLine(String.valueOf(usernameValidationCode));
				} while(usernameValidationCode != 0);	

			} catch (SocketTimeoutException e) {
				// Throw the timeout along so that we just close and don't send an error back
				throw e;
			}
			
			client.setSoTimeout(0);
			client.setKeepAlive(true);
			
			clients.putIfAbsent(username, toClient);
			messageQueue.add(new BroadcastMessage("System", username + " entered the chatroom"));
			messageQueue.add(new SendList());
			
			while (true) {
				String line = fromClient.readLine();
				
				String command = null;
				String parameter = "";
				
				if (line.contains(" ")) {
					 command = line.substring(0, line.indexOf(" "));
					 parameter = line.substring(line.indexOf(" ") + 1);
				}
				else {
					command = line;
				}
				
				
				if (command.equals("/exit")) {					
					break;
				}
				if (command.equals("/chat")) {
					int messageCode=Validator.validateMessage(parameter);
					switch (messageCode) {
					case 0: messageQueue.add(new BroadcastMessage(username, parameter)); break;
					case 1:	throw new RuntimeException("Message over 200 character limit");
					case 2:	throw new RuntimeException("Message contains invalid characters");
					}
				}
				
				if (command.equals("/pmclient")) {
					String toUser = parameter.substring(0, parameter.indexOf(" "));
					String message = parameter.substring(parameter.indexOf(" ") + 1);
					
					if (clients.containsKey(toUser))
					{
						messageQueue.add(new PrivateMessage(username, toUser, message));
					}
					else {
						writeLine(String.valueOf(4));
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			try { 
				writeLine(String.valueOf(5));
			} catch (IOException ignore) {}
		}
		finally {
			try {
				clients.remove(username);
				messageQueue.offer(new BroadcastMessage("System", username + " left the chat room"));
				messageQueue.offer(new SendList());
			} catch (Exception ignore) {}
			
			if (fromClient != null)
				try { 
					fromClient.close();
				} catch (IOException e) {
					e.printStackTrace(System.err);
				}
			if (toClient != null)
				try {
					toClient.close();
				} catch (IOException e) {
					e.printStackTrace(System.err);
				}
			if (client != null)
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace(System.err);
				}
		}
	}
}

