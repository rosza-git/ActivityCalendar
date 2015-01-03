/**
 * Category Tree.
 * 
 * @author Szalay Roland
 * 
 */
package rosza.activitycalendar;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class CategoryTree extends JTree {
  CategoryTreeModel model;

  public CategoryTree(Category graphNode) {
    super(new CategoryTreeModel(graphNode));
    getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    Icon personIcon = null;
    renderer.setLeafIcon(personIcon);
    renderer.setClosedIcon(personIcon);
    renderer.setOpenIcon(personIcon);
    setCellRenderer(renderer);
  }

  /**
   * Return the selected item in the tree.
   * 
   * @return selected item
   */
  public Category getSelected() {
    TreePath path = getSelectionModel().getSelectionPath();
    if(path != null) {
      return (Category)path.getLastPathComponent();
    }

    return null;
  }
}
