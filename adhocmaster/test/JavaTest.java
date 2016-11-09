package adhocmaster.test;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;


public class JavaTest {

    class InnerJavaTest {

        private final String[] mutable;

        public InnerJavaTest( String[] nonMutable) {
            
            this.mutable = nonMutable;
            
        }
        
        public void printMutable() {
            
            System.out.println( Arrays.toString( mutable ) );
            
        }
    }
    
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test() {

        
        String[] notmutable = new String[] {"hello", "world"};
        
        InnerJavaTest obj = new InnerJavaTest( notmutable );
        
        //saveMutable( notmutable );
        
        obj.printMutable();
        notmutable[0] = "Destroyed";
        obj.printMutable();
        
    }
    
    

}
