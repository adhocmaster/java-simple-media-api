package adhocmaster.mongo;
/**
 * Mongo manager class
 * This class is not threadsafe. Next version will be threadsafe. I am not sure whether mongoClient is threadsafe
 * Next version will also need to have a threadpool of mongo clients.
 * @author muktadir
 * @version 1.0
 * @package adhocmaster.mongo
 */
import java.util.Enumeration;
import java.util.ResourceBundle;

import org.bson.Document;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

import common.Logger;

public class MongoManager {

	Logger logger = Logger.getLogger(MongoManager.class);
	
	protected static MongoManager _instance = null;
	
	protected Morphia morphia = null;
	
	protected MongoClient mongoClient = null;
	
	protected Datastore datastore = null;
	
	protected static String databaseName = "disaster"; // can be changed in constructor
	
	private MongoManager() {

		
		ResourceBundle rb = ResourceBundle.getBundle("mongo");
		Enumeration <String> keys = rb.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = rb.getString(key);
			System.out.println(key + ": " + value);
		}
		
//		mongoClient = new MongoClient();
		
		if( rb.containsKey( "mongo.host" ) ) {
			
			String host = rb.getString( "mongo.host" );
			int port = Integer.parseInt( rb.getString( "mongo.port" ) );
			String databaseNew = rb.getString( "mongo.database" );
			
			if( ! "".equals( databaseName )) {
				
				databaseName = databaseNew;
				
			}

			System.out.println( " Connecting to " + host + ":" + port + " for database: " + databaseName);
			
			mongoClient = new MongoClient(host, port);
			
		} else {

			mongoClient = new MongoClient();
			
		}
		

		morphia = new Morphia();
		datastore = morphia.createDatastore(mongoClient, databaseName);
		
		System.out.println("Mongo manager init");
		System.out.println(morphia);
		System.out.println(mongoClient);
		System.out.println(datastore);
		
	}
	
	public static MongoManager getInstance() {
		
		if( _instance == null ) {
			
			_instance = new MongoManager();
			
		}
		
		return _instance;
		
	}
	
	public Morphia getMorphia() {

		return morphia;
		
	}
	
	public Datastore getDatastore() {
		
		return datastore;
		
		
	}

	public MongoCollection<Document> getCollection( String collectionName ) {
		
		return mongoClient.getDatabase( databaseName ).getCollection( collectionName );
		
	}
}
