package language;


abstract public class TranslationRepositoryFactory {
    
    /**
     * Creates a translation repository from a path. The path leads to a directory where files for different languages exists. Names for files is only 2-letter country code. No extension required.
     * @param name a unique name of the repository. Different repositories can be made for better isolation of repositories.
     * @param dir directory of language files.
     * @return TrasnlationRepository
     * @throws Exception
     */
    public static TranslationRepository createFromFiles( String name, String dir ) throws Exception {
        
        FileTranslationRepository repository = new FileTranslationRepository( name, dir );
        
        repository.reload();
        
        return repository;
        
    }

}
