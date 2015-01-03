/**
 * Activity Calendar
 * 
 * @author Szalay Roland
 * 
 */
package rosza.activitycalendar;

//<editor-fold defaultstate="collapsed" desc=" Import ">
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
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
import java.util.Collections;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import org.joda.time.DateTime;
//</editor-fold>

public class ActivityCalendar extends JFrame {
  // UI variables declaration
  private JLayeredPane   baseLayer;
  private JPanel         topLayer;
  private JPanel         bottomLayer;
  // // customized components
  private ActivityHeader activityHeader;
  private ActivityMenu   activityMenu;
  private AddActivity    addActivity;
  private ReportPanel    reportPanel;
  private SettingsPanel  settingsPanel;
  private ActivityPane   activityPane;
  private MonthCalendar  monthCalendar;
  // // end of customized components
  // End of UI variables declaration

  // System tray variables declaration
  private SystemTray systemTray;
  private TrayIcon trayIcon;
  private final ClassLoader cl = this.getClass().getClassLoader();
  // End system tray variables declaration

  // Mouse action variables declaration
  private int mouseX;
  private int mouseY;
  // End of mouse action variables declaration

  //Calendar variables declaration
  public static DateTime now                = new DateTime();         // get new Joda-Time for actual date
  public static int      currentYear        = now.getYear();          // current year
  public static int      currentMonth       = now.getMonthOfYear();   // current month
  public static int      currentDayOfMonth  = now.getDayOfMonth();    // current day of the month
  public static DateTime selectedDate       = new DateTime(now);      // get new Joda-TIme for selected date
  // End of calendar action variables declaration

  //<editor-fold defaultstate="collapsed" desc=" Creates new form ActivityCalendar ">
  public ActivityCalendar() {
    //setUIFont();
    initUIComponents();
    initSystemTray();
    XMLUtil.getCategories();
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Initialize UI components ">
  @SuppressWarnings("unchecked")
  private void initUIComponents() {
    topLayer       = new JPanel();
    bottomLayer    = new JPanel();
    baseLayer      = new JLayeredPane();
    activityHeader = new ActivityHeader();
    activityMenu   = new ActivityMenu();
    activityPane   = new ActivityPane(Constant.WEEK_VIEW, selectedDate.getYear(), selectedDate.getMonthOfYear(), selectedDate.getDayOfMonth());

    activityPane.addPropertyChangeListener(propertyChangeListener);

    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setTitle(Constant.APP_DISPLAY_NAME);
    setUndecorated(true);
    setResizable(false);
    getContentPane().setBackground(new Color(0, 0, 0));

    activityHeader.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
        headerPanelMouseDragged(e);
      }
    });
    activityHeader.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        headerPanelMousePressed(e);
      }
    });

    add(baseLayer);

    topLayer.setBackground(Constant.BG_COLOR);
    bottomLayer.setBackground(Constant.BG_COLOR);
    topLayer.setLayout(new BoxLayout(topLayer, BoxLayout.Y_AXIS));

    baseLayer.add(topLayer);
    baseLayer.add(bottomLayer);
    topLayer.add(activityHeader);
    topLayer.add(activityMenu);
    bottomLayer.add(activityPane);
    topLayer.setBounds(2, 2, activityPane.getPreferredSize().width, topLayer.getPreferredSize().height);
    bottomLayer.setBounds(2, 2 + topLayer.getPreferredSize().height, activityPane.getPreferredSize().width, bottomLayer.getPreferredSize().height);
    activityPane.setBounds(2, 2 + topLayer.getPreferredSize().height + bottomLayer.getPreferredSize().height, activityPane.getPreferredSize().width, activityPane.getPreferredSize().height);

    setSize(activityPane.getPreferredSize().width + 4, topLayer.getPreferredSize().height + bottomLayer.getPreferredSize().height + 4);
    setLocationRelativeTo(null);
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" System tray ">
  private void initSystemTray() {
    if(SystemTray.isSupported()) {
      PopupMenu popup = new PopupMenu();
      MenuItem exitMenuItem = new MenuItem("Exit " + Constant.APP_DISPLAY_NAME);
      MenuItem showHideMenuItem = new MenuItem("Show / Hide");
      exitMenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          exitMenuActionPerformed(e);
        }
      });
      showHideMenuItem.setActionCommand(Constant.ACTIVITY_SHOW_HIDE);
      showHideMenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          showHideMenuActionPerformed(e);
        }
      });
      popup.add(showHideMenuItem);
      popup.add(exitMenuItem);

      systemTray = SystemTray.getSystemTray();
      Dimension iconSize = systemTray.getTrayIconSize();
      int iconHeight = iconSize.height;
      Image image = Toolkit.getDefaultToolkit().getImage(cl.getResource(Constant.TRAY_ICON));
      switch(iconHeight) {
        case 32:
          image = Toolkit.getDefaultToolkit().getImage(cl.getResource(Constant.TRAY_ICON));
          break;
        case 48:
          image = Toolkit.getDefaultToolkit().getImage(cl.getResource(Constant.TRAY_ICON));
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
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Show/hide month calendar ">
  private void showHideMonthCalendar() {
    if(settingsPanel != null || reportPanel != null || addActivity != null) {
      return;
    }
    if(monthCalendar == null) {
      monthCalendar = new MonthCalendar(currentYear, currentMonth, currentDayOfMonth);
      monthCalendar.setBounds(baseLayer.getSize().width / 2 - monthCalendar.getPreferredSize().width / 2, baseLayer.getSize().height / 2 - monthCalendar.getPreferredSize().height / 2, monthCalendar.getPreferredSize().width, monthCalendar.getPreferredSize().height);
      monthCalendar.addPropertyChangeListener(propertyChangeListener);
      monthCalendar.addMouseListener(mouseListener);
      baseLayer.add(monthCalendar);
      baseLayer.setLayer(monthCalendar, JLayeredPane.MODAL_LAYER, 0);
    }
    else {
      baseLayer.remove(baseLayer.getIndexOf(monthCalendar));
      baseLayer.repaint();
      monthCalendar.removePropertyChangeListener(propertyChangeListener);
      monthCalendar.removeMouseListener(mouseListener);
      monthCalendar = null;
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Show/hide settings ">
  private void showHideSettings() {
    if(monthCalendar != null || reportPanel != null || addActivity != null) {
      return;
    }
    if(settingsPanel == null) {
      settingsPanel = new SettingsPanel();
      settingsPanel.setBounds(baseLayer.getSize().width / 2 - settingsPanel.getPreferredSize().width / 2, baseLayer.getSize().height / 2 - settingsPanel.getPreferredSize().height / 2, settingsPanel.getPreferredSize().width, settingsPanel.getPreferredSize().height);
      settingsPanel.addPropertyChangeListener(propertyChangeListener);
      settingsPanel.addMouseListener(mouseListener);
      baseLayer.add(settingsPanel, JLayeredPane.DRAG_LAYER, 0);
    }
    else {
      baseLayer.remove(baseLayer.getIndexOf(settingsPanel));
      baseLayer.revalidate();
      baseLayer.repaint();
      settingsPanel.removePropertyChangeListener(propertyChangeListener);
      settingsPanel.removeMouseListener(mouseListener);
      settingsPanel = null;
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Show/hide report ">
  private void showHideReport() {
    if(settingsPanel != null || monthCalendar != null || addActivity != null) {
      return;
    }
    if(reportPanel == null) {
      reportPanel = new ReportPanel(selectedDate.getYear(), selectedDate.getMonthOfYear(), selectedDate.getDayOfMonth());
      reportPanel.setBounds(baseLayer.getSize().width / 2 - reportPanel.getPreferredSize().width / 2, baseLayer.getSize().height / 2 - reportPanel.getPreferredSize().height / 2, reportPanel.getPreferredSize().width, reportPanel.getPreferredSize().height);
      reportPanel.addPropertyChangeListener(propertyChangeListener);
      reportPanel.addMouseListener(mouseListener);
      baseLayer.add(reportPanel);
      baseLayer.setLayer(reportPanel, JLayeredPane.MODAL_LAYER, 0);
    }
    else {
      baseLayer.remove(baseLayer.getIndexOf(reportPanel));
      baseLayer.repaint();
      reportPanel.removePropertyChangeListener(propertyChangeListener);
      reportPanel.removeMouseListener(mouseListener);
      reportPanel = null;
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Show/hide add activity ">
  private void showHideAddActivity(Activity a) {
    if(settingsPanel != null || reportPanel != null || monthCalendar != null) {
      return;
    }
    if(addActivity == null) {
      if(a == null) {
        addActivity = new AddActivity();
      }
      else {
        addActivity = new AddActivity(a);
      }
      addActivity.setBounds(baseLayer.getSize().width / 2 - addActivity.getPreferredSize().width / 2, baseLayer.getSize().height / 2 - addActivity.getPreferredSize().height / 2, addActivity.getPreferredSize().width, addActivity.getPreferredSize().height);
      addActivity.addPropertyChangeListener(propertyChangeListener);
      addActivity.addMouseListener(mouseListener);
      baseLayer.add(addActivity);
      baseLayer.setLayer(addActivity, JLayeredPane.MODAL_LAYER, 0);
    }
    else {
      baseLayer.remove(baseLayer.getIndexOf(addActivity));
      baseLayer.repaint();
      addActivity.removePropertyChangeListener(propertyChangeListener);
      addActivity.removeMouseListener(mouseListener);
      addActivity = null;
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" System tray menu actions ">
  private void exitMenuActionPerformed(ActionEvent e) {
    // Close application
    systemTray.remove(trayIcon);
    System.exit(0);
  }

  private void showHideMenuActionPerformed(ActionEvent e) {
    // Show or hide Activity Calendar window
    if(this.isShowing()) {
      this.setVisible(false);
    }
    else {
      this.setVisible(true);
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Update Activity UI ">
  /**
   * Update Activity UI with new date parameters.
   */
  private void updateActivityUI() {
    bottomLayer.remove(activityPane);
    activityPane = new ActivityPane(Constant.WEEK_VIEW, selectedDate.getYear(), selectedDate.getMonthOfYear(), selectedDate.getDayOfMonth());
    activityPane.addPropertyChangeListener(propertyChangeListener);
    bottomLayer.add(activityPane);
    if(settingsPanel != null) {
      baseLayer.setLayer(settingsPanel, JLayeredPane.MODAL_LAYER, 0);
    }
    else if(reportPanel != null) {
      baseLayer.setLayer(reportPanel, JLayeredPane.MODAL_LAYER, 0);
    }
    else if(monthCalendar != null) {
      updateMonthCalendarUI();
    }
    else if(addActivity != null) {
      baseLayer.setLayer(addActivity, JLayeredPane.MODAL_LAYER, 0);
    }
    revalidate();
    repaint();
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Update Month Calendar UI ">
  /**
   * Update Activity UI with new date parameters.
   */
  private void updateMonthCalendarUI() {
    if(monthCalendar == null) {
      return;
    }
    baseLayer.remove(monthCalendar);
    monthCalendar = new MonthCalendar(selectedDate.getYear(), selectedDate.getMonthOfYear(), selectedDate.getDayOfMonth());
    monthCalendar.setBounds(baseLayer.getSize().width / 2 - monthCalendar.getPreferredSize().width / 2, baseLayer.getSize().height / 2 - monthCalendar.getPreferredSize().height / 2, monthCalendar.getPreferredSize().width, monthCalendar.getPreferredSize().height);
    monthCalendar.addPropertyChangeListener(propertyChangeListener);
    monthCalendar.addMouseListener(mouseListener);
    baseLayer.add(monthCalendar);
    baseLayer.setLayer(monthCalendar, JLayeredPane.MODAL_LAYER, 0);
    revalidate();
    repaint();
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Button events ">
  private void closeButtonMouseClicked(MouseEvent e) {
    systemTray.remove(trayIcon);
    System.exit(0);
  }

  private void minimizeButtonMouseClicked(MouseEvent e) {
    setState(ActivityCalendar.ICONIFIED);
  }

  private void hideButtonMouseClicked(MouseEvent e) {
    // Show or hide Activity Calendar window
    if(this.isShowing()) {
      this.setVisible(false);
    }
    else {
      this.setVisible(true);
    }
  }

  private void todayButtonMouseClicked(MouseEvent e) {
    selectedDate = now;
    updateActivityUI();
  }

  private void selectedDateMouseClicked(MouseEvent e) {
    showHideMonthCalendar();
  }

  private void addButtonMouseClicked(MouseEvent e) {
    showHideAddActivity(null);
  }

  private void settingsButtonMouseClicked(MouseEvent e) {
    showHideSettings();
  }

  private void reportButtonMouseClicked(MouseEvent e) {
    showHideReport();
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Mouse events ">
  private void headerPanelMouseDragged(MouseEvent e) {
    int x = e.getXOnScreen();
    int y = e.getYOnScreen();

    setLocation(x - mouseX, y - mouseY);
  }

  private void headerPanelMousePressed(MouseEvent e) {
    mouseX = e.getX();
    mouseY = e.getY();
  }

  private void prevButtonMouseClicked(MouseEvent e) {
    selectedDate = selectedDate.minusDays(1);
    updateActivityUI();
  }

  private void nextButtonMouseClicked(MouseEvent e) {
    selectedDate = selectedDate.plusDays(1);
    updateActivityUI();
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Main method ">
  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
     * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
     */
    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        if ("Windows".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          //break;
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
    //</editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        new ActivityCalendar().setVisible(true);
      }
    });
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Mouse Listener ">
  MouseListener mouseListener = new MouseListener() {
    @Override
    public void mouseClicked(MouseEvent e) {
      if(e.getSource() instanceof TrayIcon) {
        if(SwingUtilities.isLeftMouseButton(e)) {
          hideButtonMouseClicked(e);
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
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Property change ">
  PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
    @Override
    public void propertyChange(PropertyChangeEvent e) {
      if(e.getSource() instanceof AddActivity) {
        if(e.getPropertyName().equals(Constant.CLOSE_PANE)) {
          if(e.getNewValue() != null) {
            updateActivityUI();
          }
          showHideAddActivity(null);
        }
      }
      else if(e.getSource() instanceof SettingsPanel) {
        if(e.getPropertyName().equals(Constant.CLOSE_PANE)) {
          showHideSettings();
        }
      }
      else if(e.getSource() instanceof ReportPanel) {
        if(e.getPropertyName().equals(Constant.CLOSE_PANE)) {
          showHideReport();
        }
      }
      else if(e.getSource() instanceof MonthCalendar) {
        if(e.getPropertyName().equals(Constant.CLOSE_PANE)) {
          showHideMonthCalendar();
        }
        switch (e.getPropertyName()) {
          case Constant.DAY_CHANGE:
            selectedDate = new DateTime(e.getNewValue());
            updateActivityUI();
            showHideMonthCalendar();
            break;
          case Constant.MONTH_CHANGE:
          case Constant.YEAR_CHANGE:
            selectedDate = new DateTime(e.getNewValue());
            updateMonthCalendarUI();
            break;
        }
      }
      else if(e.getSource() instanceof ActivityPane) {
        switch (e.getPropertyName()) {
          case Constant.MODIFY_ACTIVITY:
            showHideAddActivity((Activity)e.getNewValue());
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
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Change the UI font ">
  private void setUIFont() {
    UIDefaults defaults = UIManager.getDefaults( );
    List<Object> list = Collections.list(defaults.keys());
    for(Object key : list) {
      if(key != null) {
        if(key.toString().endsWith(".font")) {
          UIManager.put(key.toString(), Constant.FONT);
        }
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Activity header ">
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

      setOpaque(true);
      setBackground(Constant.BG_COLOR);
      
      headerLabel.setFont(headerLabel.getFont());
      headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD).deriveFont(22f));
      headerLabel.setText(Constant.APP_DISPLAY_NAME);
      headerLabel.setVerticalAlignment(JLabel.CENTER);
      headerLabel.setForeground(Constant.TEXT_COLOR);
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
          closeButtonMouseClicked(e);
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
          minimizeButtonMouseClicked(e);
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
          hideButtonMouseClicked(e);
        }
      });

      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
          .addGap(10)
          .addComponent(headerLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(hideButton)//, tempSize, tempSize, tempSize)
          .addGap(10)
          .addComponent(minimizeButton)//, tempSize, tempSize, tempSize)
          .addGap(10)
          .addComponent(closeButton)//, tempSize, tempSize, tempSize)
          .addGap(10)
        )
      );
      layout.setVerticalGroup(
        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
          .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(headerLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(hideButton)//, tempSize, tempSize, tempSize)
            .addComponent(minimizeButton)//, tempSize, tempSize, tempSize)
            .addComponent(closeButton)//, tempSize, tempSize, tempSize)
          )
        )
      );
    }
/*
    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      int w = getWidth();
      int h = getHeight();
      Graphics2D g2d = (Graphics2D)g;
      GradientPaint gp = new GradientPaint(0, 0, Color.white, 0, h, Color.white.darker());
      g2d.setPaint(gp);
      g2d.fillRect(0, 0, w, h);
    }
  */
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Activity menu ">
  class ActivityMenu extends JPanel {
    private JLabel timeLabel;
    private JLabel todayLabel;
    private JLabel prevButton;
    private JLabel nextButton;
    private JLabel settingsButton;
    private JLabel reportButton;
    private JLabel selectedDateLabel;
    private JLabel addButton;

    ActivityMenu() {
      initMenuComponents();
      clock();
    }

    private void initMenuComponents() {
      timeLabel          = new JLabel();
      todayLabel         = new JLabel();
      prevButton         = new JLabel();
      nextButton         = new JLabel();
      settingsButton     = new JLabel();
      reportButton       = new JLabel();
      selectedDateLabel  = new JLabel();
      addButton          = new JLabel();
      Dimension iconSize = new Dimension(40, 40);

      setOpaque(true);
      setBackground(Constant.BG_COLOR);

      timeLabel.setFont(timeLabel.getFont());
      timeLabel.setFont(timeLabel.getFont().deriveFont(Font.BOLD).deriveFont(34f));
      timeLabel.setForeground(Constant.TEXT_COLOR);
      timeLabel.setVerticalAlignment(JButton.CENTER);

      todayLabel.setFont(todayLabel.getFont());
      todayLabel.setFont(todayLabel.getFont().deriveFont(Font.BOLD).deriveFont(14f));
      todayLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
      todayLabel.setVerticalAlignment(JButton.CENTER);
      todayLabel.setToolTipText("today");
      todayLabel.setForeground(Constant.TEXT_COLOR);
      todayLabel.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          todayButtonMouseClicked(e);
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
          prevButtonMouseClicked(e);
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
          nextButtonMouseClicked(e);
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
          settingsButtonMouseClicked(e);
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
          addButtonMouseClicked(e);
        }
      });

      reportButton.setHorizontalAlignment(JButton.CENTER);
      reportButton.setIcon(new ImageIcon(cl.getResource(Constant.REPORT_ICON)));
      reportButton.setMinimumSize(iconSize);
      reportButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
      reportButton.setToolTipText("reports");
      reportButton.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          reportButtonMouseClicked(e);
        }
      });

      selectedDateLabel.setFont(selectedDateLabel.getFont());
      selectedDateLabel.setFont(selectedDateLabel.getFont().deriveFont(Font.BOLD).deriveFont(22f));
      selectedDateLabel.setHorizontalAlignment(JButton.CENTER);
      selectedDateLabel.setVerticalAlignment(JButton.CENTER);
      selectedDateLabel.setText(String.format("%d.%02d.%02d", selectedDate.getYear(), selectedDate.getMonthOfYear(), selectedDate.getDayOfMonth()));
      selectedDateLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
      selectedDateLabel.setForeground(Constant.TEXT_COLOR);
      selectedDateLabel.setToolTipText("calendar");
      selectedDateLabel.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          selectedDateMouseClicked(e);
        }
      });

      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
          .addGap(10)
          .addComponent(timeLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGap(10)
          .addComponent(todayLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGap(50)
          .addComponent(prevButton)
          .addGap(10)
          .addComponent(selectedDateLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGap(10)
          .addComponent(nextButton)
          .addGap(50)
          .addComponent(addButton)
          .addGap(50)
          .addComponent(reportButton)
          .addGap(50)
          .addComponent(settingsButton)
        )
      );
      layout.setVerticalGroup(
        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(timeLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(todayLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(prevButton)
        .addComponent(selectedDateLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(nextButton)
        .addComponent(addButton)
        .addComponent(reportButton)
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

/*    @Override
    public void paintComponent(Graphics g) {
      int w = getWidth();
      int h = getHeight();
      Graphics2D g2d = (Graphics2D) g;
      GradientPaint p = new GradientPaint(0, 0, Color.white, 0, h, Color.white.darker());

      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setPaint(p);
      g2d.fillRect(0, 0, w, h);
    }*/
  }
  //</editor-fold>
}
