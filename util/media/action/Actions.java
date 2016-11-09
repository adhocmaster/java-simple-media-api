package util.media.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adhocmaster.model.ModelException;
import login.LoginDTO;
import sessionmanager.SessionConstants;
import util.media.form.MediaForm;
import util.media.model.Media;

public class Actions extends DispatchAction implements util.interfaces.ActionInterface{
	
	@Override
	public ActionForward add(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		ActionErrors errors = new ActionErrors();
		
		LoginDTO loginDTO = (LoginDTO)request.getSession(true).getAttribute(SessionConstants.USER_LOGIN);
		
		MediaForm mediaForm = (MediaForm) form;
		
		System.out.println( "Form Data: " );
		System.out.println( mediaForm );

		try{
			
			//Media media = new Media();
			
			// Write necessary code
			
			//media.save();

			//request.setAttribute("status", "Saved");;
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError( "error.model.data" ) );
			
		}
		

		if ( ! errors.isEmpty() ) {
			
			return handleError(mapping, request, errors);
			
		}
		
		return mapping.findForward("add");
		
	}

	@Override
	public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActionForward viewAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		ActionErrors errors = new ActionErrors();
		
		try{
			
			List<Media> data = Media.get();
			
			request.setAttribute("media", data);
			
		} catch ( Exception e ) {
			
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError( "error.model.data" ) );
			//errors.add("fatal", new ActionError("error.message", "Another error") );
			
		}
		
		if ( ! errors.isEmpty() ) {
			
			return handleError(mapping, request, errors);
			
		}
		
		return mapping.findForward("viewAll");
		
	}

	@Override
	public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActionForward handleError(ActionMapping mapping, HttpServletRequest request, ActionErrors errors) {

		saveErrors(request, errors);
		
		if ( mapping.getInput() != null ) {
			
			return ( new ActionForward( mapping.getInput() ) );
			
		}
		// If no input page, use error forwarding
		return ( mapping.findForward( "actionError" ) );
		
	}
}
