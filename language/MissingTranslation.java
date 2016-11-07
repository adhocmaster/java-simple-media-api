/**
 * Special Case pattern or Null Object Pattern for Translation objects
 */
package language;


public class MissingTranslation implements Translation {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    String textCode;
    String langCode;
    
    public MissingTranslation( String textCode, String langCode ) {
       
        this.textCode = textCode;
        this.langCode = langCode;
        
    }
    @Override
    public String getText() {

        // TODO Auto-generated method stub
        return "missing translation";
    }

    @Override
    public String getTextCode() {

        // TODO Auto-generated method stub
        return textCode;
    }

    @Override
    public String getLangCode() {

        // TODO Auto-generated method stub
        return langCode;
    }
    @Override
    public String toString() {

        return "MissingTranslation [textCode=" + textCode + ", langCode=" + langCode + "]";
        
    }
    
    

}
