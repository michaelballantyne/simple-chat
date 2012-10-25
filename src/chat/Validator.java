package chat;
import java.util.regex.Pattern;

/**
 * Checks that entered usernames and messages are valid by the chat protocol.
 * 
 * @author Michael Ballantyne and Matthew Castillo
 *
 */
public class Validator
{
	public static int validateUsername(String username) {
		if (username.length() < 0 || username.length() > 20)
			return 3;
		
		// Valid characters in the username are alpanumeric, underscore and dash
		if (!(Pattern.matches("[\\p{Alnum}_-]+", username)))
			return 2;
		
		return 0;
	}

	public static int validateMessage(String message)
	{
		if(message.length() > 200)
		{
			return 1;
		}
		else
		{
			int validChars=0;
			char array[]=message.toCharArray();
			for(char c: array)
			{
				if(c<32 || c>127)
				{
					validChars=2;
				}
			}
			return validChars;
		}
	}
}
