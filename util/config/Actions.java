/**
 * 
 */
package util.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import login.LoginDTO;
import permission.Capability;
import permission.DisasterPermission;
import sessionmanager.SessionConstants;
import sessionmanager.SessionManager;

/**
 * @author Alam
 */
public class Actions extends DispatchAction {
	public ActionForward add(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
		
		SessionManager manager = new SessionManager();
		if(!manager.isLoggedIn(request)){
			return mapping.findForward("login");
		}
		
		LoginDTO loginDTO = (LoginDTO)request.getSession().getAttribute(SessionConstants.USER_LOGIN);
		
		if(!DisasterPermission.hasPermission(loginDTO, Capability.ManageSystemConfiguration)){
			return mapping.findForward("failure");
		}
		
		if(request.getParameter("formSubmitted") == null){
			return mapping.findForward("add");
		}
		
		String key = request.getParameter("name");
		String value = request.getParameter("value");
		String autoLoad = request.getParameter("autoLoad");
		String type = request.getParameter("type");
		
		Configuration conf = new Configuration(key, value,autoLoad,type);
		try{
			conf.save();
			request.setAttribute("success", "কনফিগারেশন সংযোজিত হয়েছে");
		}
		catch(Exception e){
			request.setAttribute("error", "কনফিগারেশন সংযোজিত হয়নি, পুনরায় চেষ্টা করুন");
			e.printStackTrace();
		}
		return mapping.findForward("add");
	}
	
	public ActionForward edit(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
		SessionManager manager = new SessionManager();
		if(!manager.isLoggedIn(request)){
			return mapping.findForward("login");
		}
		
		LoginDTO loginDTO = (LoginDTO)request.getSession().getAttribute(SessionConstants.USER_LOGIN);
		
		if(!DisasterPermission.hasPermission(loginDTO, Capability.ManageSystemConfiguration)){
			return mapping.findForward("failure");
		}
		
		if(request.getParameter("formSubmitted") == null){
			
			String name = request.getParameter("name");
			Configuration configuration = ConfigurationManager.getInstance().getByName(name);
			request.setAttribute("configuration", configuration);
			return mapping.findForward("add");
			
		}
		
		String key = request.getParameter("name");
		String value = request.getParameter("value");
		String autoLoad = request.getParameter("autoLoad");
		String type = request.getParameter("type");
		
		Configuration conf = new Configuration(key, value,autoLoad,type);
		try{
			conf.save();
			request.setAttribute("success", "কনফিগারেশন সম্পাদিত হয়েছে");
		}
		catch(Exception e){
			request.setAttribute("error", "কনফিগারেশন সম্পাদিত হয়নি, পুনরায় চেষ্টা করুন");
			e.printStackTrace();
		}
		return mapping.findForward("list");
	}
	
	public ActionForward list(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
		return mapping.findForward("list");
	}
}
