/**
 * Constants for ActivityCalendar.
 * 
 * @author Szalay Roland
 * 
 */
package rosza.activitycalendar;

//<editor-fold defaultstate="collapsed" desc=" Import ">
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
//</editor-fold>

public class Constant {

  // common constants
  protected static final String PROPERTIES_FILE      = "properties.ini";
  protected static final String APP_DISPLAY_NAME     = "Activity Calendar";
  protected static final String APP_NAME             = "ActivityCalendar";
  // icons source: https://www.iconfinder.com/iconsets/windows-8-metro-style
  protected static final String CLOSE_ICON           = "images/close.png";
  protected static final String MINIMIZE_ICON        = "images/minimize.png";
  protected static final String HIDE_ICON            = "images/hide.png";
  protected static final String SETTINGS_ICON        = "images/settings.png";
  protected static final String REPORT_ICON          = "images/report.png";
  protected static final String NEXT_ICON            = "images/next.png";
  protected static final String PREV_ICON            = "images/previous.png";
  protected static final String ADD_ICON             = "images/add.png";
  protected static final String TRAY_ICON            = "images/calendar16.png";
  protected static final File   FONTFILE             = new File("src/main/resources/font/Ubuntu-R.ttf");
  protected static Font         FONT;
  protected static float        FONT_SIZE;
  protected static final Font   DEFAULT_FONT         = UIManager.getDefaults().getFont("Label.font");
  protected static final int    DEFAULT_FONT_SIZE    = DEFAULT_FONT.getSize();
  protected static final String ACTIVITY_EXIT        = "EXIT";
  protected static final String ACTIVITY_SHOW_HIDE   = "SHOW_HIDE";
  protected static final String CLOSE_PANE           = "close";
  protected static final String MODIFY_ACTIVITY      = "modifyActivity";
  protected static final int    LONG_DISPLAY         = 0;
  protected static final int    SHORT_DISPLAY        = 1;

  static {
    try {
      FONT = Font.createFont(Font.TRUETYPE_FONT, Constant.FONTFILE).deriveFont(14f);
      FONT_SIZE = FONT.getSize() + 1;
    }
    catch (FontFormatException e) {
      Logger.getLogger(Constant.class.getName()).log(Level.SEVERE, null, e);
      FONT = Constant.DEFAULT_FONT;
      FONT_SIZE = DEFAULT_FONT_SIZE;
    }
    catch (IOException e) {
      Logger.getLogger(Constant.class.getName()).log(Level.SEVERE, null, e);
      FONT = Constant.DEFAULT_FONT;
      FONT_SIZE = DEFAULT_FONT_SIZE;
    }
  }

  // "ActivityPane" related constants
  protected static final int DAY_VIEW  = 0;
  protected static final int WEEK_VIEW = 1;

  protected static final int DAY_CELL_WIDTH  = DEFAULT_FONT_SIZE * 10 * 3;
  protected static final int WEEK_CELL_WIDTH = DEFAULT_FONT_SIZE * 10;
  protected static final int CELL_HEIGHT     = DEFAULT_FONT_SIZE * 3;
  protected static final int CELL_SPACER     = 2;
  protected static final int MIN_CELL_WIDTH  = DEFAULT_FONT_SIZE * 8;
  protected static final int BAR_WIDTH       = DEFAULT_FONT_SIZE * 5;
  protected static final int BAR_HEIGHT      = DEFAULT_FONT_SIZE * 3;

  protected static final Color BG_COLOR                  = Color.white;
  protected static final Color TEXT_COLOR                = Color.darkGray;
  protected static final Color DEFAULT_ACTIVITY_COLOR    = Color.gray;
  protected static final Color ERROR_ACTIVITY_COLOR      = Color.red;
  protected static final Color CELL_BORDER_COLOR         = Color.lightGray;
  protected static final Color CELL_BG_COLOR             = Color.white;
  protected static final Color SELECTED_CELLBORDER_COLOR = Color.blue;
  protected static final Color SELECTED_CELLBG_COLOR     = new Color(200, 200, 255);
  protected static final Color CELL_TEXT_COLOR           = Color.darkGray;
  protected static final Color TIMELINE_COLOR            = Color.red;

  // event constants
  protected static final String SELECTION_CHANGE = "SELECTION_CHANGE";
  protected static final String DAY_CHANGE       = "DAY_CHANGE";
  protected static final String MONTH_CHANGE     = "MONTH_CHANGE";
  protected static final String YEAR_CHANGE      = "YEAR_CHANGE";
  
  // XML constants
  protected static final String XML_ACTIVITIES_FILE = "activities.xml";
  protected static final String XML_CATEGORIES_FILE = "categories.xml";
  protected static final String XML_ACTIVITIES_ROOT = "Activities";
  protected static final String XML_CATEGORIES_ROOT = "Categories";
  protected static final String XML_ACTIVITY_TAG    = "activity";
  protected static final String XML_CATEGORY_TAG    = "category";
  protected static final String XML_ID              = "id";
  protected static final String XML_COMMENT         = "comment";
  protected static final String XML_CATEGORY        = "category";
  protected static final String XML_START           = "start";
  protected static final String XML_END             = "end";
  protected static final String XML_COLOR           = "color";
  protected static final String XML_NAME            = "name";
  protected static final String XML_PREDEFINED      = "predefined";
  protected static final String XML_PARENT          = "parent";

  // Properties constants
  protected static final String PROPS_STORAGE              = "properties.storage";
  protected static final String PROPS_XML_STORAGE          = "xml";
  protected static final String PROPS_DB_STORAGE           = "db";
  protected static final String PROPS_DB_SERVER            = "db.server";
  protected static final String PROPS_DB_SERVER_PORT       = "db.server.port";
  protected static final String PROPS_DB_USERNAME          = "db.username";
  protected static final String PROPS_DB_PASSWORD          = "db.password";
  protected static final String PROPS_EMAIL_ADDRESS        = "email.address";
  protected static final String PROPS_EMAIL_USERNAME       = "email.username";
  protected static final String PROPS_EMAIL_PASSWORD       = "email.password";
  protected static final String PROPS_EMAIL_SMTP_HOST      = "email.smtp.host";
  protected static final String PROPS_EMAIL_SMTP_PORT      = "email.smtp.port";
  protected static final String PROPS_EMAIL_AUTHENTICATION = "email.authentication";
  protected static final String PROPS_EMAIL_PROTOCOL       = "email.protocol";
  protected static final String PROPS_EMAIL_TLS            = "tls";
  protected static final String PROPS_EMAIL_SMTP           = "smtp";
  protected static final String PROPS_EMAIL_SMTPS          = "smtps";
}
