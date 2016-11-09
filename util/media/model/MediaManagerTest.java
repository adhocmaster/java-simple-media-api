package util.media.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class MediaManagerTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSave() {

		//Media save ( String tempPath, long forId, String forType ) 
		String tempPath = "E:/databasemanager.java";
		long forId = 0;
		String forType = "test";
		
		MediaManager mediaManager = new MediaManager("E:/testMediaManager");
		
		try {
			
			Media media = mediaManager.save(tempPath);
			
			System.out.println( media.toString() );
			
			//testing update
			
//			Thread.sleep(2000);
//			
//			media.save();
//			
//			System.out.println( media.toString() );
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(" Threw exception ");
		}
		
	}

}
