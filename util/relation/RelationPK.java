package util.relation;

import java.io.Serializable;

public class RelationPK implements Serializable {
	
	protected long fromId = 0;
	protected long toId = 0 ;
	private String relationType = "child";
	
	public RelationPK() {
		
	}
	
	public RelationPK(long fromId, long toId, String relationType) {
		
		this.fromId = fromId;
		this.toId = toId;
		this.relationType = relationType;
	}



	public long getFromId() {
		return fromId;
	}

	public long getToId() {
		return toId;
	}
	
	public String getRelationType() {
		return relationType;
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
		RelationPK other = (RelationPK) obj;
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

	

}
