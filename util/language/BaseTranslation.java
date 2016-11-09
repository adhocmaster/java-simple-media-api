/**
 * @author muktadir
 * @version 0.1.0
 */
package language;


public class BaseTranslation implements Translation {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    String text;
    String textCode;
    String langCode;
    
    public BaseTranslation( String text, String textCode, String langCode ) {
        
        this.text = text;
        this.textCode = textCode;
        this.langCode = langCode;
        
    }


    public String getText() {
    
        return text;
    }


    public String getTextCode() {
    
        return textCode;
    }


    public String getLangCode() {
    
        return langCode;
    }


    /**
     * langCode and textCode identifies a translation object
     */
    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( langCode == null ) ? 0 : langCode.hashCode() );
        result = prime * result + ( ( textCode == null ) ? 0 : textCode.hashCode() );
        return result;
    }


    @Override
    public boolean equals( Object obj ) {

        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        BaseTranslation other = ( BaseTranslation ) obj;
        if ( langCode == null ) {
            if ( other.langCode != null )
                return false;
        } else if ( !langCode.equals( other.langCode ) )
            return false;
        if ( textCode == null ) {
            if ( other.textCode != null )
                return false;
        } else if ( !textCode.equals( other.textCode ) )
            return false;
        return true;
    }




    @Override
    public String toString() {

        return "Translation [text=" + text + ", textCode=" + textCode + ", langCode=" + langCode + "]";
    }

    
    
}
