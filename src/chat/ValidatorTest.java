package chat;
import static org.junit.Assert.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.junit.Test;

import chat.server.Message;


public class ValidatorTest
{

	@Test
	public void testSimpleUsername()
	{
		assertEquals(0, Validator.validateUsername("draco123"));
	}

	@Test
	public void testLongUsername()
	{
		assertEquals(3, Validator.validateUsername("dracodracodracodracodraco"));
	}
	
	@Test
	public void testInvalidUsername()
	{
		assertEquals(2, Validator.validateUsername("draco!"));
	}
	
	@Test
	public void testValidUsernameWithSymbols()
	{
		assertEquals(0, Validator.validateUsername("Draco_123-matt"));
	}
	
	@Test
	public void testConcurrentMap() {
		ConcurrentMap<String, Message> map = new ConcurrentHashMap<String, Message>();
		map.remove("test");
	}
}
