/**
 * Month dialog
 * 
 * @author Szalay Roland
 * 
 */
package rosza.activitycalendar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import rosza.xcomponents.JDialogX;
import rosza.xcomponents.JButtonX;
import rosza.xcomponents.JComboBoxX;
import rosza.xcomponents.JSpinnerX;

public class MonthDialog extends JDialogX {
  // UI variables
  private JSpinner        yearSpinner;
  private JLabel          prevMonthButton;
  private JComboBox       monthComboBox;
  private JLabel          nextMonthButton;
  private JPanel          calendarControlPanel;
  private JPanel          weekLabelsPanel;
  private JPanel          dayLabelsPanel;
  private JPanel          monthView;
  private JPanel          monthPane;
  private JButtonX        closeButton;
  private final Component relativeTo;

  // Date and time variable
  private static DateTime selectedDate;

  // Class loader
  private final ClassLoader cl = this.getClass().getClassLoader();

  // Create new MonthDialog
  public MonthDialog(Frame owner, Component locationComp, String title, boolean modal, int y, int m, int d) {
    super(owner, title, modal);

    selectedDate = new DateTime(y, m, d, 0, 0);
    relativeTo = locationComp;

    createUI();
  }

  // Get short or long day names
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

  // Create UI
  @SuppressWarnings("unchecked")
  private void createUI() {
    closeButton          = new JButtonX("close");
    monthPane            = new JPanel();
    calendarControlPanel = new MonthControllerPane();   
    monthView            = new MonthView();

    monthPane.setLayout(new BoxLayout(monthPane, BoxLayout.Y_AXIS));
    monthPane.setOpaque(false);
    monthPane.add(calendarControlPanel);
    monthPane.add(monthView);

    closeButton.setText("close");
    closeButton.setFont(closeButton.getFont().deriveFont(Font.BOLD));
    closeButton.addActionListener(actionListener);
    closeButton.setActionCommand(Constant.CLOSE_DIALOG);

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
          .addComponent(monthPane)
          .addComponent(closeButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        )
        .addContainerGap()
      )
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(monthPane)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(closeButton)
        .addContainerGap()
      )
    );

    pack();
    setLocationRelativeTo(relativeTo);
  }

  /**
   * Show the dialog
   * 
   * @return selected date
   */
  public DateTime showDialog() {
    setVisible(true);

    return selectedDate;
  }

  // Action listener
  ActionListener actionListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      if(null != e.getActionCommand()) {
        switch (e.getActionCommand()) {
          case Constant.CLOSE_DIALOG:
            selectedDate = null;
            setVisible(false);
            dispose();
            break;
        }
      }
    }
  };

  // Mouse listener
  MouseListener cellListener = new MouseListener() {
    @Override
    public void mouseClicked(MouseEvent e) {
      JLabel l = (JLabel)e.getSource();
      selectedDate = new DateTime((int)yearSpinner.getValue(), monthComboBox.getSelectedIndex() + 1, Integer.parseInt(l.getText()), 0, 0);
      setVisible(false);
      dispose();
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

  /**
   * Step one month backward
   * 
   * @param e 
   */
  private void prevMonth() {
    selectedDate = selectedDate.minusMonths(1);
    monthPane.removeAll();
    monthView = new MonthView();
    calendarControlPanel = new MonthControllerPane();
    monthPane.add(calendarControlPanel);
    monthPane.add(monthView);
    pack();
  }

  /**
   * Step one month forward
   * 
   * @param e 
   */
  private void nextMonth() {
    selectedDate = selectedDate.plusMonths(1);
    monthPane.removeAll();
    monthView = new MonthView();
    calendarControlPanel = new MonthControllerPane();
    monthPane.add(calendarControlPanel);
    monthPane.add(monthView);
    pack();
  }

  /**
   * Change selected year
   * 
   * @param e 
   */
  private void yearSpinnerStateChanged() {
    selectedDate = selectedDate.withYear((int)yearSpinner.getValue());
    monthPane.removeAll();
    monthView = new MonthView();
    calendarControlPanel = new MonthControllerPane();
    monthPane.add(calendarControlPanel);
    monthPane.add(monthView);
    pack();
  }

  /**
   * Change selected month
   * 
   * @param e 
   */
  private void monthComboBoxActionPerformed() {
    selectedDate = selectedDate.withMonthOfYear(monthComboBox.getSelectedIndex() + 1);
    monthPane.removeAll();
    monthView = new MonthView();
    calendarControlPanel = new MonthControllerPane();
    monthPane.add(calendarControlPanel);
    monthPane.add(monthView);
    pack();
  }

  // Month controller pane
  private class MonthControllerPane extends JPanel {
    public MonthControllerPane() {
      yearSpinner     = new JSpinner();
      prevMonthButton = new JLabel();
      monthComboBox   = new JComboBox(getMonthsName(Constant.LONG_DISPLAY));
      nextMonthButton = new JLabel();

      setOpaque(false);

      yearSpinner.setModel(new SpinnerNumberModel(selectedDate.getYear(), null, null, 1));
      yearSpinner.setToolTipText("year");
      yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#"));
      yearSpinner.setPreferredSize(new Dimension(yearSpinner.getPreferredSize().width, 22));
      yearSpinner.setBorder(new MatteBorder(1, 1, 1, 1, Constant.BG_DARKER_BLUE));
      yearSpinner.setUI(new JSpinnerX());
      yearSpinner.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          yearSpinnerStateChanged();
        }
      });

      monthComboBox.setUI(new JComboBoxX());
      monthComboBox.setLightWeightPopupEnabled(false);
      monthComboBox.setSelectedIndex(selectedDate.getMonthOfYear() - 1);
      monthComboBox.setToolTipText("month");
      monthComboBox.setBorder(new MatteBorder(1, 1, 1, 1, Constant.BG_DARKER_BLUE));
      monthComboBox.setPreferredSize(new Dimension(monthComboBox.getPreferredSize().width, 22));
      monthComboBox.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          monthComboBoxActionPerformed();
        }
      });

      prevMonthButton.setIcon(new ImageIcon(cl.getResource(Constant.PREV24_ICON)));
      prevMonthButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
      prevMonthButton.setToolTipText("previous month");
      prevMonthButton.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          prevMonth();
        }
      });

      nextMonthButton.setIcon(new ImageIcon(cl.getResource(Constant.NEXT24_ICON)));
      nextMonthButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
      nextMonthButton.setToolTipText("next month");
      nextMonthButton.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          nextMonth();
        }
      });

      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
          .addContainerGap(0, 0)
          .addComponent(prevMonthButton)
          .addContainerGap(10, 500)
          .addComponent(yearSpinner, 55, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addContainerGap(10, 500)
          .addComponent(monthComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addContainerGap(10, 500)
          .addComponent(nextMonthButton)
          .addContainerGap(0, 0)
        )
      );
      layout.setVerticalGroup(
        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
          .addGap(5)
          .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(prevMonthButton)
            .addComponent(yearSpinner)
            .addComponent(monthComboBox)
            .addComponent(nextMonthButton)
          )
          .addGap(5)
        )
      );
    }

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
  }

  // Create a "month view" from the selected month
  private class MonthView extends JPanel {
    public MonthView() {
      weekLabelsPanel = new JPanel();
      dayLabelsPanel  = new JPanel();

      setOpaque(false);
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

      add(weekLabelsPanel);
      add(dayLabelsPanel);
    }
  }
}
