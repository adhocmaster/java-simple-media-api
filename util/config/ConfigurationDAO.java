package util.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.jdom.JDOMException;

import com.sun.net.httpserver.Authenticator.Success;

import common.Logger;
import databasemanager.DatabaseManager;
import util.DAOResult;
import util.DBUtiltity;

public class ConfigurationDAO {


	protected Logger logger = Logger.getLogger(ConfigurationDAO.class);
	
	public String getDTOName() {

		return "configuration";
		
	}
	public String getPackageName() {

		return "util.config";
		
	}
	
	public String getTableName() {
		// TODO Auto-generated method stub
		return "configuration";
	}
	
	public DAOResult update( Configuration configuration ) {
		
		DAOResult result = new DAOResult();

		Connection connection = null;
		Statement st = null;
		ResultSet resultSet = null;
		
		try {
			

			String sql = "REPLACE INTO " + this.getTableName() + "(name, value, auto_load, type)"
						+ "VALUES ( '" + configuration.getName() 
						+ "', '" + configuration.getValue()
						+ "', '" + configuration.getAutoLoad()
						+ "', '" + configuration.getType() + "' )";

			connection = DatabaseManager.getInstance().getConnection();
			
			st = connection.createStatement();
			
			st.execute(sql);
						
			result.setResult("", true, DAOResult.DONE);
			
			DBUtiltity.updateVBSequencer(this.getTableName());
			
			
		} catch ( Exception exception ) {

			result.setResult(exception.toString(), false, DAOResult.DB_EXCEPTION);
			
			logger.fatal( this.getClass().toString() + exception.toString());
			
		} finally {
			
			try {

				st.close();
				DatabaseManager.getInstance().freeConnection( connection );
				connection = null;
				
			} catch ( Exception exception ) {

				logger.error( this.getClass().toString() + exception.toString());
				
			}
			
		}
		
		return result;
		
	}
	
	public Configuration getByName( String name ) {
		
		Configuration configuration = null;

		Connection connection = null;
		Statement st = null;
		ResultSet resultSet = null;
		try {
			connection = DatabaseManager.getInstance().getConnection();
			
			st = connection.createStatement();
			
			String sql = "SELECT * FROM " + this.getTableName() + " where name = '" + name + "'";
			
			resultSet =  st.executeQuery(sql);
			
			if( resultSet.next() ) {
				
				configuration = new Configuration( resultSet.getString("name"), resultSet.getString( "value" ) );
				configuration.setAutoLoad( resultSet.getString( "auto_load" ) );
				configuration.setType( resultSet.getString( "type" ) );
				
			}
			
			resultSet.close();
			
		} catch ( Exception exception ) {
			
			logger.fatal( this.getClass().toString() + exception.toString());
			
		} finally {
			
			try {

				st.close();
				DatabaseManager.getInstance().freeConnection( connection );
				connection = null;
				
			} catch ( Exception exception ) {

				logger.error( this.getClass().toString() + exception.toString());
				
			}
			
		}
		
		
		return configuration;
		
	}
	

	public HashMap<String, Configuration> getAll( boolean autoLoadOnly )  {
		
		HashMap<String, Configuration> data = new HashMap<String, Configuration>();
		
		Connection connection = null;
		Statement st = null;
		ResultSet resultSet = null;
		
		try {
			
			connection = DatabaseManager.getInstance().getConnection();
			
			st = connection.createStatement();
			
			String cond = "";
			
			if( autoLoadOnly ) {
				
				cond = " where auto_load = 'yes' ";
				
			} 
			String sql = "SELECT * FROM " + this.getTableName() + cond;
			
			resultSet =  st.executeQuery(sql);

			Configuration configuration = null;
			
			while( resultSet.next() ) {

				configuration = new Configuration( resultSet.getString("name"), resultSet.getString( "value" ) );
				configuration.setAutoLoad( resultSet.getString( "auto_load" ) );
				configuration.setType( resultSet.getString( "type" ) );
				
				data.put(configuration.getName(), configuration);
				
			}
			
			
		} catch ( Exception exception ) {
			
			logger.fatal( this.getClass().toString() + exception.toString());
			
		} finally {
			
			try {

				st.close();
				DatabaseManager.getInstance().freeConnection( connection );
				connection = null;
				
			} catch ( Exception exception ) {

				logger.error( this.getClass().toString() + exception.toString());
				
			}
			
		}
				
		return data;
		
	}
	/**
	 * @author Alam
	 * @param configuration
	 */
	public boolean deleteByName(String name) {
		
		Connection cn = null;
		PreparedStatement ps = null;
		boolean success = false;
		
		String sql = "delete from configuration where name = ?";
		
		try{
			cn = DatabaseManager.getInstance().getConnection();
			ps = cn.prepareStatement(sql);
			ps.setString(1, name);
			
			ps.executeUpdate();
			success = true;
		}
		catch ( Exception exception ) {
			success = false;
			logger.fatal( this.getClass().toString() + exception.toString());
			
		} 
		finally {
			
			try {

				ps.close();
				DatabaseManager.getInstance().freeConnection( cn );
				cn = null;
				
			} catch ( Exception exception ) {

				logger.error( this.getClass().toString() + exception.toString());
				
			}
			
		}
		return success;
	}
	
}
