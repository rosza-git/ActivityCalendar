/**
 * Hibernate
 * 
 * @author Szalay Roland
 * 
 */
package rosza.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.joda.time.DateTime;
import rosza.activitycalendar.Activity;
import rosza.activitycalendar.XMLUtil;

public class Hibernate {
  private static final String ACTIVITY_QUERY_BASED_ON_ID         = "from Activities a where a.id = ";
  private static final String ACTIVITY_QUERY_BASED_ON_START_DATE = "from Activities a where a.start like '";
  private static final String ACTIVITY_QUERY_BASED_ON_END_DATE   = "from Activities a where a.end like '";
  private static final String ACTIVITY_QUERY_BASED_ON_CATEGORY   = "from Activities a where a.category = ";

  private ArrayList<Activity> activityList;
  private final Session session;

  public Hibernate() throws HibernateException, IllegalArgumentException, NullPointerException {
    session = HibernateUtil.getSessionFactory().openSession();
  }

  public Activity getActivityByID(int id) {
    runQueryBasedOnActivityID(id);

    if(activityList.isEmpty()) {
      return null;
    }
    else {
      return activityList.get(0);
    }
  }

  public ArrayList<Activity> getActivityByStartDate(String dbDate) {
    runQueryBasedOnActivityStartDate(dbDate);

    return activityList;
  }

  public ArrayList<Activity> getActivityByEndDate(String dbDate) {
    runQueryBasedOnActivityEndDate(dbDate);

    return activityList;
  }

  public ArrayList<Activity> getActivityByCategory(int category) {
    runQueryBasedOnActivityCategory(category);

    return activityList;
  }

  public boolean addActivity(Activity activity) {
    return activityInsert(activity);
  }

  public boolean updateActivity(Activity activity) {
    return activityUpdate(activity);
  }

  public boolean removeActivity(Activity activity) {
    return activityRemove(activity);
  }

  private void runQueryBasedOnActivityID(int id) {
    activityExecuteHQLQuery(ACTIVITY_QUERY_BASED_ON_ID + id);
  }

  private void runQueryBasedOnActivityStartDate(String dbDate) {
    activityExecuteHQLQuery(ACTIVITY_QUERY_BASED_ON_START_DATE + dbDate + "%'");
  }
  
  private void runQueryBasedOnActivityEndDate(String dbDate) {
    activityExecuteHQLQuery(ACTIVITY_QUERY_BASED_ON_END_DATE + dbDate + "%'");
  }

  private void runQueryBasedOnActivityCategory(int category) {
    activityExecuteHQLQuery(ACTIVITY_QUERY_BASED_ON_CATEGORY + category + "%'");
  }

  private void activityExecuteHQLQuery(String hql) {
    try {
      session.beginTransaction();
      Query q = session.createQuery(hql);
      List resultList = q.list();
      createList(resultList);
      session.getTransaction().commit();
    }
    catch(NullPointerException ne) {
      JOptionPane.showMessageDialog(null, "Cannot connect to server! Missing parameter(s)!", "Hibernate error", JOptionPane.ERROR_MESSAGE);
    }
    catch(IllegalArgumentException | ExceptionInInitializerError | HibernateException he) {
      JOptionPane.showMessageDialog(null, he.toString(), "Hibernate error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private boolean activityInsert(Activity activity) {
    try {
      session.beginTransaction();

      //Add new Activities object
      Activities activities = new Activities();
      activities.setComment(activity.getComment());
      activities.setCategory(activity.getCategory().getID());
      activities.setStart(new Date(activity.getStartDate().getMillis()));
      activities.setEnd(new Date(activity.getEndDate().getMillis()));

      //Save the activity in database
      session.save(activities);

      //Commit the transaction
      session.getTransaction().commit();

      return true;
    }
    catch(NullPointerException ne) {
      JOptionPane.showMessageDialog(null, "Cannot connect to server! Missing parameter(s)!", "Hibernate error", JOptionPane.ERROR_MESSAGE);
    }
    catch(IllegalArgumentException | ExceptionInInitializerError | HibernateException he) {
      JOptionPane.showMessageDialog(null, he.toString(), "Hibernate error", JOptionPane.ERROR_MESSAGE);
    }

    return false;
  }

  private boolean activityUpdate(Activity activity) {
    try {
      session.beginTransaction();

      // Create modified Activities object
      Activities activities = new Activities();
      activities.setId(activity.getID());
      activities.setComment(activity.getComment());
      activities.setCategory(activity.getCategory().getID());
      activities.setStart(new Date(activity.getStartDate().getMillis()));
      activities.setEnd(new Date(activity.getEndDate().getMillis()));

      // Update activity in database
      session.update(activities);

      //Commit the transaction
      session.getTransaction().commit();

      return true;
    }
    catch(NullPointerException ne) {
      JOptionPane.showMessageDialog(null, "Cannot connect to server! Missing parameter(s)!", "Hibernate error", JOptionPane.ERROR_MESSAGE);
    }
    catch(IllegalArgumentException | ExceptionInInitializerError | HibernateException he) {
      JOptionPane.showMessageDialog(null, he.toString(), "Hibernate error", JOptionPane.ERROR_MESSAGE);
    }

    return false;
  }

  private boolean activityRemove(Activity activity) {
    try {
      session.beginTransaction();

      // Create Activities object
      Activities activities = new Activities();
      activities.setId(activity.getID());
      activities.setComment(activity.getComment());
      activities.setCategory(activity.getCategory().getID());
      activities.setStart(new Date(activity.getStartDate().getMillis()));
      activities.setEnd(new Date(activity.getEndDate().getMillis()));

      // Update activity in database
      session.delete(activities);

      //Commit the transaction
      session.getTransaction().commit();

      return true;
    }
    catch(NullPointerException ne) {
      JOptionPane.showMessageDialog(null, "Cannot connect to server! Missing parameter(s)!", "Hibernate error", JOptionPane.ERROR_MESSAGE);
    }
    catch(IllegalArgumentException | ExceptionInInitializerError | HibernateException he) {
      JOptionPane.showMessageDialog(null, he.toString(), "Hibernate error", JOptionPane.ERROR_MESSAGE);
    }

    return false;
  }

  private void createList(List resultList) {
    activityList = new ArrayList<>();
    for(Object o : resultList) {
      Activities activity = (Activities)o;
      Activity a = new Activity(activity.getId(), activity.getComment(), XMLUtil.getCategoryByID(activity.getCategory()), new DateTime(activity.getStart()), new DateTime(activity.getEnd()));
      activityList.add(a);
    }
  }
}
