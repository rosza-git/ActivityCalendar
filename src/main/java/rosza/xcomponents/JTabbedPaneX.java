/**
 * Extended BasicScrollBarUI UI component.
 * 
 * @see http://stackoverflow.com/questions/7054466/how-can-i-change-the-shape-of-a-jtabbedpane-tab/7056093#7056093
 */
package rosza.xcomponents;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import rosza.activitycalendar.Constant;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class JTabbedPaneX  extends BasicTabbedPaneUI {
  private Polygon shape;        // tab shape
  private final int wSpacer = 5;
  private final int hSpacer = 5;

  public static ComponentUI createUI(JComponent c) {
    return new JTabbedPaneX();
  }

  @Override
  protected void installDefaults() {
    super.installDefaults();
  }

  @Override
  protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
    Graphics2D g2D = (Graphics2D) g;
    GradientPaint gradientShadow;
    int xp[];     // x points for the shape
    int yp[];     // y points for the shape
    switch (tabPlacement) {
      case LEFT:
        xp = new int[]{x, x, x + w, x + w, x};
        yp = new int[]{y, y + h, y + h, y, y};
        gradientShadow = new GradientPaint(x, y, Constant.BG_DARKER_BLUE, x, y + h, Constant.BG_BLUE);
        break;
      case RIGHT:
        xp = new int[]{x, x, x + w , x + w, x};
        yp = new int[]{y, y + h, y + h, y, y};
        gradientShadow = new GradientPaint(x, y, Constant.BG_DARKER_BLUE, x, y + h, Constant.BG_BLUE);
        break;
      case BOTTOM:
        xp = new int[]{x, x, x + w, x + w};
        yp = new int[]{y + h, y, y, y + h};
        gradientShadow = new GradientPaint(x, y, Constant.BG_DARKER_BLUE, x, y + h, Constant.BG_BLUE);
        break;
      case TOP:
      default:
        xp = new int[]{x, x, x + w, x + w, x};
        yp = new int[]{y + h, y, y, y + h, y + h};
        gradientShadow = new GradientPaint(x, y, Constant.BG_DARKER_BLUE, x, y + h, Constant.BG_BLUE);
        break;
    }
    shape = new Polygon(xp, yp, xp.length);
    if(isSelected) {
      g2D.setPaint(gradientShadow);
    }
    else {
      if(tabPane.isEnabled() && tabPane.isEnabledAt(tabIndex)) {
        GradientPaint gradientShadowTmp = new GradientPaint(x, y, Constant.BG_DARKER_COLOR, x, y + h, Constant.BG_COLOR);
        g2D.setPaint(gradientShadowTmp);
      }
      else {
        GradientPaint gradientShadowTmp = new GradientPaint(x, y, Constant.BG_DARKER_COLOR, x, y + h, Constant.BG_COLOR);
        g2D.setPaint(gradientShadowTmp);
      }
    }
    g2D.fill(shape);
    if(runCount > 1) {
      g2D.setColor(hasAlpha(getRunForTab(tabPane.getTabCount(), tabIndex) - 1));
      g2D.fill(shape);
    }
    g2D.fill(shape);
  }

  @Override
  protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
    return wSpacer + super.calculateTabWidth(tabPlacement, tabIndex, metrics);
  }

  @Override
  protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
    return hSpacer + super.calculateTabHeight(tabPlacement, tabIndex, fontHeight);
  }

  @Override
  protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
    g.setColor(Constant.BG_DARKER_BLUE);
    g.drawPolygon(shape);
  }

  protected Color hasAlpha(int line) {
    int alfa = 0;
    if(line >= 0) {
      alfa = 50 + (line > 7 ? 70 : 10 * line);
    }

    return new Color(0, 0, 0, alfa);
  }
}