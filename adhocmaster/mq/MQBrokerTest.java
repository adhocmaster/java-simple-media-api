package adhocmaster.mq;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.sun.net.httpserver.Authenticator.Success;

public class MQBrokerTest {
	
	public static String topic1 = "topic.funny";
	public static String topic2 = "topic.serious";

	public static int counterConsumed;
	public static int counterProduced;
	

	@Before
	public void setUp() throws Exception {
		
		counterConsumed = 0;
		counterProduced = 0;
	}

	@Test
	public void testStart() {
		
		MQBroker mqBroker = MQBroker.getInstance();
		assertTrue( true );
		
	}
	
	@Test
	public void testMessaging() {

		/**
		 * create 10 funny producers
		 */
		createTopicProducers(10, topic1);
		/**
		 * create 5 serious producers
		 */

		createTopicProducers(10, topic2);
		
		/**
		 * create 3 funny consumers
		 */

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testLateConsumers() {

		createTopicProducers(2, topic1);
		
		try {
			Thread.sleep( 1000 );
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		createTopicConsumers(5, topic1, 1000, false);

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Test
	public void testMultipleConsumers() {

		createTopicConsumers(5, topic1, 0, true);

		try {
			Thread.sleep( 1000 );
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		createTopicProducers(2, topic1);

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertEquals( counterConsumed, 5 );
		assertEquals( counterProduced, 2 );
		
	}
	
	@Test
	public void testQueue() {

		
		createQueueProducers(2, "queue.first" );
		createQueueProducers(2, "queue.second");

		createQueueConsumers(4,"queue.first", 0, true);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		assertEquals( 4, counterProduced );
		assertEquals(2, counterConsumed);
		

		createQueueProducers(2, "queue.first" );
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		assertEquals( 6, counterProduced );
		assertEquals( 4, counterConsumed );
		
		
	}
	
	public void createTopicProducers( int number, final String topic ) {

		for ( int i = 0; i < number; ++i ) {
			
			try {
				Thread thread = new Thread( new Runnable() {
					
					@Override
					public void run() {
						
						System.out.println( "Starting producer thread " + Thread.currentThread().getName() );
						
						MQBroker mqBroker = MQBroker.getInstance();
						
						try {
							
							mqBroker.publishOnTopic( topic, "a " + topic + " from " + Thread.currentThread().getName() );
							
							MQBrokerTest.this.counterProduced++;
							
						} catch (Exception e) {
	
							e.printStackTrace();
							fail( e.getMessage() );
							
						}
						
					}
				});
				
				//thread.setDaemon( true );
				thread.start();
			} catch ( Exception e ) {
				
				e.printStackTrace();
				
			}
			
		}
		
	}
	
	public void createQueueProducers( int number, final String queue ) {

		for ( int i = 0; i < number; ++i ) {
			
			try {
				Thread thread = new Thread( new Runnable() {
					
					@Override
					public void run() {
						
						System.out.println( "Starting producer thread " + Thread.currentThread().getName() );
						
						MQBroker mqBroker = MQBroker.getInstance();
						
						try {
							
							mqBroker.publishOnQueue( queue, "a " + queue + " from " + Thread.currentThread().getName() );
							
							MQBrokerTest.this.counterProduced++;
							
						} catch (Exception e) {
	
							e.printStackTrace();
							fail( e.getMessage() );
							
						}
						
					}
				});
				
				//thread.setDaemon( true );
				thread.start();
			} catch ( Exception e ) {
				
				e.printStackTrace();
				
			}
			
		}
		
	}
	

	public void createTopicConsumers( int number, final String topic, final int timeout, final boolean mustHaveMessage ) {

		for ( int i = 0; i < number; ++i ) {
			
			try {
				
				Thread thread = new Thread( new Runnable() {
					
					@Override
					public void run() {
						
						System.out.println( "Starting consumer thread " + Thread.currentThread().getName() );
						
						MQBroker mqBroker = MQBroker.getInstance();
						
						try {
							
							String message = mqBroker.receiveTextFromTopic( topic, timeout );
							
							
							System.out.println( Thread.currentThread().getName() + " consumer received a funny message: " + message );
							
							if( ! mustHaveMessage )
								fail( " must not have message" );
							

							MQBrokerTest.this.counterConsumed++;
							
							
						} catch (Exception e) {
	
							e.printStackTrace();
							
							if( mustHaveMessage )
								fail( e.getMessage() );
							
						}
						
					}
				});
				
				//thread.setDaemon( true );
				thread.start();
			} catch ( Exception e ) {
				
				e.printStackTrace();
				
			}
			
		}
		
	}
	public void createQueueConsumers( int number, final String queue, final int timeout, final boolean mustHaveMessage ) {

		for ( int i = 0; i < number; ++i ) {
			
			try {
				Thread thread = new Thread( new Runnable() {
					
					@Override
					public void run() {
						
						System.out.println( "Starting consumer thread " + Thread.currentThread().getName() );
						
						MQBroker mqBroker = MQBroker.getInstance();
						
						try {
							
							String message = mqBroker.receiveTextFromQueue( queue, timeout );
							
							
							System.out.println( Thread.currentThread().getName() + " consumer received a funny message: " + message );
							
							if( ! mustHaveMessage )
								fail( " must not have message" );
							

							MQBrokerTest.this.counterConsumed++;
							
							
						} catch (Exception e) {
	
							e.printStackTrace();
							
							if( mustHaveMessage )
								fail( e.getMessage() );
							
						}
						
					}
				});
				
				//thread.setDaemon( true );
				thread.start();
			} catch ( Exception e ) {
				
				e.printStackTrace();
				
			}
			
		}
		
	}
}
