/**
 * Settings handler
 * 
 * @author Szalay Roland
 * 
 */
package rosza.activitycalendar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import javax.swing.JOptionPane;

class SettingsHandler {
  // Save settings
  static void setSettings(Properties props) throws IOException {
    OutputStream output = new FileOutputStream(Constant.PROPERTIES_FILE);
    props.store(output, null);
    JOptionPane.showMessageDialog(null, "Settings saved!", "Information", JOptionPane.INFORMATION_MESSAGE);
  }

  // Load settings 
  static Properties getSettings() throws FileNotFoundException, IOException {
    Properties props = new Properties();

    InputStream input = new FileInputStream(Constant.PROPERTIES_FILE);
    props.load(input);

    return props;
  }
}
