/**
 * Extended JSpinner UI component.
 * 
 * @author Szalay Roland
 * 
 */
package rosza.xcomponents;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicSpinnerUI;
import rosza.activitycalendar.Constant;

public class JSpinnerX extends BasicSpinnerUI {
  public static ComponentUI createUI(JComponent c) {  
    return new JSpinnerX();  
  }  

  @Override
  protected Component createNextButton() {
    return new BasicArrowButton(SwingConstants.NORTH, Constant.BG_DARKER_BLUE, Constant.BG_DARKER_BLUE, Constant.TEXT_COLOR, Constant.BG_BLUE);
  }

  @Override
  protected Component createPreviousButton() {
    return new BasicArrowButton(SwingConstants.SOUTH, Constant.BG_DARKER_BLUE, Constant.BG_DARKER_BLUE, Constant.TEXT_COLOR, Constant.BG_BLUE);
  }

  @Override  
  public void installUI(JComponent c) {  
    super.installUI(c);  
  }
}
