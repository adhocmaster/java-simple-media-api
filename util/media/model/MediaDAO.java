package util.media.model;

import java.util.ArrayList;
import java.util.List;

import common.Logger;
import resource.ResourceDAO;
import util.abstracts.DAOSuper;

public class MediaDAO extends DAOSuper<Media> {

	Logger logger = Logger.getLogger(MediaDAO.class);
	
	private String tableName = "media";
	
	private static final String dtoName = "Media";
	private static final String packageName = "util.media.model";
	

	@Override
	public String getDTOName() {
		return dtoName;
	}

	@Override
	public String getPackageName() {
		return packageName;
	}

	@Override
	public ArrayList get() {
		
		Media m;
		ArrayList<Media> result = new ArrayList<Media>();
		
		for(int i=1; i < 10; ++i) {

			m = new Media();
			m.setId(i);
			m.setName("name " + i);
			m.setExtension("jpg");
			
			result.add(m);
			
		}
		
		logger.debug( result );
		
		return result;
		
	}
	
}
