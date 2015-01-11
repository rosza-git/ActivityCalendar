/**
 * XML util
 * 
 * @author Szalay Roland
 * 
 */
package rosza.activitycalendar;

import java.util.ArrayList;
import java.util.Properties;
import org.jasypt.util.text.StrongTextEncryptor;
import org.joda.time.DateTime;
import rosza.hibernate.Hibernate;

public class DataManager {
  // Properties variable
  private final Properties props;

  // Variable for encryption
  private final StrongTextEncryptor textEncryptor;      // http://technofes.blogspot.in/2011/10/orgjasyptexceptionsencryptionoperationn.html

  // Storage variable
  private final String storage;
  private final Hibernate hibernate;

  public DataManager() {
    textEncryptor = new StrongTextEncryptor();
    textEncryptor.setPassword(Constant.SALT);
    props = XMLUtil.getProperties();
    hibernate = new Hibernate();

    if(props == null) {
      storage = Constant.PROPS_XML_STORAGE;
    }
    else {
      storage = props.getProperty(Constant.PROPS_STORAGE);
    }
  }

  public Activity getActivityByID(int id) {
    switch(storage) {
      case Constant.PROPS_DB_STORAGE:
        return hibernate.getActivityByID(id);
      case Constant.PROPS_XML_STORAGE:
        return XMLUtil.getActivityByID(id);
    }

    return null;
  }

  public ArrayList<Activity> getActivityByStartDate(DateTime date) {
    switch(storage) {
      case Constant.PROPS_DB_STORAGE:
        return hibernate.getActivityByStartDate(dbDate(date));
      case Constant.PROPS_XML_STORAGE:
        return XMLUtil.getActivityByDate(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
    }

    return null;
  }

  public ArrayList<Activity> getActivityByCategory(int id) {
    switch(storage) {
      case Constant.PROPS_DB_STORAGE:
        return hibernate.getActivityByCategory(id);
      case Constant.PROPS_XML_STORAGE:
        return XMLUtil.getActivityByString(Constant.XML_CATEGORY_TAG, Integer.toString(id));
    }

    return null;
  }

  public boolean addActivity(Activity activity) {
    switch(storage) {
      case Constant.PROPS_DB_STORAGE:
        return hibernate.addActivity(activity);
      case Constant.PROPS_XML_STORAGE:
        return XMLUtil.addActivity(activity);
    }

    return false;
  }

  public boolean updateActivity(Activity activity) {
    switch(storage) {
      case Constant.PROPS_DB_STORAGE:
        return hibernate.updateActivity(activity);
      case Constant.PROPS_XML_STORAGE:
        return XMLUtil.updateActivity(activity);
    }

    return false;
  }

  public boolean removeActivity(Activity activity) {
    switch(storage) {
      case Constant.PROPS_DB_STORAGE:
        return hibernate.removeActivity(activity);
      case Constant.PROPS_XML_STORAGE:
        return XMLUtil.removeActivity(activity);
    }

    return false;
  }

  public String dbDate(DateTime date) {
    return String.format("%d-%02d-%02d", date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
  }
}
