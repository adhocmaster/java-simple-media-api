package util.relation;

import util.abstracts.DAOSuper;
import util.interfaces.ModelInterface;

public class RelationDAO extends DAOSuper<ModelInterface>{

	@Override
	public String getDTOName() {
		// TODO Auto-generated method stub
		return "Relation";
	}

	@Override
	public String getPackageName() {
		// TODO Auto-generated method stub
		return "util.relation";
	}

}
