package net.oijon.oling.datatypes.phonology.feature.sonorancy;

import java.util.ArrayList;

import net.oijon.oling.datatypes.phonology.feature.Feature;
import net.oijon.oling.datatypes.phonology.table.Phoneme;

public class SonorancyNode {

	private Feature feature;
	protected SonorancyNode parent;
	protected SonorancyNode left;
	protected SonorancyNode right;
	private int value = -1;
	
	public SonorancyNode(Feature feature) {
		this.feature = feature;
	}
	
	protected SonorancyNode() {
		// for terminals
	}
	
	protected int getValue(Phoneme p) {
		if (left == null && right == null) {
			return value;
		} else if (left != null && right != null) {
			Feature pFeature = p.getFeatures().get(feature.getName());
			if (pFeature.getValue() == feature.getValue()) {
				return left.getValue(p);
			}
		// these two normally shouldn't happen, but could due to malformed files
		} else if (left != null && right == null) {
			return left.getValue(p);
		}
		// should only ever get here if left is null and right isn't
		// would've made it more explicit w/ another else-if, but the compiler
		// wasn't all too happy with that
		return right.getValue(p);
	}
	
	public void setLeft(SonorancyNode left) {
		this.left = left;
		left.parent = this;
	}
	
	public void setRight(SonorancyNode right) {
		this.right = right;
		right.parent = this;
	}
	
	protected void calcValue(int parentValue, boolean isLeft, int currentDepth, int maxDepth, 
			ArrayList<SonorancyNode> passedNodes) {
		// this implementation only allows for trees of 32 depth, which should be fine for just
		// about any system
		
		if (checkForThis(passedNodes)) {
			return;
		}
		passedNodes.add(this);
		
		int binaryDigit = maxDepth - currentDepth;
		if (isLeft) {
			value = (int) (parentValue + (1 << binaryDigit));
		} else {
			value = parentValue;
		}
		if (left != null) {
			left.calcValue(value, true, currentDepth + 1, maxDepth, passedNodes);
		}
		if (right != null) {
			right.calcValue(value, false, currentDepth + 1, maxDepth, passedNodes);
		}
	}
	
	protected int findDeepest(ArrayList<SonorancyNode> passedNodes) {
		int deepest = 0;
		
		// Prevents any loops, possible if a program erroneously makes a child node the same as a parent
		if (checkForThis(passedNodes)) {
			return deepest;
		}
		passedNodes.add(this);
		
		if (left != null) {
			deepest = left.findDeepest(passedNodes);
		}
		if (right != null) {
			int rightDeepest = right.findDeepest(passedNodes);
			if (rightDeepest > deepest) {
				deepest = rightDeepest;
			}
		}
		return deepest + 1;
	}
	
	private boolean checkForThis(ArrayList<SonorancyNode> nodes) {
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i) == this) {
				return true;
			}
		}
		return false;
	}
	
	protected int getPhonemeValue(Phoneme p) {
		if (left == null && right == null) {
			return value;
		} else if (left != null && right == null) {
			return left.getPhonemeValue(p);
		} else if (left == null && right != null) {
			return right.getPhonemeValue(p);
		}
		
		Feature phonemeFeature = p.getFeatures().get(feature.getName());
		boolean matches;
		if (phonemeFeature != null) {
			matches = !(phonemeFeature.getValue() ^ feature.getValue());
		} else {
			// The feature cannot be the same if the phoneme doesn't have it
			matches = false;
		}
		
		if (matches) {
			return left.getPhonemeValue(p);
		} else {
			return right.getPhonemeValue(p);
		}
		
	}
}
