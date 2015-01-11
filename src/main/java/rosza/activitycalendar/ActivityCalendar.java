/**
 * Activity Calendar
 * 
 * @author Szalay Roland
 * 
 * required 3rd-party libraries (at least):
 *   - jaspyt-1.9.2.jar - http://www.jasypt.org/
 *   - javax.mail.1.5.2.jar - https://java.net/projects/javamail/pages/Home
 *   - activation-1.1.jar (via javax.mail)
 *   - joda-time.2.6.jar - http://www.joda.org/joda-time/
 *   - org.apache.commons.lang-2.6.jar - http://commons.apache.org/proper/commons-lang/download_lang.cgi
 *   - commons-lang-2.6.jar (via org.apache.commons.lang)
 * 
 * additional requirements:
 *   - JCE - http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
 * 
 */
package rosza.activitycalendar;

import rosza.xcomponents.JLabelX;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import org.joda.time.DateTime;

public class ActivityCalendar extends JFrame {
  // UI variables
  private JPanel         topLayer;
  private JPanel         bottomLayer;

  // customized components
  private ActivityHeader activityHeader;
  private ActivityMenu   activityMenu;
  private MonthDialog    monthDialog;
  private ActivityDialog activityDialog;
  private SettingsDialog settingsDialog;
  private SummaryDialog  summaryDialog;
  private ActivityPane   activityPane;

  // System tray variables declaration
  private SystemTray systemTray;
  private TrayIcon trayIcon;

  // Class loader
  private final ClassLoader cl = this.getClass().getClassLoader();

  // Mouse action variables declaration
  private int mouseX;
  private int mouseY;

  //Calendar variables declaration
  public static DateTime now = new DateTime();  // get new Joda-Time for actual date
  public static int      currentYear;           // current year
  public static int      currentMonth;          // current month
  public static int      currentDayOfMonth;     // current day of the month
  public static DateTime selectedDate;          // selected date

  // DataManager
  private final DataManager dataManager;

  // Create new ActivityCalendar form
  public ActivityCalendar() {
    currentYear       = now.getYear();          // current year
    currentMonth      = now.getMonthOfYear();   // current month
    currentDayOfMonth = now.getDayOfMonth();    // current day of the month
    selectedDate      = new DateTime(now);

    dataManager = new DataManager();

    setAppIcon();
    createUI();
    initSystemTray();
    XMLUtil.getCategories();
  }

  // Create UI
  @SuppressWarnings("unchecked")
  private void createUI() {
    topLayer       = new JPanel();
    bottomLayer    = new JPanel();
    activityHeader = new ActivityHeader();
    activityMenu   = new ActivityMenu();
    activityPane   = new ActivityPane(Constant.WEEK_VIEW, selectedDate.getYear(), selectedDate.getMonthOfYear(), selectedDate.getDayOfMonth());
    
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setTitle(Constant.APP_DISPLAY_NAME);
    setUndecorated(true);
    setResizable(false);
    setLayout(new BorderLayout());
    setBackground(Constant.BORDER_COLOR);
    rootPane.setBorder(new EmptyBorder(2, 2, 2, 2));

    activityHeader.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
        headerDragged(e);
      }
    });
    activityHeader.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        headerPressed(e);
      }
    });

    activityPane.addPropertyChangeListener(propertyChangeListener);
    activityPane.scrollToVisibleRect(new Rectangle(0, Constant.CELL_HEIGHT * now.getHourOfDay(), 100, 100));

    topLayer.setBackground(Constant.BG_COLOR);
    topLayer.setLayout(new BoxLayout(topLayer, BoxLayout.Y_AXIS));
    topLayer.add(activityHeader);
    topLayer.add(activityMenu);

    bottomLayer.setBackground(Constant.BG_COLOR);
    bottomLayer.setLayout(new BorderLayout());
    bottomLayer.add(activityPane, BorderLayout.CENTER);

    getContentPane().add(topLayer, BorderLayout.NORTH);
    getContentPane().add(bottomLayer, BorderLayout.CENTER);

    pack();
    setLocationRelativeTo(null);
  }

  // Set application icon
  private void setAppIcon() {
    Image image = Toolkit.getDefaultToolkit().getImage(cl.getResource(Constant.APP_ICON));
    setIconImage(image);
  }

  // Initialize the system tray icon and pop-up menu
  private void initSystemTray() {
    if(SystemTray.isSupported()) {
      PopupMenu popup = new PopupMenu();
      MenuItem exitMenuItem = new MenuItem("Exit " + Constant.APP_DISPLAY_NAME);
      MenuItem showHideMenuItem = new MenuItem("Show / Hide");
      exitMenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          exitApplication();
        }
      });
      showHideMenuItem.setActionCommand(Constant.ACTIVITY_SHOW_HIDE);
      showHideMenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          showHideApplication();
        }
      });
      popup.add(showHideMenuItem);
      popup.add(exitMenuItem);

      systemTray = SystemTray.getSystemTray();
      Dimension iconSize = systemTray.getTrayIconSize();
      int iconHeight = iconSize.height;
      Image image = Toolkit.getDefaultToolkit().getImage(cl.getResource(Constant.TRAY_ICON16));
      switch(iconHeight) {
        case 24:
          image = Toolkit.getDefaultToolkit().getImage(cl.getResource(Constant.TRAY_ICON24));
          break;
        case 32:
          image = Toolkit.getDefaultToolkit().getImage(cl.getResource(Constant.TRAY_ICON32));
          break;
        case 48:
          image = Toolkit.getDefaultToolkit().getImage(cl.getResource(Constant.TRAY_ICON48));
          break;
      }
      trayIcon = new TrayIcon(image, Constant.APP_DISPLAY_NAME, popup);
      trayIcon.setImageAutoSize(true);
      trayIcon.addMouseListener(mouseListener);
      try {
        systemTray.add(trayIcon);
      }
      catch(AWTException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
    else {
      JOptionPane.showMessageDialog(this, "System tray is currently not supported!", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  // Show a "date selector"
  private void showMonthDialog() {
    if(monthDialog == null || !monthDialog.isVisible()) {
      monthDialog = new MonthDialog(this, this, "jump to date", true, selectedDate.getYear(), selectedDate.getMonthOfYear(), selectedDate.getDayOfMonth());
      DateTime result = monthDialog.showDialog();
      if(result != null) {
        selectedDate = result;
        updateActivityUI();
      }
    }
    else {
      monthDialog.setVisible(false);
      monthDialog.dispose();
    }
  }

  // Show the _summary_ dialog
  private void showSummaryDialog() {
    if(summaryDialog == null || !summaryDialog.isVisible()) {
      summaryDialog = new SummaryDialog(this, this, "summary", true, selectedDate);
      summaryDialog.showDialog();
    }
    else {
      summaryDialog.setVisible(false);
      summaryDialog.dispose();
    }
  }

  // Show the _settings_ dialog
  private void showSettingsDialog() {
    if(settingsDialog == null || !settingsDialog.isVisible()) {
      settingsDialog = new SettingsDialog(this, this, "settings", true);
      settingsDialog.showDialog();
    }
    else {
      settingsDialog.setVisible(false);
      settingsDialog.dispose();
    }
  }

  // Show the _activity_ dialog
  private void showActivityDialog(Activity a) {
    if(activityDialog == null || !activityDialog.isVisible()) {
      activityDialog = new ActivityDialog(this, this, "activity", true, a);
      ActivityAction aa = activityDialog.showDialog();
      if(aa == null || aa.getActivity() == null) {
        return;
      }
      switch(aa.getActionCommand()) {
        case Constant.ADD_ACTIVITY:
          dataManager.addActivity(aa.getActivity());
          break;
        case Constant.MODIFY_ACTIVITY:
          dataManager.updateActivity(aa.getActivity());
          break;
        case Constant.REMOVE_ACTIVITY:
          dataManager.removeActivity(aa.getActivity());
          break;
      }
      selectedDate = aa.getActivity().getStartDate();
      updateActivityUI();
    }
    else {
      activityDialog.setVisible(false);
      activityDialog.dispose();
    }
  }

  // Exit application method
  private void exitApplication() {
    // Remove application from system-tray and close 
    systemTray.remove(trayIcon);
    System.exit(0);
  }

  // Show/hide application method
  private void showHideApplication() {
    // Show or hide Activity Calendar window
    if(this.isShowing()) {
      this.setVisible(false);
    }
    else {
      this.setVisible(true);
    }
  }

  // Minimize application method
  private void minimizeApplication() {
    setState(ActivityCalendar.ICONIFIED);
  }

  // Make selected date to today
  private void gotoToday() {
    selectedDate = now;
    updateActivityUI();
  }

  // Go backward one day
  private void prevDay() {
    selectedDate = selectedDate.minusDays(1);
    updateActivityUI();
  }

  // Go forward one day
  private void nextDay() {
    selectedDate = selectedDate.plusDays(1);
    updateActivityUI();
  }

  //Update Activity UI with new date parameters.
  private void updateActivityUI() {
    Rectangle rect;
    rect = activityPane.getVisibleRectangle();
    bottomLayer.remove(activityPane);
    activityPane.stopRunning();
    activityPane = new ActivityPane(Constant.WEEK_VIEW, selectedDate.getYear(), selectedDate.getMonthOfYear(), selectedDate.getDayOfMonth());
    activityPane.addPropertyChangeListener(propertyChangeListener);
    bottomLayer.add(activityPane);
    activityPane.scrollToVisibleRect(rect);
    revalidate();
    repaint();
  }

  // Application window mover methods
  private void headerDragged(MouseEvent e) {
    int x = e.getXOnScreen();
    int y = e.getYOnScreen();

    setLocation(x - mouseX, y - mouseY);
  }

  private void headerPressed(MouseEvent e) {
    mouseX = e.getX();
    mouseY = e.getY();
  }

  // Main method
  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    /* Set the Nimbus look and feel */
    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
     * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
     */
    try {
      for(javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        if("Windows".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          customizeLAF();
          break;
        }
      }
    } catch (ClassNotFoundException ex) {
      java.util.logging.Logger.getLogger(ActivityCalendar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(ActivityCalendar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(ActivityCalendar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(ActivityCalendar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        new ActivityCalendar().setVisible(true);
      }
    });
  }

  // Set look and feel colors
  private static void customizeLAF() {
    // JLabel
    UIManager.put("Label.foreground", Constant.TEXT_COLOR);

    // JTextField
    UIManager.put("text", Constant.TEXT_COLOR);
    UIManager.put("textHighlight", Constant.BG_BLUE);
    UIManager.put("textHighlightText", Constant.TEXT_COLOR);
    UIManager.put("textInactiveText", Constant.BG_DARKER_COLOR);
    UIManager.put("TextField.background", Constant.BG_COLOR);
    UIManager.put("TextField.border", new MatteBorder(1, 1, 1, 1, Constant.BG_DARKER_BLUE));
    UIManager.put("TextField.caretForeground", Constant.TEXT_COLOR);
    UIManager.put("TextField.disabledBackground", Constant.BG_DARKER_COLOR);
    UIManager.put("TextField.foreground", Constant.TEXT_COLOR);
    UIManager.put("TextField.highlight", Constant.BG_BLUE);
    UIManager.put("TextField.inactiveBackground", Constant.BG_COLOR);
    UIManager.put("TextField.inactiveForeground", Constant.BG_DARKER_COLOR);
    UIManager.put("TextField.selectionBackground", Constant.BG_BLUE);
    UIManager.put("TextField.selectionForeground", Constant.TEXT_COLOR);

    // JRadioButton
    UIManager.put("RadioButton.background", Constant.BG_COLOR);
    UIManager.put("RadioButton.border", Constant.BG_DARKER_BLUE);
    UIManager.put("RadioButton.darkShadow", Constant.BG_DARKER_BLUE);
    UIManager.put("RadioButton.disabledText", Constant.BG_DARKER_COLOR);
    UIManager.put("RadioButton.focus", Constant.TEXT_COLOR);
    UIManager.put("RadioButton.foreground", Constant.TEXT_COLOR);
    UIManager.put("RadioButton.highlight", Constant.BG_DARKER_BLUE);
    UIManager.put("RadioButton.interiorBackground", Constant.BG_COLOR);
    UIManager.put("RadioButton.light", Constant.BG_BLUE);
    UIManager.put("RadioButton.select", Constant.BG_DARKER_BLUE);
    UIManager.put("RadioButton.shadow", Constant.BG_BLUE);

    // JTree
    UIManager.put("Tree.background", Constant.BG_COLOR);
    UIManager.put("Tree.editorBorder", new MatteBorder(1, 1, 1, 1, Constant.BG_DARKER_BLUE));
    UIManager.put("Tree.editorBorderSelectionColor", Constant.BG_DARKER_COLOR);
    UIManager.put("Tree.foreground", Constant.TEXT_COLOR);
    UIManager.put("Tree.hash", Constant.BORDER_COLOR);
    UIManager.put("Tree.line", Constant.BORDER_COLOR);
    UIManager.put("Tree.selectionBackground", Constant.BG_BLUE);
    UIManager.put("Tree.selectionBorderColor", Constant.BG_DARKER_BLUE);
    UIManager.put("Tree.selectionForeground", Constant.TEXT_COLOR);
    UIManager.put("Tree.textBackground", Constant.BG_COLOR);
    UIManager.put("Tree.textForeground", Constant.TEXT_COLOR);

    // JCheckBox
    UIManager.put("CheckBox.background", Constant.BG_COLOR);
    UIManager.put("CheckBox.border", new MatteBorder(1, 1, 1, 1, Constant.BG_DARKER_BLUE));
    UIManager.put("CheckBox.darkShadow", Constant.BG_DARKER_BLUE);
    UIManager.put("CheckBox.disabledText", Constant.BG_DARKER_COLOR);
    UIManager.put("CheckBox.focus", Constant.TEXT_COLOR);
    UIManager.put("CheckBox.foreground", Constant.TEXT_COLOR);
    UIManager.put("CheckBox.highlight", Constant.BG_DARKER_BLUE);
    UIManager.put("CheckBox.interiorBackground", Constant.BG_COLOR);
    UIManager.put("CheckBox.light", Constant.BG_BLUE);
    UIManager.put("Checkbox.select", Constant.BG_DARKER_BLUE);
    UIManager.put("CheckBox.shadow", Constant.BG_BLUE);

    // JSpinner
    UIManager.put("Spinner.background", Constant.BG_COLOR);
    UIManager.put("Spinner.border", new MatteBorder(1, 1, 1, 1, Constant.BG_DARKER_BLUE));
    UIManager.put("Spinner.foreground", Constant.TEXT_COLOR);

    // JPaswordField
    UIManager.put("PasswordField.background", Constant.BG_COLOR);
    UIManager.put("PasswordField.border", new MatteBorder(1, 1, 1, 1, Constant.BG_DARKER_BLUE));
    UIManager.put("PasswordField.caretForeground", Constant.TEXT_COLOR);
    UIManager.put("PasswordField.disabledBackground", Constant.BG_DARKER_COLOR);
    UIManager.put("PasswordField.foreground", Constant.TEXT_COLOR);
    UIManager.put("PasswordField.inactiveBackground", Constant.BG_COLOR);
    UIManager.put("PasswordField.inactiveForeground", Constant.BG_DARKER_COLOR);
    UIManager.put("PasswordField.selectionBackground", Constant.BG_BLUE);
    UIManager.put("PasswordField.selectionForeground", Constant.TEXT_COLOR);

    // JEditorPane
    UIManager.put("EditorPane.background", Constant.BG_COLOR);
    UIManager.put("EditorPane.caretForeground", Constant.TEXT_COLOR);
    UIManager.put("EditorPane.foreground", Constant.TEXT_COLOR);
    UIManager.put("EditorPane.inactiveForeground", Constant.BG_DARKER_COLOR);
    UIManager.put("EditorPane.selectionBackground", Constant.BG_BLUE);
    UIManager.put("EditorPane.selectionForeground", Constant.TEXT_COLOR);

    // JSlider
    UIManager.put("Slider.altTrackColo", Constant.BG_COLOR);
    UIManager.put("Slider.background", Constant.BG_COLOR);
    UIManager.put("Slider.border", null);
    UIManager.put("Slider.darkShadow", Constant.BG_DARKER_BLUE);
    UIManager.put("Slider.focus", Constant.TEXT_COLOR);
    UIManager.put("Slider.foreground", Constant.TEXT_COLOR);
    UIManager.put("Slider.highlight", Constant.BG_DARKER_BLUE);
    UIManager.put("Slider.shadow", Constant.BG_BLUE);
    UIManager.put("Slider.thumb", Constant.BG_DARKER_BLUE);
    UIManager.put("Slider.tickColor", Constant.TEXT_COLOR);
    UIManager.put("Slider.trackBorder", null);

    // JTabbedPane
    UIManager.put("TabbedPane.background", Constant.BG_COLOR);
    UIManager.put("TabbedPane.borderHightlightColor", Constant.BG_DARKER_COLOR);
    UIManager.put("TabbedPane.contentAreaColor", Constant.BG_COLOR);
    UIManager.put("TabbedPane.darkShadow", Constant.BG_DARKER_BLUE);
    UIManager.put("TabbedPane.focus", Constant.TEXT_COLOR);
    UIManager.put("TabbedPane.foreground", Constant.TEXT_COLOR);
    UIManager.put("TabbedPane.highlight", Constant.BG_DARKER_BLUE);
    UIManager.put("TabbedPane.light", Constant.BG_BLUE);
    UIManager.put("TabbedPane.selectedForeground", Constant.TEXT_COLOR);
    UIManager.put("TabbedPane.shadow", Constant.BG_BLUE);

    // JColorChooser
    UIManager.put("ColorChooser.background", Constant.BG_COLOR);
    UIManager.put("ColorChooser.foreground", Constant.TEXT_COLOR);
    //ColorChooser.panels AbstractColorChooserPanel[ ]

    // JFormattedTextField
    UIManager.put("FormattedTextField.background", Constant.BG_COLOR);
    UIManager.put("FormattedTextField.border", new MatteBorder(1, 1, 1, 1, Constant.BG_DARKER_BLUE));
    UIManager.put("FormattedTextField.caretForeground", Constant.TEXT_COLOR);
    UIManager.put("FormattedTextField.foreground	Color", Constant.TEXT_COLOR);
    UIManager.put("FormattedTextField.inactiveBackground", Constant.BG_COLOR);
    UIManager.put("FormattedTextField.inactiveForeground", Constant.BG_DARKER_COLOR);
    UIManager.put("FormattedTextField.selectionBackground", Constant.BG_BLUE);
    UIManager.put("FormattedTextField.selectionForeground", Constant.TEXT_COLOR);

    // JPanel
    UIManager.put("Panel.background", Constant.BG_COLOR);
  }

  // Mouse listener
  MouseListener mouseListener = new MouseListener() {
    @Override
    public void mouseClicked(MouseEvent e) {
      if(e.getSource() instanceof TrayIcon) {
        if(SwingUtilities.isLeftMouseButton(e)) {
          showHideApplication();
        }
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

  // Property change listener
  PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
    @Override
    public void propertyChange(PropertyChangeEvent e) {
      if(e.getSource() instanceof MonthDialog) {
        switch (e.getPropertyName()) {
          case Constant.DAY_CHANGE:
            selectedDate = new DateTime(e.getNewValue());
            updateActivityUI();
            break;
          case Constant.MONTH_CHANGE:
          case Constant.YEAR_CHANGE:
            selectedDate = new DateTime(e.getNewValue());
            updateActivityUI();
            break;
        }
      }
      else if(e.getSource() instanceof ActivityPane) {
        switch (e.getPropertyName()) {
          case Constant.MODIFY_ACTIVITY:
            if(e.getNewValue() instanceof Activity) {
              showActivityDialog((Activity)e.getNewValue());
            }
            break;
          case Constant.SELECTION_CHANGE:
            String s = (String)e.getNewValue();
            selectedDate = new DateTime(Integer.parseInt(s.split("-")[2]), Integer.parseInt(s.split("-")[3]), Integer.parseInt(s.split("-")[4]), 0, 0);
            updateActivityUI();
            break;
        }
      }
    }
  };

  // Application header
  class ActivityHeader extends JPanel {
    private JLabel headerLabel;
    private JLabel closeButton;
    private JLabel minimizeButton;
    private JLabel hideButton;

    ActivityHeader() {
      initHeaderComponents();
    }

    private void initHeaderComponents() {
      headerLabel    = new JLabel();
      closeButton    = new JLabel();
      minimizeButton = new JLabel();
      hideButton     = new JLabel();
      Dimension iconSize = new Dimension(40, 40);

      setOpaque(false);
      
      headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD).deriveFont(22f));
      headerLabel.setText(Constant.APP_DISPLAY_NAME);
      headerLabel.setVerticalAlignment(JLabel.CENTER);
      headerLabel.setPreferredSize(new Dimension(headerLabel.getSize().width, 40));

      closeButton.setIcon(new ImageIcon(cl.getResource(Constant.CLOSE_ICON)));
      closeButton.setHorizontalAlignment(JButton.CENTER);
      closeButton.setVerticalAlignment(JButton.CENTER);
      closeButton.setMinimumSize(iconSize);
      closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
      closeButton.setToolTipText("close appliaction");
      closeButton.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          exitApplication();
        }
      });

      minimizeButton.setIcon(new ImageIcon(cl.getResource(Constant.MINIMIZE_ICON)));
      minimizeButton.setHorizontalAlignment(JButton.CENTER);
      minimizeButton.setVerticalAlignment(JButton.CENTER);
      minimizeButton.setMinimumSize(iconSize);
      minimizeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
      minimizeButton.setToolTipText("minimize appliaction");
      minimizeButton.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          minimizeApplication();
        }
      });

      hideButton.setIcon(new ImageIcon(cl.getResource(Constant.HIDE_ICON)));
      hideButton.setHorizontalAlignment(JButton.CENTER);
      hideButton.setVerticalAlignment(JButton.CENTER);
      hideButton.setMinimumSize(iconSize);
      hideButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
      hideButton.setToolTipText("hide appliaction");
      hideButton.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          showHideApplication();
        }
      });

      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
          .addGap(10)
          .addComponent(headerLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(hideButton)
          .addGap(10)
          .addComponent(minimizeButton)
          .addGap(10)
          .addComponent(closeButton)
          .addGap(10)
        )
      );
      layout.setVerticalGroup(
        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
          .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(headerLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(hideButton)
            .addComponent(minimizeButton)
            .addComponent(closeButton)
          )
        )
      );
    }

    @Override
    public void paint(Graphics g) {
      int w = getWidth();
      int h = getHeight();
      Graphics2D g2d = (Graphics2D) g;
      GradientPaint p = new GradientPaint(0, 0, Constant.BG_DARKER_COLOR, 0, h, Constant.BG_COLOR);
      g2d.setPaint(p);
      g2d.fillRect(0, 0, w, h);
      super.paint(g2d);
    }
  }

  // Application menu
  class ActivityMenu extends JPanel {
    private JLabel  timeLabel;
    private JLabelX todayLabel;
    private JLabel  prevButton;
    private JLabel  nextButton;
    private JLabel  settingsButton;
    private JLabel  summaryButton;
    private JLabelX selectedDateLabel;
    private JLabel  addButton;

    ActivityMenu() {
      createMenu();
      clock();
    }

    private void createMenu() {
      timeLabel          = new JLabel();
      todayLabel         = new JLabelX(Constant.WHITE);
      prevButton         = new JLabel();
      nextButton         = new JLabel();
      settingsButton     = new JLabel();
      summaryButton      = new JLabel();
      selectedDateLabel  = new JLabelX(Constant.WHITE);
      addButton          = new JLabel();
      Dimension iconSize = new Dimension(40, 40);

      setOpaque(false);
      setBorder(new EmptyBorder(5, 5, 5, 5));

      timeLabel.setFont(timeLabel.getFont().deriveFont(Font.BOLD).deriveFont(34f));
      timeLabel.setVerticalAlignment(JButton.CENTER);

      todayLabel.setFont(todayLabel.getFont().deriveFont(Font.BOLD).deriveFont(14f));
      todayLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
      todayLabel.setVerticalAlignment(JButton.CENTER);
      todayLabel.setToolTipText("today");
      todayLabel.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          gotoToday();
        }
      });

      prevButton.setHorizontalAlignment(JButton.CENTER);
      prevButton.setIcon(new ImageIcon(cl.getResource(Constant.PREV_ICON)));
      prevButton.setMinimumSize(iconSize);
      prevButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
      prevButton.setToolTipText("previous day");
      prevButton.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          prevDay();
        }
      });

      nextButton.setHorizontalAlignment(JButton.CENTER);
      nextButton.setIcon(new ImageIcon(cl.getResource(Constant.NEXT_ICON)));
      nextButton.setMinimumSize(iconSize);
      nextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
      nextButton.setToolTipText("next day");
      nextButton.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          nextDay();
        }
      });

      settingsButton.setHorizontalAlignment(JButton.CENTER);
      settingsButton.setIcon(new ImageIcon(cl.getResource(Constant.SETTINGS_ICON)));
      settingsButton.setMinimumSize(iconSize);
      settingsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
      settingsButton.setToolTipText("settings");
      settingsButton.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          showSettingsDialog();
        }
      });

      addButton.setHorizontalAlignment(JButton.CENTER);
      addButton.setIcon(new ImageIcon(cl.getResource(Constant.ADD_ICON)));
      addButton.setMinimumSize(iconSize);
      addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
      addButton.setToolTipText("add new activity");
      addButton.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          showActivityDialog(null);
        }
      });

      summaryButton.setHorizontalAlignment(JButton.CENTER);
      summaryButton.setIcon(new ImageIcon(cl.getResource(Constant.SUMMARY_ICON)));
      summaryButton.setMinimumSize(iconSize);
      summaryButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
      summaryButton.setToolTipText("summary");
      summaryButton.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          showSummaryDialog();
        }
      });

      selectedDateLabel.setFont(selectedDateLabel.getFont().deriveFont(Font.BOLD).deriveFont(22f));
      selectedDateLabel.setHorizontalAlignment(JButton.CENTER);
      selectedDateLabel.setVerticalAlignment(JButton.CENTER);
      selectedDateLabel.setText(String.format("%d.%02d.%02d", selectedDate.getYear(), selectedDate.getMonthOfYear(), selectedDate.getDayOfMonth()));
      selectedDateLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
      selectedDateLabel.setToolTipText("calendar");
      selectedDateLabel.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          showMonthDialog();
        }
      });

      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
          .addGap(10)
          .addComponent(timeLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGap(10)
          .addComponent(todayLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGap(50)
          .addComponent(prevButton)
          .addGap(10)
          .addComponent(selectedDateLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGap(10)
          .addComponent(nextButton)
          .addGap(50)
          .addComponent(addButton)
          .addGap(50)
          .addComponent(summaryButton)
          .addGap(50)
          .addComponent(settingsButton)
        )
      );
      layout.setVerticalGroup(
        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(timeLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(todayLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(prevButton)
        .addComponent(selectedDateLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(nextButton)
        .addComponent(addButton)
        .addComponent(summaryButton)
        .addComponent(settingsButton)
      );
    }

    private void clock() {
      new Thread() {
        @Override
        public void run() {
          while(true) {
            now = new DateTime();
            int year = now.getYear();
            int month = now.getMonthOfYear();
            int dayofmonth = now.getDayOfMonth();
            String day = now.dayOfWeek().getAsText();
            int hour = now.getHourOfDay();
            int minute = now.getMinuteOfHour();
            int second = now.getSecondOfMinute();
            timeLabel.setText(String.format("%02d:%02d:%02d", hour, minute, second));
            timeLabel.setPreferredSize(new Dimension(timeLabel.getPreferredSize().width, 40));
            todayLabel.setText(String.format("<html>%d.%02d.%02d.<br>%s</html>", year, month, dayofmonth, day));
            todayLabel.setPreferredSize(new Dimension(todayLabel.getPreferredSize().width, 40));
            selectedDateLabel.setText(String.format("%d.%02d.%02d", selectedDate.getYear(), selectedDate.getMonthOfYear(), selectedDate.getDayOfMonth()));
            selectedDateLabel.setPreferredSize(new Dimension(selectedDateLabel.getPreferredSize().width, 40));
          }
        }
      }.start();
    }

    @Override
    public void paint(Graphics g) {
      int w = getWidth();
      int h = getHeight();
      Graphics2D g2d = (Graphics2D) g;
      GradientPaint p = new GradientPaint(0, 0, Constant.BG_DARKER_BLUE, 0, h, Constant.BG_BLUE);
      g2d.setPaint(p);
      g2d.fillRect(0, 0, w, h);
      super.paint(g2d);
    }
  }
}
