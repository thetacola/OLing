package net.oijon.oling.datatypes.phonology.feature.sonorancy;

import java.util.ArrayList;

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
	
	public SonorancyNode() {
		// for terminals
	}
	
	public SonorancyNode(Element e) throws InvalidXMLException {
		fromXML(e);
	}

	public int getValue(Phoneme p) {
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
	
	public int getPhonemeValue(Phoneme p) {
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
			
			if (left != null) {
				leftE.appendChild(doc.importNode(left.toXML(), true));
			}
			root.appendChild(leftE);
			
			Element rightE = doc.createElement("right");
			if (right != null) {
				leftE.appendChild(doc.importNode(right.toXML(), true));
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
				switch (n.getNodeName()) {
					case "feature":
						featureName = n.getNodeValue();
						break;
					case "left":
						if (n.getNodeType() == Node.ELEMENT_NODE) {
							left = new SonorancyNode();
							left.parent = this;
							Element nodeElem = (Element) n;
							String type = nodeElem.getAttribute("type");
							if (type.equals("pos")) {
								featureValue = true;
							} else if (type.equals("neg")) {
								featureValue = false;
							}
							NodeList leftChildren = nodeElem.getChildNodes();
							for (int j = 0; j < leftChildren.getLength(); j++) {
								Node lchild = leftChildren.item(j);
								if (lchild.getNodeType() == Node.ELEMENT_NODE) {
									Element lchildE = (Element) lchild;
									if (lchildE.getTagName().equals("son-feature")) {
										left.fromXML(lchildE);
									}
								}
							}
						}
						
						break;
					case "right":
						right = new SonorancyNode();
						right.parent = this;
						if (n.getNodeType() == Node.ELEMENT_NODE) {
							Element nodeElem = (Element) n;
							NodeList rightChildren = nodeElem.getChildNodes();
							for (int j = 0; j < rightChildren.getLength(); j++) {
								Node rchild = rightChildren.item(j);
								if (rchild.getNodeType() == Node.ELEMENT_NODE) {
									Element rchildE = (Element) rchild;
									if (rchildE.getTagName().equals("son-feature")) {
										right.fromXML(rchildE);
									}
								}
							}
						}
				}
			}
			
			if (!featureName.equals("")) {
				feature = new Feature(featureName, featureValue, FeatureLevel.SYSTEM);
			}
		}
	}
}
