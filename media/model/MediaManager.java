package util.media.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;

import org.apache.commons.io.FilenameUtils;

import common.Logger;
import util.media.api.MediaService;

/**
 * Media Manager is used to save media files in the application server. It uses ServletContext object to get the app path.
 * 
 * @author muktadir
 * @author ximran
 * @version 1.0
 * @package util.media.model
 */
public class MediaManager {

	//convert media manager logger to non static
	
	protected static Logger logger = Logger.getLogger(MediaManager.class);
	
	protected String rootPath;
	protected String relativePath;
	
	private static MediaManager _instance = null;
	
	/**
	 * Only to be used for testing purposes. This constructor only used in JUnit tests as Context is not available.
	 * @param rootPath
	 */
	protected MediaManager ( String rootPath ) {
		
		this.rootPath = rootPath;
		this.relativePath = "";
		
	}
	
	private MediaManager( ServletContext context ) {
		
		ResourceBundle rb = ResourceBundle.getBundle("media");
		
		relativePath = rb.getString("public.folder");
		
		rootPath = context.getRealPath("/") +  relativePath;
		
	}
	
	public static MediaManager getInstance( ServletContext context ) {
		
		if( _instance ==  null ) {
			
			createInstance( context );
			
		}
		
		return _instance;
		
	}
	
	/**
	 * Synchronized method to create the singletion
	 * @author muktadir
	 * @param context
	 */
	public static synchronized void createInstance( ServletContext context ) {

		if( _instance ==  null ) {
			
			_instance = new MediaManager( context );
			
		}
		
	}
	
	/**
	 * Function to save an uploaded media. Doesn't save for/ownership
	 * @param tempPath
	 * @return 0 if failed, id of the new media on success
	 * @throws Exception 
	 */

	public Media save( String tempPath ) throws Exception {
		
		return save(tempPath, 0, "");
		
	}
	
	public Media save( InputStream fileStream, String fileName ) throws Exception {
		
		return save(fileStream, fileName,  0, "");
		
	}
	
	/**
	 * 
	 * @param fileStream
	 * @param fileName
	 * @param forId
	 * @param forType
	 * @return
	 * @throws Exception
	 */
	public Media save ( InputStream fileStream, String fileName, long forId, String forType ) throws Exception {

		//step 1. save media object
		
		Media media = getObjectFromFilename( fileName, forId, forType );
		
		// can throw exception
		
		media.save();
		
		//step 2. generate path
		
		String destPath = generatePath( media );
		
		//step 3. Move the file to the path. if move fails, rollback
		
		try {

			saveStream( fileStream, destPath );
			
		} catch ( IOException ex ) {
			
			ex.printStackTrace();
			
			media.removeFromDB();
			
			throw new Exception ("couldn't save stream to destination path: " + destPath );
			
		}
		
		//step 4. update database record with path and url
		
		media.setPath( destPath );
		media.setUrl( generateUrl(media) );
		
		media.save();
		
		return media;
		
		
	}
	
	/**
	 * Function to save an uploaded media. Saves ownership of media
	 * @param tempPath
	 * @param forId
	 * @param forType
	 * @return 0 if failed, id of the new media on success
	 * @throws Exception 
	 */
	
	public Media save ( String tempPath, long forId, String forType ) throws Exception {
		
		//step 1. save media object
		
		Media media = getObjectFromTempFile( tempPath, forId, forType );
		
		// can throw exception
		
		media.save();
		
		//step 2. generate path
		
		String destPath = generatePath( media );
		
		//step 3. Move the file to the path. if move fails, rollback
		
		try {

			move( tempPath, destPath );
			
		} catch ( IOException ex ) {
			
			ex.printStackTrace();
			
			media.removeFromDB();
			
			throw new Exception ("couldn't move temp file ( " + tempPath + " ) to destination path: " + destPath );
			
		}
		
		//step 4. update database record with path and url
		
		media.setPath( destPath );
		media.setUrl( generateUrl(media) );
		
		media.save();
		
		return media;
		
	}
	
	public Media getObjectFromTempFile(String tempPath, Long forId, String forType) {
		
		String status= "available";
		
		//Extraction of file properties
		Path filePath = Paths.get(tempPath);
		
		//step 1. Create an empty Media object
		Media media = new Media();
		
		//step 2. Set id from database sequencer
		
		//step 3. populate Media object
//		media.setName( filePath.getFileName().toString() );
//		media.setExtension(FilenameUtils.getExtension(filePath.getFileName().toString()));
//		media.setForId(forId);
//		media.setForType(forType);
//		media.setStatus(status);
//		
//		//step 4. return object
//		return media;
//		
		return getObjectFromFilename( filePath.getFileName().toString(), forId, forType );
		
	}
	
	public Media getObjectFromFilename(String fileName, Long forId, String forType) {
		
		String status= "available";
		
		//step 1. Create an empty Media object
		Media media = new Media();
		
		//step 2. Set id from database sequencer
		
		//step 3. populate Media object
		media.setName( fileName );
		media.setExtension( FilenameUtils.getExtension(fileName) );
		media.setForId(forId);
		media.setForType(forType);
		media.setStatus(status);
		
		//step 4. return object
		return media;
		
	}

	public String generatePath( Media media ) throws IOException {
		
		
		
		String generatedPath = "";
		
		
		int innerFolder = (int)(media.getId() / Media.maxFileNumber);
		
		String tempFolder = "";
		
		for(int counter = 0; counter < innerFolder; counter++){
			
			tempFolder = tempFolder + "/" + Integer.toString(0);
			
		}
		
		//Check if the folder exists? if not then first create the directory
		String tempPath = rootPath + tempFolder.trim();
		
		Path path = Paths.get(tempPath);
		
		
		if ( Files.isDirectory(path) ) {
			
			generatedPath = tempPath;
			
		}
		
		else {
			
			path = Files.createDirectories(path);
			generatedPath = path.toString();
			
		}
		
		return generatedPath + "/" + media.getId() + "-" + media.getSlug() + "." + media.getExtension();
		
	}
	
	public String generateUrl( Media media ) {


		String relativeURL = media.getPath().replace(rootPath, relativePath);
		
		return relativeURL;
		
	}

	
	public void move( String srcPath, String destPath ) throws IOException{
		
		Path source =  Paths.get(srcPath);
		Path destination =  Paths.get(destPath);
		Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
	}
	
	public void saveStream( InputStream is, String destPath ) throws IOException {
		

		FileOutputStream out = null;
		
		try {  
						
			File file = new File(destPath);
						
		    out = new FileOutputStream( file );  
		    
		    int read = 0;  
		    byte[] bytes = new byte[1024];  
		    
		    while ((read = is.read(bytes)) != -1) {
		    	
		        out.write(bytes, 0, read);  
		        
		    }  
		    
		    out.flush();  
		    out.close();  
		    
			String output = "File successfully uploaded to : " + destPath;  
			
			logger.debug( output );
			
			//now call media manager to do his stuff
			
			
		} catch (IOException e) {
						
			throw e;
			
		} finally {
			
			if ( out != null ) {

				out.close();
				
			}
		
		}
		
	}
}
