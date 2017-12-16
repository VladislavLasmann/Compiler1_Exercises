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
import mavlc.ast.nodes.function.Function;

/**
 * Error class to signal a mismatch in the argument count 
 * between function definition and call expression.
 */
public class ArgumentCountError extends CompilationError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3773327589469709302L;

	private final Function callee;
	
	private final ASTNode call;
	
	private final int expCount;
	
	private final int actCount;
	
	/**
	 * Constructor.
	 * @param funcCall The function call.
	 * @param calledFunction The definition of the called function.
	 * @param expectedCount The number of arguments expected.
	 * @param actualCount The number of arguments given.
	 */
	public ArgumentCountError(ASTNode funcCall, Function calledFunction, int expectedCount, int actualCount){
		callee = calledFunction;
		call = funcCall; 
		expCount = expectedCount;
		actCount = actualCount;
		
		StringBuilder sb = new StringBuilder();
		sb.append("Error in function call @ ").append(call.dump()).append("in line ").append(call.getSrcLine());
		sb.append(", column ").append(call.getSrcColumn()).append(": \n");
		if(actCount > expCount){
			sb.append("Too many arguments specified! Expected: ").append(expCount);
			sb.append(" Given: ").append(actCount);
			sb.append("\n").append(call.dump());
			sb.append("\n").append(callee.getSignature());
		}
		else if(actCount < expCount){
			sb.append("Not enough arguments specified! Expected: ").append(expCount);
			sb.append(" Given: ").append(actCount);
			sb.append("\n").append(call.dump());
			sb.append("\n").append(callee.getSignature());
		}
		
		this.message = sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + actCount;
		result = prime * result + ((call == null) ? 0 : call.hashCode());
		result = prime * result + ((callee == null) ? 0 : callee.hashCode());
		result = prime * result + expCount;
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
		ArgumentCountError other = (ArgumentCountError) obj;
		if (actCount != other.actCount)
			return false;
		if (call == null) {
			if (other.call != null)
				return false;
		} else if (!call.equals(other.call))
			return false;
		if (callee == null) {
			if (other.callee != null)
				return false;
		} else if (!callee.equals(other.callee))
			return false;
		if (expCount != other.expCount)
			return false;
		return true;
	}
}
