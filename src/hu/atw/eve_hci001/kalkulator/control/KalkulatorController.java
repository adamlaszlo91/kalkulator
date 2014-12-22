package hu.atw.eve_hci001.kalkulator.control;

import hu.atw.eve_hci001.kalkulator.view.KalkulatorGUI;

/**
 * Controller class for the Kalkulator.
 * 
 * @author László Ádám
 *
 */

public class KalkulatorController {
	private KalkulatorGUI gui;
	private Interpreter interpreter;

	/**
	 * Constructor.
	 */
	public KalkulatorController() {
		interpreter = new Interpreter(this);
		gui = new KalkulatorGUI(this);

	}

	/**
	 * Sets the input for interpreting.
	 * 
	 * @param input
	 *            The expression to be interpreted.
	 */
	public void setInput(String input) {
		interpreter.exec(input);
	}

	/**
	 * Sets the output (answer) and sends to the gui.
	 * 
	 * @param d
	 *            The answer.
	 */
	public void setOutput(double d) {
		gui.setOutput("ans= " + d);
	}

	/**
	 * Prints an error on the gui.
	 * 
	 * @param error
	 *            The error description.
	 */
	public void errorOccurred(String error) {
		gui.setOutput(error);
	}

}
