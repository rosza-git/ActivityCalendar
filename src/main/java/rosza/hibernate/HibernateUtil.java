/**
 * Hibernate Utility class with a convenient method to get Session Factory object.
 * 
 * @author Szalay Roland
 * 
 */
package rosza.hibernate;

import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.text.StrongTextEncryptor;
import rosza.activitycalendar.ActivityCalendar;
import rosza.activitycalendar.Constant;

public class HibernateUtil {
  private static SessionFactory sessionFactory;
  private static Properties props;
  private static StrongTextEncryptor textEncryptor;

  public static SessionFactory getSessionFactory() throws HibernateException, IllegalArgumentException, NullPointerException {
    try {
      textEncryptor = new StrongTextEncryptor();
      textEncryptor.setPassword(Constant.SALT);
      props = ActivityCalendar.getSettings();
      if(props == null) {
        throw new NullPointerException("No settings!");
      }
      if(props.getProperty(Constant.PROPS_DB_SERVER).equals("")) {
        throw new IllegalArgumentException("Missing 'server' parameter!");
      }
      else if(props.getProperty(Constant.PROPS_DB_SERVER_PORT).equals("")) {
        throw new IllegalArgumentException("Missing 'server port' parameter!");
      }
      else if(props.getProperty(Constant.PROPS_DB_USERNAME).equals("")) {
        throw new IllegalArgumentException("Missing 'database username' parameter");
      }
      else if(props.getProperty(Constant.PROPS_DB_PASSWORD).equals("")) {
        throw new IllegalArgumentException("Missing 'database password' parameter");
      }

      Configuration config = new Configuration().configure("hibernate/hibernate.cfg.xml");
      config.setProperty("hibernate.show_sql", "false");
      String url = "jdbc:mysql://" + props.getProperty(Constant.PROPS_DB_SERVER) + ":" + props.getProperty(Constant.PROPS_DB_SERVER_PORT) + "/" + "activitycalendar";
      String username = props.getProperty(Constant.PROPS_DB_USERNAME);
      String password = props.getProperty(Constant.PROPS_DB_PASSWORD);
      try {
        password = textEncryptor.decrypt(password);
      }
      catch(EncryptionOperationNotPossibleException e) {
      }
      config.setProperty("hibernate.connection.url", url);
      config.setProperty("hibernate.connection.username", username);
      config.setProperty("hibernate.connection.password", password);

      StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(config.getProperties());
      sessionFactory = config.buildSessionFactory(builder.build());
    }
    catch(HibernateException e) {
      throw new HibernateException("Error in hibernate initialization!");
    }

    return sessionFactory;
  }

	public static void shutdown() {
		// Close caches and connection pools
		getSessionFactory().close();
	}
}
