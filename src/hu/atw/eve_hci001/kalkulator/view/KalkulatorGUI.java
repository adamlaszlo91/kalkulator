package hu.atw.eve_hci001.kalkulator.view;

import hu.atw.eve_hci001.kalkulator.control.KalkulatorController;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Graphical interface for the Kalkulator.
 * 
 * @author László Ádám
 *
 */

public class KalkulatorGUI {
	private JTextField inputField;
	private JTextField outputField;
	private ArrayList<Character> allowedCharacters;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            The controller object.
	 */
	public KalkulatorGUI(KalkulatorController controller) {
		/* setting the allowed input characters */
		allowedCharacters = new ArrayList<Character>();
		allowedCharacters.add('0');
		allowedCharacters.add('1');
		allowedCharacters.add('2');
		allowedCharacters.add('3');
		allowedCharacters.add('4');
		allowedCharacters.add('5');
		allowedCharacters.add('6');
		allowedCharacters.add('7');
		allowedCharacters.add('8');
		allowedCharacters.add('9');
		allowedCharacters.add('+');
		allowedCharacters.add('-');
		allowedCharacters.add('*');
		allowedCharacters.add('/');
		allowedCharacters.add('%');
		allowedCharacters.add('(');
		allowedCharacters.add(')');
		allowedCharacters.add('.');

		JFrame frame = new JFrame("Kalkulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		frame.setContentPane(panel);

		/* input fiels */
		inputField = new JTextField();
		inputField.setPreferredSize(new Dimension(300, 20));
		String toolTipText = "Allowed inputs: 0-9, '+', '-', '/', '*', '%', '(', ')'.";
		inputField.setToolTipText(toolTipText);
		inputField.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					e.consume();
					String inputText = inputField.getText();
					/* check for Ctrl-V */
					for (int i = 0; i < inputText.length(); i++) {
						if (!allowedCharacters.contains(inputText.charAt(i))) {
							outputField
									.setText("Haha, nice try, don't copy-paste these unallowed things!");
							return;
						}
					}

					controller.setInput(inputField.getText());
				} else {
					/* swallow unallowed characters */
					if (!allowedCharacters.contains(e.getKeyChar()))
						e.consume();
				}
			}
		});
		frame.getContentPane().add(inputField);

		/* output field */
		outputField = new JTextField();
		outputField.setPreferredSize(new Dimension(300, 20));
		outputField.setHorizontalAlignment(JTextField.RIGHT);
		outputField.setEditable(false);
		frame.getContentPane().add(outputField);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	/**
	 * Sets and prints the output in the output field.
	 * 
	 * @param outPut
	 *            The output to be printed.
	 */
	public void setOutput(String outPut) {
		outputField.setText(outPut);
	}

}
