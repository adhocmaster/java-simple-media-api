/**
 * Translation repository to enable multilingual feature in your application. The repository will have separate collections of Translation for each language.
 * @author muktadir
 * @version 0.1.0
 */
package language;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.persistence.PersistenceException;

public interface TranslationRepository {

    
    /**
     * Every language repository has a name which defines its scope
     * @return name of the repository
     * @throws IllegalStateException if name is not found
     */
    public String getName() throws IllegalStateException;
    
    /**
     * returns all the translations belonging to a language
     * @param langCode
     * @return map of (textCode, Translation) tuples
     * @throws NoSuchLanguageException
     */
    public Map<String, Translation> getCollection( String langCode ) throws NoSuchLanguageException, IllegalArgumentException;
    
    /**
     * 
     * @return list of language codes
     */
    public List<String> getLangCodes();
    
    /**
     * 
     * @return list of text codes
     */
    public List<String> getTextCodes();
    
    /**
     * Checks if a language exists
     * @param langCode
     * @return
     */
    public boolean hasLang( String langCode );
    
    /**
     * Default language is en unless otherwise specified by the implementation
     * @return default language or en if no default set
     */
    public String getDefaultLang();

    /**
     * Returns text for the default language
     * @param textCode this code defines the text
     * @return returns a BaseTranslation or MissingTranslation object
     * @throws NoSuchLanguageException when no such language found
     * @throws IllegalArgumentException when arguments are invalid
     */
    public Translation get( String textCode ) throws NoSuchLanguageException, IllegalArgumentException;

    /**
     * Returns translation of a corresponding language code and text code. 
     * @param textCode this code defines the text
     * @param langCode (2 character language code)
     * @return returns a BaseTranslation or MissingTranslation object
     * @throws NoSuchLanguageException when no such language found
     * @throws IllegalArgumentException when arguments are invalid
     */
    public Translation get( String textCode, String langCode ) throws NoSuchLanguageException, IllegalArgumentException;
    
    /**
     * A variant of get ( String textCode, String langCode ) which returns default translation of a textCode if it doesn't exist in the requested language collection.
     * @param textCode
     * @param langCode
     * @param fallbackOnDefault
     * @return returns default translation of a textCode if it doesn't exist in the requested language collection. MissingTranslation object if none found
     * @throws NoSuchElementException
     * @throws NoSuchLanguageException
     * @throws IllegalArgumentException
     */
    public Translation get( String textCode, String langCode, boolean fallbackOnDefault ) throws NoSuchLanguageException, IllegalArgumentException;
    
    /**
     * Adds a translation to the collection
     * @param translation
     */
    public void save( Translation translation ) throws PersistenceException;

}
