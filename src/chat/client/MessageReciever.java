package chat.client;


/** Background thread responsible for receiving messages from the server and
 * writing them to the view.
 * 
 * @author Michael Ballantyne, Matthew Castillo
 */
public class MessageReciever implements Runnable {

	private ChatView view;
	private ChatModel model;
	public MessageReciever(ChatView view, ChatModel model) {
		this.view = view;
		this.model = model;
	}

	@Override
	public void run() {
		while(true)
		{
			String line="";
			try {
				line = model.getLineFromServer();
				
				String command = null;
				String parameter = "";
				
				if (line.contains(" ")) {
					 command = line.substring(0, line.indexOf(" "));
					 parameter = line.substring(line.indexOf(" ") + 1);
				}
				else {
					command = line;
				}
				
				String display = null;
				if (command.equals("/sendlist")) {
					display = "Users in chatroom: ";
					for (String user : parameter.split(" "))
						display = display + user + ", ";
					display = display.substring(0, display.length() - 2);
				}
				
				
				if (command.equals("/pmserver") || command.equals("/broadcast")) {
					String fromUser = parameter.substring(0, parameter.indexOf(" "));
					String message = parameter.substring(parameter.indexOf(" ") + 1);
					
					if (command.equals("/broadcast"))
						display = fromUser + ": " + message;
					
					if (command.equals("/pmserver"))
						display = "PM from " + fromUser + ": " + message;
				}
				
				if (command.equals("4"))
				{
					display = "Your private message could not be sent; that user does not exist.";
				}
				
				if (command.equals("5"))
				{
					display = "An unexpected error ocurred and the connection was closed.";
				}
				
				if (display == null)
					display = "Error - server sent unexpected message :" + line;
				
				view.setDisplayArea(display + "\n");
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}

}
