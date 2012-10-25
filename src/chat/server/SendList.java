package chat.server;
import java.io.Writer;
import java.util.concurrent.ConcurrentMap;

/**
 * Creates and sends a list of all users currently in the chat server.
 * 
 * @author Michael Ballantyne and Matthew Castillo
 *
 */
public class SendList extends Message
{

	@Override
	public void send(ConcurrentMap<String, Writer> clients)
	{
		StringBuilder userlist = new StringBuilder();
		userlist.append("/sendlist");
		for(String username : clients.keySet())
		{
			userlist.append(" ");
			userlist.append(username);
		}
		sendToAll(userlist.toString(), clients);
	}

}
