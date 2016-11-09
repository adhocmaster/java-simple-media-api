package util.bootstrap;

import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import language.FileTranslationRepository;
import language.TranslationRepository;
import language.TranslationRepositoryFactory;

public class AppStartup implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger( ServletContextListener.class );
	
	@Override
	public void contextDestroyed(ServletContextEvent contextEvent ) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void contextInitialized( ServletContextEvent contextEvent ) {

		//hospital.ParentListener.register();
		ServletContext context = contextEvent.getServletContext();
		
		// 1 Load classes that need to be available on bootstrap
	    loadStartupClasses( context );
	    
	    // 2 load language repositories
	    loadLanguageRepositories( context );
		
		
	}
	
	/**
	 * Loads start up classes defined in startup_classes.properties file. Each item is a comma separated class names. Keys are arbitrary and should be used for clean structure only
	 * @param context
	 */
    private void loadStartupClasses ( ServletContext context ) {
	    
	    logger.info( "Trying to load start up classes" );
	    ResourceBundle rb = ResourceBundle.getBundle("startup_classes");
        Enumeration <String> keys = rb.getKeys();

        while ( keys.hasMoreElements() ) {
            
            String key = keys.nextElement();
            String value = rb.getString(key);
            logger.info( "Startup class " + key + ": " + value);
            
            String[] classNames = value.split( "," );
            
            for( String className: classNames ) {
                
                if ( "".equals( className.trim() ) ) {
                    
                    continue;
                    
                }
                
                try {
                    
                    Class.forName( className.trim() );
                    
                } catch (ClassNotFoundException e) {

                    logger.error( " Could not load " + className, e );
                    throw new RuntimeException( " Could not load " + className );
                    
                }
                
            }
            
        }
	    
	}
    
    /**
     * Loads translation repositories. 
     * @param context
     */
    private void loadLanguageRepositories( ServletContext context ) {

        ResourceBundle rb = ResourceBundle.getBundle("application");
        
        if ( ! rb.containsKey( "translation.names" ) || "".equals( rb.containsKey( "translation.names" ) ) ) {
            
            logger.info( "no translation mentioned in application.properties file" );
            return;
            
        }
        
        String[] names = rb.getString( "translation.names" ).split( "," );
        String dir = null;
        TranslationRepository translationRepository = null;
        
        for ( String name : names ) {
            
            dir = context.getRealPath("/") + rb.getString( "translation." + name + ".dir" );
            
            try {
                
                translationRepository = TranslationRepositoryFactory.createFromFiles( name, dir );
                context.setAttribute( "lang." + name, translationRepository );
                logger.info( "loaded translation from directory: " + dir );
                
            } catch ( Exception e ) {

                logger.error( " Could not load translations from: " + dir, e );
                throw new RuntimeException( " Could not load translations from:  " + dir );
                
            }
            
        }
        
        
        
    }

}
