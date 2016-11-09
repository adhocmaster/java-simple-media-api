package language;


public class NoSuchLanguageException extends Exception {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String langCode="NA";
    
    /**
     * 
     * @param langCode
     * @param message
     */
    public NoSuchLanguageException( String langCode, String message ) {
        
        super( message );
        this.langCode = langCode;
        
    }
    
    /**
     * The code with the fault
     * @return language code which caused the exception
     */
    public String getLangCode() {

        return langCode;
        
    }

}
