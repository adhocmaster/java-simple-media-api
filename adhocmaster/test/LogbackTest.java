package adhocmaster.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import language.FileTranslationRepository;


public class LogbackTest {

    private static final Logger logger = LoggerFactory.getLogger( LogbackTest.class );
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test() {

        logger.error( "Where is the error?" );
        logger.debug( "Where is the debug?" );
    }

}
