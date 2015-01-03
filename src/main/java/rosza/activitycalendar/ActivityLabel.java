/**
 * Activity Label
 * 
 * @author Szalay Roland
 * 
 */
package rosza.activitycalendar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class ActivityLabel extends JPanel {
  private final Activity activity;
  private int w;
  private int h;
  JLabel timeLabel;
  JLabel commentLabel;
  JLabel categoryLabel;

  public ActivityLabel(Activity a) {
    activity = a;

    initUIComponents();
  }

  private void initUIComponents() {
    setFont(getFont().deriveFont(Font.BOLD));
    setCursor(new Cursor(Cursor.HAND_CURSOR));
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(new EmptyBorder(1, 5, 1, 5));
    setForeground(Color.white);
    setAlignmentX(JLabel.LEFT);
    setToolTipText(creatToolTip());

    DateTimeFormatter timeFormat = DateTimeFormat.forPattern("HH:mm");
    timeLabel = new JLabel(timeFormat.print(activity.getStartDate()) + " - " + timeFormat.print(activity.getEndDate()));
    add(timeLabel);

    commentLabel = new JLabel(activity.getComment());
    add(commentLabel);
  }

  public Activity getActivity() {
    return activity;
  }

  public String getComment() {
    return activity.getComment();
  }

  private String creatToolTip() {
    StringBuilder toolTip = new StringBuilder("<html><body>");
    toolTip.append("<b>comment:</b> ").append(activity.getComment()).append("<br>");
    DateTimeFormatter start = DateTimeFormat.forPattern("yyyy.MM.dd. HH:mm");
    DateTimeFormatter end = DateTimeFormat.forPattern("HH:mm");
    toolTip.append("<b>date:</b> ").append(start.print(activity.getStartDate())).append(" - ").append(end.print(activity.getEndDate())).append("<br>");
    toolTip.append("<b>category:</b> ").append(activity.getCategory().getName());
    toolTip.append("</body></html>");

    return toolTip.toString();
  }

  public void setStartTime(int h, int m) {
    setStartHour(h);
    setStartMinute(m);
    //setText(getHeader());
  }

  public void setStartHour(int h) {
    activity.setStartHour(h);
  }

  public void setStartMinute(int m) {
    activity.setStartMinute(m);
  }

  @Override
  public Dimension getPreferredSize() {
    return super.getPreferredSize();
  }

  @Override
  public void paintComponent(Graphics g) {
    w = getWidth();
    h = getHeight();
    int roundness = 15;
    Graphics2D g2d = (Graphics2D) g;
    int alpha = 150;
    Color baseColor = new Color(activity.getColor().getRed(), activity.getColor().getGreen(), activity.getColor().getBlue(), alpha);
    Color darkerColor = baseColor.darker();
    GradientPaint p = new GradientPaint(0, 0, baseColor, 0, h, darkerColor);

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setPaint(p);
    g2d.fillRoundRect(1, 1, w - 4, h - 3, roundness, roundness);
    g2d.setPaint(activity.getColor());
    g2d.setStroke(new BasicStroke(2));
    g2d.drawRoundRect(0, 0, w - 4, h - 3, roundness, roundness);
    //super.paintComponent(g);
    //g2d.dispose();
  }
}

/*
public class ActivityLabel extends JLabel {
  private final Activity activity;
  private String comment;
  //private Category category;
  private String category;
  private String toolTip;
  private int year;
  private int month;
  private int day;
  private int startHour;
  private int startMinute;
  private int endHour;
  private int endMinute;
  private Color color;

  public ActivityLabel(Activity a) {
    setCursor(new Cursor(Cursor.HAND_CURSOR));
    activity = a;
    setBorder(new EmptyBorder(2, 5, 2, 5));
    setText(getHeader());
    setForeground(Color.white);
    setVerticalAlignment(JLabel.TOP);
    setFont(getFont().deriveFont(Font.BOLD));
    setAlignmentX(JLabel.LEFT);
    setHorizontalAlignment(JLabel.LEFT);
    setToolTipText(getToolTip());
  }

  public Activity getActivity() {
    return activity;
  }

  public String getComment() {
    return comment;
  }

  public String getHeader() {
    return String.format("<html>s: %02d:%02d<br>e: %02d:%02d<br>%s<br>%d.%02d.%02d.</html>", activity.getStartHour(), activity.getStartMinute(), activity.getEndHour(), activity.getEndMinute(), activity.getComment(), activity.getStartYear(), activity.getStartMonth() + 1, activity.getStartDay());
  }

  private String getToolTip() {
    return String.format("<html><b>comment:</b> %s<br><b>date:</b> %s - %s<br>%s</html>", activity.getComment(), activity.getStartDate(), activity.getEndDate(), activity.getCategory());
  }

  public void setStartTime(int h, int m) {
    setStartHour(h);
    setStartMinute(m);
    setText(getHeader());
  }

  public void setStartHour(int h) {
    activity.setStartHour(h);
  }

  public void setStartMinute(int m) {
    activity.setStartMinute(m);
  }

  @Override
  public void paintComponent(Graphics g) {
    int w = getWidth();
    int h = getHeight();
    int roundness = 15;
    Graphics2D g2d = (Graphics2D) g;
    int alpha = 150;
    Color baseColor = new Color(activity.getColor().getRed(), activity.getColor().getGreen(), activity.getColor().getBlue(), alpha);
    Color darkerColor = baseColor.darker();
    GradientPaint p = new GradientPaint(0, 0, baseColor, 0, h, darkerColor);

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setPaint(p);
    g2d.fillRoundRect(1, 1, w - 4, h - 3, roundness, roundness);
    g2d.setPaint(activity.getColor());
    g2d.setStroke(new BasicStroke(2));
    g2d.drawRoundRect(0, 0, w - 4, h - 3, roundness, roundness);
    super.paintComponent(g);
    g2d.dispose();
  }
}
*/