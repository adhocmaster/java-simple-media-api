package util.map.solr;

/**
 *  This class is handy for creating solr documents from HashMaps for Map entries ( for our location based search )
 *  Collections having maps should use this class to create documents. Collections also need to have "map configuration" required by this class. Essentially map coordinates
 *  are given in location field and id is given in id field.
 *  
 * @author muktadir
 */
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrInputDocument;

public class MapEntry {
	
	/**
	 * 
	 * @param data data must have id, lat, lng values. 
	 * @return solr doc has coordinates in location field which our map configuration automatically parses.
	 * @throws IllegalArgumentException
	 */
	
	public static SolrInputDocument createDoc( HashMap<String, Object> data ) 
			throws IllegalArgumentException {
		
		SolrInputDocument document = new SolrInputDocument();

		if( data.get( "id" ) == null ) {
			
			throw new IllegalArgumentException("id missing");
			
		}
		
		document.addField( "id", data.get("id") );

		if( data.get("type")!=null && StringUtils.isNotBlank( data.get( "type" ).toString()) ) {
			
			//throw new IllegalArgumentException("type missing");
			document.addField( "type", data.get("type") );
		}
		
		
		

		if( data.get( "lat" ) == null ||  data.get( "lng" ) == null ) {
			
			throw new IllegalArgumentException("location missing or incomplete");
			
		}
		
		document.addField( "location", data.get( "lat" ) + "," + data.get( "lng" ) );

		if( data.containsKey( "category" ) && data.get( "category") !=null ) {

			document.addField( "category", data.get("category") );
			
		}
		
		if( data.containsKey( "text" ) && data.get( "text") !=null ) {

			document.addField( "attr_text", data.get("text") );
			
		}
		
		
		
		return document;
		
	}

}
