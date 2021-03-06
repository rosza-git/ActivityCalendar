/**
 * Activities entity
 * 
 * @author Szalay Roland
 * 
 * Activities generated by hbm2java
 * Generated 2015.01.11. 18:56:20 by Hibernate Tools 4.3.1
 * 
 */
package rosza.hibernate;

import java.io.Serializable;
import java.util.Date;

public class Activities  implements Serializable {
  private int id;
  private String comment;
  private int category;
  private Date start;
  private Date end;

  public Activities() {
  }


  public Activities(int id) {
    this.id = id;
  }

  public Activities(int id, String comment, int category, Date start, Date end) {
    this.id = id;
    this.comment = comment;
    this.category = category;
    this.start = start;
    this.end = end;
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getComment() {
    return this.comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public int getCategory() {
    return this.category;
  }

  public void setCategory(int category) {
  this.category = category;
  }

  public Date getStart() {
    return this.start;
  }

  public void setStart(Date start) {
  this.start = start;
  }

  public Date getEnd() {
    return this.end;
  }

  public void setEnd(Date end) {
    this.end = end;
  }
}
