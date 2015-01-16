/**
 * Hibernate
 * 
 * @author Szalay Roland
 * 
 */
package rosza.hibernate;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.TransactionException;
import org.hibernate.exception.SQLGrammarException;
import org.joda.time.DateTime;
import rosza.activitycalendar.Activity;
import rosza.activitycalendar.Category;

public class Hibernate {
  // Queries for Activities
  private static final String ACTIVITY_QUERY_BASED_ON_ID         = "FROM Activities a WHERE a.id = ";
  private static final String ACTIVITY_QUERY_BASED_ON_START_DATE = "FROM Activities a WHERE a.start like '";
  private static final String ACTIVITY_QUERY_BASED_ON_END_DATE   = "FROM Activities a WHERE a.end like '";
  private static final String ACTIVITY_QUERY_BASED_ON_CATEGORY   = "FROM Activities a WHERE a.category = ";

  // Queries for CategoriesEntity
  //private static final String CATEGORY_QUERY_BASED_ON_ANCESTOR = "SELECT c FROM Categories c JOIN Paths p ON (c.id = p.descendant) WHERE p.ancestor = ";
  private static final String CATEGORY_QUERY_ROOT              = "FROM Categories c WHERE c.parentId = 0 AND c.id = 0";
  private static final String CATEGORY_QUERY_BASED_ON_ANCESTOR = "FROM Categories c WHERE c.id <> c.parentId AND c.parentId = ";
  private static final String CATEGORY_QUERY_BASED_ON_ID       = "FROM Categories c WHERE c.id = ";

  private final Session session;

  public Hibernate() {
    Logger log = Logger.getLogger("org.hibernate");
    log.setLevel(Level.OFF);

    try {
      session = HibernateUtil.getSessionFactory().openSession();
    }
    catch(NullPointerException e) {
      throw new NullPointerException(e.getMessage() + " -> Hibernate");
    }
  }

  private List executeHQL(String hql) {
    try {
      session.beginTransaction();
      Query q = session.createQuery(hql);
      List resultList = q.list();
      session.getTransaction().commit();

      return resultList;
    }
    catch(NullPointerException ne) {
      JOptionPane.showMessageDialog(null, "Cannot connect to server! Missing parameter(s)!", "activityHQL", JOptionPane.ERROR_MESSAGE);
    }
    catch(IllegalArgumentException e) {
      throw new IllegalArgumentException(e);
    }
    catch(ExceptionInInitializerError e) {
      throw new ExceptionInInitializerError();
    }
    catch(SQLGrammarException e) {
      throw new SQLGrammarException("executeHQL\n" + hql, e.getSQLException());
    }
    catch(HibernateException e) {
      throw new HibernateException(e);
    }
    

    return null;
  }

  // Methods for getting, inserting, modifying and removing Categories
  public Category getCategoryByID(int id) {
    List category = executeHQL(CATEGORY_QUERY_BASED_ON_ID + id);
    if(category.isEmpty()) {
      return null;
    }
    else {
      Category c = entity2Category(category.get(0));
      return c;
    }
  }

  public Category getCategories() {
    try {
      // get the root 
      Category root = entity2Category(executeHQL(CATEGORY_QUERY_ROOT).get(0));
      // get sub-categories
      Category[] subs = getCategoryDescendants(root.getID());
      // link sub-categories to root
      Category.linkCategories(root, subs);

      return root;
    }
    catch(IndexOutOfBoundsException e) {
      // if no root, then no categories in the database, so populate it with the default categories
      insertCategory(Category.getDefaultCategories());
      return getCategories();
    }
    catch(Exception e) {
      JOptionPane.showMessageDialog(null, "Unable to get categories from database!\n" + e.getCause() + "\nUsing built-in categories!", "Hibernate error", JOptionPane.ERROR_MESSAGE);
      return Category.getDefaultCategories();
    }
  }

  public Category[] getCategoryDescendants(int id) {
    ArrayList<Category> alc = new ArrayList<>();

    List<Categories> subs = executeHQL(CATEGORY_QUERY_BASED_ON_ANCESTOR + id);
    for(Object sub : subs) {
      Categories c = (Categories)sub;
      Category tempRoot = entity2Category(c);
      Category[] tempCat = getCategoryDescendants(c.getId());
      Category.linkCategories(tempRoot, tempCat);
      alc.add(tempRoot);
    }

    return alc.toArray(new Category[alc.size()]);
  }

  public boolean insertCategory(Category category) {
    try {
      Categories categories = category2Entity(category);

      session.beginTransaction();

      //Save category in database
      session.save(categories);

      //Commit the transaction
      session.getTransaction().commit();

      if(category.hasSubCategory()) {
        for(int i = 0, s = category.getSubCount(); i < s; i++) {
          insertCategory(category.getSubAt(i));
        }
      }

      return true;
    }
    catch(Exception e) {
      JOptionPane.showMessageDialog(null, e.getCause(), "Hibernate error", JOptionPane.ERROR_MESSAGE);
    }

    return false;
  }

  public boolean removeCategory(Category category) {
    try {
      if(category.hasSubCategory()) {
        for(int i = 0, s = category.getSubCount(); i < s; i++) {
          removeCategory(category.getSubAt(i));
        }
      }

      session.beginTransaction();

      // Remove Category from database
      session.delete(category2Entity(category));

      //Commit the transaction
      session.getTransaction().commit();

      return true;
    }
    catch(TransactionException e) {
      //System.out.println(e.getMessage() + " " + e.getCause());
    }
    catch(Exception e) {
      JOptionPane.showMessageDialog(null, e.getCause(), "Hibernate error", JOptionPane.ERROR_MESSAGE);
    }

    return false;
  }

  public boolean updateCategory(Category category) {
    try {
      session.beginTransaction();
      // Remove Category from database
      session.update(category2Entity(category));

      //Commit the transaction
      session.getTransaction().commit();

      return true;
    }
    catch(Exception e) {
      JOptionPane.showMessageDialog(null, e.getCause(), "Hibernate error", JOptionPane.ERROR_MESSAGE);
    }

    return false;
  }

  // Methods for getting, inserting, modifying and removing Activities
  public Activity getActivityByID(int id) {
    ArrayList<Activity> activityList = createActivityList(executeHQL(ACTIVITY_QUERY_BASED_ON_ID + id));

    if(activityList.isEmpty()) {
      return null;
    }
    else {
      return activityList.get(0);
    }
  }

  public ArrayList<Activity> getActivityByStartDate(String dbDate) {
    ArrayList<Activity> activityList = createActivityList(executeHQL(ACTIVITY_QUERY_BASED_ON_START_DATE + dbDate + "%'"));
    return activityList;
  }

  public ArrayList<Activity> getActivityByEndDate(String dbDate) {
    ArrayList<Activity> activityList = createActivityList(executeHQL(ACTIVITY_QUERY_BASED_ON_END_DATE + dbDate + "%'"));
    return activityList;
  }

  public ArrayList<Activity> getActivityByCategory(int category) {
    ArrayList<Activity> activityList = createActivityList(executeHQL(ACTIVITY_QUERY_BASED_ON_CATEGORY + category + "%'"));
    return activityList;
  }

  public boolean insertActivity(Activity activity) {
    try {
      session.beginTransaction();

      //Save the activity in database
      session.save(activity2Entity(activity));

      //Commit the transaction
      session.getTransaction().commit();

      return true;
    }
    catch(Exception e) {
      JOptionPane.showMessageDialog(null, e.getCause(), "Hibernate error", JOptionPane.ERROR_MESSAGE);
    }

    return false;
  }

  public boolean updateActivity(Activity activity) {
    try {
      session.beginTransaction();

      // Update activity in database
      session.update(activity2Entity(activity));

      //Commit the transaction
      session.getTransaction().commit();

      return true;
    }
    catch(Exception e) {
      JOptionPane.showMessageDialog(null, e.getCause(), "Hibernate error", JOptionPane.ERROR_MESSAGE);
    }

    return false;
  }

  public boolean removeActivity(Activity activity) {
    try {
      session.beginTransaction();

      // Update activity in database
      session.delete(activity2Entity(activity));

      //Commit the transaction
      session.getTransaction().commit();

      return true;
    }
    catch(Exception e) {
      JOptionPane.showMessageDialog(null, e.getCause(), "Hibernate error", JOptionPane.ERROR_MESSAGE);
    }

    return false;
  }

  private ArrayList<Activity> createActivityList(List resultList) {
    ArrayList<Activity> activityList = new ArrayList<>();
    for(Object o : resultList) {
      Activities activities = (Activities)o;
      Activity a = entity2Activity(activities);
      activityList.add(a);
    }

    return activityList;
  }

  // Activity & Activities converter methods
  private Activity entity2Activity(Activities a) {
    return new Activity(a.getId(), a.getComment(), getCategoryByID(a.getCategory()), new DateTime(a.getStart()), new DateTime(a.getEnd()));
  }

  private Activities activity2Entity(Activity a) {
    return new Activities(a.getID(), a.getComment(), a.getCategory().getID(), new Date(a.getStartDate().getMillis()), new Date(a.getEndDate().getMillis()));
  }

  // Category & Categories converter methods
  private Category entity2Category(Object entity) {
    Categories c = (Categories)entity;

    return new Category(c.getId(), c.getName(), Color.decode(c.getColor()), c.getPredefined());
  }

  private Categories category2Entity(Category c) {
    try {
      return new Categories(c.getID(), c.getParentCategory().getID(), c.getName(), Category.color2hex(c.getColor()), c.isPredefined());
    }
    catch(NullPointerException e) {
      return new Categories(c.getID(), 0, c.getName(), Category.color2hex(c.getColor()), c.isPredefined());
    }
  }
}
