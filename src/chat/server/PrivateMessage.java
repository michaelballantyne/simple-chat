package chat.server;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.ConcurrentMap;

import chat.ChatConstants;

/**
 * Puts private messages into the proper format and sends private messages.
 * 
 * @author Michael Ballantyne and Matthew Castillo
 *
 */
public class PrivateMessage extends Message
{
	private String fromUser;
	private String toUser;
	private String message;
	
	public PrivateMessage(String fromUser, String toUser, String message) {
		this.fromUser = fromUser;
		this.toUser = toUser;
		this.message = message;
	}
	
	@Override
	public void send(ConcurrentMap<String, Writer> clients)
	{
		Writer toClient = clients.get(toUser);

		try
		{
			toClient.write("/pmserver " + fromUser + " " + message + ChatConstants.LINE_TERMINATOR);
			toClient.flush();
		} catch (IOException e)
		{
			e.printStackTrace(System.err);
			clients.remove(toClient);
		}

	}

}
