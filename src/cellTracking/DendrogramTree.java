package cellTracking;

import javax.swing.ImageIcon;

public class DendrogramTree {
// Root node pointer. Will be null for an empty tree.
private TreeNode root;


private static class TreeNode {
 TreeNode left;
 TreeNode right;
 ImageIcon icon;

 TreeNode(ImageIcon newdata) {
   left = null;
   right = null;
   icon = newdata;
 }
}

public void DendrogramTree() {
 root = null;
}



public void insert(ImageIcon data,String pos) {
 root = insert(root, data, pos);
}

private TreeNode insert(TreeNode node, ImageIcon imgicon,String pos) {
 if (node==null) {
   node = new TreeNode(imgicon);
 }
 else {
   if (pos==null) {
     node.left = insert(node.left, imgicon, pos);
   }
   else {
     node.right = insert(node.right, imgicon, pos);
   }
 }

 return(node); // in any case, return the new pointer to the caller
}


public static void main(String args[])
{
	
	
	
}
}