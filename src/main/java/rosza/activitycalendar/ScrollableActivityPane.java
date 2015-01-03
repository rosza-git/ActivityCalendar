/**
 * Scrollable Activity Pane
 * 
 * @author Szalay Roland
 * 
 */
package rosza.activitycalendar;

//<editor-fold defaultstate="collapsed" desc=" Import ">
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import org.joda.time.*;
//</editor-fold>

public class ScrollableActivityPane extends JLayeredPane implements Scrollable, MouseMotionListener {
  private final int currentView;
  private final int cellWidth;

  private DateTime  tempCalendar       = new DateTime();
  private final int selectedYear       = ActivityCalendar.selectedDate.getYear();
  private final int selectedMonth      = ActivityCalendar.selectedDate.getMonthOfYear();
  private final int selectedDayOfMonth = ActivityCalendar.selectedDate.getDayOfMonth();
  private int       currentHourOfDay;
  private int       currentMinuteOfHour;

  private int dayPaneHeight;

  private int unitIncrement = 1;

	//private MouseEvent pressed;
	//private Point      location;

  public ScrollableActivityPane(int view, int year, int month, int day) {
    setAutoscrolls(true);               //enable synthetic drag events
    addMouseMotionListener(this);       //handle mouse drags
    setBackground(Constant.CELL_BG_COLOR);
    setOpaque(true);

    currentView = view;
    cellWidth = currentView == Constant.DAY_VIEW ? Constant.DAY_CELL_WIDTH : Constant.WEEK_CELL_WIDTH;

    createDaysPane();

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
          al.addMouseMotionListener(this);
          al.addMouseListener(mouseListener);
          add(al, 5 + j, 1);
          j++;
        }
        tempCalendar = tempCalendar.plusDays(1);
      }
    }
  }

  private void createDaysPane() {
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

  private int day2x(int view, int d) {
    d = view == Constant.DAY_VIEW ? 0 : --d;
    int x = cellWidth * d + (cellWidth / 2) - ((cellWidth - Constant.CELL_SPACER * 2) / 2);

    return x;
  }

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
      timeLine();
    }

    private void timeLine() {
      new Thread() {
        @Override
        public void run() {
          while(true) {
            DateTime d = new DateTime();
            currentHourOfDay = d.getHourOfDay();
            currentMinuteOfHour = d.getMinuteOfHour();
            try {
              Thread.sleep(300000);
            } catch (InterruptedException ex) {
              Logger.getLogger(ScrollableActivityPane.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        }
      }.start();

    }

    @Override
    public void paint(Graphics g) {
      super.paint(g);

      double minutes = currentHourOfDay* 60 + currentMinuteOfHour;
      double maxMinutes = 24 * 60;
      double currentState = getHeight() * minutes / maxMinutes;
      Graphics2D g2d = (Graphics2D)g;
      g2d.setColor(Constant.TIMELINE_COLOR);
      g2d.setStroke(new BasicStroke(2));
      g2d.drawLine(0, (int)currentState, getWidth(), (int)currentState);
    }
  }

  MouseListener mouseListener = new MouseListener() {
    @Override
    public void mouseClicked(MouseEvent e) {
      if(e.getSource() instanceof ActivityLabel) {
        ActivityLabel al = (ActivityLabel)e.getSource();
        if(e.getClickCount() > 1) {
          firePropertyChange(Constant.MODIFY_ACTIVITY, null, al.getActivity());
        }
        else {
          System.out.println("itt is váltsunk dátumot??");
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

  //Methods required by the MouseMotionListener interface:
  @Override
  public void mouseMoved(MouseEvent e) {
  }

  @Override
  public void mouseDragged(MouseEvent e) {
  }

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
