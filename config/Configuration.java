package util.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.struts.action.ActionForm;

import databasemanager.DatabaseManager;
import util.DBUtiltity;
import util.interfaces.ModelInterface;


/**
 * name value pair. value cannot be multiple. if you need multiple serialize values.
 * @author muktadir
 *
 */

@Entity
@Table(name="configuration")
public class Configuration {
	
	@Id
	@GeneratedValue
	@Column(name="name")
	protected String name;
	@Column(name="value")
	protected String value;
	@Column(name="auto_load")
	protected String autoLoad = "no";
	@Column(name="type")
	protected String type = "application";

	

	public Configuration(String name, String value, String autoLoad, String type) {
		
		this.name = name;
		this.value = value;
		this.autoLoad = autoLoad;
		this.type = type;
		
	}
	public Configuration(String name, String value, String autoLoad) {
		
		this.name = name;
		this.value = value;
		this.autoLoad = autoLoad;
		
	}
	
	public Configuration(String name, String value) {
		
		this.name = name;
		this.value = value;
		
	}

	public void save() {
		
		ConfigurationDAO dao = new ConfigurationDAO();
		
		dao.update(this);
		
		DBUtiltity.updateVBSequencer("configuration");
	}

	
	public static HashMap<String, Configuration> getAll( boolean autoLoadOnly ) {

		ConfigurationDAO dao = new ConfigurationDAO();
		return dao.getAll(autoLoadOnly);
		
	}
	
	public static Configuration getByName( String name ) {

		ConfigurationDAO dao = new ConfigurationDAO();
		return dao.getByName(name);
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getAutoLoad() {
		return autoLoad;
	}

	public void setAutoLoad(String autoLoad) {
		this.autoLoad = autoLoad;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @param name2
	 */
	public static boolean delete(String name) {
		
		ConfigurationDAO dao = new ConfigurationDAO();
		return dao.deleteByName(name);
		
	}
	@Override
	public String toString() {
		return "Configuration [name=" + name + ", value=" + value + ", autoLoad=" + autoLoad + ", type=" + type + "]";
	}
}

