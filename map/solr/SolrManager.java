package util.map.solr;

/**
 * Solr Manager written for java application
 * handles connections to different servers/collections. Fixes solrj package collection problem. To use just call SolrManager->getInstance()->add....
 * 
 * @author muktadir
 * @version 1.0
 * @package util.map.solr
 */
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteSolrException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import common.Logger;

public class SolrManager {

	Logger logger = Logger.getLogger(SolrManager.class);
	protected static SolrManager _instance = null;
	protected static SolrClient defaultClient = null;
	protected static SolrClient defaultClientUpdate = null;
	protected static String host = null;
	protected static String defaultCollection = null;
	protected static HashMap<String, SolrClient> collections = null;
	
	/**
	 * Solr manager setups up default collection, default host, default update client, default insert client. 
	 * All these are set in solr.properties file.
	 */
	
	private SolrManager() {
		
		ResourceBundle rb = ResourceBundle.getBundle("solr");
		
		if( StringUtils.isNotBlank( rb.getString( "solr.url" ) ) && StringUtils.isNotBlank( rb.getString( "solr.defaultCollection" ) ) ) {

			defaultCollection = rb.getString( "solr.defaultCollection" );
			
			host = rb.getString( "solr.url" );
			
			defaultClient = new HttpSolrClient( host + "/" + defaultCollection );
			
			collections = new HashMap<String, SolrClient>();
			
			collections.put( defaultCollection, defaultClient );
			
			defaultClientUpdate = new ConcurrentUpdateSolrClient(host + "/" + defaultCollection, 
					Integer.parseInt( rb.getString( "solr.updateQueue") ), 
					Integer.parseInt( rb.getString( "solr.updateThreadCount") ) );
			
			
		} else {
			
			logger.error("Solr url or default collection missing. Check solr.properties file");
			throw new RuntimeException("Solr url or default collection missing. Check solr.properties file");
			
		}
		
	}

	public static synchronized void createInstance() {

		
		if( _instance == null ) {
			
			_instance = new SolrManager();
			
		}
		
	}
	
	public static SolrManager getInstance() {
		
		createInstance();

		try {
			
			SolrPingResponse rs = defaultClient.ping();
			System.out.println( "Solr status: " + rs.getStatus() );
//			System.out.println(rs.);
			
		} catch (SolrServerException | IOException e) {
			
			e.printStackTrace();
			
			_instance.logger.error("solr server down or mis-configured");
			
			throw new RuntimeException("solr server down or mis-configured");
			
		} 
		
		return _instance;
		
	}
	
	/**
	 * for a new collection, a new client is created. default solrj package has bugs in using different collections in a single client. 
	 * @param collectionName name of the collection. Must not contain host string. 
	 */
	
	@SuppressWarnings("resource")
	public void addCollectionClient(String collectionName) {
		
		HttpSolrClient client = new HttpSolrClient( host + "/" + collectionName );
		
		try {
			
			client.ping();
			collections.put( collectionName, client );
			
		} catch (SolrServerException | IOException e) {
			
			e.printStackTrace();
			
			_instance.logger.error("solr server down or mis-configured " + collectionName);
			
			throw new RuntimeException("solr server down or mis-configured" + collectionName);
		
		} 
		
		
	}
	/**
	 * Gets a client for a collection.
	 * @param collectionName
	 * @return client if found, null if not
	 */
	public SolrClient getCollectionClient( String collectionName ) {
		
		if ( ! collections.containsKey( collectionName ) ) {
			
			addCollectionClient(collectionName);
			
		}
		
		return collections.get(collectionName);
		
	}

	/**
	 * Adds to default collection. Default collection is set from properties file.
	 * @param doc
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws RemoteSolrException
	 */
	public void add( SolrInputDocument doc ) 
			throws SolrServerException, IOException, RemoteSolrException {
		
		add( defaultCollection, doc );
		
	}
	
	/**
	 * Adds to default collection
	 * @param docs
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws RemoteSolrException
	 */
	public void add( Collection<SolrInputDocument> docs ) 
			throws SolrServerException, IOException, RemoteSolrException {
		
		add( defaultCollection, docs );
		
	}
	
	/**
	 * 
	 * @param collection
	 * @param doc
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws RemoteSolrException
	 */
	public void add( String collection, SolrInputDocument doc ) 
			throws SolrServerException, IOException, RemoteSolrException {
		
		System.out.println("Adding to solr collection: " + collection);
		
		SolrClient solr = getCollectionClient( collection );

		UpdateResponse response = solr.add(doc);
		
		System.out.println( response );
		
		UpdateResponse commitResponse = solr.commit();
		
		System.out.println( commitResponse );
		
	}
	
	/**
	 * 
	 * @param collection
	 * @param docs
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws RemoteSolrException
	 */
	public void add( String collection, Collection<SolrInputDocument> docs ) 
			throws SolrServerException, IOException, RemoteSolrException {

		System.out.println("Adding to solr collection: " + collection);
		
		SolrClient solr = getCollectionClient( collection );

		UpdateResponse response = solr.add(docs);
		
		System.out.println( response );
		
		UpdateResponse commitResponse = solr.commit();
		
		System.out.println( commitResponse );
		
		
	}
	
	/**
	 * Deletes from default collection
	 * @param id
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws RemoteSolrException
	 */
	public void deleteById( String id ) 
			throws SolrServerException, IOException, RemoteSolrException {
		
		deleteById( defaultCollection, id );
		
	}
	
	/**
	 * Deletes from default collection
	 * @param ids
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws RemoteSolrException
	 */
	public void deleteById( List<String> ids ) 
			throws SolrServerException, IOException, RemoteSolrException {
		
		deleteById( defaultCollection, ids );
		
	}
	
	/**
	 * 
	 * @param collection
	 * @param id
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws RemoteSolrException
	 */
	public void deleteById( String collection,  String id ) 
			throws SolrServerException, IOException, RemoteSolrException {

		System.out.println("Deleting " + id + " from solr collection: " + collection);
		
		SolrClient solr = getCollectionClient( collection );

		UpdateResponse response = solr.deleteById(id);
		
		System.out.println( response );
		
		UpdateResponse commitResponse = solr.commit();
		
		System.out.println( commitResponse );
		
	}
	/**
	 * 
	 * @param collection
	 * @param ids
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws RemoteSolrException
	 */
	public void deleteById( String collection,  List<String> ids ) 
			throws SolrServerException, IOException, RemoteSolrException {

		System.out.println("Deleting " + ids + " from solr collection: " + collection);
		
		SolrClient solr = getCollectionClient( collection );

		UpdateResponse response = solr.deleteById(ids);
		
		System.out.println( response );
		
		UpdateResponse commitResponse = solr.commit();
		
		System.out.println( commitResponse );
		
	}
	/**
	 * Searches in default collection
	 * @param lat
	 * @param lng
	 * @param distance
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws RemoteSolrException
	 */

	public SolrDocumentList get( String lat, String lng, String distance ) 
			throws SolrServerException, IOException, RemoteSolrException {
		
		return get(defaultCollection, lat, lng, distance);
	
	}
	/**
	 * 
	 * @param collection
	 * @param lat
	 * @param lng
	 * @param distance
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws RemoteSolrException
	 */
	public SolrDocumentList get(String collection, String lat, String lng, String distance) 
			throws SolrServerException, IOException, RemoteSolrException {
		
		System.out.println("Searching " + lat + "," + lng + " from solr collection: " + collection);
		
		SolrClient solr = getCollectionClient( collection );
		
		SolrQuery query = new SolrQuery();
		
		String queryString ="*:*";
		
		query.setQuery(queryString);
		query.setFilterQueries("{!bbox sfield=location}");
		query.setParam( "pt", lat + "," + lng );
		query.setParam( "d", distance );
		
		QueryResponse rsp = solr.query(query);
		
		SolrDocumentList docs = rsp.getResults();
		
		return docs;
		
	}
}
