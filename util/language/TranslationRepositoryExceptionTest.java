package language;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class TranslationRepositoryExceptionTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test() {

        TranslationRepositoryException exception = new TranslationRepositoryException( "NoRep", new Exception("This is a cause") );

        assertEquals( "This is a cause", exception.getCause().getMessage() );
        assertEquals( "NoRep", exception.getRepositoryName() );
        
    }

}
