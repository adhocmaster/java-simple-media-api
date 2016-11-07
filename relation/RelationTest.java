package util.relation;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import adhocmaster.model.ModelException;

public class RelationTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSave() {

		try{

			Relation r = new Relation(10, 20, "test");
			r.save();
			
		} catch ( Exception e ) {
			
			fail( e.getMessage() );
			
		}
		
		Relation r = Relation.getById(10,  20, "test");
		
		assertNotNull( r );
		
		try {
			
			Relation.delete(10,  20, "test");
			
			assertNull(Relation.getById(10,  20, "test"));
			
		} catch (ModelException e) {
			// TODO Auto-generated catch block
			fail( e.getMessage() );
			
		}
		
	}

	@Test
	public void testGetByReltype() {
		
		try{

			Relation r = new Relation(10, 20, "test");
			r.save();
			r = new Relation(10, 30, "test");
			r.save();

			r = new Relation(10, 40, "notest");
			r.save();

			r = new Relation(20, 30, "test");
			r.save();
			
			List<Relation> relations =  Relation.getByReltype(10, "test");

			System.out.println("Relations: " + relations.toString() );
			
			assertNotNull( relations );
			
			assertEquals(2, relations.size());
			
			//Relation r3 = relations.get(0);

			assertEquals(relations.get(0), new Relation(10, 20, "test"));
			assertEquals(relations.get(1), new Relation(10, 30, "test"));
			

			Relation.delete(10,  20, "test");
			Relation.delete(10,  30, "test");
			Relation.delete(10,  40, "test");
			Relation.delete(20,  30, "test");
			
			
		} catch ( Exception e ) {
			
			e.printStackTrace();
			fail( e.getMessage() );
			
		}
	}

}
