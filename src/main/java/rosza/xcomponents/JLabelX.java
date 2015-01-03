/**
 * Extended JLabel UI component.
 * 
 * @author Szalay Roland
 * 
 */
package rosza.xcomponents;

import java.awt.BasicStroke;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import rosza.activitycalendar.Constant;

public class JLabelX extends JLabel {
  private int color = -1;

  public JLabelX() {
    setBorder(new EmptyBorder(2, 5, 2, 5));
  }

  public JLabelX(int color) {
    this.color = color;
    setBorder(new EmptyBorder(2, 5, 2, 5));
  }

  public JLabelX(int top, int left, int bottom, int right) {
    setBorder(new EmptyBorder(top, left, bottom, right));
  }

  public JLabelX(int top, int left, int bottom, int right, int color) {
    this.color = color;
    setBorder(new EmptyBorder(top, left, bottom, right));
  }

  @Override
  public void paint(Graphics g) {
    int roundness = 20;
    Graphics2D g2d = (Graphics2D)g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    GradientPaint gp;
    switch(color) {
      case Constant.WHITE:
        gp = new GradientPaint(0, getHeight() / 2, Constant.BG_COLOR, 0, getHeight(), Constant.BG_DARKER_COLOR);
        break;
      case Constant.BLUE:
      default:
        gp = new GradientPaint(0, getHeight() / 2, Constant.BG_DARKER_BLUE, 0, getHeight(), Constant.BG_BLUE);
        break;
    }
    g2d.setPaint(gp);
    g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, roundness, roundness);
    g2d.setPaint(Constant.BORDER_COLOR);
    g2d.setStroke(new BasicStroke(2));
    g2d.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, roundness, roundness);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    super.paint(g);
    g.dispose();
  }
}

