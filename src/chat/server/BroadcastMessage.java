package chat.server;
import java.io.Writer;
import java.util.concurrent.ConcurrentMap;

/**
 * Used to put /chat messages sent by clients into the proper format and
 * send them to all clients.
 * 
 * @author Michael Ballantyne and Matthew Castillo
 *
 */
public class BroadcastMessage extends Message
{
	private String fromUser;
	private String message;
	
	public BroadcastMessage(String fromUser, String message) {
		this.fromUser = fromUser;
		this.message = message;
	}
	
	@Override
	public void send(ConcurrentMap<String, Writer> clients)
	{
		sendToAll("/broadcast " + fromUser + " " + message, clients);
	}

}
