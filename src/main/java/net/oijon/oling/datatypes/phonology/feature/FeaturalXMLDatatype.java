package net.oijon.oling.datatypes.phonology.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;
import net.oijon.oling.datatypes.XMLDatatype;
import net.oijon.oling.datatypes.phonology.table.Phoneme;

public abstract class FeaturalXMLDatatype implements XMLDatatype {

	protected ArrayList<FeaturalXMLDatatype> lowerObj = new ArrayList<>();
	protected HashMap<String, Feature> features = new HashMap<String, Feature>();
	protected FeatureLevel level;
	
	protected abstract void initFeatures();
	
	public void addFeature(Feature f) {
    	features.putIfAbsent(f.getName(), f);
    	applyFeatures();
    }
    
    public void removeFeature(String name) {
    	features.remove(name);
    	applyFeatures();
    }
    
    public void setFeature(String name, boolean value, FeatureLevel level) {
    	features.put(name, new Feature(name, value, level));
    	applyFeatures();
    }
    
    public HashMap<String, Feature> getFeatures() {
    	applyFeatures();
    	return features;
    }
    
    /**
	 * Finds a particular phoneme object from a given string
	 * @param value The string representation of the wanted phoneme
	 * @return The phoneme that matches the given string.
	 */
    public Phoneme find(String value) {
    	for (int i = 0; i < lowerObj.size(); i++) {
    		if (lowerObj.get(i).find(value) != null) {
    			return lowerObj.get(i).find(value);
    		}
    	}
    	return null;
    }
    
    public ArrayList<Phoneme> getAllPhonemes() {
    	ArrayList<Phoneme> list = new ArrayList<>();
    	
    	for (int i = 0; i < lowerObj.size(); i++) {
    		FeaturalXMLDatatype fxd = lowerObj.get(i);
    		list.addAll(fxd.getAllPhonemes());
    	}
    	
    	return list;
    }
    
    protected void applyFeatures() {    	
    	// get features from all other lower datatypes, only apply those that are true to all
    	for (FeaturalXMLDatatype fxd : lowerObj) {
    		// set true features to lower datatype
    		features.forEach(new BiConsumer<String, Feature>() {
   				@Override
    			public void accept(String name, Feature f) {
    				if (f.getValue()) {
    					fxd.addFeature(f);
   					} else {
    					fxd.addFeature(new Feature(name, false, FeatureLevel.SYSTEM));
   					}
    			}
    		});
    		// get missing features from lower datatype
			fxd.getFeatures().forEach(new BiConsumer<String, Feature>() {
				@Override
				public void accept(String name, Feature f) {
					// false features should be system level
					features.putIfAbsent(name, new Feature(name, false, FeatureLevel.SYSTEM));
				}
			});
    	}
    }
	
}
