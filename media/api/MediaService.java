package util.media.api;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.google.gson.Gson;

import common.Logger;
import util.media.model.Media;
import util.media.model.MediaManager;
import util.restApi.RestError;

@Path("/media-service")
public class MediaService {

	Logger logger = Logger.getLogger(MediaService.class);
	
	@SuppressWarnings("rawtypes")
	@GET
	@Path("/media")
	@Produces(MediaType.APPLICATION_JSON)
	public List get() {
		
		return Media.get();
		
	}
	
	
	@POST
	@Path("/media/add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Media add(
			
			@Context ServletContext application,
			@Context ServletRequest request,
	        @FormDataParam("file") InputStream uploadedInputStream,
	        @FormDataParam("file") FormDataContentDisposition fileDetail
			
			) {
		
		Media media = null;
		
		try {  
			
			media = MediaManager.getInstance(application).save( uploadedInputStream, fileDetail.getFileName() );
			
		} catch ( Exception e ) {
			
			logger.error(e);
			e.printStackTrace();
			
		}
	
		return media;
	}
	
	@GET
	@Path("/get/by-id")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String getMediaById( @QueryParam("fileId") long fileId ) {
		
		Media media = Media.getById(fileId);
		Gson gson = new Gson();
		
		if( media == null ){
			return "file not found";
		}
		
		return gson.toJson(media);
		
	}
	
	@GET
	@Path("/get/by-search-criteria")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String getMediaBySearchCriteria ( 
			@Context HttpServletRequest request,
			@QueryParam("start") int start,
			@QueryParam("length") int length,
			@QueryParam("search[value]") String search,
			@QueryParam("order[0][column]") int columnNo,
			@QueryParam("order[0][dir]") String orderDir,
			@QueryParam("draw") String draw ){
		
		HashMap<Integer, String> columnMapping = new HashMap<Integer,String>();
		String returnStr = "";
		Gson gson = new Gson();
		
		columnMapping.put( 0, "id");
		columnMapping.put( 1, "name");
		columnMapping.put( 2, "extension");
		columnMapping.put( 3, "url");
		columnMapping.put( 4, "date_added");
		columnMapping.put( 5, "date_modified");
		
		ArrayList<Media> medias = Media.get(start, length, search, columnMapping.get( columnNo ), orderDir );
		
		if( medias == null ){
			
			RestError error = new RestError("DB_ERROR", "Error fetching data from DB");
			return gson.toJson( error );
			
		}
		
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> temp = new ArrayList<String>();
		
		for ( Media media : medias ) {
			
			temp.clear();
			
			temp.add( Long.toString( media.getId() ) );
			temp.add( media.getName() );
			temp.add( media.getExtension() );
			temp.add( request.getServerName()+ ":" + request.getServerPort() + request.getContextPath() + "/" + media.getUrl() );
			
			if( media.getDateAdded() == null )
				temp.add( "N/A" );
			else
				temp.add( media.getDateAdded().toString() );
			
			if( media.getDateModified() == null )
				temp.add( "N/A" );
			else
				temp.add( media.getDateModified().toString() );
			
			data.add( temp );
			
		}
		
		returnStr += "{ \"draw\":" + draw + ",";
		returnStr += "\"recordsTotal\":"+Media.getTotalRowCount()+",";
		returnStr += "\"data\":";
		
		returnStr += gson.toJson(data)+",";
		
		returnStr += "\"recordsFiltered\":"+Media.getFilteredDataCount(search);
		returnStr += "}";
		
		return returnStr;
		
	}
}
