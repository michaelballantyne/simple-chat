package chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import chat.ChatConstants;

/** Model class of the client. Encapsulates the connection to the server and
 * the available protocol functionality.
 * 
 * @author Michael Ballantyne, Matthew Castillo
 */
public class ChatModel {
	public final static int PORT = 3520;
	private InetAddress serverHost;

	Socket socket;
	BufferedReader fromServer;
	PrintWriter toServer;

	public ChatModel(String hostname) throws UnknownHostException {
		serverHost = InetAddress.getByName(hostname);
	}

	private void sendLine(String line) throws IOException {
		if (toServer == null) {
			throw new IOException("Connection to server is closed.");
		}

		toServer.write(line + ChatConstants.LINE_TERMINATOR);
		toServer.flush();
	}

	public void connect(String username) throws IOException {
		SocketAddress addr = new InetSocketAddress(serverHost, PORT);
		socket = new Socket();
		socket.connect(addr, 1000);
		socket.setSoTimeout(50);
		
		fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		toServer = new PrintWriter(socket.getOutputStream());

		String serverLine = fromServer.readLine();
		String correct = "Enter username:";
		if(!(serverLine.equals(correct))) {
			throw new IOException("Server did not respond with correct login prompt");
		}

		sendLine(username);

		serverLine = fromServer.readLine();
		int responseCode = Integer.valueOf(serverLine);
		switch (responseCode) {
		case 1: throw new RuntimeException("Username already in use, pick a different one");
		case 2: throw new RuntimeException("Invalid characters in username; only a-z,A-Z,0-9,-, and _ allowed in username");
		case 3: throw new RuntimeException("Username exceeds 20 character in length");
		}
		
		socket.setSoTimeout(0);
	}

	public void sendMessage(String message) throws IOException {
		int spaceIndex = message.indexOf(" ");
		String startCode = null;

		if (spaceIndex != -1)
			startCode = message.substring(0, spaceIndex);
		else
			startCode = "";

		if(startCode.equals("/pmclient")||startCode.equals("/chat"))
		{
			sendLine(message);
		}

		else
		{
			sendLine("/chat " + message);
		}
	}
	
	public String getLineFromServer() throws IOException
	{
		return fromServer.readLine();
	}
	
	public void closeConnection() {
		try {
			sendLine("/exit");
		} catch (Exception e) {}
		if(toServer != null)
		{
			toServer.close();
		}
		if(fromServer != null)
		{ 
			try {
				fromServer.close();
			} catch (IOException e) {}
		}
		if(socket != null)
		{
			try {
				socket.close();
			} catch (IOException e) {}
		}
	}

}
