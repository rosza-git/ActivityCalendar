/**
 * Extended BasicScrollBarUI UI component.
 * 
 * @author Szalay Roland
 */
package rosza.xcomponents;

import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;
import rosza.activitycalendar.Constant;

public class JScrollBarX extends BasicScrollBarUI {
  @Override
  protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
    g.setColor(Constant.BG_COLOR);
    g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);

    if(trackHighlight == DECREASE_HIGHLIGHT){
      paintDecreaseHighlight(g);
    }
    else if(trackHighlight == INCREASE_HIGHLIGHT) {
      paintIncreaseHighlight(g);
    }
  }

  @Override
  protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
    if(thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
      return;
    }

    Graphics2D g2d = (Graphics2D)g;

    int w = thumbBounds.width;
    int h = thumbBounds.height;

    g.translate(thumbBounds.x, thumbBounds.y);

    g.setColor(Constant.BG_DARKER_BLUE.darker());
    g.drawRect(0, 0, w - 1, h - 1);
    GradientPaint gp = new GradientPaint(0, 0, Constant.BG_DARKER_BLUE, 0, h, Constant.BG_BLUE);
    g2d.setPaint(gp);
    g2d.fillRect(0, 0, w - 1, h - 1);

    g.setColor(Constant.BG_BLUE);
    g.drawLine(1, 1, 1, h - 2);
    g.drawLine(2, 1, w - 3, 1);

    g.setColor(Constant.BG_DARKER_BLUE);
    g.drawLine(2, h - 2, w - 2, h - 2);
    g.drawLine(w - 2, 1, w - 2, h - 3);

    g.translate(-thumbBounds.x, -thumbBounds.y);
  }

  @Override
  protected JButton createDecreaseButton(int orientation) {
    return new BasicArrowButton(orientation, Constant.BG_DARKER_BLUE, Constant.BG_DARKER_BLUE, Constant.TEXT_COLOR, Constant.BG_BLUE);
  }

  @Override
  protected JButton createIncreaseButton(int orientation) {
    return new BasicArrowButton(orientation, Constant.BG_DARKER_BLUE, Constant.BG_DARKER_BLUE, Constant.TEXT_COLOR, Constant.BG_BLUE);
  }
}
