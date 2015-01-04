/**
 * Add Activity.
 * 
 * @author Szalay Roland
 * 
 */
package rosza.activitycalendar;

//<editor-fold defaultstate="collapsed" desc=" Import ">
import rosza.xcomponents.JLabelX;
import rosza.xcomponents.JPanelX;
import rosza.xcomponents.JButtonX;
import rosza.xcomponents.JScrollBarX;
import rosza.xcomponents.JSpinnerX;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import org.joda.time.DateTime;
import org.joda.time.Interval;
//</editor-fold>

public class AddActivity extends JPanelX {
  //<editor-fold defaultstate="collapsed" desc=" Variables declaration ">
  private JLabelX      headerLabel;
  private JLabel       categoryLabel;
  private JLabel       commentLabel;
  private JTextField   commentTextField;
  private JLabel       startLabel;
  private JSpinner     startDateSpinner;
  private JSpinner     startTimeSpinner;
  private JLabel       endLabel;
  private JSpinner     endDateSpinner;
  private JSpinner     endTimeSpinner;
  private CategoryTree categoryTree;
  private JButtonX     addActivityButton;
  private JButtonX     modifyActivityButton;
  private JButtonX     removeActivityButton;
  private JButtonX     cancelButton;
  private Category     categoryTreeElements = XMLUtil.getCategories();
  private JScrollPane  categoryScrollPane;
  private Activity     activity;
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Create new Add Activity panel ">
  public AddActivity() {
    initComponents();
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Create new Add Activity panel with initial values ">
  public AddActivity(Activity a) {
    activity = a;
    initComponents();
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Initialize UI components ">
  @SuppressWarnings("unchecked")
  private void initComponents() {
    headerLabel          = new JLabelX(5, 0, 5, 0);
    addActivityButton    = new JButtonX("add");
    modifyActivityButton = new JButtonX("modify");
    removeActivityButton = new JButtonX("remove");
    cancelButton         = new JButtonX("cancel");
    commentTextField     = new JTextField();
    commentLabel         = new JLabel();
    startLabel           = new JLabel();
    startDateSpinner     = new JSpinner();
    startTimeSpinner     = new JSpinner();
    endLabel             = new JLabel();
    endDateSpinner       = new JSpinner();
    endTimeSpinner       = new JSpinner();
    categoryLabel        = new JLabel();
    categoryTree         = new CategoryTree(categoryTreeElements);
    categoryScrollPane   = new JScrollPane();

    headerLabel.setText("add activity");
    headerLabel.setHorizontalAlignment(JLabel.CENTER);
    headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD).deriveFont(18f));

    modifyActivityButton.setText("modify");
    modifyActivityButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        modifyActivityButtonActionPerformed(e);
      }
    });
    modifyActivityButton.setEnabled((activity != null));

    addActivityButton.setText("add");
    addActivityButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        addActivityButtonActionPerformed(e);
      }
    });
    addActivityButton.setEnabled((activity == null));

    removeActivityButton.setText("remove");
    removeActivityButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        removeActivityButtonActionPerformed(e);
      }
    });
    removeActivityButton.setEnabled((activity != null));

    cancelButton.setText("cancel");
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        cancelButtonActionPerformed(e);
      }
    });

    commentLabel.setFont(commentLabel.getFont().deriveFont(Font.BOLD));
    commentLabel.setLabelFor(commentTextField);
    commentLabel.setText("comment:");

    String ctf = "";
    if(activity != null) {
      ctf = activity.getComment();
    }
    commentTextField.setText(ctf);
    //commentTextField.setText(activity == null ? "" : ctf);

    startLabel.setFont(startLabel.getFont().deriveFont(Font.BOLD));
    startLabel.setLabelFor(startDateSpinner);
    startLabel.setText("start:");

    startDateSpinner.setModel(new SpinnerDateModel(activity == null ? new Date(ActivityCalendar.selectedDate.getMillis()) : new Date(activity.getStartDate().getMillis()), null, null, Calendar.DAY_OF_MONTH));
    startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy.MM.dd."));
    startDateSpinner.setUI(new JSpinnerX());
    startDateSpinner.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        startDateSpinnerStateChanged(e);
      }
    });

    startTimeSpinner.setModel(new SpinnerDateModel(activity == null ? new Date(ActivityCalendar.selectedDate.getMillis()) : new Date(activity.getStartDate().getMillis()), null, null, Calendar.MINUTE));
    startTimeSpinner.setEditor(new JSpinner.DateEditor(startTimeSpinner, "HH:mm"));
    startTimeSpinner.setUI(new JSpinnerX());
    startTimeSpinner.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        startTimeSpinnerStateChanged(e);
      }
    });

    endLabel.setFont(endLabel.getFont().deriveFont(Font.BOLD));
    endLabel.setLabelFor(endDateSpinner);
    endLabel.setText("end:");

    endDateSpinner.setModel(new SpinnerDateModel(activity == null ? new Date(ActivityCalendar.selectedDate.getMillis()) : new Date(activity.getEndDate().getMillis()), null, null, Calendar.DAY_OF_MONTH));
    endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy.MM.dd."));
    endDateSpinner.setUI(new JSpinnerX());
    endDateSpinner.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        endDateSpinnerStateChanged(e);
      }
    });

    endTimeSpinner.setModel(new SpinnerDateModel(activity == null ? new Date(ActivityCalendar.selectedDate.getMillis()) : new Date(activity.getEndDate().getMillis()), null, null, Calendar.MINUTE));
    endTimeSpinner.setEditor(new JSpinner.DateEditor(endTimeSpinner, "HH:mm"));
    endTimeSpinner.setUI(new JSpinnerX());
    endTimeSpinner.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        endTimeSpinnerStateChanged(e);
      }
    });


    categoryLabel.setFont(categoryLabel.getFont().deriveFont(Font.BOLD));
    categoryLabel.setText("category:");
    categoryLabel.setLabelFor(categoryTree);

    categoryTree.setExpandsSelectedPaths(true);
    categoryTree.addTreeSelectionListener(new TreeSelectionListener() {
      @Override
      public void valueChanged(TreeSelectionEvent e) {
        categoryTreeSelectionChanged(e);
      }
    });
    try {
      categoryTree.setSelectionPath(rebuildCategoryPath(activity.getCategory()));
    }
    catch(NullPointerException e) {
      categoryTree.setSelectionPath(categoryTree.getPathForRow(1));
    }

    categoryScrollPane.setViewportView(categoryTree);
    categoryScrollPane.setPreferredSize(new Dimension(categoryTree.getWidth(), (int)Constant.FONT_SIZE * 6));
    categoryScrollPane.getVerticalScrollBar().setUI(new JScrollBarX());
    categoryScrollPane.getHorizontalScrollBar().setUI(new JScrollBarX());

    GroupLayout layout = new GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addComponent(headerLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(removeActivityButton)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(modifyActivityButton)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(addActivityButton)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(cancelButton)
          )
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
              .addComponent(categoryLabel)
              .addComponent(startLabel)
              .addComponent(endLabel)
              .addComponent(commentLabel)
            )
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
              .addComponent(commentTextField)
              .addGroup(layout.createSequentialGroup()
                .addComponent(startDateSpinner, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(startTimeSpinner, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
              )
              .addGroup(layout.createSequentialGroup()
                .addComponent(endDateSpinner, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(endTimeSpinner, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
              )
              .addComponent(categoryScrollPane, GroupLayout.PREFERRED_SIZE, 215, GroupLayout.PREFERRED_SIZE)
            )
          )
        )
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      )
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(headerLabel)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(commentLabel)
          .addComponent(commentTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        )
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(categoryLabel)
          .addComponent(categoryScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        )
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(startDateSpinner, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
          .addComponent(startLabel)
          .addComponent(startTimeSpinner, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
        )
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(endDateSpinner, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
          .addComponent(endLabel)
          .addComponent(endTimeSpinner, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
        )
        .addGap(18, 18, 18)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(cancelButton)
          .addComponent(addActivityButton)
          .addComponent(modifyActivityButton)
          .addComponent(removeActivityButton)
        )
        .addGap(0, 14, Short.MAX_VALUE)
      )
    );
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Button events ">
  private void addActivityButtonActionPerformed(ActionEvent e) {
    String comment = commentTextField.getText();
    Category category = categoryTree.getSelected();
    DateTime sdate = new DateTime(startDateSpinner.getValue());
    DateTime edate = new DateTime(endDateSpinner.getValue());

    DateTime stime = new DateTime(startTimeSpinner.getValue());
    DateTime etime = new DateTime(endTimeSpinner.getValue());

    DateTime start = new DateTime(sdate.getYear(), sdate.getMonthOfYear(), sdate.getDayOfMonth(), stime.getHourOfDay(), stime.getMinuteOfHour());
    DateTime end = new DateTime(edate.getYear(), edate.getMonthOfYear(), edate.getDayOfMonth(), etime.getHourOfDay(), etime.getMinuteOfHour());

    Activity a = new Activity(comment, category, start, end);

    ArrayList<Activity> activityList = XMLUtil.getActivityByDate(sdate.getYear(), sdate.getMonthOfYear(), sdate.getDayOfMonth());
    if(activityList != null) {
      for(Activity act : activityList) {
        if(checkOverlaps(a.getStartDate(), a.getEndDate(), act.getStartDate(), act.getEndDate())) {
          JOptionPane.showMessageDialog(this, "Overlapping activities are not allowed!", "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
      }
    }

    XMLUtil.addActivity(a);
    firePropertyChange(Constant.CLOSE_PANE, null, this);
  }

  private void modifyActivityButtonActionPerformed(ActionEvent e) {
    String comment = commentTextField.getText();
    Category category = categoryTree.getSelected();
    DateTime sdate = new DateTime(startDateSpinner.getValue());
    DateTime edate = new DateTime(endDateSpinner.getValue());

    DateTime stime = new DateTime(startTimeSpinner.getValue());
    DateTime etime = new DateTime(endTimeSpinner.getValue());

    DateTime start = new DateTime(sdate.getYear(), sdate.getMonthOfYear(), sdate.getDayOfMonth(), stime.getHourOfDay(), stime.getMinuteOfHour());
    DateTime end = new DateTime(edate.getYear(), edate.getMonthOfYear(), edate.getDayOfMonth(), etime.getHourOfDay(), etime.getMinuteOfHour());

    Activity a = new Activity(activity.getID(), comment, category, start, end);

    ArrayList<Activity> activityList = XMLUtil.getActivityByDate(sdate.getYear(), sdate.getMonthOfYear(), sdate.getDayOfMonth());
    for(Activity act : activityList) {
      if(act.getID() == a.getID()) {
        continue;
      }
      if(checkOverlaps(a.getStartDate(), a.getEndDate(), act.getStartDate(), act.getEndDate())) {
        JOptionPane.showMessageDialog(this, "Overlapping activities are not allowed!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
    }

    XMLUtil.updateActivity(a);
    firePropertyChange(Constant.CLOSE_PANE, null, this);
  }

  private void removeActivityButtonActionPerformed(ActionEvent e) {
    int reply = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove '" + activity.getComment() + "'?", "Question", JOptionPane.YES_NO_OPTION);
    if(reply == JOptionPane.YES_OPTION) {
      XMLUtil.removeActivity(activity);
      firePropertyChange(Constant.CLOSE_PANE, null, this);
    }
  }

  private void cancelButtonActionPerformed(ActionEvent e) {
    firePropertyChange(Constant.CLOSE_PANE, null, null);
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Change events ">
  private void startDateSpinnerStateChanged(ChangeEvent e) {
    Date sd = new Date();
    sd.setYear(((Date)startDateSpinner.getValue()).getYear());
    sd.setMonth(((Date)startDateSpinner.getValue()).getMonth());
    sd.setDate(((Date)startDateSpinner.getValue()).getDate());
    Date ed = new Date();
    ed.setYear(((Date)endDateSpinner.getValue()).getYear());
    ed.setMonth(((Date)endDateSpinner.getValue()).getMonth());
    ed.setDate(((Date)endDateSpinner.getValue()).getDate());

/*    if(sd.after(ed)) {
      endDateSpinner.setValue(sd);
    }*/
    // different dates are not allowed
    if(sd.compareTo(ed) != 0) {
      endDateSpinner.setValue(sd);
    }
  }

  private void endDateSpinnerStateChanged(ChangeEvent e) {
    Date sd = new Date();
    sd.setYear(((Date)startDateSpinner.getValue()).getYear());
    sd.setMonth(((Date)startDateSpinner.getValue()).getMonth());
    sd.setDate(((Date)startDateSpinner.getValue()).getDate());
    Date ed = new Date();
    ed.setYear(((Date)endDateSpinner.getValue()).getYear());
    ed.setMonth(((Date)endDateSpinner.getValue()).getMonth());
    ed.setDate(((Date)endDateSpinner.getValue()).getDate());

/*    if(sd.after(ed)) {
      startDateSpinner.setValue(ed);
    }*/
    // different dates are not allowed
    if(ed.compareTo(sd) != 0) {
      startDateSpinner.setValue(ed);
    }
  }

  private void startTimeSpinnerStateChanged(ChangeEvent e) {
    Date sd = new Date();
    sd.setHours(((Date)startTimeSpinner.getValue()).getHours());
    sd.setMinutes(((Date)startTimeSpinner.getValue()).getMinutes());
    Date ed = new Date();
    ed.setHours(((Date)endTimeSpinner.getValue()).getHours());
    ed.setMinutes(((Date)endTimeSpinner.getValue()).getMinutes());

    if(sd.after(ed)) {
      endTimeSpinner.setValue(sd);
    }
  }

  private void endTimeSpinnerStateChanged(ChangeEvent e) {
    Date sd = new Date();
    sd.setHours(((Date)startTimeSpinner.getValue()).getHours());
    sd.setMinutes(((Date)startTimeSpinner.getValue()).getMinutes());
    Date ed = new Date();
    ed.setHours(((Date)endTimeSpinner.getValue()).getHours());
    ed.setMinutes(((Date)endTimeSpinner.getValue()).getMinutes());

    if(sd.after(ed)) {
      startTimeSpinner.setValue(ed);
    }
  }

  private void categoryTreeSelectionChanged(TreeSelectionEvent e) {
    //TreePath path = categoryTree.getSelectionPath();
    //System.out.println(path);
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Checks time conflicts ">
  /**
   * Checks time conflicts.
   * 
   * @param first date has to be before "last"
   * @param last date hast to be after "first"
   * @return true if first precedes last, otherwise dalse
   */
  private boolean checkOverlaps(DateTime firstStart, DateTime firstEnd, DateTime lastStart, DateTime lastEnd) {
    Interval firstI = new Interval(firstStart, firstEnd);
    Interval lastI = new Interval(lastStart, lastEnd);

    return firstI.overlaps(lastI);
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc=" Rebuild Category path">
  /**
   * Build a TreePath from the root path to the Category "c"
   * 
   * @param c Category "node"
   * @return path to "c" Category node
   */
  private TreePath rebuildCategoryPath(Category c) {
    ArrayList<Category> path = new ArrayList<>();
    path.add(0, c);
    while(categoryTreeElements.getCategoryByID(c.getID()).getParentCategory() != null) {
      path.add(0, categoryTreeElements.getCategoryByID(c.getID()).getParentCategory());
      c = categoryTreeElements.getCategoryByID(c.getID()).getParentCategory();
    }

    return new TreePath(path.toArray());
    //return path.toArray(new Category[path.size()]);
  }
  //</editor-fold>
}
