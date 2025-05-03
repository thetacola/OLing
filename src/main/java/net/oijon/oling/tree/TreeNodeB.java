package net.oijon.oling.tree;

public class TreeNodeB extends TreeNode {

	public static final TreeNodeB NULL = new TreeNodeB("null", "null");
	
	protected TreeNodeB leftNode = TreeNodeB.NULL;
	protected TreeNodeB rightNode = TreeNodeB.NULL;
	
	public TreeNodeB() {
		super();
	}
	
	public TreeNodeB(String name) {
		super(name);
	}
	
	public TreeNodeB(String name, String data) {
		super(name, data);
	}
	
	public TreeNode getLeftNode() {
		return leftNode;
	}
	
	public void setLeftNode(TreeNodeB leftNode) {
		this.leftNode = leftNode;
	}
	
	public TreeNode getRightNode() {
		return rightNode;
	}
	
	public void setRightNode(TreeNodeB rightNode) {
		this.rightNode = rightNode;
	}
	
	public void removeLeftNode() {
		this.leftNode = TreeNodeB.NULL;
	}
	
	public void removeRightNode() {
		this.rightNode = TreeNodeB.NULL;
	}
	
	public int getWidth() {
		int width = 0;
		if (leftNode.equals(TreeNodeB.NULL) & rightNode.equals(TreeNodeB.NULL)) {
			width = this.width;
		} else {
			if (!leftNode.equals(TreeNodeB.NULL)) {
				width += leftNode.getWidth();
			}
			if (!rightNode.equals(TreeNodeB.NULL)) {
				width += rightNode.getWidth();
			}
		}
		return width;
	}
	
	@Override
	public String toString() {
		String returnString = "[" + name;
		
		if (!data.equals("")) {
			returnString += " " + data;
		}
		
		if (!leftNode.equals(TreeNodeB.NULL)) {
			returnString += " " + leftNode.toString();
		}		
		if (!rightNode.equals(TreeNodeB.NULL)) {
			returnString += " " + rightNode.toString();
		}
		returnString += "]";
		return returnString;
	}
}
