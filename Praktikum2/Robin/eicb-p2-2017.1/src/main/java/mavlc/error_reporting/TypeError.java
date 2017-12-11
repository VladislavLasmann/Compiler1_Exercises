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

import mavlc.ast.nodes.ASTNode;
import mavlc.ast.type.Type;

/**
 * Error class to signal a type mismatch.
 */
public class TypeError extends CompilationError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6551681531224842799L;
	
	private final ASTNode errorOccur;
	
	private final Type t1;
	
	private final Type t2;
	
	/**
	 * Constructor. 
	 * @param errorOccurence The AST-node where the error occurred.
	 * @param type1 The first compared type.
	 * @param type2 The second compared type.
	 */
	public TypeError(ASTNode errorOccurence, Type type1, Type type2){
		errorOccur = errorOccurence;
		t1 = type1;
		t2 = type2;
		
		StringBuilder sb = new StringBuilder();
		sb.append("Type error @ ").append(errorOccur.dump());
		sb.append(" in line ").append(errorOccur.getSrcLine());
		sb.append(", column ").append(errorOccur.getSrcColumn());
		sb.append(": \n");
		sb.append(t1.toString());
		sb.append(" does not match ").append(t2.toString());
		
		this.message = sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((errorOccur == null) ? 0 : errorOccur.hashCode());
		result = prime * result + ((t1 == null) ? 0 : t1.hashCode());
		result = prime * result + ((t2 == null) ? 0 : t2.hashCode());
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
		TypeError other = (TypeError) obj;
		if (errorOccur == null) {
			if (other.errorOccur != null)
				return false;
		} else if (!errorOccur.equals(other.errorOccur))
			return false;
		if (t1 == null) {
			if (other.t1 != null)
				return false;
		} else if (!t1.equals(other.t1))
			return false;
		if (t2 == null) {
			if (other.t2 != null)
				return false;
		} else if (!t2.equals(other.t2))
			return false;
		return true;
	}

}
