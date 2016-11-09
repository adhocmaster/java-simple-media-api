package util.map.solr;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.junit.*;


public class SolrManagerTest {

	@Test
	public void testAdd() {
//		fail("Not yet implemented");
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		
		data.put("id", 1L);
		
		try {
		
			SolrInputDocument doc = MapEntry.createDoc(data);
			
			System.out.println(doc);
			
		} catch (IllegalArgumentException e) {
			
//			e.printStackTrace();
			
			assertEquals(e.getMessage(), "type missing");
			
//			if( ! e.getMessage().equals("type missing") )
				
			
		}
		
		data.put("type", "updated");

		data.put("lat", "9.487036");
		data.put("lng", "81.458001");

		try {
		
			SolrInputDocument doc = MapEntry.createDoc(data);
			
			System.out.println(doc);
			assertEquals(data.get("id"), doc.getFieldValue("id"));
			assertEquals(data.get("type"), doc.getFieldValue("type"));
			assertEquals(data.get( "lat" ) + "," + data.get( "lng" ), doc.getFieldValue("location"));
//			assertEquals(data.get("id"), doc.getFieldValue("id"));
			
			SolrManager.getInstance().add(doc);
			
			SolrManager.getInstance().add("map2", doc);
			
		} catch (Exception e) {
			
			e.printStackTrace();

			fail();
				
			
		}
		
		
		
	}
	
	@Test
	public void TestAddCollection(){
		
		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		
		List<String> ids = new ArrayList<String>();
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		
		data.put("id", 5000L);
		ids.add("5000");
		
		data.put("type", "equipment");

		data.put("lat", "31.487036");
		data.put("lng", "81.458001");

		SolrInputDocument doc = MapEntry.createDoc(data);
		
		docs.add(doc);
		
		data = new HashMap<String, Object>();
		
		data.put("id", 6000L);
		ids.add("6000");
		
		data.put("type", "equipment");

		data.put("lat", "19.487036");
		data.put("lng", "1.458001");

		doc = MapEntry.createDoc(data);
		
		docs.add(doc);
		
		try {

			SolrManager.getInstance().add(docs);
			SolrManager.getInstance().add("map2", docs);

			SolrManager.getInstance().deleteById(ids);
			SolrManager.getInstance().deleteById("map2", ids);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
			
		}
	}
	

}
