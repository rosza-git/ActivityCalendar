/**
 * Activity Calendar
 * 
 * @author Szalay Roland
 * 
 * used 3rd-party libraries:
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

//<editor-fold defaultstate="collapsed" desc=" Import ">
import rosza.xcomponents.JLabelX;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import org.joda.time.DateTime;
import rosza.xcomponents.JComboBoxX;
//</editor-fold>

public class ActivityCalendar extends JFrame {
  //<editor-fold defaultstate="collapsed" desc=" Variables declaration ">
  // UI variables declaration
  private JLayeredPane   baseLayer;
  private JPanel         topLayer;
  private JPanel         bottomLayer;
  // // customized components
  private ActivityHeader activityHeader;
  private ActivityMenu   activityMenu;
  private AddActivity    addActivity;
  private SummaryPanel   summaryPanel;
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
  private int            currentHour        = now.getHourOfDay();
  private int            currentMinute      = now.getMinuteOfHour();
  // End of calendar action variables declaration

  private JPanel glass;
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Create new form ActivityCalendar ">
  public ActivityCalendar() {
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
    glass = new JPanel() {
      @Override
      public void paintComponent(Graphics g) {
        g.setColor(new Color(0, 0, 0, 0.2f));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
      }
    };
    setGlassPane(glass);
    //glass.setBorder(new EmptyBorder(100, 20, 20, 20));
    glass.setOpaque(false);
    glass.setVisible(false);
    
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
    if(settingsPanel != null || summaryPanel != null || addActivity != null) {
      return;
    }
    if(monthCalendar == null) {
      monthCalendar = new MonthCalendar(selectedDate.getYear(), selectedDate.getMonthOfYear(), selectedDate.getDayOfMonth());
      monthCalendar.addPropertyChangeListener(propertyChangeListener);
      monthCalendar.addMouseListener(mouseListener);
      glass.add(monthCalendar);
      glass.setVisible(true);  
    }
    else {
      glass.setVisible(false);
      glass.remove(monthCalendar);
      monthCalendar.removePropertyChangeListener(propertyChangeListener);
      monthCalendar.removeMouseListener(mouseListener);
      monthCalendar = null;
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Show/hide settings ">
  private void showHideSettings() {
    if(monthCalendar != null || summaryPanel != null || addActivity != null) {
      return;
    }
    if(settingsPanel == null) {
      settingsPanel = new SettingsPanel();
      settingsPanel.addPropertyChangeListener(propertyChangeListener);
      settingsPanel.addMouseListener(mouseListener);
      glass.add(settingsPanel);
      glass.setVisible(true);  
    }
    else {
      glass.setVisible(false);
      glass.remove(settingsPanel);
      settingsPanel.removePropertyChangeListener(propertyChangeListener);
      settingsPanel.removeMouseListener(mouseListener);
      settingsPanel = null;
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Show/hide summary ">
  private void showHideSummary() {
    if(settingsPanel != null || monthCalendar != null || addActivity != null) {
      return;
    }
    if(summaryPanel == null) {
      summaryPanel = new SummaryPanel(selectedDate.getYear(), selectedDate.getMonthOfYear(), selectedDate.getDayOfMonth());
      summaryPanel.addPropertyChangeListener(propertyChangeListener);
      summaryPanel.addMouseListener(mouseListener);
      glass.add(summaryPanel);
      glass.setVisible(true);  
    }
    else {
      glass.setVisible(false);
      glass.remove(summaryPanel);
      summaryPanel.removePropertyChangeListener(propertyChangeListener);
      summaryPanel.removeMouseListener(mouseListener);
      summaryPanel = null;
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Show/hide add activity ">
  private void showHideAddActivity(Activity a) {
    if(settingsPanel != null || summaryPanel != null || monthCalendar != null) {
      return;
    }
    if(addActivity == null) {
      if(a == null) {
        addActivity = new AddActivity();
      }
      else {
        addActivity = new AddActivity(a);
      }
      addActivity.addPropertyChangeListener(propertyChangeListener);
      addActivity.addMouseListener(mouseListener);
      glass.add(addActivity);
      glass.setVisible(true);  
    }
    else {
      glass.setVisible(false);
      glass.remove(addActivity);
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
    activityPane.stopRunning();
    activityPane = new ActivityPane(Constant.WEEK_VIEW, selectedDate.getYear(), selectedDate.getMonthOfYear(), selectedDate.getDayOfMonth());
    activityPane.addPropertyChangeListener(propertyChangeListener);
    bottomLayer.add(activityPane);
    if(settingsPanel != null) {
      baseLayer.setLayer(settingsPanel, JLayeredPane.MODAL_LAYER, 0);
    }
    else if(summaryPanel != null) {
      baseLayer.setLayer(summaryPanel, JLayeredPane.MODAL_LAYER, 0);
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
    glass.remove(monthCalendar);
    monthCalendar = new MonthCalendar(selectedDate.getYear(), selectedDate.getMonthOfYear(), selectedDate.getDayOfMonth());
    //monthCalendar.setBounds(baseLayer.getSize().width / 2 - monthCalendar.getPreferredSize().width / 2, baseLayer.getSize().height / 2 - monthCalendar.getPreferredSize().height / 2, monthCalendar.getPreferredSize().width, monthCalendar.getPreferredSize().height);
    monthCalendar.addPropertyChangeListener(propertyChangeListener);
    monthCalendar.addMouseListener(mouseListener);
    glass.add(monthCalendar);
    //baseLayer.setLayer(monthCalendar, JLayeredPane.MODAL_LAYER, 0);
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

  private void summaryButtonMouseClicked(MouseEvent e) {
    showHideSummary();
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
    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
     * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
     */
    try {
      for(javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        if("Windows".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
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
      else if(e.getSource() instanceof SummaryPanel) {
        if(e.getPropertyName().equals(Constant.CLOSE_PANE)) {
          showHideSummary();
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

      setOpaque(false);
      
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
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Activity menu ">
  class ActivityMenu extends JPanel {
    private JLabel timeLabel;
    private JLabelX todayLabel;
    private JLabel prevButton;
    private JLabel nextButton;
    private JLabel settingsButton;
    private JLabel summaryButton;
    private JLabelX selectedDateLabel;
    private JLabel addButton;

    ActivityMenu() {
      initMenuComponents();
      clock();
    }

    private void initMenuComponents() {
      timeLabel          = new JLabel();
      todayLabel         = new JLabelX(Constant.WHITE);
      prevButton         = new JLabel();
      nextButton         = new JLabel();
      settingsButton     = new JLabel();
      summaryButton       = new JLabel();
      selectedDateLabel  = new JLabelX(Constant.WHITE);
      addButton          = new JLabel();
      Dimension iconSize = new Dimension(40, 40);

      setOpaque(false);
      this.setBorder(new EmptyBorder(5, 5, 5, 5));

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

      summaryButton.setHorizontalAlignment(JButton.CENTER);
      summaryButton.setIcon(new ImageIcon(cl.getResource(Constant.SUMMARY_ICON)));
      summaryButton.setMinimumSize(iconSize);
      summaryButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
      summaryButton.setToolTipText("summary");
      summaryButton.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          summaryButtonMouseClicked(e);
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
          .addComponent(summaryButton)
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
  //</editor-fold>
}
