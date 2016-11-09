package util.bootstrap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import databasemanager.DatabaseManager;
import repository.RepositoryManager;
import util.DBUtiltity;

public class AppShutDown implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * Close all databse connection while application is closing
	 * @author Alam
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

		RepositoryManager.getInstance().shutDown();
		DBUtiltity.ShutDownDatabaseManager();

	}

}
