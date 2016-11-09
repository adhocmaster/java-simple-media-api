/**
 * Translation repository. It reads translations from language files. 
 * 
 * Format:
 * 
 * Language file name: "en", "bn", etc. Only 2 digit code without any extension.
 * Language file charset: UTF-8
 * Essentially the contents are plain properties file to be parsed by ResourceBundle. Check ResourceBundle for details.
 * 
 * @author muktadir
 * @version 0.1.0
 * 
 */
package language;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTranslationRepository extends AbstractTranslationRepository {
    
    private static final Logger logger = LoggerFactory.getLogger( FileTranslationRepository.class );
    
    private static final int MAX_THREADS = 10;
    
    String directoryPath;

    protected FileTranslationRepository( String name ) {
        
        super( name );
        
    }

    /**
     * Creates the repository from files
     * @param name unique name for the repo. 
     * @param dirPath folder where translation files can be found
     */
    public FileTranslationRepository( String name, String dirPath ) {

        this( name );
        this.directoryPath = dirPath;
        
    }

    @Override
    public void save( Translation translation ) throws PersistenceException {

        throw new PersistenceException( "You need to add items to translation files." );
        
    }
    
    @Override
    protected void reload() throws Exception {

        try {
            
            validateLanguageDirPath();
            
            parseDirectoryAndPopulateTranslations();
            
        } catch ( IOException e ) {
            
            throw new Exception("Exception with root cause", e );
            
        }
        
    }
    
    private void validateLanguageDirPath() throws FileNotFoundException {
        
        Path dir = Paths.get( this.directoryPath );
        
        if ( ! Files.isDirectory( dir ) )
            throw new FileNotFoundException( directoryPath + " is not a valid directory." );
        
        
        
    }
    
    /**
     * parses and processes each language file asynchronously
     * @throws IOException
     */
    private void parseDirectoryAndPopulateTranslations() throws IOException {

        Path dir = Paths.get( this.directoryPath );
        
        try ( DirectoryStream<Path> dirStream = Files.newDirectoryStream( dir ) ) {
            
            ExecutorService executorService = Executors.newFixedThreadPool( MAX_THREADS );
            
            for ( Path file : dirStream ) {
                
                Runnable runnable = getRunnableFileParser( file );
                
                executorService.execute( runnable );
                
            }
            
            shutDownAndAwaitTermination( executorService );
            
        } 
        
    }
    
    private Runnable getRunnableFileParser( final Path file ) {
        
        Runnable runnable = new Runnable() {
            
            @Override
            public void run() {
                
                try {
                    
                    parseFile( file );
                    
                } catch ( Exception e ) {

                    logger.error( "Error parsing language file: " + file.toString() );
                    
                }
                
            }
        };
        
        return runnable;
        
    }
    
    private void parseFile( Path file ) throws Exception {
        
        String langCode = file.getFileName().toString();
        Reader reader = null;
        ResourceBundle resourceBundle = null;
        
        logger.debug( "parsing language file: " + file.toString() );
        
        try {
            
            reader = Files.newBufferedReader( file, StandardCharsets.UTF_8 );
            resourceBundle = new PropertyResourceBundle( reader );
            
            populateTranslationsFromRS( langCode, resourceBundle );
            
        } catch ( IOException | IllegalArgumentException | NoSuchLanguageException e ) {
            
            if ( null != reader ) {
                
                reader.close();
                
            }
            
            throw e;
                        
        }    
        
    }
    
    private void populateTranslationsFromRS( final String langCode, ResourceBundle resourceBundle ) throws IllegalArgumentException, NoSuchLanguageException {
        
        logger.debug( "populating language: " + langCode );
        
        Enumeration<String> keys = resourceBundle.getKeys();
        
        while ( keys.hasMoreElements() ) {
            
            String textCode = keys.nextElement();
            String text = resourceBundle.getString( textCode );
            
            Translation translation = new BaseTranslation( text, textCode, langCode );
            
            addTranslationToCollection( translation );
            
            logger.debug( "added translation: " + translation );
            
        }
        
    }
    
    /**
     * This method can be taken out to a utility class.
     * @param pool
     */
    protected void shutDownAndAwaitTermination( ExecutorService pool ) {
        
        pool.shutdown();
        
        try {
            
            //System.out.println( "entering timeout" );
            if (! pool.awaitTermination( 1000, TimeUnit.MILLISECONDS ) )
                shutDownAndAwaitTermination( pool );
            
        } catch ( InterruptedException e ) {

            //System.out.println( "entering InterruptedException" );
            
            if ( ! pool.isShutdown() )
                shutDownAndAwaitTermination( pool );
                
            Thread.currentThread().interrupt();
            
        }
        
    }

}
