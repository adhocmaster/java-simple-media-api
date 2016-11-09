/**
 * @author muktadir
 * @version 0.1.0
 */
package language;

import java.io.Serializable;

public interface Translation extends Serializable{
    

    public String getText();

    public String getTextCode();

    public String getLangCode();

    public String toString();
    
    
}
