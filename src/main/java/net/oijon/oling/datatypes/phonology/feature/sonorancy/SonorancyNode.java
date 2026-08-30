package net.oijon.oling.datatypes.phonology.feature.sonorancy;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.phonology.feature.Feature;
import net.oijon.oling.datatypes.phonology.feature.FeatureLevel;
import net.oijon.oling.datatypes.phonology.surface.Sound;
import net.oijon.oling.datatypes.phonology.table.Phoneme;
import net.oijon.oling.info.Info;
import net.oijon.olog.Log;

public class SonorancyNode {

	private Feature feature;
	protected SonorancyNode parent;
	protected SonorancyNode left;
	protected SonorancyNode right;
	private int value = -1;
	
	private static Log log = Info.log;
	
	public SonorancyNode(Feature feature) {
		this.feature = feature;
	}
	
	public SonorancyNode() {
		// for terminals
	}
	
	public SonorancyNode(Element e) throws InvalidXMLException {
		fromXML(e);
	}

	@Deprecated
	public int getPhonemeValue(Phoneme p) {
		return getValue(p);
	}
	
	public int getValue(Phoneme p) {
		return getValue(p.getFeatures());
	}
	
	public int getValue(Sound s) {
		return getValue(s.getFeatures());
	}
	
	public int getValue(HashMap<String, Feature> features) {
		if (left == null && right == null) {
			return value;
		} else if (left != null && right == null) {
			return left.getValue(features);
		} else if (left == null && right != null) {
			return right.getValue(features);
		}
		
		Feature phonemeFeature = features.get(feature.getName());
		boolean matches;
		if (phonemeFeature != null) {
			matches = (phonemeFeature.getValue() == feature.getValue());
		} else {
			// The feature cannot be the same if the phoneme doesn't have it
			matches = false;
		}
		
		if (matches) {
			return left.getValue(features);
		} else {
			return right.getValue(features);
		}
	}
	
	public void setLeft(SonorancyNode left) {
		this.left = left;
		left.parent = this;
	}
	
	public void setRight(SonorancyNode right) {
		this.right = right;
		right.parent = this;
	}
	
	public SonorancyNode getLeft() {
		return left;
	}
	
	public SonorancyNode getRight() {
		return right;
	}
	
	public void calcValue(int parentValue, boolean isLeft, int currentDepth, int maxDepth, 
			ArrayList<SonorancyNode> passedNodes) {
		// this implementation only allows for trees of 32 depth, which should be fine for just
		// about any system
		
		if (checkForThis(passedNodes)) {
			log.err("Loop in sonorancy tree detected!");
			return;
		} else if (currentDepth > 30) {
			log.err("Sonorancy tree deeper than maximum! Comparisons will be inaccurate...");
		}
		passedNodes.add(this);
		
		int binaryDigit = maxDepth - currentDepth - 1;
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
	
	public int findDeepest(ArrayList<SonorancyNode> passedNodes) {
		int deepest = 0;
		
		// Prevents any loops, possible if a program erroneously makes a child node the same as a parent
		if (checkForThis(passedNodes)) {
			log.err("Loop detected in sonorancy tree!");
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
	
	public Element toXML() throws ParserConfigurationException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.newDocument();
		Element root = doc.createElement("son-feature");
		
		if (feature != null) {
			Element featureE = doc.createElement("feature");
			featureE.setTextContent(feature.getName());
			root.appendChild(featureE);
			
			Element leftE = doc.createElement("left");
			if (feature.getValue()) {
				leftE.setAttribute("type", "pos");
			} else {
				leftE.setAttribute("type", "neg");
			}
			
			if (left != null && left.feature != null) {
				leftE.appendChild(doc.importNode(left.toXML(), true));
			}
			root.appendChild(leftE);
			
			Element rightE = doc.createElement("right");
			if (right != null && right.feature != null) {
				rightE.appendChild(doc.importNode(right.toXML(), true));
			}
			root.appendChild(rightE);
			
		}
		
		
		return root;
	}
	
	public void fromXML(Element e) throws InvalidXMLException {
		if (e.getTagName().equals("son-feature")) {
			NodeList nodes = e.getChildNodes();
			
			String featureName = "";
			boolean featureValue = false;
			for (int i = 0; i < nodes.getLength(); i++) {
				Node n = nodes.item(i);
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element ne = (Element) n;
					switch (ne.getTagName()) {
						case "feature":
							featureName = ne.getTextContent();
							break;
						case "left":
							left = new SonorancyNode();
							String type = ne.getAttribute("type");
							if (type.equals("pos")) {
								featureValue = true;
							} else if (type.equals("neg")) {
								featureValue = false;
							}
							NodeList leftChildren = ne.getChildNodes();
							for (int j = 0; j < leftChildren.getLength(); j++) {
								Node lchild = leftChildren.item(j);
								if (lchild.getNodeType() == Node.ELEMENT_NODE) {
									Element lchildE = (Element) lchild;
									if (lchildE.getTagName().equals("son-feature")) {
										left = new SonorancyNode(lchildE);
									}
								}
							}
							left.parent = this;
							break;
						case "right":
							right = new SonorancyNode();
							
							NodeList rightChildren = ne.getChildNodes();
							for (int j = 0; j < rightChildren.getLength(); j++) {
								Node rchild = rightChildren.item(j);
								if (rchild.getNodeType() == Node.ELEMENT_NODE) {
									Element rchildE = (Element) rchild;
									if (rchildE.getTagName().equals("son-feature")) {
										right = new SonorancyNode(rchildE);
									}
								}
							}
							right.parent = this;
							break;
					}
			
					if (!featureName.equals("")) {
						feature = new Feature(featureName, featureValue, FeatureLevel.SYSTEM);
					}
				}
			}
		} else {
			throw new InvalidXMLException("Expected son-feature, got " + e.getTagName());
		}
	}
			
	
	@Override
	public boolean equals (Object o) {
		if (o instanceof SonorancyNode) {
			SonorancyNode sn = (SonorancyNode) o;
			boolean featureMatches = false;
			if (feature != null && sn.feature != null) {
				featureMatches = (feature.getName().equals(sn.feature.getName()) &&
						feature.getValue() == sn.feature.getValue());
			} else if (feature == null && sn.feature == null) {
				featureMatches = true;
			}
			
			boolean leftMatches = false;
			if (left != null && sn.left != null) {
				leftMatches = left.equals(sn.left);
			} else if (left == null && sn.left == null) {
				leftMatches = true;
			}
			
			boolean rightMatches = false;
			if (right != null && sn.right != null) {
				rightMatches = right.equals(sn.right);
			} else if (right == null && sn.right == null) {
				rightMatches = true;
			}
			return (leftMatches & rightMatches & featureMatches);
		}
		return false;
	}
}
