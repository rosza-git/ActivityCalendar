/**
 * Activity dialog
 * 
 * @author Szalay Roland
 * 
 */
package rosza.activitycalendar;

// Import 
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
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
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import rosza.xcomponents.JButtonX;
import rosza.xcomponents.JDialogX;
import rosza.xcomponents.JScrollBarX;
import rosza.xcomponents.JSpinnerX;

public class ActivityDialog extends JDialogX {
  // ActivityDialog
  private static Activity       activity;
  private static ActivityAction activityAction;
  // UI variables
  private JLabel         categoryLabel;
  private JLabel         commentLabel;
  private JTextField     commentTextField;
  private JLabel         startLabel;
  private JSpinner       startDateSpinner;
  private JSpinner       startTimeSpinner;
  private JLabel         endLabel;
  private JSpinner       endDateSpinner;
  private JSpinner       endTimeSpinner;
  private CategoryTree   categoryTree;
  private JButtonX       addActivityButton;
  private JButtonX       modifyActivityButton;
  private JButtonX       removeActivityButton;
  private JButtonX       cancelButton;
  private JScrollPane    categoryScrollPane;
  private Category       categoryTreeElements;

  // Create new Activity dialog
  public ActivityDialog(Frame frame, Component locationComp, String title, boolean modal, Activity initialValue) {
    super(frame, title, modal);

    // Get categories
    try {
      categoryTreeElements = new DataManager().getCategories();
    }
    catch(NullPointerException e) {
    }

    // Set initial value.
    activity = initialValue;
    activityAction = new ActivityAction(Constant.MODIFY_ACTIVITY, activity);

    // Create UI.
    createUI(locationComp);
  }

  /**
   * Create dialog UI.
   * 
   * @param locationComp null if you want the dialog to come up with its left
   *                     corner in the center of the screen; otherwise, it should
   *                     be the component on top of which the dialog should appear
   */
  @SuppressWarnings("unchecked")
  private void createUI(Component locationComp) {
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

    setUndecorated(true);
    setTitle(activity == null ? "add " + getTitle() : "modify " + getTitle());

    modifyActivityButton.setText("modify");
    modifyActivityButton.setActionCommand(Constant.MODIFY_ACTIVITY);
    modifyActivityButton.addActionListener(actionListener);
    modifyActivityButton.setEnabled(activity != null);

    addActivityButton.setText("add");
    addActivityButton.setActionCommand(Constant.ADD_ACTIVITY);
    addActivityButton.addActionListener(actionListener);
    addActivityButton.setEnabled(activity == null);

    removeActivityButton.setText("remove");
    removeActivityButton.setActionCommand(Constant.REMOVE_ACTIVITY);
    removeActivityButton.addActionListener(actionListener);
    removeActivityButton.setEnabled((activity != null));

    cancelButton.setText("cancel");
    cancelButton.addActionListener(actionListener);
    cancelButton.setActionCommand(Constant.CLOSE_DIALOG);

    commentLabel.setFont(commentLabel.getFont().deriveFont(Font.BOLD));
    commentLabel.setLabelFor(commentTextField);
    commentLabel.setText("comment:");

    commentTextField.setText(activity == null ? "" : activity.getComment());

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
    try {
      categoryTree.setSelectionPath(rebuildCategoryPath(activity.getCategory()));
    }
    catch(NullPointerException e) {
      categoryTree.setSelectionPath(categoryTree.getPathForRow(1));
    }
    categoryTree.addTreeSelectionListener(new TreeSelectionListener() {
      @Override
      public void valueChanged(TreeSelectionEvent e) {
        categoryTreeSelectionChanged(e);
      }
    });

    categoryScrollPane.setViewportView(categoryTree);
    categoryScrollPane.setPreferredSize(new Dimension(categoryTree.getWidth(), (int)Constant.FONT_SIZE * 6));
    categoryScrollPane.getVerticalScrollBar().setUI(new JScrollBarX());
    categoryScrollPane.getHorizontalScrollBar().setUI(new JScrollBarX());
    categoryScrollPane.setBorder(new MatteBorder(1, 1, 1, 1, Constant.BG_DARKER_BLUE));

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(removeActivityButton)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
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
                .addComponent(startDateSpinner, 90, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(startTimeSpinner, 55, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
              )
              .addGroup(layout.createSequentialGroup()
                .addComponent(endDateSpinner, 90, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(endTimeSpinner, 55, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
              )
              .addComponent(categoryScrollPane, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)
            )
          )
        )
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      )
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(commentLabel)
          .addComponent(commentTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        )
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(categoryLabel)
          .addComponent(categoryScrollPane, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
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

    pack();
    setLocationRelativeTo(locationComp);
  }

  /**
   * Show the dialog.
   * 
   * @return a new ActivityAction class
   */
  public ActivityAction showDialog() {
    setVisible(true);

    return activityAction;
  }

  /**
   * Build a TreePath from the root path to the Category 'c'
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
  }

  /**
   * Create Activity class based on user input.
   * 
   * @return true if the datas are suitable for add or modify Activity, otherwise false
   */
  private boolean createActivity(String command) {
    String comment = commentTextField.getText();
    Category category = categoryTree.getSelected();
    DateTime sdate = new DateTime(startDateSpinner.getValue());
    DateTime edate = new DateTime(endDateSpinner.getValue());

    DateTime stime = new DateTime(startTimeSpinner.getValue());
    DateTime etime = new DateTime(endTimeSpinner.getValue());

    DateTime start = new DateTime(sdate.getYear(), sdate.getMonthOfYear(), sdate.getDayOfMonth(), stime.getHourOfDay(), stime.getMinuteOfHour());
    DateTime end = new DateTime(edate.getYear(), edate.getMonthOfYear(), edate.getDayOfMonth(), etime.getHourOfDay(), etime.getMinuteOfHour());

    if(activity == null) {
      activity = new Activity(comment, category, start, end);
    }
    else {
      activity = new Activity(activity.getID(), comment, category, start, end);
    }

    if(command.equals(Constant.ADD_ACTIVITY) || command.equals(Constant.MODIFY_ACTIVITY)) {
      ArrayList<Activity> activityList;
      try {
        activityList = new DataManager().getActivityByStartDate(sdate);
      }
      catch(Exception e) {
        JOptionPane.showMessageDialog(null, e.getCause(), "Activity", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      if(activityList != null) {
        for(Activity act : activityList) {
          if(act.getID() == activity.getID()) {
            continue;
          }
          if(checkOverlaps(activity.getStartDate(), activity.getEndDate(), act.getStartDate(), act.getEndDate())) {
            JOptionPane.showMessageDialog(this, "Overlapping activities are not allowed!", "Activity error", JOptionPane.ERROR_MESSAGE);
            return false;
          }
        }
      }
      return true;
    }

    return false;
  }

  /**
   * Check time interval conflicts.
   * 
   * @param start1 date 1 start
   * @param end1   date 1 end
   * @param start2 date 2 start
   * @param end2   date 2 end
   * @return true if overlapping, otherwise false
   */
  private boolean checkOverlaps(DateTime start1, DateTime end1, DateTime start2, DateTime end2) {
    Interval firstI = new Interval(start1, end1);
    Interval lastI = new Interval(start2, end2);

    return firstI.overlaps(lastI);
  }

  // Change events
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
    // different dates are not allowed (for now)
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
    // different dates are not allowed (for now)
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

  // Category-tree selection changed
  private void categoryTreeSelectionChanged(TreeSelectionEvent e) {
    if(categoryTree.getSelected() == null) {
      return;
    }
    int id = categoryTree.getSelected().getID();
    if(id == 0) {
      categoryTree.setSelectionPath(categoryTree.getPathForRow(id + 1));
    }
  }

  // Action listener
  ActionListener actionListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      if(null != e.getActionCommand()) {
        switch (e.getActionCommand()) {
          case Constant.ADD_ACTIVITY:
            if(createActivity(Constant.ADD_ACTIVITY)) {
              activityAction = new ActivityAction(Constant.ADD_ACTIVITY, activity);
              setVisible(false);
              dispose();
            }
            break;
          case Constant.MODIFY_ACTIVITY:
            if(createActivity(Constant.MODIFY_ACTIVITY)) {
              activityAction = new ActivityAction(Constant.MODIFY_ACTIVITY, activity);
              setVisible(false);
              dispose();
            }
            break;
          case Constant.REMOVE_ACTIVITY:
            int result = JOptionPane.showConfirmDialog((Component)e.getSource(), "Are you sure to remove selected Activity?", "Activity question", JOptionPane.YES_NO_CANCEL_OPTION);
            if(result == JOptionPane.YES_OPTION) {
              createActivity(Constant.REMOVE_ACTIVITY);
              activityAction = new ActivityAction(Constant.REMOVE_ACTIVITY, activity);
              setVisible(false);
              dispose();
            }
            break;
          case Constant.CLOSE_DIALOG:
            activityAction = null;
            setVisible(false);
            dispose();
            break;
        }
      }
    }
  };
}
