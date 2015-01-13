/**
 * Summary dialog
 * 
 * @author Szalay Roland
 * 
 */
package rosza.activitycalendar;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.GroupLayout;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;
import org.joda.time.DateTime;
import rosza.xcomponents.JDialogX;
import rosza.xcomponents.JButtonX;
import rosza.xcomponents.JScrollBarX;

public class SummaryDialog extends JDialogX {
  // UI variables
  private JButtonX    closeButton;
  private JLabel      dateLabel;
  private JScrollPane editorScrollPane;
  private JButtonX    sendButton;
  private JLabel      sendToLabel;
  private JTextField  sendToTextField;
  private JLabel      subjectLabel;
  private JTextField  subjectTextField;
  private JEditorPane summaryEditorPane;
  private JLabel      summaryLabel;

  // Date and time variables
  private static DateTime selectedDate;
  private static int selectedYear;
  private static int selectedMonth;
  private static int selectedDayOfMonth;

  // Generated summary variable
  private StringBuilder html = new StringBuilder();

  // Variable to store the summary informations
  private Summary summary;
  
  // Properties variables declaration
  private Properties props;

  // Email variable
  private Email email;

  // Create summary dialog
  public SummaryDialog(Frame owner, Component locationComp, String title, boolean modal, DateTime date) {
    super(owner, title, modal);

    props = ActivityCalendar.getProperties();
    email = checkEmailSettings();

    selectedDate = date;
    selectedYear = selectedDate.getYear();
    selectedMonth = selectedDate.getMonthOfYear();
    selectedDayOfMonth = selectedDate.getDayOfMonth();

    createUI(locationComp);
    ArrayList<Activity> activityList = new DataManager().getActivityByStartDate(selectedDate);
    Category category = XMLUtil.getCategories();
    summary = buildSummary(category, activityList)[0];
    for(int i = 0, size = summary.children.size(); i < size; i++) {
      summary.total += summary.children.get(i).total;
    }
    generateHTMLSummary();
  }

  // Create UI
  @SuppressWarnings("unchecked")
  private void createUI(Component locationComp) {
    summaryLabel      = new JLabel();
    editorScrollPane  = new JScrollPane();
    summaryEditorPane = new JEditorPane();
    sendToLabel       = new JLabel();
    subjectLabel      = new JLabel();
    sendToTextField   = new JTextField();
    subjectTextField  = new JTextField();
    sendButton        = new JButtonX("send");
    dateLabel         = new JLabel();
    closeButton       = new JButtonX("close");

    summaryLabel.setFont(summaryLabel.getFont().deriveFont(Font.BOLD));
    summaryLabel.setText("summary: ");
    summaryLabel.setHorizontalAlignment(JLabel.RIGHT);

    summaryEditorPane.setEditable(false);
    summaryEditorPane.setContentType("text/html");
    summaryEditorPane.setFont(summaryEditorPane.getFont().deriveFont(14f));
    summaryEditorPane.setText("");
    summaryEditorPane.setMinimumSize(new Dimension(600, 500));
    summaryEditorPane.setMaximumSize(new Dimension(600, 500));
    summaryEditorPane.setPreferredSize(new Dimension(600, 500));

    editorScrollPane.setViewportView(summaryEditorPane);
    editorScrollPane.getVerticalScrollBar().setUI(new JScrollBarX());
    editorScrollPane.getHorizontalScrollBar().setUI(new JScrollBarX());
    editorScrollPane.setBorder(new MatteBorder(1, 1, 1, 1, Constant.BG_DARKER_BLUE));

    sendToLabel.setFont(sendToLabel.getFont().deriveFont(Font.BOLD));
    sendToLabel.setText("send to: ");
    sendToLabel.setHorizontalAlignment(JLabel.RIGHT);

    subjectLabel.setFont(subjectLabel.getFont().deriveFont(Font.BOLD));
    subjectLabel.setText("subject: ");
    subjectLabel.setHorizontalAlignment(JLabel.RIGHT);

    subjectTextField.setText(String.format("%s - %d.%02d.%02d. summary", Constant.APP_DISPLAY_NAME, selectedYear, selectedMonth, selectedDayOfMonth));

    sendToTextField.setInputVerifier(new EmailVerifier());
    sendToTextField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        sendButton.setEnabled(enableSendButton());
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        sendButton.setEnabled(enableSendButton());
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        sendButton.setEnabled(enableSendButton());
      }
    });

    sendButton.setText("send");
    sendButton.setFont(sendButton.getFont().deriveFont(Font.BOLD));
    sendButton.setEnabled(false);
    sendButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        sendButtonActionPerformed(e);
      }
    });

    dateLabel.setText(String.format("%d.%02d.%02d.", selectedYear, selectedMonth, selectedDayOfMonth));
    dateLabel.setFont(dateLabel.getFont().deriveFont(Font.BOLD));

    closeButton.setText("close");
    closeButton.setFont(closeButton.getFont().deriveFont(Font.BOLD));
    closeButton.addActionListener(actionListener);
    closeButton.setActionCommand(Constant.CLOSE_DIALOG);

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
          .addComponent(summaryLabel)
          .addComponent(sendToLabel)
          .addComponent(subjectLabel)
        )
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(editorScrollPane)
          .addGroup(layout.createSequentialGroup()
            .addComponent(dateLabel)
          )
          .addComponent(sendToTextField)
          .addComponent(subjectTextField)
          .addGroup(layout.createSequentialGroup()
            .addComponent(sendButton)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(closeButton)
          )
        )
        .addContainerGap()
      )
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        //.addGap(18, 18, 18)
        .addContainerGap()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(summaryLabel)
          .addComponent(dateLabel)
        )
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(editorScrollPane, 200, 200, 200)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(sendToLabel)
          .addComponent(sendToTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        )
        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(subjectTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(subjectLabel)
        )
        .addGap(18, 18, 18)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(sendButton)
          .addComponent(closeButton)
        )
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      )
    );

    pack();
    setLocationRelativeTo(locationComp);
  }

  //Show the dialog.
  public void showDialog() {
    setVisible(true);
  }

  // Build summary "database"
  private Summary[] buildSummary(Category c, ArrayList<Activity> activityList) {
    return visitCategories(c, activityList);
  }

  // Visit categories
  private Summary[] visitCategories(Category c, ArrayList<Activity> activityList) {
    ArrayList<Summary> sum = new ArrayList<>();

    Summary s = activityCategoryMatch(c.getName(), activityList);
    if(s == null) {
      s = new Summary(c.getName());
    }
    sum.add(s);

    if(c.hasSubCategory()){
      Summary[] tempSum;
      for(int i = 0, size = c.getSubCount(); i < size; i++) {
        tempSum = visitCategories(c.getSubAt(i), activityList);
        linkSummary(s, tempSum);
      }
    }

    Summary[] tempSum = sum.toArray(new Summary[sum.size()]);

    return tempSum;
  }

  // Link together all members of a summary
  public static void linkSummary(Summary parent, Summary[] children) {
    for(Summary sum : children) {
      parent.children.add(sum);
      sum.parent = parent;
      for(int i = 0, size = sum.children.size(); i < size; i++) {
        sum.total += sum.children.get(i).total;
      }
    }
  }

  // Activity-Category match
  private Summary activityCategoryMatch(String cat, ArrayList<Activity> activities) {
    Summary s = new Summary(cat);
    if(activities != null) {
      for(Activity a : activities) {
        if(a.getCategory().getName().equals(cat)) {
          s.category.add(a.getCategory().getName());
          s.comment.add(a.getComment());
          s.duration.add(a.getDuration());
          s.total += a.getDuration();
        }
      }
    }

    return s;
  }

  // Generate HTML document from summary
  private void generateHTMLSummary() {
    parentheticRepresentation(summary, "");
    StringBuilder body = new StringBuilder("<html><head></head><body><font face=\"arial\">");
    body.append(html);
    body.append("</font></body></html>");
    summaryEditorPane.setText(body.toString());
  }

  // Generate parenthetic representation of summary class in HTML format
  private void parentheticRepresentation(Summary sum, String indent) {
    html.append(indent);
    if(sum.comment.isEmpty()) {
      html.append("<b>").append(escapeHtml(sum.category.get(0))).append("</b>");
      html.append(" - total: ").append(Activity.getDurationInHM(sum.total));
      html.append(" - ").append(Activity.time2fraction(sum.total));
      html.append("<br>");
    }
    else {
      html.append("<b>").append(escapeHtml(sum.category.get(0))).append("</b>");
      html.append(" - total: ").append(Activity.getDurationInHM(sum.total));
      html.append(" - ").append(Activity.time2fraction(sum.total));
      html.append("<br>");
      for(int i = 0, size = sum.comment.size(); i < size; i++) {
        html.append(indent).append(indent);
        html.append(escapeHtml(sum.comment.get(i)));
        html.append(" - time: ").append(Activity.getDurationInHM(sum.duration.get(i)));
        html.append(" - ").append(Activity.time2fraction(sum.duration.get(i)));
        html.append("<br>");
      }
    }
    if(!sum.children.isEmpty()) {
      indent += "&nbsp;&nbsp;";
      for(int i = 0, size = sum.children.size(); i < size; i++) {
        parentheticRepresentation(sum.children.get(i), indent);
      }
    }
  }

  // Send button state
  private boolean enableSendButton() {
    boolean en = (!sendToTextField.getText().equals(""));
    if(props == null) {
      en = false;
    }

    return en;
  }

  // Check e-mail settings
  private Email checkEmailSettings() {
    if(props != null) {
      if(props.getProperty(Constant.PROPS_EMAIL_ADDRESS) == null || props.getProperty(Constant.PROPS_EMAIL_ADDRESS).equals("")) {
        JOptionPane.showMessageDialog(null, "Missing senders e-mail address!\nPlease go to settings panel if you want to send e-mail.", "Error", JOptionPane.ERROR_MESSAGE);
        return null;
      }
      else if(props.getProperty(Constant.PROPS_EMAIL_SMTP_HOST) == null || props.getProperty(Constant.PROPS_EMAIL_SMTP_HOST).equals("")) {
        JOptionPane.showMessageDialog(null, "Missing SMTP host!\nPlease go to settings panel if you want to send e-mail.", "Error", JOptionPane.ERROR_MESSAGE);
        return null;
      }
      else if(props.getProperty(Constant.PROPS_EMAIL_SMTP_PORT) == null || props.getProperty(Constant.PROPS_EMAIL_SMTP_PORT).equals("")) {
        JOptionPane.showMessageDialog(null, "Missing SMTP port!\nPlease go to settings panel if you want to send e-mail.", "Error", JOptionPane.ERROR_MESSAGE);
        return null;
      }
      else if(props.getProperty(Constant.PROPS_EMAIL_USERNAME) == null || props.getProperty(Constant.PROPS_EMAIL_USERNAME).equals("")) {
        JOptionPane.showMessageDialog(null, "Missing e-mail username!\nPlease go to settings panel if you want to send e-mail.", "Error", JOptionPane.ERROR_MESSAGE);
        return null;
      }
      else if(props.getProperty(Constant.PROPS_EMAIL_PASSWORD) == null || props.getProperty(Constant.PROPS_EMAIL_PASSWORD).equals("")) {
        JOptionPane.showMessageDialog(null, "Missing e-mail password!\nPlease go to settings panel if you want to send e-mail.", "Error", JOptionPane.ERROR_MESSAGE);
        return null;
      }
      else if(props.getProperty(Constant.PROPS_EMAIL_PROTOCOL) == null || props.getProperty(Constant.PROPS_EMAIL_PROTOCOL).equals("")) {
        JOptionPane.showMessageDialog(null, "Missing e-mail protocol!\nPlease go to settings panel if you want to send e-mail.", "Error", JOptionPane.ERROR_MESSAGE);
        return null;
      }
    }
    else {
      JOptionPane.showMessageDialog(null, "No e-mail settings found!\nPlease go to settings panel if you want to send e-mail.", "Information", JOptionPane.INFORMATION_MESSAGE);
      return null;
    }
    return new Email(props);
  }

  // Action listener
  ActionListener actionListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      if(null != e.getActionCommand()) {
        switch (e.getActionCommand()) {
          case Constant.CLOSE_DIALOG:
            setVisible(false);
            dispose();
            break;
        }
      }
    }
  };

  // Button events
  private void sendButtonActionPerformed(ActionEvent e) {
    if(email != null) {
      email.sendMail(sendToTextField.getText(), subjectTextField.getText(), summaryEditorPane.getText());
    }
  }

  // Summary class
  public class Summary {
    public ArrayList<String> comment = new ArrayList<>();
    public ArrayList<String> category = new ArrayList<>();
    public ArrayList<Integer> duration = new ArrayList<>();
    public int total = 0;
    public Summary parent;
    public ArrayList<Summary> children = new ArrayList<>();

    public Summary() {
    }

    public Summary(String cat) {
      this.category.add(cat);
    }

    public Summary(String cat, String com, int duration) {
      this(cat);
      this.comment.add(com);
      this.duration.add(duration);
      this.children = new ArrayList<>();
    }
  }
}
