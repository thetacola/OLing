package net.oijon.oling.tree;

public abstract class TreeNode {
	
	protected String name;
	protected String data;
	protected final int height = 50;
	protected final int width = 50;
	
	public TreeNode() {
		this.name = "";
		this.data = "";
	}
	
	public TreeNode(String name) {
		this.name = name;
		this.data = "";
	}
	
	public TreeNode(String name, String data) {
		this.name = name;
		this.data = data;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getData() {
		return this.data;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof TreeNode) {
			TreeNode tn = (TreeNode) o;
			if (tn.name.equals(this.name) & tn.data.equals(this.data)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "[" + this.name + " " + this.data + "]";
	}

}
