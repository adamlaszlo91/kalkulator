package hu.atw.eve_hci001.kalkulator.model;

import java.util.ArrayList;

/**
 * This class stores a parse three node.
 * 
 * @author László Ádám
 *
 */

public class ParseTree {
	private String type;
	private Object value;
	private ArrayList<ParseTree> children;

	/**
	 * Constructor.
	 * 
	 * @param type
	 *            Type of the node.
	 * @param value
	 *            Value of the node.
	 */
	public ParseTree(String type, Object value) {
		this.type = type;
		this.value = value;
		children = new ArrayList<ParseTree>();
	}

	@Override
	public String toString() {
		String s = type + " [" + value.toString() + "](";
		for (ParseTree child : children) {
			s += child;
		}
		s += ")";
		return s;
	}

	/**
	 * 
	 * @return The type of the node.
	 */
	public String getType() {
		return type;
	}

	/**
	 * 
	 * @param type
	 *            The type of the node.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 
	 * @return The value of the node.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * 
	 * @param value
	 *            the value of the node.
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * 
	 * @return The children of the node.
	 */
	public ArrayList<ParseTree> getChildren() {
		return children;
	}
}
