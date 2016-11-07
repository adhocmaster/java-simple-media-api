package util.relation;

/**
 * A class to save arbitar relations between entities. relation type must be less than 11 characters and must be unique for each kind or relation
 * @author muktadir
 *
 */
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.apache.struts.action.ActionForm;
import org.jdom.JDOMException;

import adhocmaster.model.ModelException;
import util.DAOResult;
import util.interfaces.ModelInterface;

@Entity
@Table(name="relation")
@IdClass(RelationPK.class)
public class Relation implements ModelInterface {

	@Id
	@Column(name="from_id")
	private long fromId;
	
	@Id
	@Column(name="to_id")
	private long toId;

	@Id
	@Column(name="relation_type")
	private String relationType = "test-child";
	
	
	
	public long getFromId() {
		return fromId;
	}

	public void setFromId(long fromId) {
		this.fromId = fromId;
	}

	public long getToId() {
		return toId;
	}

	public void setToId(long toId) {
		this.toId = toId;
	}

	public String getRelationType() {
		return relationType;
	}

	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}
	
	@Override
	public String toString() {
		return "Relation [fromId=" + fromId + ", toId=" + toId + ", relationType=" + relationType + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (fromId ^ (fromId >>> 32));
		result = prime * result + ((relationType == null) ? 0 : relationType.hashCode());
		result = prime * result + (int) (toId ^ (toId >>> 32));
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
		Relation other = (Relation) obj;
		if (fromId != other.fromId)
			return false;
		if (relationType == null) {
			if (other.relationType != null)
				return false;
		} else if (!relationType.equals(other.relationType))
			return false;
		if (toId != other.toId)
			return false;
		return true;
	}

	/** 
	 * Default constructor needed for Hibernate
	 */
	public Relation() {
		
	}

	public Relation(long fromId, long toId, String relationType) {
		
		this.fromId = fromId;
		this.toId = toId;
		this.relationType = relationType;
		
	}

	@Override
	public void save() throws ModelException {

		RelationDAO dao = new RelationDAO();
		
//		Relation chkerObj = Relation.getById(fromId, toId);
//		
//		if ( chkerObj != null  )
//			return;
//		
		DAOResult result = dao.saveOrUpdate(this);
		
		if( ! result.isSuccessful() ) 
			throw new ModelException( result.getMessage() );
		
	}

	@Override
	public HashMap<String, String> getPropertyList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadFromForm(ActionForm form) {
		// TODO Auto-generated method stub
		
	}
	
	public static Relation getById( long fromId, long toId, String relationType ) {
		
		RelationPK pk = new RelationPK( fromId, toId, relationType );
		
		RelationDAO dao = new RelationDAO();
		
		return (Relation) dao.getById(pk);
		
	}
	
	public static void delete( RelationPK pk ) throws ModelException {

		RelationDAO dao = new RelationDAO();
		
		DAOResult result = dao.delete( pk );
		
		if( ! result.isSuccessful() ) 
			throw new ModelException( result.getMessage() );
		
	}

	public static void delete( long fromId, long toId, String relationType ) throws ModelException {

		RelationDAO dao = new RelationDAO();
		DAOResult result = dao.delete( new RelationPK( fromId, toId, relationType ) );
		

		if( ! result.isSuccessful() ) 
			throw new ModelException( result.getMessage() );
		
	}
	/*
	 * a handy function to delete all relations of a specific owner
	 */
	public static void delete( long fromId, String relationType ) throws ModelException {

		RelationDAO dao = new RelationDAO();
		
		HashMap<String, String> conditions = new HashMap<String, String>();
		
		conditions.put( "from_id", String.valueOf( fromId ) );
		conditions.put( "relation_type", relationType );
		
		DAOResult result = dao.delete( conditions, false, true );

		if( ! result.isSuccessful() ) 
			throw new ModelException( result.getMessage() );
		
	}
	
	public static List<Relation> getByReltype( long fromId, String relationType ) {

		RelationDAO dao = new RelationDAO();
		HashMap<String, String> conditions = new HashMap<String, String>();
		
		conditions.put("from_id", String.valueOf( fromId ) );
		conditions.put("relation_type", relationType );
		
		@SuppressWarnings("unchecked")
		List<Relation> results = dao.get( conditions , true); //and conditions
		
		return results;
	}
	

}
