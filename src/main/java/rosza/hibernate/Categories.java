/**
 * Categories entity
 * 
 * @author Szalay Roland
 * 
 * Categories generated by hbm2java
 * Generated 2015.01.14. 14:17:25 by Hibernate Tools 4.3.1
 * 
 */
package rosza.hibernate;

import java.io.Serializable;

public class Categories  implements Serializable {
  private int id;
  private int parentId;
  private String name;
  private String color;
  private Boolean predefined;

  public Categories() {
  }

  public Categories(int parentId, String name, String color, Boolean predefined) {
    this.parentId = parentId;
    this.name = name;
    this.color = color;
    this.predefined = predefined;
  }

  public Categories(int id, int parentId, String name, String color, Boolean predefined) {
    this(parentId, name, color, predefined);
    this.id = id;
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getParentId() {
    return this.parentId;
  }

  public void setParentId(int parentId) {
    this.parentId = parentId;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getColor() {
    return this.color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public Boolean getPredefined() {
    return this.predefined;
  }

  public void setPredefined(Boolean predefined) {
    this.predefined = predefined;
  }
}