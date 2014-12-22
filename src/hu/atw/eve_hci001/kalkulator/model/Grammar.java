package hu.atw.eve_hci001.kalkulator.model;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class stores rules for the grammar and performs high level operations
 * related to them.
 * 
 * @author László Ádám
 *
 */

public class Grammar {
	private ArrayList<Character> operators;
	private ArrayList<Rule> rules;
	private int[][] parseTable;
	private HashSet<String> ruleSymbols;
	private ArrayList<String> terminals;
	private ArrayList<String> nonTerminals;

	/**
	 * Constructor.
	 */
	public Grammar() {
		setOperators();
		setRules();
		autoSetRuleSymbols();
		autoSetNonTerminals();
		autoSetTerminals();
		autoGenerateParsingTable();
	}

	/**
	 * Determines if a character is an operator.
	 * 
	 * @param ch
	 *            The character.
	 * @return True, if it's an operator, false othwewise.
	 */
	public boolean isOperator(Character ch) {
		return operators.contains(ch);
	}

	/**
	 * Determines if the character is an addition operator (+, -).
	 * 
	 * @param op
	 *            The operator.
	 * @return True, if it's an addition operator, false otherwise.
	 */
	public boolean isAddOperator(String op) {
		if (op.equals("+") || op.equals("-")) {
			return true;
		}
		return false;
	}

	/**
	 * Determines if the character is a multiplication operator (*, /, %).
	 * 
	 * @param op
	 *            The operator.
	 * @return True, if it's a multiplication operator, false otherwise.
	 */
	public boolean isMulOperator(String op) {
		if (op.equals("/") || op.equals("*") || op.equals("%")) {
			return true;
		}
		return false;
	}

	/**
	 * Determines of a symbol is a terminal.
	 * 
	 * @param symbol
	 *            The symbol.
	 * @return True, if it's a terminal of the grammar, false otherwise.
	 */
	public boolean isTerminal(String symbol) {
		return terminals.contains(symbol);
	}

	/**
	 * Determines if the nonterminal can be removed ( = is there an empty rule
	 * on its right side).
	 * 
	 * @param nonTerminal
	 *            Nonterminal.
	 * @return True, if it can be removed, false otherwise.
	 */
	public boolean isRemoveable(String nonTerminal) {
		for (Rule rule : rules) {
			if (rule.getLeft().equals(nonTerminal)) {
				String[] rightPossibilities = rule.getRight().split("\\|");
				for (String tokens : rightPossibilities) {
					if (tokens.equals("$"))
						return true;
				}
				break;
			}
		}
		return false;
	}

	/**
	 * Returns the index-th grammar.
	 * 
	 * @param index
	 *            Index of the grammar.
	 * @return The index-th grammar.
	 */
	public Rule getRuleByIndex(int index) {
		return rules.get(index);
	}

	/**
	 * Returns the corresponding rule to apply based on the top of the stack and
	 * the next lexer token.
	 * 
	 * @param stackType
	 *            Type of the top element of the stack.
	 * @param tokenType
	 *            Type of the next element of the lexer tokens.
	 * @return The corresponding grammar.
	 */
	public int getApplyRuleIndex(String stackType, String tokenType) {
		int nt = nonTerminals.indexOf(stackType);
		int t = terminals.indexOf(tokenType);
		return parseTable[nt][t];
	}

	/**
	 * Prints the auto-created parsing table for debugging purposes.
	 */
	public void printParsingTale() {
		System.out.println("Parsing table:");
		System.out.print("\t");
		for (int i = 0; i < terminals.size(); i++) {
			System.out.print(terminals.get(i) + "\t");
		}
		System.out.println();
		for (int i = 0; i < nonTerminals.size(); i++) {
			System.out.print(nonTerminals.get(i) + "\t");
			for (int k = 0; k < terminals.size(); k++) {
				System.out.print("" + parseTable[i][k] + "\t");
			}
			System.out.println();
		}
		System.out.println();
	}

	/**
	 * Sets the operators of the grammar.
	 */
	private void setOperators() {
		operators = new ArrayList<Character>();
		operators.add('*');
		operators.add('/');
		operators.add('+');
		operators.add('-');
		operators.add('%');
		operators.add('(');
		operators.add(')');
	}

	/**
	 * Sets the rules of the grammar.
	 */
	private void setRules() {
		rules = new ArrayList<Rule>();
		rules.add(new Rule("VAR", "double|( EXP )"));
		rules.add(new Rule("MUD", "VAR __A"));
		rules.add(new Rule("__A", "* MUD|/ MUD|% MUD|$"));
		rules.add(new Rule("ADD", "MUD __B"));
		rules.add(new Rule("__B", "+ ADD|- ADD|$"));
		rules.add(new Rule("EXP", "ADD|- ADD|+ ADD"));
		rules.add(new Rule("S", "EXP|$"));
	}

	/**
	 * Determines the symbols of the grammar based on the given rules.
	 */
	private void autoSetRuleSymbols() {
		ruleSymbols = new HashSet<String>();
		for (Rule rule : rules) {
			ruleSymbols.add(rule.getLeft());
			for (String right : rule.getRight().split("\\|")) {
				for (String sym : right.split(" ")) {
					ruleSymbols.add(sym);
				}
			}
		}
	}

	/**
	 * Determines the nonterminals based on the given rules.
	 */
	private void autoSetNonTerminals() {
		nonTerminals = new ArrayList<String>();
		for (Rule rule : rules) {
			String left = rule.getLeft();
			nonTerminals.add(left);
			ruleSymbols.remove(left);
		}
	}

	/**
	 * Determines the terminals based on the given rules.
	 */
	private void autoSetTerminals() {
		terminals = new ArrayList<String>();
		for (String symbol : ruleSymbols) {
			terminals.add(symbol);
		}
	}

	/**
	 * Generates the parsing table.
	 */
	private void autoGenerateParsingTable() {
		int nt = nonTerminals.size();
		int t = terminals.size();
		parseTable = new int[nt][t];
		for (int i = 0; i < nt; i++) {
			for (int k = 0; k < t; k++) {
				parseTable[i][k] = -1;
			}
		}
		for (int i = 0; i < rules.size(); i++) {
			String left = rules.get(i).getLeft();
			tableGenerator(left, i, i);
		}
	}

	/**
	 * Recursively generates a row of the the parsing table for a nonterminal.
	 * 
	 * @param origNonTerm
	 *            The starting nonterminal.
	 * @param origRuleIndex
	 *            The starting rule index.
	 * @param ruleIndex
	 *            The current rule index.
	 */
	private void tableGenerator(String origNonTerm, int origRuleIndex,
			int ruleIndex) {
		String[] rightPossibilities = rules.get(ruleIndex).getRight()
				.split("\\|");
		for (String tokens : rightPossibilities) {
			String firstToken = tokens.split(" ")[0];
			if (terminals.contains(firstToken)) {
				parseTable[nonTerminals.indexOf(origNonTerm)][terminals
						.indexOf(firstToken)] = origRuleIndex;
			} else {
				for (int k = 0; k < this.rules.size(); k++) {
					if (rules.get(k).getLeft().equals(firstToken)) {
						tableGenerator(origNonTerm, origRuleIndex, k);
						break;
					}
				}
			}
		}
	}

}
