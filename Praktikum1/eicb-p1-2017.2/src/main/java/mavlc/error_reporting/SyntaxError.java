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
package mavlc.error_reporting;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mavlc.parser.recursive_descent.Token;

/**
 * Error class to signal a syntax error during parsing.
 */
public class SyntaxError extends CompilationError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2713014512912045390L;
	
	private final int line;
	
	private final int column;
	
	private final Token actual;
	
	private final List<Token.TokenType> expected;
		
	/**
	 * Constructor. 
	 * @param actualToken The misplaced token.
	 * @param expectedTokens The expected/allowed tokens.
	 */
	public SyntaxError(Token actualToken, Token.TokenType... expectedTokens){
		line = actualToken.line;
		column = actualToken.column;
		actual = actualToken;
		expected = Arrays.asList(expectedTokens);
		/*
		 * Sort for stable XML-serialization.
		 */
		Collections.sort(expected, new Comparator<Token.TokenType>() {

			@Override
			public int compare(Token.TokenType o1, Token.TokenType o2) {
				return o1.name().compareTo(o2.name());
			}
		});
		StringBuilder sb = new StringBuilder();
		sb.append("Syntax error in line ").append(line).append(", column ").append(column).append("\n");
		if(expected.size()==0){
			sb.append("Unexpected token "+actual.toString());
		}
		else{
			sb.append("Got token ").append(actual.toString());
			if(expected.size()==1){
				sb.append(" but expected ").append(expected.get(0).toString());
			}
			else if(expected.size()==2){
				sb.append(" but expected ").append(expected.get(0).toString()).append(" or ").append(expected.get(1).toString());
			}
			else{
				sb.append(" but expected one of ");
				boolean first = true;
				for(Token.TokenType e : expected){
					if(!first){
						sb.append(", ");
					}
					sb.append(e.name());
					first = false;
				}
			}
		}
		message = sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actual == null) ? 0 : actual.hashCode());
		result = prime * result + column;
		result = prime * result + ((expected == null) ? 0 : expected.hashCode());
		result = prime * result + line;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SyntaxError other = (SyntaxError) obj;
		if (actual == null) {
			if (other.actual != null)
				return false;
		} else if (!actual.equals(other.actual))
			return false;
		if (column != other.column)
			return false;
		if (expected == null) {
			if (other.expected != null)
				return false;
		} else if (!expected.equals(other.expected))
			return false;
		if (line != other.line)
			return false;
		return true;
	}
	

}
