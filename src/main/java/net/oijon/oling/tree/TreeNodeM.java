package net.oijon.oling.tree;

import java.util.ArrayList;

public class TreeNodeM extends TreeNode {

	protected ArrayList<TreeNodeM> children = new ArrayList<TreeNodeM>();
	
	public TreeNodeM() {
		super();
	}
	
	public TreeNodeM(String name) {
		super(name);
	}
	
	public TreeNodeM(String name, String data) {
		super(name, data);
	}
	
	public ArrayList<TreeNodeM> getChildren() {
		return children;
	}
	
	public int getWidth() {
		int neededWidth = 0;
		if (children.size() == 0) {
			neededWidth = width;
		} else {
			for (int i = 0; i < children.size(); i++) {
				neededWidth += children.get(i).getWidth();
			}
		}
		return neededWidth;
	}
	
	@Override
	public String toString() {
		String returnString = "[" + name;
		if (!data.equals("")) {
			returnString += " " + data;
		}
		for (int i = 0; i < children.size(); i++) {
			returnString += " " + children.get(i).toString();
		}
		returnString += "]";
		return returnString;
	}
}
