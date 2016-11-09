/**
 * 
 */
package util.config.api;

import javax.print.attribute.standard.Media;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import login.LoginDTO;
import permission.Capability;
import permission.DisasterPermission;
import sessionmanager.SessionConstants;
import util.config.ConfigurationManager;
import util.restApi.RestError;

/**
 * @author Alam
 */
@Path("/configuration-service")
public class ConfigurationService {
	
	@GET
	@Path("/get-all")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String getAll( @Context ServletContext context,
			@Context HttpServletRequest request){
		
		Gson gson = new Gson();
		
		LoginDTO loginDTO = (LoginDTO)request.getSession().getAttribute(SessionConstants.USER_LOGIN);
		if(loginDTO == null){
			return gson.toJson(new RestError(RestError.Codes.NOT_LOGGED_IN));
		}
		
		if(!DisasterPermission.hasPermission(loginDTO, Capability.ManageSystemConfiguration)){
			return gson.toJson(new RestError(RestError.Codes.PERMISSION_DENIED));
		}
		
		return gson.toJson(ConfigurationManager.getInstance().getAll());
	}
	
	@POST
	@Path("/delete")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String delete(@Context ServletContext context,
			@Context HttpServletRequest request,
			@FormParam("name") String name){
		
		return new Gson().toJson(ConfigurationManager.getInstance().delete(name));
		
	}

}
