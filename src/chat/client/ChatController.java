package chat.client;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import chat.Validator;

/**
 * Controller class for the client - sets up the view and model then links them with the appropriate logic.
 * 
 * @author Michael Ballantyne, Matthew Castillo
 */
public class ChatController {
	private static ChatView view;
	private static ChatModel model;

	public static void main(String[] args) {
		view = new ChatView();

		view.setSendListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sendMessage();
			}

		});

		view.setExitListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (model != null)
					model.closeConnection();
				System.exit(0);
			}
		});

		view.setSendKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				/** not implemented */
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				/** not implemented */
			}
		});

		try {
			if (args.length != 2) {
				throw new RuntimeException("<username> and <hostname> arguments must be provided");
			}


			model = new ChatModel(args[1]);
			model.connect(args[0]);

			Thread serverLineReader = new Thread(new MessageReciever(view,model));
			serverLineReader.start();
		}
		catch (Exception e) {
			view.setDisplayArea("Error: " + e.getMessage() + "\n");
		}
	}

	private static void sendMessage() {
		try {
			if (model == null)
				throw new RuntimeException("can't send - connection was not established");
			
			String message = view.getSendText();
			int messageCode=Validator.validateMessage(message);
			switch (messageCode) {
			case 0: model.sendMessage(view.getSendText()); break;
			case 1: view.setDisplayArea("Your message exceeded 200 characters and was not sent\n"); break;
			case 2: view.setDisplayArea("Your message contains invaid characters and was not sent\n"); break;
			}
		} catch (Exception e) {
			view.setDisplayArea("Error: " + e.getMessage() + "\n");
		}
		view.clearSendText();
	}

}
