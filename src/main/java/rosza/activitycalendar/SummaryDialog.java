/**
 * Summary dialog
 * 
 * @author Szalay Roland
 * 
 */
package rosza.activitycalendar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.swing.GroupLayout;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.text.StrongTextEncryptor;
import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;
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
  private static int selectedYear;
  private static int selectedMonth;
  private static int selectedDayOfMonth;

  // Generated summary variable
  private StringBuilder html = new StringBuilder();

  // Variable to store the summary informations
  private Summary summary;
  
  // Properties variables declaration
  private final Properties props = XMLUtil.getProperties();

  // Create summary dialog
  public SummaryDialog(Frame owner, Component locationComp, String title, boolean modal, int y, int m, int d) {
    super(owner, title, modal);

    selectedYear = y;
    selectedMonth = m;
    selectedDayOfMonth = d;

    createUI(locationComp);
    ArrayList<Activity> activityList = XMLUtil.getActivityByDate(y, m, d);
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
  //</editor-fold>

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

  // Send e-mail
  private void sendMail(String to, String subject, String body) {
    StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
    textEncryptor.setPassword(Constant.SALT);
    Properties eMailProps = new Properties();

		eMailProps.put("mail.smtp.host", props.getProperty(Constant.PROPS_EMAIL_SMTP_HOST));
    eMailProps.put("mail.smtp.port", props.getProperty(Constant.PROPS_EMAIL_SMTP_PORT));
    switch(props.getProperty(Constant.PROPS_EMAIL_PROTOCOL)) {
    case Constant.PROPS_EMAIL_SMTPS:
        eMailProps.put("mail.smtp.ssl.enable", true);
        break;
    case Constant.PROPS_EMAIL_TLS:
        eMailProps.put("mail.smtp.starttls.enable", true);
        break;
    }

    final String from = props.getProperty(Constant.PROPS_EMAIL_ADDRESS);
    final String username = props.getProperty(Constant.PROPS_EMAIL_USERNAME);
    final String password;
    String pwd = props.getProperty(Constant.PROPS_EMAIL_PASSWORD);
    try {
      pwd = textEncryptor.decrypt(pwd);
    }
    catch(EncryptionOperationNotPossibleException e) {
      JOptionPane.showMessageDialog(this, "Error in jaspyt!\n" + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    password = pwd;

    Authenticator authenticator = null;
    if(props.getProperty(Constant.PROPS_EMAIL_AUTHENTICATION).equals("true")) {
      eMailProps.put("mail.smtp.auth", true);
      authenticator = new Authenticator() {
        private final PasswordAuthentication pa = new PasswordAuthentication(username, password);
        @Override
        public PasswordAuthentication getPasswordAuthentication() {
          return pa;
        }
      };
    }

    Session session = Session.getDefaultInstance(eMailProps, authenticator);

    try {
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(from));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
      message.setSubject(subject);
			//message.setText(body);

      Multipart multipart = new MimeMultipart("alternative");
      // Create a MimeBodyPart instance to contain the text body part
      MimeBodyPart textPart = new MimeBodyPart();
      String textContent = "";
      textPart.setText(textContent);

      // Create a MimeBodyPart instance to contain the HTML body part.
      // Order is important, the preferred format of an alternative multi-part message should be added last.
      MimeBodyPart htmlPart = new MimeBodyPart();
      String htmlContent = body;
      htmlPart.setContent(htmlContent, "text/html");

      // Add both MimeBodyPart instances to the MimeMultipart instance and set the MimeMultipart instance as the MimeMessage.
      //multipart.addBodyPart(textPart);
      multipart.addBodyPart(htmlPart);
      message.setContent(multipart);

      Transport.send(message);

      JOptionPane.showMessageDialog(this, "Message has been sent to " + to, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    catch(AuthenticationFailedException e) {
      JOptionPane.showMessageDialog(this, "Error in username or password!", "Error", JOptionPane.ERROR_MESSAGE);
    }
    catch(MessagingException e) {
      JOptionPane.showMessageDialog(this, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

  // E-mail verifier
  private class EmailVerifier extends InputVerifier {
    @Override
    public boolean verify(JComponent input) {
      String email = ((JTextField)input).getText();
      final String singleValidExpression = "[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}";  
      final String validExpression = "^" + singleValidExpression + "(\\s*,\\s*" + singleValidExpression + ")*$";  

      Pattern compare = Pattern.compile(validExpression, Pattern.CASE_INSENSITIVE);
      Matcher matcher = compare.matcher(email); 

      if(matcher.matches()) {
        input.setBackground(UIManager.getColor("TextField.background"));
        return true;
      }
      else {
        input.setBackground( Color.red );
        return false;
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
    sendMail(sendToTextField.getText(), subjectTextField.getText(), summaryEditorPane.getText());
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
