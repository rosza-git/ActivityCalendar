/**
 * Extended JPanel UI component.
 * 
 * @author Szalay Roland
 * 
 */
package rosza.xcomponents;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import rosza.activitycalendar.Constant;

public class JPanelX extends JPanel {
  public JPanelX() {
    setBorder(new EmptyBorder(0, 0, 0, 0));
    setOpaque(false);
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    int roundness = 20;
    Graphics2D g2d = (Graphics2D)g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setPaint(Constant.BG_COLOR);
    g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, roundness, roundness);
    g2d.setPaint(Constant.BORDER_COLOR);
    g2d.setStroke(new BasicStroke(2));
    g2d.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, roundness, roundness);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
  }
}

