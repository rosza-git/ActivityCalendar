/**
 * Category
 * 
 * @author Szalay Roland
 * 
 */
package rosza.activitycalendar;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Objects;

public class Category {
  private Category                  parent;
  private int                       parentID;
  private final ArrayList<Category> sub;
  private final int                 ID;
  private String                    name;
  private Color                     color;
  private final boolean             predefined;
  private boolean                   hasSubcat;

  /**
   * Create new Category instance
   * 
   * @param id ID of this category
   * @param name category name
   * @param color color
   * @param predefined predefined category or not (true or false)
   */
  public Category(int id, String name, Color color, boolean predefined) {
    this.ID = id;
    this.name = name;
    this.color = color;
    this.predefined = predefined;
    this.sub = new ArrayList<>();
  }

  /**
   * Create new Category instance
   * 
   * @param id ID of this category
   * @param parentId id of parent category
   * @param name category name
   * @param color color
   * @param predefined predefined category or not (true or false)
   */
  public Category(int id, int parentId, String name, Color color, boolean predefined) {
    this.ID = id;
    this.parentID = parentId;
    this.name = name;
    this.color = color;
    this.predefined = predefined;
    this.sub = new ArrayList<>();
  }

  /**
   * Link together all categories.
   *
   * @param cat the category
   * @param subs the subcategories
   */
  public static void linkCategories(Category cat, Category[] subs) {
    for(Category sub : subs) {
      cat.sub.add(sub);
      cat.hasSubcat = true;
      sub.parent = cat;
    }
  }

  // Getter methods
  public int getID() {
    return this.ID;
  }

  public int getParentID() {
    return this.parentID;
  }

  public String getName() {
    return this.name;
  }

  public Color getColor() {
    return this.color;
  }

  public Category getParentCategory() {
    return this.parent;
  }

  public ArrayList<Category> getSubCategories() {
    return this.sub;
  }

  public Category getCategoryByID(int id) {
    if(this.ID == id) {
      return this;
    }
    else {
      return searchCategories(id, this);
    }
  }

  public int getSubCount() {
    return sub.size();
  }

  public Category getSubAt(int i) {
    return (Category)sub.get(i);
  }

  public int getIndexOfSub(Category subCat) {
    return sub.indexOf(subCat);
  }

  public static int getLastID(Category c, int id) {
    for(int i = 0, size = c.getSubCount(); i < size; i++) {
      int cid = (c.getSubAt(i)).getID();
      id = getMax(id, getLastID(c.getSubAt(i), cid));
    }

    return id;
  }

  public boolean isPredefined() {
    return this.predefined;
  }

  public boolean hasSubCategory() {
    return this.hasSubcat;
  }

  // Converter method
  public static String color2hex(Color c) {
    return String.format("#%02x%02x%02x", c.getRed(),c.getGreen(), c.getBlue());
  }

  // Search all Category for th given ID
  private Category searchCategories(int id, Category c) {
    for(int i = 0, size = c.getSubCount(); i < size; i++) {
      if((c.getSubAt(i)).getID() == id) {
        return c.getSubAt(i);
      }
      Category temp = searchCategories(id, c.getSubAt(i));
      if(temp != null) {
        return temp;
      }
    }

    return c.getID() == id ? c : null;
  }

  // Category manipulator methods
  public static void addCategory(Category child, Category parent) {
    Category[] c = {child};
    Category.linkCategories(parent, c);
  }

  public void removeCategory(Category category) {
    Category p = category.getParentCategory();
    int id = p.getIndexOfSub(category);
    p.sub.remove(id);
    if(p.getSubCount() == 0) {
      p.hasSubcat = false;
    }
  }

  public static void modifiyCategory(Category c, String newName, Color newColor) {
    c.name = newName;
    c.color = newColor;
  }

  // Get the greater number between two given arguments
  private static int getMax(int currentID, int newID) {
    if(currentID > newID) {
      return currentID;
    }

    return newID;
  }

  // Create built-in categories
  public static Category getDefaultCategories() {
    Category root = new Category(0, "categories", Color.black, true);
    Category c1 = new Category(1, "personal", Color.red, true);
    Category c2 = new Category(2, "business", Color.blue, true);
    Category c101 = new  Category(3, "birthday", Color.yellow, false);
    Category c102 = new  Category(4, "anniversary", Color.orange, false);
    Category c201 = new  Category(5, "boss", Color.magenta, false);
    Category c202 = new  Category(6, "employee", Color.pink, false);
    Category c2011 = new  Category(7, "meeting", Color.green, false);
    Category c2021 = new  Category(8, "testing", Color.cyan, false);
    Category c2022 = new  Category(9, "coding", Color.gray, false);

    Category.linkCategories(root, new Category[] {c1, c2});
    Category.linkCategories(c1, new Category[] {c101, c102});
    Category.linkCategories(c2, new Category[] {c201, c202});
    Category.linkCategories(c201, new Category[] {c2011});
    Category.linkCategories(c202, new Category[] {c2021, c2022});

    return root;
  }

  // toString method
  @Override
  public String toString() {
    return this.name;
  }

  // equals method
  @Override
  public boolean equals(Object obj) {
    if(obj == this) {
      return true;
    }
    if(obj instanceof Category) {
      boolean result = true;
      Category cat = (Category)obj;
      result &= this.ID == cat.getID();
      result &= this.name.equals(cat.getName());
      result &= this.parentID == cat.getParentID();
      result &= this.color.equals(cat.getColor());
      result &= this.predefined == cat.isPredefined();

      return result;
    }

    return false;
  }

  // hashCode method
  @Override
  public int hashCode() {
    int hash = 7;
    hash = 83 * hash + this.ID;
    hash = 83 * hash + Objects.hashCode(this.name);
    hash = 83 * hash + this.parentID;
    hash = 83 * hash + Objects.hashCode(this.color);
    hash = 83 * hash + (this.predefined ? 1 : 0);

    return hash;
  }
}
