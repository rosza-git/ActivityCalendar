/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rosza.activitycalendar;

import java.util.ArrayList;
import java.util.Vector;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class CategoryTreeModel implements TreeModel {
    private ArrayList<TreeModelListener> treeModelListeners = new ArrayList<>();
    private final Category rootCategory;

    public CategoryTreeModel(Category root) {
      rootCategory = root;
    }

    /**
     * Adds a listener for the TreeModelEvent posted after the tree changes.
     */
    @Override
    public void addTreeModelListener(TreeModelListener l) {
      treeModelListeners.add(l);
    }

    /**
     * Returns the child of parent at index index in the parent's child array.
     * @param index
     */
    @Override
    public Object getChild(Object parent, int index) {
      Category p = (Category)parent;

      return p.getSubAt(index);
    }

    /**
     * Returns the number of children of parent.
     */
    @Override
    public int getChildCount(Object parent) {
      Category p = (Category)parent;

      return p.getSubCount();
    }

    /**
     * Returns the index of child in parent.
     */
    @Override
    public int getIndexOfChild(Object parent, Object child) {
      Category p = (Category)parent;

      return p.getIndexOfSub((Category)child);
    }

    /**
     * Returns the root of the tree.
     */
    @Override
    public Object getRoot() {
      return rootCategory;
    }

    /**
     * Returns true if node is a leaf.
     */
    @Override
    public boolean isLeaf(Object node) {
      Category p = (Category)node;

      return p.getSubCount() == 0;
    }

    /**
     * Removes a listener previously added with addTreeModelListener().
     */
    @Override
    public void removeTreeModelListener(TreeModelListener l) {
      treeModelListeners.remove(l);
    }

    /**
     * Messaged when the user has altered the value for the item
     * identified by path to newValue.  Not used by this model.
     */
    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
      System.out.println("*** valueForPathChanged : " + path + " --> " + newValue);
    }
}
