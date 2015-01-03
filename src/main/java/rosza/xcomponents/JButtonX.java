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
import java.awt.RenderingHints;
import javax.swing.JButton;
import rosza.activitycalendar.Constant;

public class JButtonX extends JButton {
  private static final long serialVersionUID = 9032198125140247116L;

  public JButtonX(String text) {
    super(text);
    setBorderPainted(false);
    //addMouseListener(mouseListener);
    setContentAreaFilled(false);
    setCursor(new Cursor(Cursor.HAND_CURSOR));
  }

  @Override
  protected void paintComponent(Graphics g) {
    int roundness = 15;
    Graphics2D g2d = (Graphics2D)g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    GradientPaint gp = new GradientPaint(0, getHeight() / 2, Constant.BG_DARKER_BLUE, 0, getHeight(), Constant.BG_BLUE);
    g2d.setPaint(gp);
    g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, roundness, roundness);
    g2d.setPaint(Constant.BORDER_COLOR);
    g2d.setStroke(new BasicStroke(2));
    g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, roundness, roundness);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    super.paintComponent(g);
    g2d.dispose();
  }

  @Override
  public Dimension getPreferredSize() {
    Dimension size = super.getPreferredSize();
    size.width += size.height;

    return size;
  }
/*
  MouseListener mouseListener = new MouseListener() {
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
  };*/
}
