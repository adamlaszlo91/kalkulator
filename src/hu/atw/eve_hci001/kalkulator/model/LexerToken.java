package hu.atw.eve_hci001.kalkulator.model;

/**
 * Stores a Token for the lexer.
 * 
 * @author László Ádám
 *
 */

public class LexerToken {
	private String type;
	private Object value;

	/**
	 * Constructor.
	 * 
	 * @param type
	 *            Type of the token.
	 * @param value
	 *            Value of the token.
	 */
	public LexerToken(String type, Object value) {
		this.type = type;
		this.value = value;
	}

	/**
	 * 
	 * @return The type of the token.
	 */
	public String getType() {
		return type;
	}

	/**
	 * 
	 * @return The value of the token.
	 */
	public Object getValue() {
		return value;
	}

}
