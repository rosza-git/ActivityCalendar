/**
 * Extended JDialog component.
 * 
 * @author Szalay Roland
 * 
 */
package rosza.xcomponents;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import rosza.activitycalendar.Constant;

public class JDialogX extends JDialog {
  // UI variables
  private TitleBarX    titleLabel;
  private final JPanel contentPane;

  // Mouse variables
  private int mouseX;
  private int mouseY;

  /**
   * Creates a customized JDialog.
   * 
   * @param owner the frame from which the dialog is displayed
   * @param title the title of the dialog
   * @param modal specifies whether dialog blocks user input to other top-level
   *              windows when shown
   */
  public JDialogX(Frame owner, String title, boolean modal) {
    super(owner, title, modal);

    contentPane = new JPanel();
    // Create UI.
    createUI(title);
  }

  /**
   * Create UI.
   * 
   * @param title the title of the dialog
   */
  @SuppressWarnings("unchecked")
  private void createUI(String title) {
    titleLabel = new TitleBarX();

    setUndecorated(true);

    rootPane.setBackground(Constant.BORDER_COLOR);
    rootPane.setBorder(new EmptyBorder(2, 2, 2, 2));
    rootPane.setLayout(new BorderLayout());

    contentPane.setBackground(Constant.BG_COLOR);

    titleLabel.setText(title);
    titleLabel.setHorizontalAlignment(JLabel.CENTER);
    titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD).deriveFont(18f));
    titleLabel.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
        headerMouseDragged(e);
      }
    });
    titleLabel.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        headerMousePressed(e);
      }
    });

    rootPane.add(titleLabel, BorderLayout.NORTH);
    rootPane.add(contentPane, BorderLayout.CENTER);
  }

  @Override
  public Container getContentPane() {
    return contentPane;
  }

  @Override
  public void setTitle(String title) {
    titleLabel.setText(title);
  }

  @Override
  public String getTitle() {
    return titleLabel.getText();
  }

  // Handle dialog dragging
  private void headerMouseDragged(MouseEvent e) {
    int x = e.getXOnScreen();
    int y = e.getYOnScreen();

    setLocation(x - mouseX, y - mouseY);
  }

  private void headerMousePressed(MouseEvent e) {
    mouseX = e.getX();
    mouseY = e.getY();
  }
}
