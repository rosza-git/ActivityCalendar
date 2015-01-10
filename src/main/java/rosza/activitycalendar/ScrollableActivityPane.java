/**
 * Scrollable Activity Pane
 * 
 * @author Szalay Roland
 * 
 */
package rosza.activitycalendar;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

public class ScrollableActivityPane extends JLayeredPane implements Scrollable {
  // Date and time variables
  private DateTime  tempCalendar       = new DateTime();
  private final int selectedYear       = ActivityCalendar.selectedDate.getYear();
  private final int selectedMonth      = ActivityCalendar.selectedDate.getMonthOfYear();
  private final int selectedDayOfMonth = ActivityCalendar.selectedDate.getDayOfMonth();
  private int       currentHour        = tempCalendar.getHourOfDay();
  private int       currentMinute      = tempCalendar.getMinuteOfHour();

  private final int currentView;
  private final int cellWidth;
  private int       dayPaneHeight;

  // Scroll unit size
  private int unitIncrement = Constant.FONT_SIZE;

  // Thread variables
  private volatile Thread thread;
  private volatile boolean running = true;
  private static int count = 0;

  // Create scrollable activity pane
  public ScrollableActivityPane(int view, int year, int month, int day) {
    setAutoscrolls(true);
    addMouseMotionListener(mouseMotionListener);
    setBackground(Constant.CELL_BG_COLOR);
    setOpaque(true);

    currentView = view;
    cellWidth = currentView == Constant.DAY_VIEW ? Constant.DAY_CELL_WIDTH : Constant.WEEK_CELL_WIDTH;

    createView();

    ArrayList<Activity> actList;

    tempCalendar = new DateTime(year, month, day, 0, 0);
    tempCalendar = new DateTime(tempCalendar.withDayOfWeek(DateTimeConstants.MONDAY));
    for(int i = 0; i < 7; i++) {
      actList = XMLUtil.getActivityByDate(tempCalendar.getYear(), tempCalendar.getMonthOfYear(), tempCalendar.getDayOfMonth());
      if(actList != null) {
        int j = 0;
        for(Activity a : actList) {
          ActivityLabel al = new ActivityLabel(a);
          int cellHeight = (int)(Constant.CELL_HEIGHT * Activity.time2fraction(a.getDuration()));
          if(cellHeight < Constant.CELL_HEIGHT) {
            cellHeight = Constant.CELL_HEIGHT / 2;
          }
          al.setBounds(day2x(currentView, tempCalendar.getDayOfWeek()), time2y(a.getStartHour(), a.getStartMinute()), cellWidth - Constant.CELL_SPACER, cellHeight);
          al.addMouseMotionListener(mouseMotionListener);
          al.addMouseListener(mouseListener);
          add(al, 5 + j, 1);
          j++;
        }
        tempCalendar = tempCalendar.plusDays(1);
      }
    }

    thread = new TimeLine("SAP_TimeLine" + count++);
    thread.start();
  }

  // Stop the thread
  public void stopRunning() {
    thread.interrupt();
    running = false;
    thread = null;
  }

  // Create and redraw the time line
  class TimeLine extends Thread {
    TimeLine(String n) {
      super(n);
    }

    @Override
    public void run() {
      try {
        while(!thread.isInterrupted() | !running) {
          DateTime d = new DateTime();
          currentHour = d.getHourOfDay();
          currentMinute = d.getMinuteOfHour();
          repaint();
          try {
            Thread.sleep(Constant.REFRESH_TIME);
          }
          catch (InterruptedException ex) {
          }
        }
      }
      catch(NullPointerException e) {
      }
    }
  }

  // Creates a day, week (or a month (not implemented yet)) view
  private void createView() {
    tempCalendar = new DateTime(selectedYear, selectedMonth, selectedDayOfMonth, 0, 0);

    if(currentView == Constant.DAY_VIEW) {
      DayPane daysPane = new DayPane(Constant.DAY_VIEW, tempCalendar.getYear(), tempCalendar.getMonthOfYear(), tempCalendar.getDayOfMonth(), true);

      daysPane.setBounds(0, 0, daysPane.getWidth(), daysPane.getHeight());
      dayPaneHeight = daysPane.getHeight();
      daysPane.addMouseListener(mouseListener);

      add(daysPane);

      setPreferredSize(new Dimension(daysPane.getWidth(), daysPane.getHeight()));
    }
    else if(currentView == Constant.WEEK_VIEW) {
      DayPane daysPane[] = new DayPane[7];

      tempCalendar = new DateTime(tempCalendar.withDayOfWeek(DateTimeConstants.MONDAY));
      for(int i = 0; i < 7; i++) {
        boolean selected = (selectedDayOfMonth == tempCalendar.getDayOfMonth());
        daysPane[i] = new DayPane(Constant.WEEK_VIEW, tempCalendar.getYear(), tempCalendar.getMonthOfYear(), tempCalendar.getDayOfMonth(), selected);
        daysPane[i].setBounds(daysPane[0].getWidth() * i, 0, daysPane[i].getWidth(), daysPane[i].getHeight());
        tempCalendar = tempCalendar.plusDays(1);

        add(daysPane[i]);
      }
      dayPaneHeight = daysPane[0].getHeight();

      setPreferredSize(new Dimension(daysPane[0].getWidth() * 7, daysPane[0].getHeight()));
    }
  }

  // Time and coordinate converters
  private int time2y(int h, int m) {
    return (int)(h * Constant.CELL_HEIGHT + (m / 60.0) * Constant.CELL_HEIGHT);
  }

  private int[] y2time(int y) {
    int[] r = new int[2];
    double z = y / (double)Constant.CELL_HEIGHT;
    r[0] = (int)z;
    r[1] = (int)((z - r[0]) * 60);

    return r;
  }

  // Day and coordinate converter
  private int day2x(int view, int d) {
    d = view == Constant.DAY_VIEW ? 0 : --d;
    int x = cellWidth * d + (cellWidth / 2) - ((cellWidth - Constant.CELL_SPACER * 2) / 2);

    return x;
  }

  // Create a day column
  private class DayPane extends JPanel {
    private DayPane(int view, int y, int m, int d, boolean selected) {
      int dHeight = 0;
      Dimension labelSize = new Dimension(cellWidth, Constant.CELL_HEIGHT);

      setOpaque(selected);
      setBackground(Constant.SELECTED_CELLBG_COLOR);

      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      for(int i = 0; i < 24; i++) {
        JLabel l = new JLabel("");

        l.setName(String.format("day-%d-%d-%d-%d", i, y, m, d));
        l.setHorizontalAlignment(JLabel.CENTER);
        l.setVerticalAlignment(JLabel.CENTER);
        l.setOpaque(false);
        l.setForeground(Constant.CELL_TEXT_COLOR);
        l.setBorder(new MatteBorder(0, 0, 1, 1, Constant.CELL_BORDER_COLOR));
        l.setMinimumSize(labelSize);
        l.setMaximumSize(labelSize);
        l.setPreferredSize(labelSize);
        l.addMouseListener(mouseListener);

        dHeight += l.getPreferredSize().height;

        add(l);
      }
      setSize(view == Constant.DAY_VIEW ? Constant.DAY_CELL_WIDTH : Constant.WEEK_CELL_WIDTH, dHeight);
    }

    @Override
    public void paint(Graphics g) {
      super.paint(g);

      double minutes = currentHour * 60 + currentMinute;
      double maxMinutes = 24 * 60;
      double currentState = getHeight() * minutes / maxMinutes;
      Graphics2D g2d = (Graphics2D)g;
      g2d.setColor(Constant.TIMELINE_COLOR);
      g2d.setStroke(new BasicStroke(2));
      g2d.drawLine(0, (int)currentState, getWidth(), (int)currentState);
    }
  }

  // Mouse listener
  MouseListener mouseListener = new MouseListener() {
    @Override
    public void mouseClicked(MouseEvent e) {
      if(e.getSource() instanceof ActivityLabel) {
        ActivityLabel al = (ActivityLabel)e.getSource();
        if(e.getClickCount() > 1) {
          firePropertyChange(Constant.MODIFY_ACTIVITY, null, al.getActivity());
        }
        else {
        }
      }
      else if(e.getSource() instanceof JLabel) {
        firePropertyChange(Constant.SELECTION_CHANGE, null, ((JLabel)e.getSource()).getName());
      }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
   public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
  };

  // Mouse motion listener
  MouseMotionListener mouseMotionListener = new MouseMotionListener() {
    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }
  };

  // Methods for scrollpane
  @Override
  public Dimension getPreferredSize() {
    return super.getPreferredSize();
  }

  @Override
  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }

  @Override
  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
    //Get the current position.
    int currentPosition = 0;
    if(orientation == SwingConstants.HORIZONTAL) {
      currentPosition = visibleRect.x;
    }
    else {
      currentPosition = visibleRect.y;
    }

    //Return the number of pixels between currentPosition
    //and the nearest tick mark in the indicated direction.
    if(direction < 0) {
      int newPosition = currentPosition - (currentPosition / unitIncrement) * unitIncrement;
      return (newPosition == 0) ? unitIncrement : newPosition;
    }
    else {
      return ((currentPosition / unitIncrement) + 1) * unitIncrement - currentPosition;
    }
  }

  @Override
  public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
    if(orientation == SwingConstants.HORIZONTAL) {
      return visibleRect.width - unitIncrement;
    }
    else {
      return visibleRect.height - unitIncrement;
    }
  }

  @Override
  public boolean getScrollableTracksViewportWidth() {
    return false;
  }

  @Override
  public boolean getScrollableTracksViewportHeight() {
    return false;
  }

  public void setUnitIncrement(int u) {
    unitIncrement = u;
  }
}
