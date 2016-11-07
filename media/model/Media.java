package util.media.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.struts.action.ActionForm;
import org.jdom.JDOMException;

import databasemanager.DatabaseManager;
import util.interfaces.ModelInterface;

/**
 * Model for media. Does only database related functions. Uses hibernate for CRUD operations.
 * Annotations in this class are used for hibernate. These are simple mapping variable field to the column of database. 
 * Conversion of data type from mysql data type to java data type are done by hibernate.
 * 
 * @author muktadir
 * @author ximran
 * @version 1.0
 * @package util.media.model
 */
@Entity
@Table(name="media")
public class Media implements ModelInterface{

	private static final long serialVersionUID = 1L;
	public static final long maxFileNumber = 32000;
	public static final int maxSlugLength = 100;
	
	@Id
	@Column(name="id")
	protected long id;
	@Column(name="for_id")
	protected long forId = 0;
	@Column(name="for_type")
	protected String forType = "";
	@Column(name="name")
	protected String name;
	@Column(name="slug")
	protected String slug = "";
	@Column(name="extension")
	protected String extension = "";
	@Column(name="type")
	protected String type = "";
	@Column(name="url")
	protected String url = "";
	@Column(name="path")
	protected String path = "";
	@Column(name="status")
	protected String status = "available";

	@Column(name="date_added", columnDefinition="DATETIME", nullable= true, updatable= false)
	protected Date dateAdded = null;
	@Column(name="date_modified", columnDefinition="DATETIME", nullable= true, updatable= true)
	protected Date dateModified = null;
	
	public Media() {
		
		//set all the values to null
		
	}
	
	public Media( long id ) {
		
		//read database and populate properties
		
	}
	
	public static Media getById(long id){
		MediaDAO dao = new MediaDAO();
		return dao.getById(id);
	}
	public void save() throws ClassNotFoundException, IllegalAccessException, InstantiationException, JDOMException, SQLException, Exception {
		
		//if id exists then update otherwise generate id and add
		long id = 0;
		MediaDAO dao = new MediaDAO();
		
			
			// default date values
//			Date dt = new Date(); //current date
//			
//			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			
//			String currentDateStr = df.format(dt);
//			
//			setStrDateAdded(currentDateStr);
//			setStrDateModified(currentDateStr);
			
		
		if( this.getId() > 0 ) {
			//update

			Date dt = new Date(); //current date
			this.setDateModified(dt);
			dao.update(this);
			
		}
		else {
				
			id = DatabaseManager.getInstance().getNextSequenceId("media");
			
			Date dt = new Date(); //current date
			
			this.setDateAdded(dt);
			
			this.setDateModified(dt);
			
			this.setId(id);
			
			dao.add(this);
			
		}
	}

	public void removeFromDB() {


		MediaDAO dao = new MediaDAO();
		
		dao.delete( this.id );
		
	}
	/* Getters and Setters */
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getForId() {
		return forId;
	}

	public void setForId(long forId) {
		this.forId = forId;
	}

	public String getForType() {
		return forType;
	}

	public void setForType(String forType) {
		this.forType = forType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		
		this.name = name;
		genSlug();
		
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}
	
	public void genSlug() {
		
		int length = name.length();
		
		String slug = name.toLowerCase().replace(' ', '-').replace('.', '-');
		
		if ( length > maxSlugLength ) {
			
			slug = slug.substring(0, maxSlugLength - 1);
			
		}
		setSlug( slug );
		
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	


	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		
		this.dateAdded = dateAdded;
		
//		System.out.println( "setting dateAdded" + dateAdded );
		
	}

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		
		this.dateModified = dateModified;

		
//		System.out.println( "setting dateModified" + dateModified );
		
	}
	
	

	@Override
	public String toString() {
		return "Media [id=" + id + ", forId=" + forId + ", forType=" + forType + ", name=" + name + ", slug=" + slug
				+ ", extension=" + extension + ", type=" + type + ", url=" + url + ", path=" + path + ", status="
				+ status + ", dateAdded=" + dateAdded + ", dateModified=" + dateModified + "]";
	}

	@Override
	public boolean equals(Object other) {

//		return super.equals(arg0);

        if (this == other) return true;
        
        if ( !(other instanceof Media) ) return false;

        final Media media = (Media) other;

        if ( media.getId() != getId() ) return false;

        return true;
		
	}

	@Override
	public int hashCode() {

		//hashcode based on slug and extension
		
		return getSlug().hashCode() * 31 + getExtension().hashCode();
		
	}

	public static List<Media> get() {
		
		MediaDAO dao = new MediaDAO();
		
		return dao.get();
		
	}

	@Override
	public HashMap<String, String> getPropertyList() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see util.interfaces.ModelInterface#loadFromForm(org.apache.struts.action.ActionForm)
	 */
	@Override
	public void loadFromForm(ActionForm form) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 
	 * @param start
	 * @param length
	 * @param search
	 * @param column
	 * @param dir
	 * @return
	 */
	public static ArrayList<Media> get( int start, int length,String search, String column, String dir ) {
		
		MediaDAO dao = new MediaDAO();
		
		HashMap<String, String> condition = new HashMap<String, String>();
		
		condition.put( "name" , search );
		
		HashMap<String, String> orderBy = new HashMap<String, String>();
		
		orderBy.put( column, dir );
		
		@SuppressWarnings("unchecked")
		ArrayList<Media> data = dao.get(condition, orderBy, start, length, true, true);
		
		return data;
		
	}

	/**
	 * Returns total no of row in media table
	 * @author Alam
	 * @return
	 */
	public static long getTotalRowCount() {
		
		MediaDAO dao = new MediaDAO();
		
		return dao.getTotalRowCount();
		
	}
	
	/**
	 * This method returns the number of row that mathces the given condition from media table
	 * @param search text to search with in table
	 * @author Alam
	 * @return
	 */
	public static long getFilteredDataCount( String search ){
		
		MediaDAO dao = new MediaDAO();
		
		HashMap<String, String> condition = new HashMap<String, String>();
		
		condition.put( "name" , search );
		
		return dao.getFilteredDataCount( condition, true, true );
		
	}

}
