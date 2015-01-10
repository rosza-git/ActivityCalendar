/**
 * Customized title bar component from JLabel.
 * 
 * @author Szalay Roland
 * 
 */
package rosza.xcomponents;

import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import rosza.activitycalendar.Constant;

public class TitleBarX extends JLabel {
  public TitleBarX() {
    super();

    setBorder(new EmptyBorder(5, 0, 5, 0));
  }

  @Override
  public void paint(Graphics g) {
    int w = getWidth();
    int h = getHeight();
    Graphics2D g2d = (Graphics2D) g;
    GradientPaint p = new GradientPaint(0, 0, Constant.BG_DARKER_BLUE, 0, h, Constant.BG_BLUE);
    g2d.setPaint(p);
    g2d.fillRect(0, 0, w, h);
    super.paint(g2d);
  }
}
