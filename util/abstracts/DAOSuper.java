package util.abstracts;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;


import common.Logger;
import util.DAOResult;
import util.interfaces.ModelInterface;

/**
 * This class will be extended by all the DAO classes. Basic functionalities are defined here
 * @author Muktadir
 * @contributor Alam
 * @version 1.0
 * @package util.abstracts
 * @param <DTO> Any DTO Class
 */
public abstract class DAOSuper<DTO extends ModelInterface> {

	protected Logger logger = Logger.getLogger(DAOSuper.class);
	private static SessionFactory factory = null;
	private static ServiceRegistry serviceRegistry = null;

	public abstract String getDTOName();
	public abstract String getPackageName();
	
	/**
	 * static initializer added by Muktadir
	 */
	static {

		createSessionFactory();
		
	}
	
	public static SessionFactory getSesstionFactory()
	{
		if(factory==null)
		{
			createSessionFactory();
		}
		return factory;
	}
	
	/**
	 * This method is used to make a system wide Session Factory. It's Thread safe.
	 * @author Alam
	 * @return SessionFactory
	 */
	private synchronized static void createSessionFactory() 
	{
		if(factory==null)
		{
			Configuration configuration = new Configuration().configure();
			serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
			factory = configuration.buildSessionFactory(serviceRegistry);
		}
	}
	
	/**
	 * This method can be used to save a DTO to database. DTO must have mapping it's field to database column
	 * @author Alam
	 * @param dto DTO class to be saved
	 * @return DAO result containing result message
	 * @throws DataFormatException 
	 */
	public DAOResult add(DTO dto) 
	{
		DAOResult result = new DAOResult();
		try
		{
			Session session = getSesstionFactory().openSession();
			
			Transaction tx = session.beginTransaction();
			
			session.save(dto);
			session.flush();
			session.clear();
			
			tx.commit();
			session.close();
			result.setResult("", true, DAOResult.DONE);
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
			result.setResult(exception.toString(), false, DAOResult.DB_EXCEPTION);
			logger.fatal( this.getClass().toString() + exception.toString());
		}
		return result;
	}
	
	public DAOResult saveOrUpdate(DTO dto) {

		DAOResult result = new DAOResult();
		try
		{
			Session session = getSesstionFactory().openSession();
			
			Transaction tx = session.beginTransaction();
			
			session.saveOrUpdate(dto);
			session.flush();
			session.clear();
			
			tx.commit();
			session.close();
			result.setResult("", true, DAOResult.DONE);
		}
		catch(Exception exception)
		{
			result.setResult(exception.toString(), false, DAOResult.DB_EXCEPTION);
			logger.fatal( this.getClass().toString() + exception.toString());
		}
		return result;
		
	}
	
	
	/**
	 * this method works just like as add method except it processes a batch of insert
	 * 20 records are processed in a batch
	 * @author Alam
	 * @param dto List of model object to be inserted
	 * @return
	 */
	public DAOResult add(ArrayList<DTO> dto){
		DAOResult result = new DAOResult();
		try
		{
			Session session = getSesstionFactory().openSession();
			Transaction tx = session.beginTransaction();
			for(int i=0;i<dto.size();i++){
				session.save(dto.get(i));
				if(i%20==0){
					session.flush();
					session.clear();
				}
			}
				
			tx.commit();
			session.close();
			
			result.setResult("", true, DAOResult.DONE);
		}
		catch(Exception exception)
		{
			result.setResult(exception.toString(), false, DAOResult.DB_EXCEPTION);
			logger.fatal("DAO "+exception.toString());
		}
		return result;
	}

	/**
	 * This method is used to delete an entity from table using it's primary key.
	 * @author Alam
	 * @param id Primary key of entity
	 * @return
	 */
	public DAOResult delete(long id){
		DAOResult result = new DAOResult();
		try
		{
			Session session = getSesstionFactory().openSession();
			Transaction tx = session.beginTransaction();
			
			Object dto = session.get(Class.forName(getPackageName()+"."+getDTOName()), id);
			session.delete(dto);
			
			tx.commit();
			session.close();
			
			result.setResult("", true, DAOResult.DONE);
		}
		catch(Exception exception)
		{
			result.setResult(exception.getMessage(), false, DAOResult.DB_EXCEPTION);
			logger.fatal("DAO "+exception.toString());
		}
		return result;
	}
	
	/**
	 * This method delete one or more entity from database based on some condition
	 * @author Alam
	 * @param conditions key value pair map for conditions
	 * @param useLike true if want to match condition using mysql like, false to check using equals.
	 * @param useAnd set true if want to and multiple conditions. if false, or will be used
	 * @return true if successfully deleted. otherwise false
	 */
	public DAOResult delete(HashMap<String, String> conditions, boolean useLike,boolean useAnd){
		
		DAOResult result = new DAOResult();
		StringBuilder sql = new StringBuilder();
		
		sql.append("delete from "+getPackageName()+"."+getDTOName());
		sql.append(" where ");
		
		for(String key: conditions.keySet())
		{
			if(!useLike){
				sql.append(" "+key+" = '"+conditions.get(key)+"' ");
				if(useAnd)
					sql.append("and");
				else
					sql.append("or "); //This space after or is important
			}
			else{
				sql.append(" "+key+" like '%"+conditions.get(key)+"%' ");
				if(useAnd)
					sql.append("and");
				else
					sql.append("or "); //This space after or is important
			}
		}
		if(conditions.size()!=0)
			sql.setLength(sql.length()-3); //Removes the last 'and' or 'or'
		else
			sql.setLength(sql.length()-6); //Removes the last where
		try{
			Session session = getSesstionFactory().openSession();
			session.getTransaction().begin();
			
			Query query = session.createQuery(sql.toString());
			query.executeUpdate();
			
			session.getTransaction().commit();
			result.setResult("", true, DAOResult.DONE);
			session.close();
			
			result.setResult("", true, DAOResult.DONE);
		}
		catch(Exception exception)
		{
			result.setResult(exception.toString(), false, DAOResult.DB_EXCEPTION);
			logger.fatal( this.getClass().getName() + exception.toString());
		}
		return result;
	}

	/**
	 * Deletes by serializable id
	 * @param id
	 * @return
	 */

	public DAOResult delete(Serializable id){
		
		DAOResult result = new DAOResult();
		try
		{
			Session session = getSesstionFactory().openSession();
			Transaction tx = session.beginTransaction();
			
			Object dto = session.get(Class.forName(getPackageName()+"."+getDTOName()), id);
			session.delete(dto);
			
			tx.commit();
			session.close();
			
			result.setResult("", true, DAOResult.DONE);
		}
		catch(Exception exception)
		{
			result.setResult(exception.toString(), false, DAOResult.DB_EXCEPTION);
			logger.fatal( this.getClass().getName() + exception.toString());
		}
		return result;
	}
	
	/**
	 * This method can be used to Edit a field of database. DTO must have mapping it's field to database column
	 * @author Alam
	 * @param conditions Condition value pair
	 * @param tuple Column value pair
	 * @return 
	 */
	public DAOResult update(HashMap<String, String> conditions, HashMap<String, String> tuple) 
	{
		DAOResult result = new DAOResult();
		StringBuilder sql = new StringBuilder();
		
		sql.append("update "+getDTOName()+" set ");
		for(String key: tuple.keySet())
		{
			sql.append(key+" = '"+tuple.get(key)+"',");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(" where ");
		for(String key: conditions.keySet())
		{
			sql.append(" "+key+" = '"+conditions.get(key)+"' and");
		}
		sql.setLength(sql.length()-3);
		
		try
		{
			Session session = getSesstionFactory().openSession();
			session.getTransaction().begin();
			
			Query query = session.createQuery(sql.toString());
			query.executeUpdate();
			
			session.getTransaction().commit();
			result.setResult("", true, DAOResult.DONE);
			session.close();
		}
		catch(Exception e)
		{
			result.setResult(e.toString(), false, DAOResult.DB_EXCEPTION);
			logger.fatal( this.getClass().getName() + e.toString());
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * This method can be used to set a dto. It will remove if a field isn't set. Will create new row if primary key is not found.
	 * To update an existing row, first get the dto by calling getByID() and then set specific property. Then call this method with dto.
	 * @param dto DTO to be updated
	 * @return
	 */
	public DAOResult update(DTO dto)
	{
		DAOResult result = new DAOResult();
		try
		{
			Session session = getSesstionFactory().openSession();
			
			session.getTransaction().begin();
			
			session.update(dto);
			session.flush();
			session.getTransaction().commit();
			
			session.close();
			result.setResult("", true, DAOResult.DONE);
		}
		catch(Exception e)
		{
			result.setResult(e.toString(), false, DAOResult.DB_EXCEPTION);
			logger.debug("DAO "+e.toString());
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * This method returns array list of dto but only specified fields are set.It should be used when Only specific columns are needed 
	 * thus reduce data transfer between java and mysql.
	 * @author Alam
	 * @param columnNames List of column names needed
	 * @param conditions Condition on which data will be searced
	 * @param orderBy Column name and order direction mapping
	 * @param offset How many rows to skip. -1 to skip no row
	 * @param limit How many rows to read. -1 to fetch all rows
	 * @param useLike set true if data is mathced using like, false to check by equal
	 * @param useAnd set true to and the conditions.false to or the conditions
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "null" })
	public ArrayList getFields(ArrayList<String> columnNames,HashMap<String, String> conditions,HashMap<String, String> orderBy,long offset,long limit,boolean useLike,boolean useAnd){
		StringBuilder sql = new StringBuilder();
		ArrayList data = new ArrayList<>();
		
		//If column name is not null , append the column names after select
		if(columnNames != null) { 
			sql.append("select ");
			for(String columnsName: columnNames){
				sql.append(columnsName+",");
			}
			sql.setLength(sql.length()-1);
		}
		
		sql.append(" from "+getPackageName()+"."+getDTOName());
		sql.append(" where ");
		
		if(conditions != null || conditions.size()!=0){
			for(String key: conditions.keySet())
			{
				if(!useLike){
					sql.append(" "+key+" = '"+conditions.get(key)+"' ");
					if(useAnd)
						sql.append("and");
					else
						sql.append("or "); //This space after or is importatn
				}
				else{
					sql.append(" "+key+" like '%"+conditions.get(key)+"%' ");
					if(useAnd)
						sql.append("and");
					else
						sql.append("or "); //This space after or is importatn
				}
			}
		}
		
		if( conditions.size() == 0)
			sql.setLength(sql.length()-6); //Removes the last where
		else
			sql.setLength(sql.length()-3); //Removes the last and
		
		if( orderBy.size()!=0){
			sql.append(" order by ");
			for(String key: orderBy.keySet()){
				sql.append(key+" "+orderBy.get(key)+", ");
			}
			sql.setLength(sql.length()-2);
		}
		
		System.out.println(sql.toString());
		try
		{
			Session session = getSesstionFactory().openSession();
			Transaction tx = session.beginTransaction();
			
			Query query = session.createQuery(sql.toString());
			if(offset > 0)
				query.setFirstResult((int) offset);
			if(limit > 0)
				query.setMaxResults((int) limit);
			
			
			if(columnNames.size()>1){
				
				//If required column count is more then one, then a list of Object array is returned
				ArrayList<Object[]> listOfData= (ArrayList<Object[]>)query.list();
			
				//List of Object array is returned
				for(Object[] temp:listOfData){
					
					//Create an instance of model class.
					Object model = Class.forName(getPackageName()+"."+getDTOName()).newInstance();
					
					//Iterate through the object array and set value to model property
					for(int m=0;m<temp.length;m++){
						
						//This method set a value to specified property
						setProperty(temp[m], Class.forName(getPackageName()+"."+getDTOName()), model, columnNames.get(m));
					}
					
					//add the model to list
					data.add(model);
				}
			}
			else if(columnNames.size()==1){
				
				//If required column count is 1, then a list of object is returned
				ArrayList<Object> listOfData = (ArrayList<Object>)query.list();
				
				for(Object temp:listOfData){
					
					//Create an instance of model class
					Object model = Class.forName(getPackageName()+"."+getDTOName()).newInstance();
					
					//Set the property of model from fetched value
					setProperty(temp, Class.forName(getPackageName()+"."+getDTOName()), model, columnNames.get(0));
					
					//add the model to list
					data.add(model);
				}
			}
			tx.commit();
			session.close();
		}
		catch(Exception e)
		{
			logger.debug("DAO "+e.toString());
			e.printStackTrace();
		}
		return data;
	}
	
	/**
	 * This method is used to set data to specified field. 
	 * @author Alam
	 * @param data Data to be set to property
	 * @param dto Class object of DTO/Model
	 * @param model a model object, this object's property will be set.
	 * @param fieldName property name, of which value will be set. Write as written in your model/DTO class
	 * @throws NumberFormatException 
	 * @throws NoSuchFieldException Throws when no field Name matches with passed parameter
	 * @throws SecurityException 
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws IntrospectionException
	 */
	@SuppressWarnings("rawtypes")
	protected void setProperty(Object data,Class dto,Object model,String fieldName) throws NumberFormatException, NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException {
		
		//get property descriptor by giving property name (as written in DTO/Model class)
		PropertyDescriptor pd = new PropertyDescriptor( fieldName, dto );
		
		//Get setter method for that property
		Method setter = pd.getWriteMethod();

		setter.invoke(model, data);
	}
	
	/**
	 * This method is used to get data which match certain conditions given by map and supports order by multiple column
	 * Order by column name and order direction map has to be provided
	 * @author Alam
	 * @param conditions Column name and condition map. Conditions will be anded. 
	 * @param orderBy Column name and order direction mapping
	 * @param offset No of row to skip. -1 to skip no row
	 * @param limit No of row to return. -1 to set no limit
	 * @param useLike set true if want to search using like, false for search by equality check
	 * @param useAnd true to search by anding the condition. set false to search by oring the condtions
	 * @return ArrayList of DTOs
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList get(HashMap<String, String> conditions,HashMap<String, String> orderBy,long offset,long limit,boolean useLike,boolean useAnd){
		
		StringBuilder sql = new StringBuilder();
		ArrayList data = null;
		sql.append("from "+getPackageName()+"."+getDTOName());
		sql.append(" where ");
		
		for(String key: conditions.keySet())
		{
			if(!useLike){
				sql.append(" "+key+" = '"+conditions.get(key)+"' ");
				if(useAnd)
					sql.append("and");
				else
					sql.append("or "); //This space after or is important
			}
			else{
				sql.append(" "+key+" like '%"+conditions.get(key)+"%' ");
				if(useAnd)
					sql.append("and");
				else
					sql.append("or "); //This space after or is important
			}
		}
		if(conditions.size()!=0)
			sql.setLength(sql.length()-3); //Removes the last 'and' or 'or'
		else
			sql.setLength(sql.length()-6); //Removes the last where
		
		if(orderBy.size()!=0){
			sql.append(" order by ");
			for(String key: orderBy.keySet()){
				sql.append(key+" "+orderBy.get(key)+", ");
			}
			sql.setLength(sql.length()-2);
		}
		
		
		data = get(sql.toString(), (int) offset, (int) limit);
		
		return data;
	}

	@SuppressWarnings("rawtypes")
	public List getCountByConditionString( String conditionString ) {
		
		StringBuilder sql = new StringBuilder();
		ArrayList data = null;
		sql.append("select count(*) as count from "+getPackageName()+"."+getDTOName());
		sql.append(" where ");
		sql.append( conditionString );
		
		System.out.println(sql.toString());
		
		data = get(sql.toString(), 0, 0);
		
		return data;
	}
	
	@SuppressWarnings("rawtypes")
	public List getByConditionString(String conditionString, HashMap<String, String> orderBy,long offset,long limit){
		
		StringBuilder sql = new StringBuilder();
		ArrayList data = null;
		sql.append("from "+getPackageName()+"."+getDTOName());
		sql.append(" where ");
		sql.append( conditionString );
		
		
		if(orderBy.size()!=0){
			sql.append(" order by ");
			for(String key: orderBy.keySet()){
				sql.append(key+" "+orderBy.get(key)+", ");
			}
			sql.setLength(sql.length()-2);
		}
		
		System.out.println(sql.toString());
		
		data = get(sql.toString(), (int) offset, (int) limit);
		
		return data;
	}
	
	@SuppressWarnings("rawtypes")
	public List getByConditionString( String conditionString, HashMap<String, String> orderBy ){
		
		StringBuilder sql = new StringBuilder();
		ArrayList data = null;
		sql.append( "from " + getPackageName() + "." + getDTOName() );
		sql.append(" where ");
		sql.append( conditionString );
		
		
		if(orderBy.size()!=0){
			sql.append(" order by ");
			for(String key: orderBy.keySet()){
				sql.append(key+" "+orderBy.get(key)+", ");
			}
			sql.setLength(sql.length()-2);
		}
		
		System.out.println(sql.toString());
		
		data = get( sql.toString(),0 ,0 );
		
		return data;
	}
	
	/**
	 * HQL uses class FDQN, and property names ( not db col names ). HQL starts from "FROM" keyword
	 * @author muktadir
	 * @param sql
	 * @param bean
	 * @param offset
	 * @param limit
	 * @return
	 */

	@SuppressWarnings("rawtypes")
	public ArrayList get( String hql, int offset, int limit ) {

		System.out.println( "HQL: " + hql + " offset " + offset + " limit " + limit );
		
		ArrayList data = null;
		try
		{
			Session session = getSesstionFactory().openSession();
			Transaction tx = session.beginTransaction();
			
			Query query = session.createQuery(hql.toString());
			if(offset > 0)
				query.setFirstResult((int) offset);
			
			if(limit > 0)
				query.setMaxResults((int) limit);
			data = (ArrayList) query.list();
			
			tx.commit();
			session.close();
		}
		catch(Exception e)
		{
			logger.debug( this.getClass().toString() + e.toString());
			e.printStackTrace();
		}
		
		//System.out.println( "HQL DATA: " + data.toString() );
		return data;
		
	}
	
	/**
	 * Prepared statements for searching. Hql :col, beans is a col->val map. HQL uses class FDQN, and property names ( not col names)
	 * @author muktadir
	 * @param sql
	 * @param bean
	 * @param offset
	 * @param limit
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList get( String hql, Map bean, int offset, int limit ) {

		System.out.println( "HQL: " + hql + " offset " + offset + " limit " + limit );
		
		ArrayList data = null;
		try
		{
			Session session = getSesstionFactory().openSession();
			Transaction tx = session.beginTransaction();
			
			Query query = session.createQuery(hql).setProperties( bean );
			
			if(offset > 0)
				query.setFirstResult((int) offset);
			
			if(limit > 0)
				query.setMaxResults((int) limit);
			data = (ArrayList) query.list();
			
			tx.commit();
			session.close();
		}
		catch(Exception e)
		{
			logger.debug( this.getClass().toString() + e.toString());
			e.printStackTrace();
		}
		
		return data;
		
	}
	

	/**
	 * Defaults or conditions
	 * @author muktadir
	 * @param conditions
	 * @param orderBy
	 * @param offset
	 * @param limit
	 * @return
	 */
	public ArrayList get(HashMap<String, String> conditions,HashMap<String, String> orderBy,long offset,long limit){

		return get( conditions, orderBy, offset, limit, false, false);
	}

	/**
	 * with and condition
	 * @author muktadir
	 * @param conditions
	 * @return
	 */
	public ArrayList get( HashMap<String, String> conditions, boolean useAnd ) {
		
		return get( conditions, new HashMap<String, String>(), -1, -1, false, true);
		
	}	
	/**
	 * with and condition
	 * @author muktadir
	 * @param conditions
	 * @return
	 */
	public ArrayList get( HashMap<String, String> conditions, boolean useAnd, HashMap<String, String> orderBy ) {
		
		return get( conditions, orderBy, -1, -1, false, true);
		
	}

	/**
	 * Defaults or conditions
	 * @author muktadir
	 * @param conditions
	 * @return
	 */
	public ArrayList get( HashMap<String, String> conditions ) {
		
		return get( conditions, new HashMap<String, String>(), -1, -1, false, false);
		
	}
	/**
	 * Defaults or conditions
	 * @author muktadir
	 * @return
	 */
	public ArrayList get() {
		
		return get( new HashMap<String, String>(), new HashMap<String, String>(), -1, -1, false, false);
		
	}
	
	/**
	 * This method can be used to get collection of rows that match certain conditions
	 * @author Alam
	 * @deprecated This method is deprecated. Use get(HashMap,HashMap,offset,limit,useLike,useAnd) instead.That method will give more functionality
	 * and search options. If you doesn't need all the columns of a database table,then use getFields() method . This method takes list of column
	 * name as argument and returns only those columns ,thus reduce data transfer.
	 * @param conditions column data pair ,on this condition table will be searched
	 * @param orderBy column name on which data should be sorted. empty string for no ordering
	 * @param orderDirection sort direction, 'Asc' for ascending order, 'Desc' for descending order. Empty string for no order direction
	 * @param offset row offset,this number of row will be skipped. -1 for no offset
	 * @param limit limit the number of rows returned. -1 for no limit
	 * @return Return a ArrayList of DTO
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList get(HashMap<String, String> conditions, String orderBy,String orderDirection ,long offset, long limit) {
		
		StringBuilder sql = new StringBuilder();
		ArrayList data = null;
		sql.append("from "+getPackageName()+"."+getDTOName());
		sql.append(" where ");
		for(String key: conditions.keySet())
		{
			sql.append(" "+key+" = '"+conditions.get(key)+"' and");
		}
		if(conditions.size()!=0)
			sql.setLength(sql.length()-3); //Removes the last and
		else
			sql.setLength(sql.length()-6); //Removes the last where
		if(!orderBy.equals(""))
			sql.append(" order by "+orderBy+" ");
		if(!orderBy.equals(""))
			sql.append(orderDirection);
		System.out.println(sql.toString());
		try
		{
			Session session = getSesstionFactory().openSession();
			
			Query query = session.createQuery(sql.toString());
			if(offset!=-1)
				query.setFirstResult((int) offset);
			if(limit!=-1)
				query.setMaxResults((int) limit);
			data = (ArrayList) query.list();
			
			session.close();
		}
		catch(Exception e)
		{
			logger.debug( this.getClass().toString() + e.toString());
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * This method returns the number of row that mathced the search criteria
	 * @author Alam
	 * @param conditions Condition on which data will be searched
 	 * @param useLike set true if want to search using like, false for search by equality check
	 * @param useAnd true to search by anding the condition. set false to search by oring the condtions
	 * @return count of data that mathced the search criteria
	 */
	public long getFilteredDataCount(HashMap<String, String> conditions,boolean useLike, boolean useAnd) {
		StringBuilder sql = new StringBuilder();
		long count = -1;
		sql.append("select count(*) from "+getPackageName()+"."+getDTOName());
		sql.append(" where ");
		for(String key: conditions.keySet())
		{
			if(!useLike){
				sql.append(" "+key+" = '"+conditions.get(key)+"' ");
				if(useAnd)
					sql.append("and");
				else
					sql.append("or");
			}
			else{
				sql.append(" "+key+" like '%"+conditions.get(key)+"%' ");
				if(useAnd)
					sql.append("and");
				else
					sql.append("or");
			}
		}
		if(conditions.size()!=0)
			sql.setLength(sql.length()-3); //Removes the last and
		else
			sql.setLength(sql.length()-6); //Removes the last where
		System.out.println(sql.toString());
		try
		{
			Session session = getSesstionFactory().openSession();
			
			Query query = session.createQuery(sql.toString());
			count = (long) (query.uniqueResult());
			
			session.close();
		}
		catch(Exception e)
		{
			logger.debug( this.getClass().toString() + e.toString());
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * This method can be used to get a dto by id.
	 * @param id primary key
	 * @return	object if found, null otherwise. 
	 * @SuppressWarnings("unchecked")
	 */
	@SuppressWarnings("unchecked")
	public DTO getById(long id) {
		DTO dto = null;
		try
		{
			Session session = getSesstionFactory().openSession();
			
			dto = (DTO) session.get( Class.forName( getPackageName() + "." + getDTOName() ), id );
			
			session.close();
		}
		catch(Exception e)
		{
			logger.debug( this.getClass().toString() + e.toString());
			e.printStackTrace();
		}
		return dto;
	}

	/**
	 * This method can be used to get a dto by composite id.
	 * @param id primary key
	 * @return	object if found, null otherwise. 
	 * @SuppressWarnings("unchecked")
	 */
	@SuppressWarnings("unchecked")
	public DTO getById(Serializable id) {
		
		DTO dto = null;
		try
		{
			Session session = getSesstionFactory().openSession();
			
			dto = (DTO) session.get( Class.forName( getPackageName() + "." + getDTOName() ), id );
			
			session.close();
		}
		catch(Exception e)
		{
			logger.debug( this.getClass().toString() + e.toString());
			e.printStackTrace();
		}
		return dto;
	}

	/**
	 * This method is used to get all the ids of a table
	 * @author Alam
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList getIds(){
		ArrayList data = new ArrayList<>();
		String sql = "select id from "+getPackageName()+"."+getDTOName();
		try
		{
			Session session = getSesstionFactory().openSession();
			
			Query query = session.createQuery(sql.toString());
			
			data = (ArrayList) query.list();
			
			session.close();
		}
		catch(Exception e)
		{
			logger.debug( this.getClass().toString() + e.toString());
			e.printStackTrace();
		}
		return data;
	}
	
	/**
	 * This method is used to count number of rows in a table
	 * @author Alam
	 * @return count of numbe of rows
	 */
	public long getTotalRowCount(){
		long totalCount = -1;
		String sql = "select count(*) from "+ getPackageName()+"."+ getDTOName();
		try{
			Session session = getSesstionFactory().openSession();
			
			Query query = session.createQuery(sql);
			totalCount = (long) (query.uniqueResult());
		}
		catch(Exception e){
			logger.debug( this.getClass().toString() + e.toString());
			e.printStackTrace();
		}
		return totalCount;
	}
	

}
