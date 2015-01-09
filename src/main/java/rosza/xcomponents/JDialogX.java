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
  private TitleBarX    headerLabel;
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
   * Create dialog UI.
   * 
   * @param title the title of the dialog
   */
  @SuppressWarnings("unchecked")
  private void createUI(String title) {
    headerLabel = new TitleBarX();

    setUndecorated(true);

    rootPane.setBackground(Color.black);
    rootPane.setBorder(new EmptyBorder(2, 2, 2, 2));
    rootPane.setLayout(new BorderLayout());

    contentPane.setBackground(Constant.BG_COLOR);

    headerLabel.setText(title);
    headerLabel.setHorizontalAlignment(JLabel.CENTER);
    headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD).deriveFont(18f));
    headerLabel.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
        headerMouseDragged(e);
      }
    });
    headerLabel.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        headerMousePressed(e);
      }
    });

    rootPane.add(headerLabel, BorderLayout.NORTH);
    rootPane.add(contentPane, BorderLayout.CENTER);
  }

  @Override
  public Container getContentPane() {
    return contentPane;
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

////////
//
//  implement this method to get JDialogX
//  change ... to appropriate return type or void and xxx to the new class's name
//  arguments can be extended
//
//  /**
//   * Set up and show the dialog.
//   * 
//   * @param frameComp determines which frame the dialog depends on;
//   *                  it should be a component in the dialog's controlling frame
//   * @param locationComp null if you want the dialog to come up with its left
//   *                     corner in the center of the screen; otherwise, it should
//   *                     be the component on top of which the dialog should appear
//   * @param title the title of the dialog
//   * @param modal specifies whether dialog blocks user input to other top-level
//   *              windows when shown
//   * @return ...
//   */
//  public static ... showDialog(Component frameComp, Component locationComp, String title, boolean modal) {
//    Frame frame = JOptionPane.getFrameForComponent(frameComp);
//    dialog = new xxx(frame, locationComp, title, modal);
//    dialog.setVisible(true);
//
//    return ...;
//  }
//
////////
//
//  implement this method to get whether this JDialogX is visible
//
//  public static boolean getVisible() {
//    return dialog == null ? false : dialog.isVisible();
//  }
}
