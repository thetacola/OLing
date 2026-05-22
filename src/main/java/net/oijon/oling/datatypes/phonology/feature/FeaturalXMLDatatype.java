package net.oijon.oling.datatypes.phonology.feature;

import java.util.ArrayList;

import net.oijon.oling.datatypes.XMLDatatype;

public abstract class FeaturalXMLDatatype implements XMLDatatype {

	protected ArrayList<FeaturalXMLDatatype> lowerObj = new ArrayList<>();
	protected ArrayList<Feature> features = new ArrayList<Feature>();
	protected FeatureLevel level;
	
	protected abstract void initFeatures();
	
	public void addFeature(Feature f) {
    	boolean found = false;
    	for (int i = 0; i < features.size(); i++) {
    		if (features.get(i).getName().equals(f.getName())) {
    			found = true;
    			break;
    		}
    	}
    	if (!found) {
    		features.add(f);
    		for (int i = 0; i < lowerObj.size(); i++) {
    			lowerObj.get(i).addFeature(f);
    		}
    	}
    	
    	applyFeatures();
    }
    
    public void removeFeature(String name) {
    	for (int i = 0; i < features.size(); i++) {
    		if (features.get(i).getName().equals(name)) {
    			features.remove(i);
    			break;
    		}
    	}
    	
    	for (int i = 0; i < lowerObj.size(); i++) {
    		lowerObj.get(i).removeFeature(name);
    	}
    	
    	applyFeatures();
    }
    
    public void setFeature(String name, boolean value, FeatureLevel level) {
    	boolean found = false;
    	for (int i = 0; i < features.size(); i++) {
    		if (features.get(i).getName().equals(name)) {
    			features.get(i).setValue(value);
    			for (int j = 0; j < lowerObj.size(); j++) {
    				lowerObj.get(j).setFeature(name, value, level);
    			}
    			found = true;
    			break;
    		}
    	}
    	if (!found) {
    		this.addFeature(new Feature(name, value, level));
    	}
    	
    	applyFeatures();
    }
    
    public ArrayList<Feature> getFeatures() {
    	return new ArrayList<Feature>(features);
    }

    protected ArrayList<Feature> getAllFeatures(ArrayList<? extends FeaturalXMLDatatype> afxd) {
    	ArrayList<Feature> allFeatures = new ArrayList<Feature>(features);
    	for (int i = 0; i < afxd.size(); i++) {
    		ArrayList<Feature> lowerFeatures = afxd.get(i).getFeatures();
    		for (int j = 0; j < lowerFeatures.size(); j++) {
    			boolean found = false;
    	    	for (int k = 0; k < allFeatures.size(); k++) {
    	    		if (allFeatures.get(k).getName().equals(lowerFeatures.get(j).getName())) {
    	    			found = true;
    	    			break;
    	    		}
    	    	}
    	    	if (!found) {
    	    		Feature f = new Feature(lowerFeatures.get(j).getName(), false, lowerFeatures.get(j).getLevel());
    	    		allFeatures.add(f);
    	    	}
    		}
    	}
    	return allFeatures;
    }
    
    protected void applyFeatures() {    	
    	// get features from all other phonemes, only apply those that are true to all
    	ArrayList<Feature> allFeatures = new ArrayList<Feature>();
    	allFeatures.addAll(getAllFeatures(this.lowerObj));
    	
    	for (int i = 0; i < this.lowerObj.size(); i++) {
    		this.lowerObj.get(i).applyFeatures();
    	}
    	
    	for (int i = 0; i < allFeatures.size(); i++) {
    		Feature f = allFeatures.get(i);
    		Feature newF = new Feature(f.getName(), false, f.getLevel());
    		for (int j = 0; j < lowerObj.size(); j++) {
    			// add shouldn't overwrite anything :)
    			lowerObj.get(j).addFeature(newF);
    		}
    	}
    	
    	for (int i = 0; i < features.size(); i++) {
    		Feature f = features.get(i);
    		for (int j = 0; j < lowerObj.size(); j++) {
    			if (f.getValue()) {
    				lowerObj.get(j).setFeature(f.getName(), f.getValue(), f.getLevel());
    			} else {
    				lowerObj.get(j).addFeature(f);
    			}
    		}
    	}
    }
	
}
