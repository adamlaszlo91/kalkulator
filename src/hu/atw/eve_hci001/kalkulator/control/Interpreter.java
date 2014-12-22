package hu.atw.eve_hci001.kalkulator.control;

import hu.atw.eve_hci001.kalkulator.model.Grammar;
import hu.atw.eve_hci001.kalkulator.model.LexerToken;
import hu.atw.eve_hci001.kalkulator.model.ParseTree;
import hu.atw.eve_hci001.kalkulator.model.Rule;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Interpreter for a simple calculator. Assumes that the input is checked before
 * the interpretation starts.
 * 
 * @author László Ádám
 *
 */

public class Interpreter {
	private KalkulatorController controller;
	private String input;
	private ArrayList<LexerToken> lexerTokens;
	private ParseTree parseRoot;
	private Stack<Double> evalStack;
	private Grammar grammar;
	private boolean errorOccurred;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            The controller class.
	 */
	public Interpreter(KalkulatorController controller) {
		this.controller = controller;
		lexerTokens = new ArrayList<LexerToken>();
		evalStack = new Stack<Double>();
		grammar = new Grammar();
		grammar.printParsingTale();
	}

	/**
	 * Executes the input (interpretation starts here).
	 * 
	 * @param input
	 *            The input to be interpreted.
	 */
	public void exec(String input) {
		this.input = input;
		errorOccurred = false;
		/* lexer */
		lexer();
		printLexerOutput();
		if (errorOccurred) {
			return;
		}
		/* parser */
		parser();
		if (errorOccurred) {
			return;
		}
		evalStack.clear();
		System.out.println("\nEvaluating...");
		/* evaluator */
		evaluator(parseRoot, null, null);
		if (!errorOccurred)
			controller.setOutput(evalStack.pop());
	}

	/**
	 * The lexer method. Generates the lexer tokens.
	 */
	private void lexer() {
		lexerTokens.clear();
		String tokenString = "";
		for (int i = 0; i < input.length(); i++) {
			char ch = input.charAt(i);
			if (Character.isDigit(ch)) {
				boolean decimalPoint = false;
				while (Character.isDigit(ch)) {
					tokenString += ch;
					++i;
					if (i == input.length())
						break;
					ch = input.charAt(i);
					if (ch == '.') {
						if (!decimalPoint) {
							/* number with decimal point */
							tokenString += ch;
							decimalPoint = true;
							i++;
							if (i == input.length())
								break;
							ch = input.charAt(i);
						} else {
							errorOccurred = true;
							controller
									.errorOccurred("Wrong decimal point placement.");
							return;
						}
					}
				}
				if (tokenString.endsWith(".")) {
					errorOccurred = true;
					controller.errorOccurred("Wrong decimal point placement.");
					return;
				}
				lexerTokens.add(new LexerToken("double", Double
						.parseDouble(tokenString)));
				tokenString = "";
				--i;
			} else if (grammar.isOperator(ch)) {
				lexerTokens.add(new LexerToken("" + ch, ""));
			} else {
				/* the input is well checked, this will never happen */
			}
		}
		lexerTokens.add(new LexerToken("$", ""));
	}

	/**
	 * Prints the generated lexer tokens.
	 */
	private void printLexerOutput() {
		String s = "Lexer:\n";
		for (LexerToken token : lexerTokens) {
			s += token.getType() + " " + token.getValue().toString() + "\n";
		}
		System.out.print(s);
	}

	/**
	 * Parser method. Generates the parse tree.
	 */
	private void parser() {
		System.out.println("\nParsing...");
		parseRoot = new ParseTree("S", "");
		Stack<ParseTree> stack = new Stack<ParseTree>();
		stack.push(new ParseTree("$", ""));
		stack.push(parseRoot);

		while (stack.size() != 0) {
			String stackType = stack.peek().getType();
			String tokenType = lexerTokens.get(0).getType();

			System.out.println("\nStack:" + stack);
			System.out.println("ParseTree:" + parseRoot);
			printLexerOutput();

			if (grammar.isTerminal(stackType)) {
				if (stackType.equals(tokenType)) {
					stack.peek().setValue(lexerTokens.get(0).getValue());
					stack.pop();
					lexerTokens.remove(0);
				} else {
					controller.errorOccurred("Malformed input at token: \""
							+ tokenType + "\" expected: \"" + stackType + "\"");
					errorOccurred = true;
					return;
				}
			} else {
				int applyRuleIndex = grammar.getApplyRuleIndex(stackType,
						tokenType);
				if (applyRuleIndex == -1) {
					if (grammar.isRemoveable(stack.peek().getType())) {
						stack.pop();
					} else {
						controller.errorOccurred("Malformed input at token: \""
								+ tokenType + "\" expected: \"" + stackType
								+ "\"");
						errorOccurred = true;
						return;
					}
				} else {
					ParseTree outGoing = stack.pop();
					Rule applyRule = grammar.getRuleByIndex(applyRuleIndex);
					String[] rightPossibilities = applyRule.getRight().split(
							"\\|");
					boolean wasTerminal = false;
					for (String tokens : rightPossibilities) {
						String[] symbols = tokens.split(" ");
						if (symbols[0].equals(tokenType)) {
							if (tokenType.equals("$"))
								break;
							wasTerminal = true;
							for (int i = 0; i < symbols.length; i++) {
								ParseTree child = new ParseTree(symbols[i], "");
								outGoing.getChildren().add(child);
							}
							break;
						}
					}
					if (!wasTerminal) {
						for (String tokens : rightPossibilities) {
							String[] symbols = tokens.split(" ");
							if (!grammar.isTerminal(symbols[0])) {
								for (int i = 0; i < symbols.length; i++) {
									ParseTree child = new ParseTree(symbols[i],
											"");
									outGoing.getChildren().add(child);
								}
								break;
							}
						}
					}
					for (int i = outGoing.getChildren().size() - 1; i >= 0; i--) {
						stack.push(outGoing.getChildren().get(i));
					}
				}
			}
		}
	}

	/**
	 * The evealuator method. Recursively evaluates the parse tree. The
	 * operators are shifted for postfix notation.
	 * 
	 * @param root
	 *            Root of the parse tree.
	 * @param __A
	 *            Last multiplication operator.
	 * @param __B
	 *            Last addition operator.
	 */
	private void evaluator(ParseTree root, String __A, String __B) {
		if (errorOccurred)
			return;
		System.out.println("evalStack: " + evalStack);

		String rootType = root.getType();
		if (rootType.equals("double")) {
			evalStack.push((Double) root.getValue());
			return;
		}

		if (rootType.equals("EXP")) {
			if (root.getChildren().size() == 1) {
				evaluator(root.getChildren().get(0), null, null);
			} else {
				evaluator(root.getChildren().get(1), null, null);
				if (root.getChildren().get(0).getType().equals("-")) {
					evalStack.push(evalStack.pop() * -1);
				}
			}
			return;
		}

		if (rootType.equals("__B")) {
			if (__B != null) {
				double a = evalStack.pop();
				double b = evalStack.pop();
				if (__B.equals("+"))
					evalStack.push(b + a);
				if (__B.equals("-"))
					evalStack.push(b - a);
				__B = null;
			}
			if (root.getChildren().size() == 2) {
				__B = root.getChildren().get(0).getType();
				evaluator(root.getChildren().get(1), __A, __B);
				return;
			}
		}

		if (rootType.equals("__A")) {
			if (__A != null) {
				double a = evalStack.pop();
				double b = evalStack.pop();
				if (__A.equals("*"))
					evalStack.push(b * a);
				if (__A.equals("/")) {
					if (a == 0.0) {
						errorOccurred = true;
						controller.errorOccurred("Divide by zero.");
						return;
					}
					evalStack.push(b / a);
				}
				if (__A.equals("%")) {
					if (a == 0.0) {
						errorOccurred = true;
						controller.errorOccurred("Divide by zero.");
						return;
					}
					evalStack.push(b % a);
				}
				__A = null;
			}
			if (root.getChildren().size() == 2) {
				__A = root.getChildren().get(0).getType();
				evaluator(root.getChildren().get(1), __A, __B);
				return;
			}
		}

		for (ParseTree child : root.getChildren()) {
			evaluator(child, __A, __B);
		}
	}
}
