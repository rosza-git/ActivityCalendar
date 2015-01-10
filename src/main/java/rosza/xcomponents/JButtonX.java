/**
 * Extended JButton UI component.
 * 
 * @author Szalay Roland
 * 
 */
package rosza.xcomponents;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JButton;
import rosza.activitycalendar.Constant;

public class JButtonX extends JButton {
  public JButtonX(String text) {
    super(text);
    setBorderPainted(false);
    setContentAreaFilled(false);
    setCursor(new Cursor(Cursor.HAND_CURSOR));
    setForeground(Constant.TEXT_COLOR);
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D)g;
    GradientPaint gp = new GradientPaint(0, getHeight() / 2, Constant.BG_DARKER_BLUE, 0, getHeight(), Constant.BG_BLUE);
    g2d.setPaint(gp);
    g2d.fillRect(2, 2, getWidth() - 4, getHeight() - 4);
    g2d.setPaint(Constant.BORDER_COLOR);
    g2d.setStroke(new BasicStroke(2));
    g2d.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
    super.paintComponent(g);
    g2d.dispose();
  }

  @Override
  public Dimension getPreferredSize() {
    Dimension size = super.getPreferredSize();
    size.width += size.height;

    return size;
  }
}
