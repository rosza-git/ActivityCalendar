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
  public static final String PROPERTIES_FILE      = "properties.ini";
  public static final String APP_DISPLAY_NAME     = "Activity Calendar";
  public static final String APP_NAME             = "ActivityCalendar";
  // icons source: https://www.iconfinder.com/iconsets/windows-8-metro-style
  public static final String CLOSE_ICON           = "images/close.png";
  public static final String MINIMIZE_ICON        = "images/minimize.png";
  public static final String HIDE_ICON            = "images/hide.png";
  public static final String SETTINGS_ICON        = "images/settings.png";
  public static final String REPORT_ICON          = "images/report.png";
  public static final String NEXT_ICON            = "images/next.png";
  public static final String PREV_ICON            = "images/previous.png";
  public static final String ADD_ICON             = "images/add.png";
  public static final String TRAY_ICON            = "images/calendar16.png";
  public static final File   FONTFILE             = new File("src/main/resources/font/Ubuntu-R.ttf");
  public static Font         FONT;
  public static float        FONT_SIZE;
  public static final Font   DEFAULT_FONT         = UIManager.getDefaults().getFont("Label.font");
  public static final int    DEFAULT_FONT_SIZE    = DEFAULT_FONT.getSize();
  public static final String ACTIVITY_EXIT        = "EXIT";
  public static final String ACTIVITY_SHOW_HIDE   = "SHOW_HIDE";
  public static final String CLOSE_PANE           = "close";
  public static final String MODIFY_ACTIVITY      = "modifyActivity";
  public static final int    LONG_DISPLAY         = 0;
  public static final int    SHORT_DISPLAY        = 1;

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
  public static final int DAY_VIEW  = 0;
  public static final int WEEK_VIEW = 1;

  public static final int DAY_CELL_WIDTH  = DEFAULT_FONT_SIZE * 10 * 3;
  public static final int WEEK_CELL_WIDTH = DEFAULT_FONT_SIZE * 10;
  public static final int CELL_HEIGHT     = DEFAULT_FONT_SIZE * 3;
  public static final int CELL_SPACER     = 2;
  public static final int MIN_CELL_WIDTH  = DEFAULT_FONT_SIZE * 8;
  public static final int BAR_WIDTH       = DEFAULT_FONT_SIZE * 5;
  public static final int BAR_HEIGHT      = DEFAULT_FONT_SIZE * 3;

  public static final Color BG_COLOR                  = new Color(250, 250, 250);
  public static final Color BG_DARKER_COLOR           = new Color(200, 200, 200);
  public static final Color BG_BLUE                   = new Color(76, 160, 201);
  public static final Color BG_DARKER_BLUE            = new Color(34, 131, 178);
  public static final Color TEXT_COLOR                = new Color(50, 50, 50);
  public static final Color BORDER_COLOR              = new Color(50, 50, 50);
  public static final Color DEFAULT_ACTIVITY_COLOR    = Color.gray;
  public static final Color ERROR_ACTIVITY_COLOR      = Color.red;
  public static final Color CELL_BORDER_COLOR         = new Color(220, 220, 220);
  public static final Color CELL_BG_COLOR             = BG_COLOR;
  public static final Color SELECTED_CELLBORDER_COLOR = Color.blue;
  public static final Color SELECTED_CELLBG_COLOR     = new Color(230, 230, 255);
  public static final Color CELL_TEXT_COLOR           = TEXT_COLOR;
  public static final Color TIMELINE_COLOR            = Color.red;
  public static final int WHITE                       = 0;
  public static final int BLUE                        = 1;

  // event constants
  public static final String SELECTION_CHANGE = "SELECTION_CHANGE";
  public static final String DAY_CHANGE       = "DAY_CHANGE";
  public static final String MONTH_CHANGE     = "MONTH_CHANGE";
  public static final String YEAR_CHANGE      = "YEAR_CHANGE";
  
  // XML constants
  public static final String XML_ACTIVITIES_FILE = "activities.xml";
  public static final String XML_CATEGORIES_FILE = "categories.xml";
  public static final String XML_ACTIVITIES_ROOT = "Activities";
  public static final String XML_CATEGORIES_ROOT = "Categories";
  public static final String XML_ACTIVITY_TAG    = "activity";
  public static final String XML_CATEGORY_TAG    = "category";
  public static final String XML_ID              = "id";
  public static final String XML_COMMENT         = "comment";
  public static final String XML_CATEGORY        = "category";
  public static final String XML_START           = "start";
  public static final String XML_END             = "end";
  public static final String XML_COLOR           = "color";
  public static final String XML_NAME            = "name";
  public static final String XML_PREDEFINED      = "predefined";
  public static final String XML_PARENT          = "parent";

  // Properties constants
  public static final String PROPS_STORAGE              = "properties.storage";
  public static final String PROPS_XML_STORAGE          = "xml";
  public static final String PROPS_DB_STORAGE           = "db";
  public static final String PROPS_DB_SERVER            = "db.server";
  public static final String PROPS_DB_SERVER_PORT       = "db.server.port";
  public static final String PROPS_DB_USERNAME          = "db.username";
  public static final String PROPS_DB_PASSWORD          = "db.password";
  public static final String PROPS_EMAIL_ADDRESS        = "email.address";
  public static final String PROPS_EMAIL_USERNAME       = "email.username";
  public static final String PROPS_EMAIL_PASSWORD       = "email.password";
  public static final String PROPS_EMAIL_SMTP_HOST      = "email.smtp.host";
  public static final String PROPS_EMAIL_SMTP_PORT      = "email.smtp.port";
  public static final String PROPS_EMAIL_AUTHENTICATION = "email.authentication";
  public static final String PROPS_EMAIL_PROTOCOL       = "email.protocol";
  public static final String PROPS_EMAIL_TLS            = "tls";
  public static final String PROPS_EMAIL_SMTP           = "smtp";
  public static final String PROPS_EMAIL_SMTPS          = "smtps";
}
