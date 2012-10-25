package chat.server;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import chat.ChatConstants;

/**
 * Abstract class for a message to be sent by the broadcast thread.
 * 
 * @author Michael Ballantyne and Matthew Castillo
 *
 */
public abstract class Message
{
	public abstract void send(ConcurrentMap<String, Writer> clients);
	
	protected void sendToAll(String command, ConcurrentMap<String, Writer> clients) {
		for (Map.Entry<String, Writer> client : clients.entrySet()) {
			try {
				client.getValue().write(command + ChatConstants.LINE_TERMINATOR);
				client.getValue().flush();
			} catch (IOException e) {
				e.printStackTrace(System.err);
				clients.remove(client.getKey());
			}
		}
	}
}
