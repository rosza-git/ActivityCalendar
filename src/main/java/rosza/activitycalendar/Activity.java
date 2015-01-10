/**
 * Activity
 * 
 * @author Szalay Roland
 * 
 */
package rosza.activitycalendar;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

public class Activity {
  private int      id;
  private String   comment;
  private Category category;
  private DateTime start;
  private DateTime end;
  private int      duration;
  private Color    color;
  

  // Create new Activity instance
  /**
   * Create new Activity instance with the given data.
   * 
   * @param comment the comment of the activity
   * @param category the category of the activity
   * @param sdate the start date and time of the activity
   * @param edate the end date and time of the activity
   */
  public Activity(String comment, Category category, DateTime sdate, DateTime edate) {
    this(0, comment, category, sdate, edate);
  }

  /**
   * Create new Activity instance with the given data.
   * 
   * @param id the id of the activity (necessary if modifying)
   * @param comment the comment of the activity
   * @param category the category of the activity
   * @param sdate the start date and time of the activity
   * @param edate the end date and time of the activity
   */
  public Activity(int id, String comment, Category category, DateTime sdate, DateTime edate) {
    this.id = id;
    this.comment = comment;
    this.category = category;
    this.start = new DateTime(sdate);
    this.end = new DateTime(edate);
    this.duration = durationInMinutes().getMinutes();
    try {
      this.color = this.category.getColor();
    }
    catch(NullPointerException e) {
      this.color = Constant.DEFAULT_ACTIVITY_COLOR;
    }
  }

  // Get all available field
  public ArrayList<String[]> getFields() {
    ArrayList<String[]> fields = new ArrayList<>();
    fields.add(new String[]{Constant.XML_ID, Integer.toString(this.id)});
    fields.add(new String[]{Constant.XML_COMMENT, this.comment});
    fields.add(new String[]{Constant.XML_CATEGORY, Integer.toString(this.category.getID())});
    fields.add(new String[]{Constant.XML_START, this.start.toString()});
    fields.add(new String[]{Constant.XML_END, this.end.toString()});

    return fields;
  }

  // Getter methods
  public int getID() {
    return this.id;
  }

  public String getComment() {
    return this.comment;
  }

  public Category getCategory() {
    return this.category;
  }

  public Color getColor() {
    return this.color;
  }

  public DateTime getStartDate() {
    return this.start;
  }

  public DateTime getEndDate() {
    return this.end;
  }

  public int getStartYear() {
    return this.start.getYear();
  }

  public int getStartMonth() {
    return this.start.getMonthOfYear();
  }

  public int getStartDay() {
    return this.start.getDayOfMonth();
  }

  public int getEndYear() {
    return this.end.getYear();
  }

  public int getEndMonth() {
    return this.end.getMonthOfYear();
  }

  public int getEndDay() {
    return this.end.getDayOfMonth();
  }

  public int getStartHour() {
    return this.start.getHourOfDay();
  }

  public int getStartMinute() {
    return this.start.getMinuteOfHour();
  }

  public int getEndHour() {
    return this.end.getHourOfDay();
  }

  public int getEndMinute() {
    return this.end.getMinuteOfHour();
  }

  public int getDuration() {
    return this.duration;
  }

  public String getDurationInHM() {
    int h = this.duration / 60;
    long m = this.duration % 60; 

    return String.format("%02d:%02d", h, m);
  }

  public static String getDurationInHM(int duration) {
    int h = duration / 60;
    long m = duration % 60; 

    return String.format("%02d:%02d", h, m);
  }

  // Setter methods
  public void setStartHour(int h) {
    start = start.withHourOfDay(h);
    end = start.plusMinutes(duration);
  }

  public void setStartMinute(int m) {
    start = start.withMinuteOfHour(m);
    end = start.plusMinutes(duration);
  }

  // Converter methods
  private Minutes durationInMinutes() {
    return Minutes.minutesBetween(start, end);
  }

  public static double time2fraction(int duration) {
    return round(duration /60.0);
  }

  public static double time2fraction(int hour, int minutes) {
    return round(time2fraction(hour * 60 + minutes));
  }

  private static double round(double value) {
    return round(value, 2);
  }

  private static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);

    return bd.doubleValue();
  }

  // toString method
  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append("id         :").append(this.id);
    s.append("\n");
    s.append("comment    :").append(this.comment);
    s.append("\n");
    s.append("category   :").append(this.category);
    s.append("\n");
    s.append("start date :").append(this.start);
    s.append("\n");
    s.append("end date   :").append(this.end);

    return s.toString();
  }
}
