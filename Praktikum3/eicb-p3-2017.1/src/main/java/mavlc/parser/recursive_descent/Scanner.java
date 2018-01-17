/*******************************************************************************
 * Copyright (C) 2016, 2017 Embedded Systems and Applications Group
 * Department of Computer Science, Technische Universitaet Darmstadt,
 * Hochschulstr. 10, 64289 Darmstadt, Germany.
 * 
 * All rights reserved.
 * 
 * This software is provided free for educational use only.
 * It may not be used for commercial purposes without the
 * prior written permission of the authors.
 ******************************************************************************/
package mavlc.parser.recursive_descent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import static mavlc.parser.recursive_descent.Token.TokenType.*;

/**
 * A simple scanner for MAVL.
 */
public final class Scanner {

	private int currentLine = 1, currentColumn = 0;
	private int lastLine = 1, lastColumn = 0;
	private int currentChar;
	private StringBuilder currentSpelling;
	private FileInputStream fileInputStream;

	private static final List<Token.TokenType> keywords = Arrays.asList(
			INT, FLOAT, BOOL, VOID, STRING, MATRIX, VECTOR,
			VAL, VAR, FOR, IF, ELSE, RETURN, FUNCTION, SWITCH, CASE, DEFAULT, FOREACH, RECORD);

	/**
	 * Constructor.
	 * 
	 * @param file The input program to tokenize.
	 * @throws IOException in case an error occurs while accessing the given file.
	 */
	public Scanner(File file) throws IOException {
		fileInputStream = new FileInputStream(file);
		currentChar = fileInputStream.read();
	}

	/**
	 * Scans the input program.
	 * 
	 * @return A queue containing the tokenized representation of the input program.
	 */
	public Deque<Token> scan() {
		ArrayDeque<Token> result = new ArrayDeque<>();
		while (currentChar != -1) {

			/* Skip all whitespaces immediately */
			while (currentChar == ' ' || currentChar == '\n' || currentChar == '\r' || currentChar == '\t')
				skipIt();

			currentSpelling = new StringBuilder(16);
			lastLine = this.currentLine;
			lastColumn = this.currentColumn;

			/* Deal with line and block comments */
			if (currentChar == '/') {
				takeIt();

				// Line comments
				if (currentChar == '/') {
					while (currentChar != '\n')
						skipIt();
					continue;
				}

				// Block comments
				if (currentChar == '*') {
					skipIt();
					while (true) {
						if (currentChar == '*') {
							skipIt();
							if (currentChar == '/') {
								skipIt();
								break;
							}
						} else {
							skipIt();
						}
					}
					continue;
				}

				// is actually operator
				result.add(new Token(DIV, currentSpelling.toString(), lastLine, lastColumn));
				continue;
			}

			if (currentChar != -1) {
				Token.TokenType type = scanToken();
				result.add(new Token(type, currentSpelling.toString(), lastLine, lastColumn));
			}
		}
		
		result.add(new Token(EOF, EOF.pattern, currentLine, currentColumn));
		return result;
	}

	private void takeIt() {
		currentSpelling.append((char) currentChar);
		skipIt();
	}

	private void skipIt() {
		if ((char) currentChar == '\n') {
			currentLine++;
			currentColumn = 0;
		} else {
			currentColumn++;
		}
		try {
			int old = currentChar;
			currentChar = fileInputStream.read();
			if (old == -1 && currentChar == -1) {
				throw new RuntimeException(
						String.format("Reached EOF while scanning, Token started at line %d, column %d",
								lastLine, lastColumn));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Token.TokenType scanIdentifierLikeToken() {
		takeIt();
		while (isLetter(currentChar) || isDigit(currentChar) || currentChar == '_')
			takeIt();

		String spelling = currentSpelling.toString();
		
		if ("true".equals(spelling) || "false".equals(spelling))
			return BOOLLIT;
		
		for (Token.TokenType keyword : keywords)		
			if (keyword.pattern.equals(currentSpelling.toString()))
				return keyword;
		
		return ID;
	}

	private Token.TokenType scanIntOrFloat() {
		takeIt();
		while (isDigit(currentChar) || currentChar == '.')
			takeIt();
		return currentSpelling.toString().contains(".") ? FLOATLIT : INTLIT;
	}

	private Token.TokenType scanString() {
		skipIt();
		while (currentChar != '"')
			takeIt();
		skipIt();
		return STRINGLIT;
	}

	private Token.TokenType scanDot() {
		takeIt();

		if (currentChar == '*') {
			takeIt();
			return DOTPROD;
		}

		while (isLetter(currentChar))
			takeIt();

		if (currentSpelling.toString().equals(DIM.pattern))
			return DIM;
		if (currentSpelling.toString().equals(XDIM.pattern))
			return XDIM;
		if (currentSpelling.toString().equals(YDIM.pattern))
			return YDIM;

		return ERROR;
	}

	private Token.TokenType scanToken() {
		if (isLetter(currentChar))
			return scanIdentifierLikeToken();

		if (isDigit(currentChar))
			return scanIntOrFloat();

		if (currentChar == '\"')
			return scanString();

		if (currentChar == '.')
			return scanDot();

		switch (currentChar) {
		case ';':
			takeIt();
			return SEMICOLON;
		case ',':
			takeIt();
			return COMMA;
		case '(':
			takeIt();
			return LPAREN;
		case ')':
			takeIt();
			return RPAREN;
		case '[':
			takeIt();
			return LBRACKET;
		case ']':
			takeIt();
			return RBRACKET;
		case '{':
			takeIt();
			return LBRACE;
		case '}':
			takeIt();
			return RBRACE;
		case '*':
			takeIt();
			return MULT;
		case '/':
			takeIt();
			return DIV;
		case '+':
			takeIt();
			return ADD;
		case '-':
			takeIt();
			return SUB;
		case ':':
			takeIt();
			return COLON;
		case '#':
			takeIt();
			return MATMULT;
		case '?':
			takeIt();
			return QMARK;
		case '&':
			takeIt();
			return AND;
		case '|':
			takeIt();
			return OR;
		case '@':
			takeIt();
			return AT;
		case '^':
			takeIt();
			return EXP;
		case '<':
			takeIt();
			if (currentChar == '=') {
				takeIt();
				return CMPLE;
			}
			return LANGLE;
		case '>':
			takeIt();
			if (currentChar == '=') {
				takeIt();
				return CMPGE;
			}
			return RANGLE;
		case '=':
			takeIt();
			if (currentChar == '=') {
				takeIt();
				return CMPEQ;
			}
			return ASSIGN;
		case '!':
			takeIt();
			if (currentChar == '=') {
				takeIt();
				return CMPNE;
			}
			return NOT;
		}
		takeIt();
		return ERROR;
	}

	private boolean isLetter(int c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	private boolean isDigit(int c) {
		return (c >= '0' && c <= '9');
	}

}
