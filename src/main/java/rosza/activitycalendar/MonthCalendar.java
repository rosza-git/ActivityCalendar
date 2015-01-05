/**
 * Month Calendar.
 * 
 * @author Szalay Roland
 * 
 */
package rosza.activitycalendar;

//<editor-fold defaultstate="collapsed" desc=" Import ">
import rosza.xcomponents.JLabelX;
import rosza.xcomponents.JPanelX;
import rosza.xcomponents.JButtonX;
import rosza.xcomponents.JComboBoxX;
import rosza.xcomponents.JSpinnerX;
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
//</editor-fold>

public class MonthCalendar extends JPanelX {
  //<editor-fold defaultstate="collapsed" desc=" Variables declaration ">
  private JSpinner  yearSpinner;
  private JButton   prevMonthButton;
  private JComboBox monthComboBox;
  private JButton   nextMonthButton;
  private JLabelX   headerLabel;
  private JPanel    calendarControlPanel;
  private JPanel    weekLabelsPanel;
  private JPanel    dayLabelsPanel;
  private JButtonX  closeButton;

  private DateTime selectedDate;
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Create new MonthCalendar ">
  public MonthCalendar(int y, int m, int d) {
    selectedDate = new DateTime(y, m, d, 0, 0);

    init();
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Get short or long day names ">
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
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Initialize UI components ">
  private void init() {
    headerLabel          = new JLabelX();
    closeButton          = new JButtonX("close");
    calendarControlPanel = new MonthControllerPane();   
    weekLabelsPanel      = new JPanel();
    dayLabelsPanel       = new JPanel();

    setOpaque(false);


    closeButton.setText("close");
    closeButton.setFont(closeButton.getFont().deriveFont(Font.BOLD));
    closeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        closeButtonActionPerformed(e);
      }
    });

    headerLabel.setText("month calendar");
    headerLabel.setHorizontalAlignment(JLabel.CENTER);
    headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD).deriveFont(18f));

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    GridLayout gLayout = new GridLayout(1, 7, 2, 2);
    EmptyBorder pBorder = new EmptyBorder(1, 2, 2, 2);
    CompoundBorder labelBorder = new CompoundBorder(new EmptyBorder(1, 1, 1, 1), new EmptyBorder(5, 5, 5, 5));
    CompoundBorder labelSelectedBorder = new CompoundBorder(new MatteBorder(1, 1, 1, 1, Color.red), new EmptyBorder(5, 5, 5, 5));
    String[] daysShortName = getDaysName(Constant.SHORT_DISPLAY);

    weekLabelsPanel.setLayout(gLayout);
    weekLabelsPanel.setOpaque(true);
    weekLabelsPanel.setBackground(Constant.BG_DARKER_BLUE);
    weekLabelsPanel.setBorder(pBorder);

    for(int i = 1; i < 8; i++) {
      JLabel l = new JLabel(daysShortName[i - 1], JLabel.CENTER);

      l.setFont(l.getFont().deriveFont(Font.BOLD));
      l.setBorder(labelBorder);
      l.setForeground(i == DateTimeConstants.SUNDAY ? new Color(190, 58, 61) : Constant.TEXT_COLOR);

      weekLabelsPanel.add(l);
    }

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
      l.setForeground(Constant.TEXT_COLOR);
      l.setBackground(Color.white);
      l.setOpaque(true);
      if((i - emptyBefore == ActivityCalendar.currentDayOfMonth) && (selectedDate.getYear() == ActivityCalendar.currentYear) && (selectedDate.getMonthOfYear() == ActivityCalendar.currentMonth)) {
        l.setForeground(Constant.BG_DARKER_BLUE);
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
      dayLabelsPanel.setLayout(new BoxLayout(dayLabelsPanel, BoxLayout.Y_AXIS));
      dayLabelsPanel.setOpaque(false);
      dayLabelsPanel.add(p);
    }

    GroupLayout layout = new GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addComponent(headerLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
          .addComponent(dayLabelsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(weekLabelsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(calendarControlPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(closeButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        )
        .addContainerGap()
      )
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(headerLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(calendarControlPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(weekLabelsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(dayLabelsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addContainerGap()
        .addComponent(closeButton)
        .addContainerGap()
      )
    );
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" MouseListener ">
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
      l.setBackground(Constant.SELECTED_CELLBG_COLOR);
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
  //</editor-fold>

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
    selectedDate = selectedDate.withMonthOfYear(monthComboBox.getSelectedIndex() + 1);
    DateTime d = new DateTime(selectedDate.getYear(), selectedDate.getMonthOfYear(), selectedDate.getDayOfMonth(), 0, 0);
    firePropertyChange(Constant.MONTH_CHANGE, null, d);
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Month change action ">
  private class MonthControllerPane extends JPanel {
    public MonthControllerPane() {
      yearSpinner     = new JSpinner();
      prevMonthButton = new JButtonX("<");
      monthComboBox   = new JComboBox();
      nextMonthButton = new JButtonX(">");

      setOpaque(false);

      yearSpinner.setModel(new SpinnerNumberModel(selectedDate.getYear(), null, null, 1));
      yearSpinner.setToolTipText("year");
      yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#"));
      yearSpinner.setPreferredSize(new Dimension(52, 20));
      yearSpinner.setUI(new JSpinnerX());
      yearSpinner.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          yearSpinnerStateChanged(e);
        }
      });

      prevMonthButton.setFont(prevMonthButton.getFont().deriveFont(Font.BOLD));
      prevMonthButton.setText("<");
      prevMonthButton.setToolTipText("previous month");
      prevMonthButton.setPreferredSize(new Dimension(25, 25));
      prevMonthButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          prevMonthButtonActionPerformed(e);
        }
      });

      monthComboBox.setFont(monthComboBox.getFont());
      monthComboBox.setUI(new JComboBoxX());
      monthComboBox.setModel(new DefaultComboBoxModel(getMonthsName(Constant.LONG_DISPLAY)));
      monthComboBox.setSelectedIndex(selectedDate.getMonthOfYear() - 1);
      monthComboBox.setToolTipText("month");
      monthComboBox.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          monthComboBoxActionPerformed(e);
        }
      });

      nextMonthButton.setFont(nextMonthButton.getFont().deriveFont(Font.BOLD));
      nextMonthButton.setText(">");
      nextMonthButton.setToolTipText("next month");
      nextMonthButton.setPreferredSize(new Dimension(25, 25));
      nextMonthButton.addActionListener(new ActionListener() {
        @Override
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
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Close button action ">
  private void closeButtonActionPerformed(ActionEvent e) {
    firePropertyChange(Constant.CLOSE_PANE, null, this);
  }
  //</editor-fold>
}
