package net.oijon.oling.datatypes;

/**
 * An exception thrown when a given XML node is not the one expected by the datatype.
 * This prevents shenanigans with subnodes that may be named the same between various
 * datatypes, thus preventing the creation of a corrupt subnode.
 */
public class InvalidRootNodeException extends Exception {

	
	private static final long serialVersionUID = 5846608809359030229L;

	public InvalidRootNodeException() {
		super("Root node for provided document not root node needed for parsing.");
	}
	
	public InvalidRootNodeException(String msg) {
		super("Root node for provided document not root node needed for parsing - " + msg);
	}
}
