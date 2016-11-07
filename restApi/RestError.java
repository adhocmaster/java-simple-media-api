package util.restApi;

/**
 * Rest Error codes & Bean
 * @author muktadir
 * 
 */

import java.io.Serializable;

public class RestError implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public enum Codes 
	{ 
		SAVE_DB ( "Failed to save in DB" ),
		NULL_FORM ( "Form null" ),
		PERMISSION_DENIED ("You don't have permission to do this operation" ),
		SERVICE_UNAVAILABLE ( "Service unavailable" ),
        NOT_LOGGED_IN ( "Log in first to access this data" ),
        INVALID_PATH ( "Invalid resource path" ),
		EXCEPTION ( "Something happened:" );
		
		private String message;
		
		Codes( String message ) {
			
			this.message = message;
		}
		
		String getMessage() {
			
			return message;
			
		}
	};
	
	protected String code;
	protected String message;
	protected boolean success = false;
	protected boolean error = true;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}


	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	

	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	/**
	 * Custom code, custom message
	 * @param code custom string
	 * @param message custom message
	 */
	public RestError(String code, String message) {

		this.code = code;
		this.message = message;
		
	}
	
	/**
	 * Predefined code, custom message
	 * @param code Predefined code
	 * @param message custom message
	 */
	public RestError(Codes code, String message) {

		this.code = code.name();
		this.message = message;
		
	}

	/**
	 * Predefined code, Predefined message
	 * @param code Predefined string
	 */
	public RestError( Codes code ) {
		
		this.code = code.name();
		this.message = code.getMessage();
		
	}



}
