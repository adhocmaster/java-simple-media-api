package util.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import common.Logger;
import repository.Repository;
import repository.RepositoryManager;
import repository.RepositoryMuktadir;
import util.DAOResult;
import util.DBUtiltity;

public class ConfigurationManager implements Repository, RepositoryMuktadir {

	protected Logger logger = Logger.getLogger(ConfigurationManager.class);
	
	private static ConfigurationManager _instance = null;
	
	private long lastUpdateTime = 0;
	
	protected HashMap<String, Configuration> data = null;
	
	/**
	 * Static initializer
	 */
	static {
		
		createInstance();
		
	}
	

	private ConfigurationManager() {
		
		//load auto_load configs
		data = new HashMap<String, Configuration>();
		RepositoryManager.getInstance().addRepository(this);
		
	}
	
	public static ConfigurationManager getInstance( ) {
		
		if( _instance ==  null ) {
			
			createInstance();
			
		}
		
		return _instance;
		
	}
	
	/**
	 * Synchronized method to create the singletion
	 * @author muktadir
	 */
	public static synchronized void createInstance( ) {

		if( _instance ==  null ) {
			
			_instance = new ConfigurationManager( );
			
		}
		
	}

	@Override
	public long getLastUpdatedTime() {

		return lastUpdateTime;
		
	}

	@Override
	public void setLastUpdatedTime(long lastUpdatedTime) {

		this.lastUpdateTime = lastUpdatedTime;
		
		
	}

	@Override
	public synchronized void reload(boolean realoadAll) {

		data = Configuration.getAll( false );
		
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "configuration";
	}
	
	/**
	 * Returns string value of a configuration. Returns empty string if not found
	 * @param name
	 * @return
	 */
	
	public String getVal( String name ) {
		
		
		Configuration configuration = this.getByName(name);
		
		if( configuration ==  null )
			return "";
		
		return configuration.getValue();
		
		
	}
	
	/**
	 * Handy function to create new configurations if doesn't exits
	 * @param name
	 * @param value
	 */
	public void updateVal( String name, String value ) {
		
		Configuration configuration = new Configuration(name, value);
		
		configuration.save();

		data.put(name, getByName( name )); // reloads from db
		
	}
	/**
	 * Handy function to create new configurations if doesn't exits
	 * @param name
	 * @param value
	 * @param autoload yes/no. if yes, it will be loaded when data repository is loaded.
	 */
	public void updateVal( String name, String value, String autoload ) {
		
		Configuration configuration = new Configuration(name, value, autoload);
		
		configuration.save();
		
		data.put(name, getByName( name )); // reloads from db
		
	}
	/**
	 * returns if in repo. if not in repo, loads from data, adds to repo, and returns.
	 * @param name
	 * @return
	 */
	
	public Configuration getByName( String name ) {

		if( data != null ) {
			
			if ( data.containsKey( name ) ) {
				
				return data.get( name );
				
			}
			
		}
		// is not available in data map. get from db
		
		Configuration configuration = Configuration.getByName(name);
		
		if( configuration ==  null )
			return null;
		
		data.put(name, configuration);
		
		return configuration;
		
	}
	
	/**
	 * This method is used to get all the configuration of current system
	 * @author Alam
	 * @return an array list of configuration. If no configuration is yet set, empty arraylist will be returned
	 */
	public List<Configuration> getAll(){
		
		List<Configuration> configurations = new ArrayList<Configuration>();
		
		for (String key : data.keySet()) {
			configurations.add(data.get(key));
		}
		
		return configurations;
	}

	public boolean delete(String name){
		
		boolean deleted = Configuration.delete(name);
		DBUtiltity.updateVBSequencer("configuration");
		return deleted;
		
	}
}
