/**
 * 
 */
package util.config;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Alam
 */
public class ConfigurationTest {

	Configuration configuration = null;
	String name = "Test";
	
	@Before
	public void setUp() throws Exception {
		configuration = new Configuration( name, "test" );
	}

	@Test
	public void Addtest() {
		
		configuration.save();
		Configuration data = Configuration.getByName( name );
		
		assertEquals( configuration.getName(), data.getName() );
		assertEquals( configuration.getValue(), data.getValue() );
	}
	
	@Test
	public void EditTest(){
		
		configuration.setValue( "edit" );
		configuration.save();
		
		Configuration data = Configuration.getByName( name );
		
		assertEquals( configuration.getName(), data.getName() );
		assertEquals( configuration.getValue(), data.getValue() );
		
	}
	
	@After
	public void tearDown() throws Exception {
		
		Configuration.delete( name );
		
	}

}
