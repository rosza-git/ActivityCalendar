/**
 * Extended JComboBox UI component.
 * 
 * @author Szalay Roland
 * 
 */
package rosza.xcomponents;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import rosza.activitycalendar.Constant;

public class JComboBoxX extends BasicComboBoxUI {
  public static ComponentUI createUI(JComponent c) {
    return new JComboBoxX();
  }

  @Override
  protected ComboPopup createPopup() {
    JBasicComboPopupX popupX = new JBasicComboPopupX(comboBox);
    JList list = popupX.getList();
    list.setSelectionBackground(Constant.BG_BLUE);

    return popupX;
  }

  @Override
  protected JButton createArrowButton() {
    return new BasicArrowButton(BasicArrowButton.SOUTH, Constant.BG_DARKER_BLUE, Constant.BG_DARKER_BLUE, Constant.TEXT_COLOR, Constant.BG_BLUE);
  }
}

class JBasicComboPopupX extends BasicComboPopup {
  JBasicComboPopupX(JComboBox box) {
    super(box);
    setBorder(new MatteBorder(1, 1, 1, 1, Constant.BG_DARKER_BLUE));
  }

  @Override
  protected JScrollPane createScroller() {
    JScrollPane pane = new JScrollPane(list, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    pane.setBackground(Constant.BG_COLOR);
    pane.getVerticalScrollBar().setUI(new JScrollBarX());
    pane.getHorizontalScrollBar().setUI(new JScrollBarX());

    return pane;
  }
}
