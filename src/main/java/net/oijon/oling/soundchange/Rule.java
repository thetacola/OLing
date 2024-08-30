package net.oijon.oling.soundchange;


import java.util.ArrayList;
import java.util.Stack;

/**
 * @deprecated This will be part of Phonutate instead
 */
public class Rule {

	String target = "";
	String result = "";
	String env = "";
	ArrayList<Category> categories = new ArrayList<Category>();
	
	public Rule(String target, String result, String env) {
		this.target = target;
		this.result = result;
		this.env = env;
	}
	
	public Rule(String target, String result, String env, ArrayList<Category> categories) {
		this(target, result, env);
		this.categories = categories;
	}
	
	public String getTarget() {
		return target;
	}
	
	public String getResult() {
		return result;
	}
	
	public String getEnv() {
		return env;
	}
	
	public void setTarget(String target) {
		this.target = target;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public void setEnv(String env) {
		this.env = env;
	}
	
	public ArrayList<Category> getCategories() {
		return categories;
	}
	
	public String run(String input) {
		String result = "";
		Stack<Category> usefulCategories = new Stack<Category>();
		
		for (int i = 0; i < env.length(); i++) {
			for (int j = 0; j < categories.size(); j++) {
				if (Character.toString(env.charAt(i)).equals(categories.get(j).getName())) {
					usefulCategories.add(categories.get(j));
					break;
				}
			}
		}
		
		int usefulCatLength = usefulCategories.size();
		
		Stack<String> possibleMatchingString = new Stack<String>();
		
		for (int i = 0; i < usefulCatLength; i++) {
			Category c = usefulCategories.pop();
			for (int j = 0; j < c.getChildren().size(); j++) {
				String envWithTarget = env.replace("_", target);
				
			}
		}
		
		return result;
	}
}
