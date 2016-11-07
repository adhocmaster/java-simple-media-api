package adhocmaster.mq;


/**
 * Interface to make a event based wire. 
 * @author muktadir
 *
 */
public interface MessageListener {
	
	public void onMessageReceived( String event, String message );
	
	public boolean implementHashCodeAndEqualsMethodsPlease();
	
	

}
