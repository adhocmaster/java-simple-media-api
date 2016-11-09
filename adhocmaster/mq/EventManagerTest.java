package adhocmaster.mq;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Before;
import org.junit.Test;

public class EventManagerTest {

	public EventManager eventManager;
	
	@Before
	public void setUp() throws Exception {
		
		eventManager = EventManager.getInstance();
		
	}

	@Test
	public void testValidateEvents() {

		createEventThreads(5, "event.roar");
		createEventThreads(5, "event.bark");
		createEventThreads(5, "event.quack");
		

		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//fail("Not yet implemented");
	}
	
	@Test 
	public void testListeners() {

		createEventListeners(5, "event.roar");
		createEventListeners(2, "event.bark");
		createEventListeners(1, "event.quack");


		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Set<MessageListener> listeners = eventManager.getListeners("event.roar");
		
		System.out.println( listeners );
		
		assertEquals( 5, listeners.size() );
		
		listeners = eventManager.getListeners("event.bark");
		
		System.out.println( listeners );
		
		assertEquals( 2, listeners.size() );
		
		listeners = eventManager.getListeners("event.quack");
		
		System.out.println( listeners );
		
		assertEquals( 1, listeners.size() );

		
	}
	
	@Test
	public void testFire() {
		
		String event = "event.roar";
		
		try {
			
			eventManager.fireEvent(event, " just one event fired ");
			
		} catch (Exception e) {
			
			fail( e.getMessage() );
			
		}
		
		
	}
	@Test
	public void testEventFired() {
		
		MQBrokerTest brokerTest =  new MQBrokerTest();
		
		try {

			brokerTest.setUp();

			brokerTest.createQueueProducers(1, "event.roar");
			brokerTest.createQueueProducers(3, "event.bark");
			createEventListeners(5, "event.roar");
			createEventListeners(5, "event.bark");
			

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} catch ( Exception e ) {
			
			e.printStackTrace();
			fail( e.getMessage() );
		}
		
		
	}
	
	/*
	 * how we add listeners
	 */
	private void createEventListeners( int number, final String event ) {
		
		
		for( int i = 0; i < number; ++ i ) {
			
			Thread thread = new Thread( new Runnable() {

				@Override
				public void run() {

					System.out.println( event + ": Run eventListener thread " + Thread.currentThread().getName());
					
					MessageListener listener = new TestMessageListener(event);
					
					try {
						
						EventManagerTest.this.eventManager.addListener(event, listener);
						
					} catch ( Exception e ) {
						
						e.printStackTrace();
						fail( e.getMessage() );
						
					}
					
					
				}
				
			});
			
			thread.start();
			System.out.println( event + ": Started eventListener thread " + thread.getName() );
			
		}
		
	}
	
	private void createEventThreads( int number, final String event ) {
		
		
		for( int i = 0; i < number; ++ i ) {
			
			Thread thread = new Thread( new Runnable() {

				@Override
				public void run() {

					System.out.println(" Run event validator thread " + Thread.currentThread().getName() );
					
					EventManagerTest.this.eventManager.prepareListenerSet(event);
					
					
				}
				
			});
			
			thread.start();
			System.out.println(" Started event validator thread " + thread.getName() );
			
		}
		
	}
	
	/**
	 * A sample listener class which is to be followed by users.
	 * @author muktadir
	 *
	 */
	public class TestMessageListener implements MessageListener {
		
		private int id = 10;
		
		private String event;
		
		public TestMessageListener( String event ) {

			id = ThreadLocalRandom.current().nextInt(1, 10000);
			this.event = event;
		}

		@Override
		public void onMessageReceived( String event, String message) {

			System.out.println( event + ":Listener got: " + message );
			
		}

		@Override
		public boolean implementHashCodeAndEqualsMethodsPlease() {
			// TODO Auto-generated method stub
			return true;
		}


		private EventManagerTest getOuterType() {
			return EventManagerTest.this;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((event == null) ? 0 : event.hashCode());
			result = prime * result + id;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TestMessageListener other = (TestMessageListener) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (event == null) {
				if (other.event != null)
					return false;
			} else if (!event.equals(other.event))
				return false;
			if (id != other.id)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "TestMessageListener [id=" + id + ", event=" + event + "]";
		}
		
		
		
		
		
	}

	
}
