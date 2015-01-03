/**
 * Month Calendar.
 * 
 * @author Szalay Roland
 * 
 */
package rosza.activitycalendar;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

public class MonthCalendar extends JPanel {
  private JSpinner  yearSpinner;
  private JButton   prevMonthButton;
  private JComboBox monthComboBox;
  private JButton   nextMonthButton;

  private DateTime selectedDate;

  public MonthCalendar(int y, int m, int d) {
    selectedDate = new DateTime(y, m, d, 0, 0);

    init();
  }

  private static String[] getDaysName(int style) {
    String[] d = new String[7];
    DateTime date = new DateTime();
    switch(style) {
      case Constant.LONG_DISPLAY:
        for(int i = 0; i < 7;i++) {
          d[i] = date.withDayOfWeek(i + 1).dayOfWeek().getAsText();
        }
        break;
      case Constant.SHORT_DISPLAY:
      default:
        for(int i = 0; i < 7;i++) {
          d[i] = date.withDayOfWeek(i + 1).dayOfWeek().getAsShortText();
        }
    }

    return d;
  }

  private void init() {
    setBorder(new MatteBorder(1, 1, 1, 1, Color.black));
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    GridLayout gLayout = new GridLayout(1, 7, 2, 2);
    EmptyBorder pBorder = new EmptyBorder(1, 2, 2, 2);
    CompoundBorder labelBorder = new CompoundBorder(new MatteBorder(1, 1, 1, 1, Color.white), new EmptyBorder(5, 5, 5, 5));
    CompoundBorder labelSelectedBorder = new CompoundBorder(new MatteBorder(1, 1, 1, 1, Color.red), new EmptyBorder(5, 5, 5, 5));
    String[] daysShortName = getDaysName(Constant.SHORT_DISPLAY);
    JPanel calendarControlPanel = new MonthControllerPane();                    // for controllers
    JPanel daysPanel = new JPanel();                  // for week labels

    add(calendarControlPanel);

    daysPanel.setLayout(gLayout);
    daysPanel.setOpaque(false);
    daysPanel.setBorder(pBorder);

    for(int i = 1; i < 8; i++) {
      JLabel l = new JLabel(daysShortName[i - 1], JLabel.CENTER);

      l.setFont(l.getFont().deriveFont(Font.BOLD));
      l.setBorder(labelBorder);
      l.setForeground(i == DateTimeConstants.SUNDAY ? new Color(190, 58, 61) : Color.black);

      daysPanel.add(l);
    }
    add(daysPanel);

    // calculate necessary cells
    int cells = 0;
    DateTime tempDate = new DateTime(selectedDate.getYear(), selectedDate.getMonthOfYear(), 1, 0, 0);
    int dom = tempDate.dayOfMonth().getMaximumValue();
    cells += dom;
    tempDate = new DateTime(tempDate.withDayOfWeek(DateTimeConstants.MONDAY));
    int emptyBefore = 0;
    if(tempDate.getDayOfMonth() > 1) {
      emptyBefore = tempDate.dayOfMonth().getMaximumValue() - tempDate.getDayOfMonth() + 1;
    }
    cells += emptyBefore;

    // calculate necessary cells for full rows
    while(cells % 7 != 0) {
      cells++;
    }
    JPanel p = new JPanel();
    for(int i = 1; i <= cells; i++) {
      String t;
      JLabel l = new JLabel();
      if((i == 1) || ((i - 1) % 7 == 0)) {
        p = new JPanel();
        p.setLayout(gLayout);
      }
      if((i <= emptyBefore) || (i >  dom + emptyBefore)) {
        t = "";
      }
      else {
        t = Integer.toString(i - emptyBefore);
        l.addMouseListener(cellListener);
        l.setCursor(new Cursor(Cursor.HAND_CURSOR));
      }
      l.setText(t);
      l.setHorizontalAlignment(JLabel.CENTER);
      l.setFont(l.getFont().deriveFont(Font.BOLD));
      l.setForeground(Color.darkGray);
      l.setBackground(Color.white);
      l.setOpaque(true);
      if((i - emptyBefore == ActivityCalendar.currentDayOfMonth) && (selectedDate.getYear() == ActivityCalendar.currentYear) && (selectedDate.getMonthOfYear() == ActivityCalendar.currentMonth)) {
        l.setForeground(Color.blue);
      }
      if((i - emptyBefore == selectedDate.getDayOfMonth()) && (selectedDate.getYear() == ActivityCalendar.selectedDate.getYear()) && (selectedDate.getMonthOfYear() == ActivityCalendar.selectedDate.getMonthOfYear())) {
        l.setBorder(labelSelectedBorder);
      }
      else {
        l.setBorder(labelBorder);
      }
      p.add(l);
      p.setOpaque(false);
      p.setBorder(pBorder);
      add(p);
    }
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    GradientPaint gradientPaint;
    g2d.setPaint(new Color(246, 246, 246));
    g2d.fillRect(0, 0, getWidth(), getHeight());

    gradientPaint = new GradientPaint(0, 0, new Color(38, 142, 191), 0, Constant.FONT_SIZE * 2 + 2, new Color(22, 131, 175), false);
    g2d.setPaint(gradientPaint);
    g2d.fillRect(0, 0, getWidth(), (int)Constant.FONT_SIZE * 2 + 2);

//    g2d.fillRoundRect(25, 10, 75, 30, 10, 10);

    g2d.setPaint(Color.BLACK);
  }

  MouseListener cellListener = new MouseListener() {
    @Override
    public void mouseClicked(MouseEvent e) {
      JLabel l = (JLabel)e.getSource();
      DateTime d = new DateTime((int)yearSpinner.getValue(), monthComboBox.getSelectedIndex() + 1, Integer.parseInt(l.getText()), 0, 0);
      firePropertyChange(Constant.DAY_CHANGE, null, d);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
      JLabel l = (JLabel)e.getSource();
      l.setBackground(Color.lightGray);
    }

    @Override
    public void mouseExited(MouseEvent e) {
      JLabel l = (JLabel)e.getSource();
      l.setBackground(Color.white);
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }
  };

    //<editor-fold defaultstate="collapsed" desc=" Previous month action ">
    /**
     * Step one month backward.
     * 
     * @param e 
     */
    private void prevMonthButtonActionPerformed(ActionEvent e) {
      selectedDate = selectedDate.minusMonths(1);
      DateTime d = new DateTime(selectedDate.getYear(), selectedDate.getMonthOfYear(), selectedDate.getDayOfMonth(), 0, 0);
      firePropertyChange(Constant.MONTH_CHANGE, null, d);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Next month action ">
    /**
     * Step one month forward.
     * 
     * @param e 
     */
    private void nextMonthButtonActionPerformed(ActionEvent e) {
      selectedDate = selectedDate.plusMonths(1);
      DateTime d = new DateTime(selectedDate.getYear(), selectedDate.getMonthOfYear(), selectedDate.getDayOfMonth(), 0, 0);
      firePropertyChange(Constant.MONTH_CHANGE, null, d);
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Year change action ">
    private void yearSpinnerStateChanged(ChangeEvent e) {
      selectedDate = selectedDate.withYear((int)yearSpinner.getValue());
      DateTime d = new DateTime(selectedDate.getYear(), selectedDate.getMonthOfYear(), selectedDate.getDayOfMonth(), 0, 0);
      firePropertyChange(Constant.YEAR_CHANGE, null, d);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Month change action ">
    private void monthComboBoxActionPerformed(ActionEvent e) {
      selectedDate = selectedDate.withMonthOfYear(monthComboBox.getSelectedIndex());
      DateTime d = new DateTime(selectedDate.getYear(), selectedDate.getMonthOfYear(), selectedDate.getDayOfMonth(), 0, 0);
      firePropertyChange(Constant.MONTH_CHANGE, null, d);
    }
    //</editor-fold>

  private class MonthControllerPane extends JPanel {
    public MonthControllerPane() {
      yearSpinner     = new JSpinner();
      prevMonthButton = new JButton();
      monthComboBox   = new JComboBox();
      nextMonthButton = new JButton();

      yearSpinner.setModel(new SpinnerNumberModel(selectedDate.getYear(), null, null, 1));
      yearSpinner.setToolTipText("year");
      yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#"));
      yearSpinner.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          yearSpinnerStateChanged(e);
        }
      });

      prevMonthButton.setFont(prevMonthButton.getFont());
      prevMonthButton.setText("<");
      prevMonthButton.setToolTipText("previous month");
      prevMonthButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          prevMonthButtonActionPerformed(e);
        }
      });

      monthComboBox.setFont(monthComboBox.getFont());
      monthComboBox.setModel(new DefaultComboBoxModel(getMonthsName(Constant.LONG_DISPLAY)));
      monthComboBox.setSelectedIndex(selectedDate.getMonthOfYear() - 1);
      monthComboBox.setToolTipText("month");
      monthComboBox.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          monthComboBoxActionPerformed(e);
        }
      });

      nextMonthButton.setFont(nextMonthButton.getFont());
      nextMonthButton.setText(">");
      nextMonthButton.setToolTipText("next month");
      nextMonthButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          nextMonthButtonActionPerformed(e);
        }
      });

      add(yearSpinner);
      add(prevMonthButton);
      add(monthComboBox);
      add(nextMonthButton);
    }

    //<editor-fold defaultstate="collapsed" desc=" Get months name ">
    /**
     * Get the long or short name of the months.
     * 
     * @param style display style of the months (Constant.SHORT_DISPLAY or Constant.LONG_DISPLAY)
     * @return String array with the names
     */
    private String[] getMonthsName(int style) {
      DateTime d = new DateTime();
      String[] temp = new String[d.monthOfYear().getMaximumValue()];
      for(int i = 0, s = temp.length; i < s; i++) {
        switch(style) {
          case Constant.SHORT_DISPLAY:
            temp[i] = d.monthOfYear().getAsShortText();
            break;
          case Constant.LONG_DISPLAY:
          default:
            temp[i] = d.monthOfYear().getAsText();
        }
        d = d.plusMonths(1);
      }

      return temp;
    }
    //</editor-fold>
  }
}
