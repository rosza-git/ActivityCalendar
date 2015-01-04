/**
 * Activity Panel
 * 
 * @author Szalay Roland
 */
package rosza.activitycalendar;

//<editor-fold defaultstate="collapsed" desc=" Import ">
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.border.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import rosza.xcomponents.JScrollBarX;
//</editor-fold>

public class ActivityPane extends JPanel  {
  //<editor-fold defaultstate="collapsed" desc=" Variables declaration ">
  private static Bars daysBar;
  private static Bars hoursBar;

  private final DateTime now = new DateTime();
  private DateTime  tempCalendar = new DateTime(now);
  private int       currentHour  = now.getHourOfDay();
  private int       currentMinute = now.getMinuteOfHour();
  private final int selectedYear = ActivityCalendar.selectedDate.getYear();
  private final int selectedMonth = ActivityCalendar.selectedDate.getMonthOfYear();
  private final int selectedDayOfMonth = ActivityCalendar.selectedDate.getDayOfMonth();
  private final ScrollableActivityPane scrollableActivityPane;
  private final JScrollPane scrollPane;

  private final int currentView;
  private final int cellWidth;

  private volatile Thread thread;
  private volatile boolean running = true;
  private static int count = 0;
  //</editor-fold>

  public ActivityPane(int view, int year, int month, int day) {
    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
    setOpaque(true);

    tempCalendar = new DateTime(year, month, day, 0, 0);
    currentView = view;
    cellWidth = currentView == Constant.DAY_VIEW ? Constant.DAY_CELL_WIDTH : Constant.WEEK_CELL_WIDTH;

    // Create the row and column headers.
    daysBar = new Bars(Bars.DAYS);
    hoursBar = new Bars(Bars.HOURS);

    daysBar.addPropertyChangeListener(propertyChangeListener);

    // Set up the scroll pane.
    scrollableActivityPane = new ScrollableActivityPane(currentView, selectedYear, selectedMonth, selectedDayOfMonth);
    scrollableActivityPane.addPropertyChangeListener(propertyChangeListener);
    scrollPane = new JScrollPane(scrollableActivityPane);
    int paneWidth = 5 + scrollPane.getVerticalScrollBar().getMaximumSize().width + Constant.BAR_WIDTH + cellWidth * (currentView == Constant.DAY_VIEW ? 1 : 7);
    scrollPane.setPreferredSize(new Dimension(paneWidth, Constant.CELL_HEIGHT * 16));
    scrollPane.setColumnHeaderView(daysBar);
    scrollPane.setRowHeaderView(hoursBar);
    scrollPane.getVerticalScrollBar().setUI(new JScrollBarX());
    scrollPane.getHorizontalScrollBar().setUI(new JScrollBarX());

    //Set the corners.
    scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, new Corner("time"));
    scrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER, new Corner());
    scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new Corner());

    add(scrollPane);

    thread = new TimeLine("AP_TimeLine" + count++);
    thread.start();
  }

  PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
    @Override
    public void propertyChange(PropertyChangeEvent e) {
      if(e.getPropertyName().equals(Constant.SELECTION_CHANGE)) {
        firePropertyChange(Constant.SELECTION_CHANGE, null, (String)e.getNewValue());
      }
      if(e.getSource() instanceof ScrollableActivityPane) {
        if(e.getPropertyName().equals(Constant.MODIFY_ACTIVITY)) {
          firePropertyChange(Constant.MODIFY_ACTIVITY, null, e.getNewValue());
        }
      }
    }
  };

  private class Corner extends JLabel {
    private Corner() {
      setBackground(Constant.BG_COLOR);
      setOpaque(true);
    }

    private Corner(String s) {
      setText(s);
      setHorizontalAlignment(JLabel.CENTER);
      setVerticalAlignment(JLabel.CENTER);
      setFont(getFont().deriveFont(Font.BOLD));
      setForeground(Constant.TEXT_COLOR);
      setBackground(Constant.BG_COLOR);
      setOpaque(true);
      setBorder(new MatteBorder(0, 0, 1, 1, Constant.CELL_BORDER_COLOR));
    }
  }

  public void stopRunning() {
    scrollableActivityPane.stopRunning();
    thread.interrupt();
    running = false;
    thread = null;
  }

  class TimeLine extends Thread {
    TimeLine(String n) {
      super(n);
    }

    @Override
    public void run() {
      while(!thread.isInterrupted() | !running) {
        DateTime d = new DateTime();
        currentHour = d.getHourOfDay();
        currentMinute = d.getMinuteOfHour();
        repaint();
        try {
          Thread.sleep(10);
        }
        catch (InterruptedException ex) {
        }
      }
    }
  }

  private class Bars extends JPanel {
    public static final int HOURS = 0;
    public static final int DAYS = 1;
    public static final int TIME = 2;

    private int header;

    public Bars(int h) {
      setBackground(Constant.CELL_BG_COLOR);
      setOpaque(true);

      header = h;

      switch(header) {
        case HOURS:
          setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
          for(int i = 0; i < 24; i++) {
            add(hourLabel(i));
          }
          break;
        case DAYS:
          setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

          if(currentView == Constant.DAY_VIEW) {
            boolean today = (tempCalendar.getYear() == ActivityCalendar.currentYear) & (tempCalendar.getMonthOfYear() == ActivityCalendar.currentMonth) & (tempCalendar.getDayOfMonth() == ActivityCalendar.currentDayOfMonth);
            add(dayLabel(selectedYear, selectedMonth, selectedDayOfMonth, tempCalendar.dayOfWeek().getAsText(), today, true));
          }
          else {
            tempCalendar = new DateTime(tempCalendar.withDayOfWeek(DateTimeConstants.MONDAY));
            for(int i = 1; i < 8; i++) {
              boolean selected = (selectedDayOfMonth == tempCalendar.getDayOfMonth());
              boolean today = (tempCalendar.getYear() == ActivityCalendar.currentYear) & (tempCalendar.getMonthOfYear() == ActivityCalendar.currentMonth) & (tempCalendar.getDayOfMonth() == ActivityCalendar.currentDayOfMonth);
              add(dayLabel(tempCalendar.getYear(), tempCalendar.getMonthOfYear(), tempCalendar.getDayOfMonth(), tempCalendar.dayOfWeek().getAsText(), today, selected));
              tempCalendar = tempCalendar.plusDays(1);
            }
          }
          break;
      }
    }

    private JLabel hourLabel(int hour) {
      JLabel l = new JLabel();

      l.setText(String.format("%02d:00", hour));
      l.setOpaque(false);
      l.setHorizontalAlignment(JLabel.CENTER);
      l.setVerticalAlignment(JLabel.CENTER);
      l.setFont(getFont().deriveFont(Font.BOLD));
      l.setForeground(Constant.CELL_TEXT_COLOR);
      l.setBorder(new MatteBorder(0, 0, 1, 1, Constant.CELL_BORDER_COLOR));
      l.setMaximumSize(new Dimension(Constant.BAR_WIDTH, Constant.CELL_HEIGHT));
      l.setMinimumSize(new Dimension(Constant.BAR_WIDTH, Constant.CELL_HEIGHT));
      l.setPreferredSize(new Dimension(Constant.BAR_WIDTH, Constant.CELL_HEIGHT));

      return l;
    }

    private JLabel dayLabel(int year, int month, int day, String dayName, boolean today, boolean selected) {
      JLabel l = new JLabel();

      if(today) {
        l.setFont(l.getFont().deriveFont(Font.BOLD | Font.ITALIC));
      }
      else {
        l.setFont(l.getFont().deriveFont(Font.BOLD));
      }

      l.setName(String.format("day-0-%d-%d-%d", year, month, day));
      l.setOpaque(selected);
      l.setBackground(Constant.SELECTED_CELLBG_COLOR);
      l.setText(String.format("%02d.%02d. %s", month, day, dayName));
      l.setHorizontalAlignment(JLabel.CENTER);
      l.setVerticalAlignment(JLabel.CENTER);
      l.setForeground(Constant.CELL_TEXT_COLOR);
      l.setBorder(new MatteBorder(0, 0, 1, 1, Constant.CELL_BORDER_COLOR));
      l.setMaximumSize(new Dimension(cellWidth, Constant.BAR_HEIGHT));
      l.setMinimumSize(new Dimension(cellWidth, Constant.BAR_HEIGHT));
      l.setPreferredSize(new Dimension(cellWidth, Constant.BAR_HEIGHT));
      l.addMouseListener(dayLabelMouseListener);

      return l;
    }

    public void setPreferredHeight(int ph) {
        setPreferredSize(new Dimension(Constant.BAR_WIDTH, ph));
    }

    public void setPreferredWidth(int pw) {
        setPreferredSize(new Dimension(pw, Constant.BAR_HEIGHT));
    }

    @Override
    public void paint(Graphics g) {
      super.paint(g);

      double minutes = currentHour * 60 + currentMinute;
      double maxMinutes = 24 * 60;
      double currentState = getHeight() * minutes / maxMinutes;

      if(header == HOURS) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(Constant.TIMELINE_COLOR);
        g2d.setStroke(new BasicStroke(4));
        g2d.drawLine(getWidth() - 10, (int)currentState, getWidth() - 6, (int)currentState);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(getWidth() - 10, (int)currentState, getWidth(), (int)currentState);
      }
    }

    MouseListener dayLabelMouseListener = new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if(e.getSource() instanceof JLabel) {
          firePropertyChange(Constant.SELECTION_CHANGE, null, ((JLabel)e.getSource()).getName());
        }
      }

      @Override
      public void mousePressed(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }

      @Override
      public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }
    };
  }
}
