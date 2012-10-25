package chat.server;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread used to send all messages.
 * 
 * @author Michael Ballantyne and Matthew Castillo
 *
 */
public class BroadcastThread implements Runnable
{
	ConcurrentMap<String, Writer> clients;
	BlockingQueue<Message> messageQueue;

	public BroadcastThread(ConcurrentMap<String, Writer> clients, BlockingQueue<Message> messageQueue) {
		this.clients = clients;
		this.messageQueue = messageQueue;
	}

	@Override
	public void run()
	{
		while (true) {
			Message message = null;
			try {
				message = messageQueue.take(); 
			} catch (InterruptedException Ignore) {}

			message.send(clients);
		}
	}
}
