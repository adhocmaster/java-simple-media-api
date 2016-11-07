package language;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.Before;
import org.junit.Test;



public class FileTranslationRepositoryTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testShutDownAndAwaitTermination() {

        FileTranslationRepository repository = new FileTranslationRepository( "TestRepo", "" );
        
        ExecutorService pool = Executors.newFixedThreadPool( 10 );
        
        for( int i = 0; i < 100; ++i ) {
            
            final Integer id = i;
            
            Runnable runnable = new Runnable() {
                
                
                @Override
                public void run() {
                    
                    for ( int j = 0; j < 5; ++j ) {

                        System.out.println( Thread.currentThread().getName() + " id#" + id + " loop #" + j );
                        try {
                            Thread.sleep( 1000 );
                        } catch ( InterruptedException e ) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            
                            Thread.currentThread().interrupt();
                        }
                        
                    }
                    
                }
            };
            
            pool.execute( runnable );
            
        }
        
        repository.shutDownAndAwaitTermination( pool );
        
        System.out.println( "END" );
        
    }

    @Test
    public void testWhole() {

        //System.out.println( System.getProperty( "user.home" ) );
        System.out.println( System.getProperty( "user.dir" ) );
        
        String projectDir = System.getProperty( "user.dir" );
        
        String name = "app";
        String path =  projectDir + java.io.File.separator +  "src" + java.io.File.separator + "translations" + java.io.File.separator + "test";

        
        try {
            
            TranslationRepository repository = TranslationRepositoryFactory.createFromFiles( name, path );

            Map<String, Translation> collection = repository.getCollection( "en" );
            
            Set<String> keys = collection.keySet();
            
            for ( String textCode : keys ) {
                
                System.out.println( collection.get( textCode ) );
                
            }
            collection = repository.getCollection( "bn" );
            
            keys = collection.keySet();
            
            for ( String textCode : keys ) {
                
                System.out.println( collection.get( textCode ) );
                
            }
            
            
            System.out.println( repository.getDefaultLang() );
            // try some exceptions
            
            try {
                
                repository.get( "FIRST" );
                
            } catch ( Exception e ) {
                
                fail( "FIRST" );
                
            }

            try {

                System.out.println( repository.get( "THIRD" ) );    
                System.out.println( repository.get( "THIRD", "en" ) );                
                
            } catch ( Exception e ) {

                fail( "THIRD" );
                
                
            }
            
            try {

                System.out.println( repository.get( "SECOND", "bn" ) );     
                System.out.println( repository.get( "THIRD", "bn" ) );     
                
            } catch ( Exception e ) {

                System.out.println( " THIRD NOT FOUND in BN" );
                fail( "THIRD bn"  );         
                
            }
            
            try {
  
                Translation translation = repository.get( "THIRD", "bn", true ) ;   
                
                if (  translation instanceof MissingTranslation ) {
                    
                    fail( "THIRD must be pulled from en" );
                    
                }  
                
            } catch ( Exception e ) {

                fail( "THIRD bn"  );           
                
            }
            
            try {
                
                Translation translation = repository.get( "FOURTH" ) ;

                if ( !( translation instanceof MissingTranslation ) ) {
                    
                    fail( "FOURTH is not a missing translation" );
                    
                }
                
                System.out.println( "FOURTH is a missing translation" );
                
            } catch ( Exception e ) {

                fail( "FOURTH is not a missing translation" );
                
            }
            
        } catch ( Exception e ) {
            
            e.printStackTrace();
            fail(e.getMessage());
            
        }
        
       
        
    }
}
