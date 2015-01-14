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
import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

public class ActivityLabel extends JPanel {
  private final Activity activity;
  private int w;
  private int h;
  private JLabel timeLabel;
  private JLabel commentLabel;

  // Create new Activity label on Activity panel
  public ActivityLabel(Activity a) {
    activity = a;

    createUI();
  }

  // Create UI
  @SuppressWarnings("unchecked")
  private void createUI() {
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

  // Getter methods
  public Activity getActivity() {
    return activity;
  }

  public String getComment() {
    return activity.getComment();
  }

  @Override
  public Dimension getPreferredSize() {
    return super.getPreferredSize();
  }

  // Setter methods
  public void setStartTime(int h, int m) {
    setStartHour(h);
    setStartMinute(m);
  }

  public void setStartHour(int h) {
    activity.setStartHour(h);
  }

  public void setStartMinute(int m) {
    activity.setStartMinute(m);
  }

  // Create tooltip
  private String creatToolTip() {
    StringBuilder toolTip = new StringBuilder("<html><body>");
    toolTip.append("<b>comment:</b> ").append(escapeHtml(activity.getComment())).append("<br>");
    DateTimeFormatter start = DateTimeFormat.forPattern("yyyy.MM.dd. HH:mm");
    DateTimeFormatter end = DateTimeFormat.forPattern("HH:mm");
    toolTip.append("<b>date:</b> ").append(start.print(activity.getStartDate())).append(" - ").append(end.print(activity.getEndDate())).append("<br>");
    try {
      toolTip.append("<b>category:</b> ").append(escapeHtml(activity.getCategory().getName()));
    }
    catch(NullPointerException e) {
      toolTip.append("-");
    }
    toolTip.append("</body></html>");

    return toolTip.toString();
  }

  // Paint the label
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
  }
}
