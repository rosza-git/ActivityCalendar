/**
 * XML util
 * 
 * @author Szalay Roland
 * 
 */
package rosza.activitycalendar;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.hibernate.exception.JDBCConnectionException;
import org.jasypt.util.text.StrongTextEncryptor;
import org.joda.time.DateTime;
import rosza.hibernate.Hibernate;

public class DataManager {
  // Variable for encryption
  private static final StrongTextEncryptor textEncryptor = new StrongTextEncryptor();      // http://technofes.blogspot.in/2011/10/orgjasyptexceptionsencryptionoperationn.html

  // Storage variable
  private String storage;
  private Hibernate hibernate;

  public DataManager() {
    textEncryptor.setPassword(Constant.SALT);
    try {
      storage = ActivityCalendar.getSettings().getProperty(Constant.PROPS_STORAGE);
      if(storage == null) {
        storage = Constant.PROPS_XML_STORAGE;
      }
    }
    catch(NullPointerException e) {
      storage = Constant.PROPS_XML_STORAGE;
    }
    if(storage.equals(Constant.PROPS_DB_STORAGE)) {
      try {
        hibernate = new Hibernate();
      }
      catch(JDBCConnectionException e) {
        throw new JDBCConnectionException("DataManager", e.getSQLException());
      }
      catch(NullPointerException e) {
        throw new NullPointerException(e.getMessage() + " -> DataManager");
      }
      catch(IllegalArgumentException e) {
        JOptionPane.showMessageDialog(null, e.getMessage(), "DataManager error", JOptionPane.ERROR_MESSAGE);
      }
      catch(Exception e) {
        JOptionPane.showMessageDialog(null, e.getCause() + "\n" + e.getMessage(), "DataManager - " + e.toString(), JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  public Category getCategoryByID(int id) {
    switch(storage) {
      case Constant.PROPS_DB_STORAGE:
        return hibernate.getCategoryByID(id);
      case Constant.PROPS_XML_STORAGE:
        return XMLUtil.getCategoryByID(id);
    }

    return null;
  }

  public Category getCategories() {
    switch(storage) {
      case Constant.PROPS_DB_STORAGE:
        return hibernate.getCategories();
      case Constant.PROPS_XML_STORAGE:
        return XMLUtil.getCategories();
    }

    return null;
   
  }

  public boolean insertCategory(Category category, int id) {
    switch(storage) {
      case Constant.PROPS_DB_STORAGE:
        return hibernate.insertCategory(category.getCategoryByID(id));
      case Constant.PROPS_XML_STORAGE:
        return XMLUtil.createCategoriesXML(category);
    }

    return false;
  }

  public boolean removeCategory(Category category, int id) {
    switch(storage) {
      case Constant.PROPS_DB_STORAGE:
        return hibernate.removeCategory(category.getCategoryByID(id));
      case Constant.PROPS_XML_STORAGE:
        return XMLUtil.createCategoriesXML(category);
    }

    return false;
  }

  public boolean modifyCategory(Category category, int id) {
    switch(storage) {
      case Constant.PROPS_DB_STORAGE:
        return hibernate.updateCategory(category.getCategoryByID(id));
      case Constant.PROPS_XML_STORAGE:
        return XMLUtil.createCategoriesXML(category);
    }

    return false;
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
        return hibernate.insertActivity(activity);
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

  private String dbDate(DateTime date) {
    return String.format("%d-%02d-%02d", date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
  }
}
