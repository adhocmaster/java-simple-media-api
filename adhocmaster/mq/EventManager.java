package adhocmaster.mq;

/**
 * For each events, an event manager automatically creates a consumer and listen to the queue. 
 * As topics are not sent out if published before a consumer registers, we are using a queue and broadcasting messages as events
 * So multiple listeners get notified for a queued message
 * queue and event have the same string
 * 
 * Listening to an event
 * you need to pass a event and a MessageListener object. On an event happening, the listerner will get the message along with the event
 * 
 * Firing an event
 * just fire.
 */

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.transaction.Synchronization;

import org.apache.log4j.Logger;

public class EventManager {

	private final static Logger logger = Logger.getLogger( EventManager.class );
	private static final long consumerTimeout = 2000L; // 1 second
	
	private ConcurrentHashMap< String, Set<MessageListener> > eventMap; // will be automatically initialized to null as this is a object field
	private ConcurrentHashMap< String, Thread > consumerMap;
	
	transient private static EventManager _instance = null; //not initialized because this is a class member
	transient private Long listCreationLock = 1L; // a dummy object to do synchronization when creating a list for an event for the first time.
	
	transient private ConcurrentHashMap< String, Long > statsFired;
	transient private Long statsLock = 1L;
	
	transient private ConcurrentHashMap<String, Boolean> events;
	
	
	private EventManager() {
		
		eventMap = new ConcurrentHashMap<String, Set<MessageListener>>( 16, 0.75F, 4 );
		consumerMap = new ConcurrentHashMap<String, Thread>(16, 1F, 4);
		statsFired = new ConcurrentHashMap<String, Long>(16, .5F, 1);
		events = new ConcurrentHashMap<String, Boolean>();
		
	}
	
	private static synchronized void createInstance() {
		
		if ( null == _instance ) {
			
			_instance = new EventManager();
			
		}
		
	}
	
	public static EventManager getInstance() {
		
		if ( null == _instance ) {
			
			createInstance();
			
		}
		
		return _instance;
		
	}
	
	/**
	 * Thread safe
	 * checks if there is a listener set for an event. If null, a new empty set is created
	 * @param event
	 */
	public void prepareListenerSet( String event ){

		if( null == eventMap.get( event ) ) {
			
			System.out.println( event + " waiting for listCreationLock" );
			
			synchronized ( listCreationLock ) {
				
				System.out.println( event + " got the lock ");
				
				if ( null == eventMap.get( event ) ) {
					
					System.out.println( event + " is null in eventMap after getting lock");
										
					// we are creating synchronized set because it can be accessed by multiple threads for the same event
					Set<MessageListener> listeners = Collections.synchronizedSet( new HashSet<MessageListener>() );
					
					eventMap.put( event, listeners );
					
					createConsumerThread(event);
					
				} else {

					System.out.println( event + " is NOT null in eventMap after getting lock");
					
				}
				
			}
			
		} else {

			
			System.out.println( event + " did not wait for listCreationLock as it exists" );
			
		}
	}

	public boolean hasListener( String event, MessageListener listener ) {

		Set<MessageListener> listeners = getListeners( event );
		
		if ( null == listeners )
			return false;
		
		return listeners.contains( listener );
		
	}
	public void addListener( String event, MessageListener listener ) throws IllegalArgumentException {

		if( null == event || "".equals( event.trim() )) {
			
			throw new IllegalArgumentException( "event cannot be null or empty" );
		}
		
		if( null == listener ) {
			
			throw new IllegalArgumentException( "listener cannot be null" );
		}
		
		prepareListenerSet( event );
		
		Set<MessageListener> listeners = eventMap.get( event );
		
		// need to synchronize
		
		synchronized (listeners) {
			
			listeners.add( listener );
			
		}
		
	}
	
	public void removeListener( String event, MessageListener listener ) {
		
		if( null == event || "".equals( event.trim() )) {
			
			throw new IllegalArgumentException( "event cannot be null or empty" );
		}
		
		if( null == listener ) {
			
			throw new IllegalArgumentException( "listener cannot be null" );
		}

		prepareListenerSet( event );

		Set<MessageListener> listeners = eventMap.get( event );
		
		synchronized (listeners) {
			
			listeners.remove( listener );
			
		}
		
	}

	/** 
	 * returned set must be synchronized before iterating. add, remove doesn't need synchronization
	 * @param event
	 * @return returned set must be synchronized before iterating. add, remove doesn't need synchronization
	 */
	public Set<MessageListener> getListeners( String event ) {
		
		return eventMap.get( event );
		
	}
	
	
	/** For each event, there will be a single thread which will listen for a message on the event queue
	 * Not thread safe. It was not built for threadsafety because it's a integral part of creating the event which is properly threadsafe. Never use it ever
	 * Doesn't need {@link Synchronization} because it's called only when creating the event 
	 * @param event
	 */
	private void createConsumerThread( final String event ) {
		
		if ( null == consumerMap.get( event ) ) {
			
			Thread thread = new Thread( new Runnable() {
				
				@Override
				public void run() {

					while( true ) {
						
						try {
							
							//System.out.println("Consumer thread '" + Thread.currentThread().getName() + "' is trying to get message for: " + event);
							String message = MQBroker.getInstance().receiveTextFromQueue( event, EventManager.consumerTimeout );
							
							if( null != message ) {
								
								// call the event processor
								EventManager.this.eventFired( event, message ); // or getinstance?
								
							}
							
						} catch (Exception e1) {
							// It may not be an error
							//e1.printStackTrace();
							logger.debug( e1.getMessage(), e1);
						}
						
						try { 
							
							Thread.sleep( 1000 );
							
						} catch ( InterruptedException e ) {
							
							// Do nothing
							
						}
						
					}
					
				}
			});
			
			consumerMap.put( event,  thread );
			System.out.println( event + ": starting consumer thread " + thread.getName() );
			thread.setDaemon( true );
			thread.start();
			
		}
		
	}
	
	/**
	 * Called when a event is fired through MQ. This function needs to be non-blocking
	 */
	private void eventFired( final String event, final String message ) {
		
		// process in a separate thread
		
		
		Long counter = statsFired.get( event );

		if( null == counter ) {

			synchronized ( statsLock ) {

				if( null == counter ) 
					counter = 0L;
				
			}
		}

		synchronized ( statsLock ) {
			
			++counter;
			statsFired.put(event, counter); 
			
		}
		
		Thread thread = new Thread( new Runnable() {
			
			@Override
			public void run() {


				System.out.println( event + " fired thread '" + Thread.currentThread().getName() + " will call listener methods with message: " + message );
				
				Set<MessageListener> listeners = EventManager.this.getListeners( event );
				
				if( null == listeners || listeners.isEmpty() ) {
					
					System.out.println( event + ": No listener");
					return;
					
				}
				
				// need to be synchronized as it's updated by other threads
				synchronized ( listeners ) {
					
					System.out.println( event + ": Listeners found");
					System.out.println( listeners );
					
					for ( MessageListener listener : listeners ) {
						
						listener.onMessageReceived( event, message );
						
					}
					
				}
				
				
			}
		});

		System.out.println( event + ": starting event fire " + thread.getName() );
		thread.start();
		
	}

	public ConcurrentHashMap<String, Long> getStatsFired() {
		return statsFired;
	}
	public Long getStatsFired( String event ) {
		return statsFired.get( event );
	}
	
	
	/**
	 * Now about producers
	 */
	
	/**
	 * Add if it does not exist. Producers can add events indirectly
	 * @param event
	 */
	private void addEvent( String event ) {
		
		if( null == events.get( event ) )
			events.put( event, true ); // started on creation
		
	}
	
	/**
	 * Necessary to avoid loops. Producers cannot pause events. Only consumers. You will lose events. Becareful of this function. We need to pause events of an object, not a functionality
	 * @param event
	 */
	public void pauseEvent( String event ) {
		
		events.put( event, false );
		
	}
	
	/**
	 * Producers cannot start events. Only consumers
	 * @param event
	 */
	public void startEvent( String event ) {

		events.put( event, true );
		
	}
	
	/**
	 * adds the event if not found.
	 * @param event
	 * @return
	 */
	public boolean isPaused( String event ) {
		
		if ( null == events.get( event ) ) {
			
			addEvent(event);
			
			return false;
			
		}
		
		return events.get( event );
		
	}
	
	public void fireEvent( String event, String message ) throws Exception {
		
		// 1. Check if paused
		
		if( isPaused(event) ) {
			
			System.out.println( event + " is paused.");

			return;
			
		}
		
		MQBroker.getInstance().publishOnQueue( event , message );
		
	}
	
}
