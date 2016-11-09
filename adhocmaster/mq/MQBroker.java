package adhocmaster.mq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

public class MQBroker implements ExceptionListener {

	private static Logger logger = Logger.getLogger( MQBroker.class );
	
	BrokerService broker;

	public static final String BROKER_NAME = "disaster";
	public static final String storageLimit = "disaster";
	public static final String BROKER_URI = "vm://" + BROKER_NAME;
	public static final String LOCAL_PORT = "61616";
	
	Connection _connection = null;
	
	public static MQBroker _instance = null;
	
	// static initializer to setup broker on app start
	static {
		
		System.out.println(" Starting up broker in static initializer ");
		getInstance();
		
	}
	
	private MQBroker() {
		
		broker = new BrokerService();
		
		broker.setUseJmx( false );
		broker.setBrokerName(BROKER_NAME);
		broker.setPersistent( false );
		
//		broker.getSystemUsage().getTempUsage().setLimit(100L);
//		
//		KahaDBPersistenceAdapter adapter = new KahaDBPersistenceAdapter();
//		
//		adapter.
//		
//		//PersistenceAdapter adapter = broker.getPersistenceAdapter();
//		
//		adapter.
		
		try {

			broker.addConnector( "tcp://localhost:" + LOCAL_PORT );
			broker.start();
			logger.debug( "MQBroker started at" + broker.getVmConnectorURI().toString() );
			System.out.println(  "MQBroker started at " + broker.getVmConnectorURI().toString() );
			
			System.out.println( broker.getSystemUsage().getTempUsage() );
			
			
		} catch (Exception e) {

			e.printStackTrace();
			logger.error( "MQBroker could not start", e );
			
			throw new RuntimeException( "MQBroker could not start" );
			
		}
		
		
	}
	
	private static synchronized void createInstance() {

		if ( null == _instance ) {
			
			_instance = new MQBroker();
			
		}
		
		if ( null == _instance._connection ) {
			
			_instance._connection = createConnection(); 
			
		}
		
		
	}
	
	public static MQBroker getInstance() {
		
		if ( null == _instance ) {
			
			createInstance();
			
		}
		
		return _instance;
		
	}
	
	/**
	 * This function is exclusively used in createInstance. Only one connection is used for creating all the sessions.
	 * @return
	 */
	private static Connection createConnection() {
		
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory( BROKER_URI + "?create=false"); // don't create another broker
		
		try {
			
			Connection connection = connectionFactory.createConnection();

			connection.start();
			// exception listener gets exceptions as it's a asynchronous process.
			connection.setExceptionListener( _instance );
			
			return connection;
			
			
		} catch ( Exception e ) {
			
			String message = "Could not create jms connection: "  + e.getMessage();
			
			logger.error( message, e );
			e.printStackTrace();

			throw new RuntimeException( "MQBroker could not create the connection" );
			
		}
		
		
	}
	
	/**
	 * reuse single connection. The connection object is thread safe and can be used concurrently
	 * @return
	 */
	
	public Connection getConnection() {
		
		return _connection;
		
	}
	
	public Session getAutoSession() throws Exception {
		
		Connection connection = MQBroker.getInstance().getConnection();
		
		if ( null == connection ) {
			
			return null;
			
		}
		
		try {
			
			// Create a Session. disabled transaction, auto acknowledge upon sending/receiving a message
			Session session = connection.createSession( false, Session.AUTO_ACKNOWLEDGE );
			
			return session;
			
		} catch ( Exception e ) {

			String message = "Could not create jms session: " + e.getMessage();
			
			logger.error( message, e );
			e.printStackTrace();
			throw new Exception( message );
			
		}
		
		
	}

	@Override
	public void onException( JMSException e ) {

        System.out.println("JMS Exception occured.  Shutting down client." + e.getMessage() );		
        logger.error( "JMS Exception occured.  Shutting down client.", e);
		
	}

	/**
	 * non persistent producer of a topic with auto acknowledgement. Message expiry is strictly 60 seconds
	 * @param topic should be in format like a package name organization.Organization.eventName
	 * @return null, or a auto producer
	 * @throws Exception 
	 */
	public MessageProducer getAutoTopicProducer( Session session, String topic ) throws Exception {

		if ( null == session )
			throw new IllegalArgumentException( "Session cannot be null" );
		
		if ( null == topic )
			throw new IllegalArgumentException( "Topic cannot be null" );
		
		try {
			
			// Create the destination (Topic or Queue)
			Destination destination = session.createTopic( topic );
			
			// Create a MessageProducer from the Session to the Topic or Queue
			MessageProducer producer = session.createProducer( destination );
			
			producer.setDeliveryMode( DeliveryMode.NON_PERSISTENT );
			producer.setTimeToLive( 60_000L );
			
			return producer;
			
			
		} catch ( Exception e ) {
			
			String message = "Could not create jms producer: " + e.getMessage();
			
			logger.error( message, e );
			e.printStackTrace();
			
			throw new Exception( message );
			
		}
				
		
	}
	
	/**
	 * non persistent producer of a queue with auto acknowledgement. Message expiry is strictly 60 seconds
	 * @param queue should be in format like a package name organization.Organization.eventName
	 * @return null, or a auto producer
	 * @throws Exception 
	 */
	public MessageProducer getAutoQueueProducer( Session session, String queue ) throws Exception {

		if ( null == session )
			throw new IllegalArgumentException( "Session cannot be null" );
		
		if ( null == queue )
			throw new IllegalArgumentException( "queue cannot be null" );
		
		try {
			
			// Create the destination (Topic or Queue)
			Destination destination = session.createQueue( queue );
			
			// Create a MessageProducer from the Session to the Topic or Queue
			MessageProducer producer = session.createProducer( destination );
			
			producer.setDeliveryMode( DeliveryMode.NON_PERSISTENT );
			producer.setTimeToLive( 60_000L );
			
			return producer;
			
			
		} catch ( Exception e ) {
			
			String message = "Could not create jms producer: " + e.getMessage();
			
			logger.error( message, e );
			e.printStackTrace();
			
			throw new Exception( message );
			
		}
				
		
	}

	/**
	 * auto consumer of a topic. Auto consumer sends ack automatically after receiving message
	 * @param session
	 * @param topic
	 * @return
	 * @throws Exception
	 */
	public MessageConsumer getAutoTopicConsumer( Session session, String topic ) throws Exception {

		if ( null == session )
			throw new IllegalArgumentException( "Session cannot be null" );
		
		if ( null == topic )
			throw new IllegalArgumentException( "Topic cannot be null" );
		
		try {
			
			// Create the destination (Topic or Queue)
			Destination destination = session.createTopic( topic );
			
			// Create a MessageConsumer from the Session to the Topic or Queue
			MessageConsumer consumer = session.createConsumer( destination );
						
			return consumer;
						
			
		} catch ( Exception e ) {
			
			String message = "Could not create jms consumer: " + e.getMessage();
			
			logger.error( message, e );
			e.printStackTrace();
			throw new Exception( message );
			
		}
		
		
	}
	

	/**
	 * auto consumer of a topic. Auto consumer sends ack automatically after receiving message
	 * @param session
	 * @param queue
	 * @return
	 * @throws Exception
	 */
	public MessageConsumer getAutoQueueConsumer( Session session, String queue ) throws Exception {

		if ( null == session )
			throw new IllegalArgumentException( "Session cannot be null" );
		
		if ( null == queue )
			throw new IllegalArgumentException( "queue cannot be null" );
		
		try {
			
			// Create the destination (Topic or Queue)
			Destination destination = session.createQueue( queue );
			
			// Create a MessageConsumer from the Session to the Topic or Queue
			MessageConsumer consumer = session.createConsumer( destination );
						
			return consumer;
						
			
		} catch ( Exception e ) {
			
			String message = "Could not create jms consumer: " + e.getMessage();
			
			logger.error( message, e );
			e.printStackTrace();
			throw new Exception( message );
			
		}
		
		
	}
	
	public void closeSession ( Session session ) {
		
		try {
			
			if ( null != session )
				session.close();
			
		} catch ( Exception e) {
			
			// DO NOTHING
			
		}
		
	}

	public void publishOnTopic( String topic, String message ) throws Exception {
		
		Session session = null;
		
		try {

			// 1 Create session
			session  = getAutoSession();
						
			// 2 Create producer
			MessageProducer producer = getAutoTopicProducer(session, topic);
			
			// 3 Send
			TextMessage textMessage = session.createTextMessage( message );
			producer.send( textMessage );
			System.out.println( topic + ":Sent message: " + textMessage.toString() );
			
			// 4 close session
			closeSession(session);
			
		} catch ( Exception e ) {
			
			closeSession(session);
			
			String error = "Could not publish message: " + e.getMessage();
			
			logger.error( error, e );
			e.printStackTrace();
			throw new Exception( error );
			
		}
		
	}

	public void publishOnQueue( String queue, String message ) throws Exception {
		
		Session session = null;
		
		try {

			// 1 Create session
			session  = getAutoSession();
						
			// 2 Create producer
			MessageProducer producer = getAutoQueueProducer(session, queue);
			
			// 3 Send
			TextMessage textMessage = session.createTextMessage( message );
			producer.send( textMessage );
			System.out.println( queue + ": Sent message: " + textMessage.toString() );
			
			// 4 close session
			closeSession(session);
			
		} catch ( Exception e ) {
			
			closeSession(session);
			
			String error = "Could not publish message: " + e.getMessage();
			
			logger.error( error, e );
			e.printStackTrace();
			throw new Exception( error );
			
		}
		
	}
	
	public void publishOnTopic( String topic, Serializable object ) {
		
		// TODO

		// 1 Create session
		// 2 Create producer
		// 3 Send
		
		// 4 close session
		
	}
	/**
	 * 
	 * @param topic
	 * @param timeout 0 to wait indefitinely, in miliseconds
	 * @return null if received message is not a TextMessage, or the contents of the textmessage
	 * @throws Exception
	 */
	public String receiveTextFromTopic( String topic, long timeout ) throws Exception {

		Session session = null;
		String text = null;
		
		try {

			// 1 Create session
			session  = getAutoSession();
			
			// 2 get consumer
			MessageConsumer consumer = getAutoTopicConsumer(session, topic);
			
			// 3 wait for message
			Message message = consumer.receive( timeout );
			
			// 4 parse message
			
			if ( message ==  null ) {

				String error = "Timeout expired or consumer is closed unexpectedly";
				throw new Exception( error );
				
			}
			
			if( message instanceof TextMessage ) {
				
				TextMessage textMessage = (TextMessage) message;
				text = textMessage.getText();
				//System.out.println( "Received: " + text );
				
			} else {

				//System.out.println( "Received malformed data: " + message );
				String error = "Received malformed data: ";
				//logger.error( error );
				throw new Exception( error );
				
			}
			
			// 5 clean up
			closeSession( session );
		
			
		} catch ( Exception e ) {
			
			closeSession( session );
			
			String error = "Could not get message: " + e.getMessage();
			
			logger.error( error, e );
			//e.printStackTrace();
			throw new Exception( error );
			
		}
		
		return text;
		
	}
	
	/**
	 * Basic function. Implement yours for persisted consumers.
	 * @param queue
	 * @param timeout 0 to wait indefinitely, in milliseconds
	 * @return null if received message is not a TextMessage, or the contents of the text message
	 * @throws Exception
	 */
	public String receiveTextFromQueue( String queue, long timeout ) throws Exception {

		Session session = null;
		String text = null;
		
		try {

			// 1 Create session
			session  = getAutoSession();
			
			// 2 get consumer
			MessageConsumer consumer = getAutoQueueConsumer(session, queue);
			
			// 3 wait for message
			Message message = consumer.receive( timeout );
			
			// 4 parse message
			
			if ( message ==  null ) {

				String error = "Timeout expired or consumer is closed unexpectedly";
				throw new Exception( error );
				
			}
			
			if( message instanceof TextMessage ) {
				
				TextMessage textMessage = (TextMessage) message;
				text = textMessage.getText();
				//System.out.println( "Received: " + text );
				
			} else {

				//System.out.println( "Received malformed data: " + message );
				String error = "Received malformed data: ";
				//logger.error( error );
				throw new Exception( error );
				
			}
			
			// 5 clean up
			closeSession( session );
		
			
		} catch ( Exception e ) {
			
			closeSession( session );
			
			String error = "Could not get message: " + e.getMessage();
			
			//logger.error( error, e );
			//e.printStackTrace();
			throw new Exception( error );
			
		}
		
		return text;
		
	}
	
	

}
