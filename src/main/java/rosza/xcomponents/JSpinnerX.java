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
    Component c = new BasicArrowButton(SwingConstants.NORTH, Constant.BG_DARKER_BLUE, Constant.BG_DARKER_BLUE, Constant.TEXT_COLOR, Constant.BG_BLUE);
    c.setName("Spinner.nextButton");
    installNextButtonListeners(c);

    return c;
  }

  @Override
  protected Component createPreviousButton() {
    Component c = new BasicArrowButton(SwingConstants.SOUTH, Constant.BG_DARKER_BLUE, Constant.BG_DARKER_BLUE, Constant.TEXT_COLOR, Constant.BG_BLUE);
    c.setName("Spinner.previousButton");
    installPreviousButtonListeners(c);

    return c;
  }

  @Override  
  public void installUI(JComponent c) {  
    super.installUI(c);
  }
}
