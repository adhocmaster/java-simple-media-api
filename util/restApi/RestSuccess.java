package util.restApi;

import java.io.Serializable;

import util.restApi.RestError.Codes;

public class RestSuccess implements Serializable {

	/**
	 * 
	 */
	public enum Codes 
	{ 
		SAVE_DB ("Saved in DB");
		
		private String message;
		
		Codes( String message ) {
			
			this.message = message;
		}
		
		String getMessage() {
			
			return message;
			
		}
	};
	
	private static final long serialVersionUID = 1L;
	protected String code;
	protected String message;
	protected boolean success = true;
	

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


	/**
	 * Custom code, custom message
	 * @param code custom string
	 * @param message custom message
	 */
	public RestSuccess(String code, String message) {

		this.code = code;
		this.message = message;
		
	}
	
	/**
	 * Predefined code, custom message
	 * @param code Predefined code
	 * @param message custom message
	 */
	public RestSuccess( Codes code, String message ) {

		this.code = code.name();
		this.message = message;
		
	}

	/**
	 * Predefined code, Predefined message
	 * @param code Predefined string
	 */
	public RestSuccess( Codes code ) {
		
		this.code = code.name();
		this.message = code.getMessage();
		
	}
}
