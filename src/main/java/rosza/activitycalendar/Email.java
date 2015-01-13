/**
 * E-mail sender & E-mail verifier
 * 
 * @author Szalay Roland
 * 
 */
package rosza.activitycalendar;

import java.awt.Color;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.text.StrongTextEncryptor;

// E-mail sender
class Email {
  // Properties variables declaration
  private final Properties props;

  public Email(Properties p) {
    this.props = p;
  }

  public void sendMail(String to, String subject, String body) {
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
      JOptionPane.showMessageDialog(null, "Error in jaspyt!\n" + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
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

    Session session = Session.getInstance(eMailProps, authenticator);

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

      JOptionPane.showMessageDialog(null, "Message has been sent to " + to, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    catch(AuthenticationFailedException e) {
      JOptionPane.showMessageDialog(null, "Error in username or password!", "Error", JOptionPane.ERROR_MESSAGE);
    }
    catch(MessagingException e) {
      JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}

// E-mail verifier
class EmailVerifier extends InputVerifier {
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
      input.setBackground(Color.red );
      return false;
    }
  }
}
