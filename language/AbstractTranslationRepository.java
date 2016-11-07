/**
 * Abstract Translation Repository. Concrete classes should extend this class instead of implementing the Repository as it already has the common logic implemented
 */
package language;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.PersistenceException;

public abstract class AbstractTranslationRepository implements TranslationRepository {
    
    /*
     * Some constant needed for internal optimizations
     */
    protected static final int INITIAL_NUMBER_OF_TRANSLATIONS_PER_LANG = 100;
    protected static final int WRITE_CONCURRENCY_LEVEL = 4;
    protected static final float LOAD_FACTOR = 0.9F;
    
    
    String name;
    String defaultLang = "en";
    ConcurrentMap< String, ConcurrentMap< String, Translation > > languageTranslationCollection;
    List<String> textCodes;
    
    /**
     * This constructor must be called by the sub class.
     * @param name
     */
    protected AbstractTranslationRepository( String name ) {

        languageTranslationCollection = new ConcurrentHashMap<String, ConcurrentMap<String,Translation>>( 2, LOAD_FACTOR, WRITE_CONCURRENCY_LEVEL );
        textCodes = Collections.synchronizedList( new ArrayList<String>() );
        
        this.name = name;
        
    }
    
    @Override
    public final ConcurrentMap<String, Translation> getCollection( String langCode ) throws NoSuchLanguageException, IllegalArgumentException {

        if ( null == langCode ) {

            throw new IllegalArgumentException( langCode );

        }
        if ( ! languageTranslationCollection.containsKey( langCode )  ) {

            throw new NoSuchLanguageException( langCode, "Language does not exist" );

        }

        return languageTranslationCollection.get( langCode );
        
    }

    @Override
    public final boolean hasLang( String langCode ) {

        if( languageTranslationCollection.containsKey( langCode ) ) {
            
            return true;
        }
        
        return false;
        
    }

    @Override
    public final Translation get( String textCode )
            throws NoSuchElementException, NoSuchLanguageException, IllegalArgumentException {

        return get( textCode, defaultLang );

    }

    @Override
    public final Translation get( String textCode, String langCode )
            throws NoSuchLanguageException, IllegalArgumentException {

        if ( null == textCode  ) {
            
            throw new IllegalArgumentException( textCode );
            //return new MissingTranslation( textCode, langCode );
        }

        /** this check is not required here as it's the responsibility of another function
        if ( null == langCode ) {
            
            throw new IllegalArgumentException( langCode );
        }
        **/

        ConcurrentMap<String, Translation> translations = getCollection( langCode );
        
        if ( ! translations.containsKey( textCode ) ) {
            
            //throw new NoSuchElementException( textCode );
            return new MissingTranslation( textCode, langCode );
            
        }
        
        return translations.get( textCode );

    }
    
    @Override
    public Translation get( String textCode, String langCode, boolean fallbackOnDefault ) 
            throws NoSuchLanguageException, IllegalArgumentException {
        
        try {
            
            Translation translation = get( textCode, langCode );
            
            if ( translation instanceof MissingTranslation ) {
                
                return get( textCode );
                
            }
            
            return translation;
            
        } catch ( Exception e ) {
            
            return get( textCode );
            
        }
        
        
    }

    /**
     * Generates a list every time it is called.
     */
    @Override
    public final List<String> getLangCodes() {

        List<String> langCodes = new ArrayList<String>();
        
        Set<String> keySet = languageTranslationCollection.keySet();
        
        for ( String lang : keySet ) {
            
            langCodes.add( lang );
            
        }
        
        return langCodes;
        
    }

    @Override
    public final List<String> getTextCodes() {

        return textCodes;
        
    }

    @Override
    public final String getDefaultLang() {

        return defaultLang;
        
    }
    
    @Override
    public final String getName() {
        
        return name;
        
    }
    
    /**
     * Adds a translation to the corresponding language collection. Creates the collection if doesn't exist already
     * @param translation
     * @throws IllegalArgumentException
     * @throws NoSuchLanguageException
     */
    protected void addTranslationToCollection( Translation translation ) throws IllegalArgumentException, NoSuchLanguageException {
        
        String langCode = translation.getLangCode();
        
        if ( ! hasLang( langCode ) ) {
            
            addLanguageToCollection( langCode );
            
        }
        
        // now collection exists
        ConcurrentMap<String, Translation> concurrentMap = getCollection( langCode );
        
        concurrentMap.putIfAbsent( translation.getTextCode(), translation );
        
    }
    
    /**
     * 
     * @param langCode
     */
    private void addLanguageToCollection( String langCode ) {

        /*
        if ( hasLang( langCode ) )
            return;
        */
        ConcurrentMap<String, Translation> translationCollection = new ConcurrentHashMap<String, Translation>( INITIAL_NUMBER_OF_TRANSLATIONS_PER_LANG, LOAD_FACTOR, WRITE_CONCURRENCY_LEVEL );
        languageTranslationCollection.putIfAbsent( langCode, translationCollection );
        
    }
    
    
    /**
     * Reloads data from data source.
     * Reasons why this method is not in the interface and why it is protected
     * 1. Interface is the public api. It hides the data source methods. Public api doesn't even know
     * 2. The data source and persistence management should be in the same package as the concrete repository class. 
     * 3. reload method should be managed by a Loader class from the same package of the concrete class.
     * 4. reload can be very resource consuming, so we decoupled it from client access.
     * @throws Exception 
     */
    protected abstract void reload() throws Exception;

}
