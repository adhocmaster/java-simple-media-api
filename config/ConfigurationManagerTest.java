package util.config;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ConfigurationManagerTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetVal() {

		assertEquals( ConfigurationManager.getInstance().getVal("OLKASDIew"), "");
			
	}


	@Test
	public void testUpdateValStringString() {
		ConfigurationManager.getInstance().updateVal("test", "value");
		ConfigurationManager.getInstance().reload(false);
		assertEquals( ConfigurationManager.getInstance().getVal("test"), "value");
	}
	@Test
	public void testUpdateValString() {

		new Configuration("test2", "value2", "yes", "developer").save();

		assertEquals( ConfigurationManager.getInstance().getVal("test2"), "value2");

		
		
	}

}
