package language;


public class TranslationRepositoryException extends Exception {
    
    String repositoryName;
    
    @SuppressWarnings( "unused" )
    private TranslationRepositoryException( String message ) {
        
        super( message );
        
    }
    
    public TranslationRepositoryException( String repositoryName, Throwable cause ) {
        
        super( cause.getMessage(), cause );
        
        this.repositoryName = repositoryName;
        
    }
    
    public String getRepositoryName() {
        
        return repositoryName;
        
    }
    
    public String getMessage() {
        
        return this.getCause().getMessage();
        
    }

}
