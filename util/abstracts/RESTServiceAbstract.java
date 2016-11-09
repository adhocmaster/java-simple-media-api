package util.abstracts;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.google.gson.Gson;

import util.restApi.RestError;

public abstract class RESTServiceAbstract {

    /**
     * Should be overridden by client
     * the default method to be called for the respective application path for GET 
     * @return
     */
    @GET
    public String defaultGET() {

        Gson gson = new Gson();

        return gson.toJson(
                new RestError( RestError.Codes.INVALID_PATH ) );
        
    }
    
    /**
     * Should be overridden by client
     * the default method to be called for the respective application path for POST 
     * @return
     */
    @POST
    public String defaultPOST() {

        return defaultGET();
        
    }

    /**
     * Should not be overridden by client
     * Not found path. This method is executed when no method is matched with the path requested by client
     * @param notFoundPath
     * @return
     */
    @GET
    @Path( "/{notFoundPath: .+}" )
    public String notFoundGET( @PathParam("notFoundPath") String notFoundPath ) {

        Gson gson = new Gson();

        return gson.toJson(
                new RestError( RestError.Codes.INVALID_PATH, notFoundPath + " - path not found" ) );
        
    }

    /**
     * Should not be overridden by client
     * Not found path. This method is executed when no method is matched with the path requested by client
     * @param notFoundPath
     * @return
     */
    @POST
    @Path( "/{notFoundPath: .+}" )
    public String notFoundPOST( @PathParam("notFoundPath") String notFoundPath ) {

        return notFoundGET( notFoundPath );
        
    }

}
