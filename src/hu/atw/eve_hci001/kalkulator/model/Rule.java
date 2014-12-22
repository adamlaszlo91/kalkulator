package hu.atw.eve_hci001.kalkulator.model;

/**
 * This class stores a rule of a grammar.
 * 
 * @author László Ádám
 *
 */

public class Rule {
	private String left;
	private String right;

	/**
	 * Constructor.
	 * 
	 * @param left
	 *            Left side of the rule.
	 * @param right
	 *            Right side of the rule, the possibilities divided by "|", the
	 *            symbols divided by space.
	 */
	public Rule(String left, String right) {
		this.left = left;
		this.right = right;
	}

	/**
	 * 
	 * @return Left side of the rule.
	 */
	public String getLeft() {
		return left;
	}

	/**
	 * 
	 * @return Right side of the rule.
	 */
	public String getRight() {
		return right;
	}
}
