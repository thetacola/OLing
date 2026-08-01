package net.oijon.oling.datatypes.phonology.feature.sonorancy;

import java.util.ArrayList;

import net.oijon.oling.datatypes.phonology.feature.Feature;
import net.oijon.oling.datatypes.phonology.table.Phoneme;

public class SonorancyTree extends SonorancyNode {

	public SonorancyTree(Feature feature) {
		super(feature);
		this.left = new SonorancyNode();
		this.right = new SonorancyNode();
	}
	
	public void calcAllValues() {
		ArrayList<SonorancyNode> visited = new ArrayList<>();
		visited.add(this);
		int maxDepth = super.findDeepest(visited);
		visited.clear();
		visited.add(this);
		
		if (left != null) {
			left.calcValue(0, true, 1, maxDepth, visited);
		}
		if (right != null) {
			right.calcValue(0, false, 1, maxDepth, visited);
		}
	}
	
	@Override
	public int getPhonemeValue(Phoneme p) {
		return super.getPhonemeValue(p);
	}

}
