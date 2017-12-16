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

/**
 * Error class to signal a non-permitted assignment to a constant value.
 */
public class ConstantAssignmentError extends CompilationError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7908547281497905581L;
	
	private final ASTNode ref;
	
	/**
	 * Constructor.
	 * @param lhsRef The left-hand side of the assignment.
	 */
	public ConstantAssignmentError(ASTNode lhsRef){
		ref = lhsRef;
		
		StringBuilder sb = new StringBuilder();
		sb.append("Error @ ").append(ref.dump()).append("in line ").append(ref.getSrcLine());
		sb.append(", column ").append(ref.getSrcColumn()).append(": \n");
		sb.append("Cannot assign to constant value!");
		this.message = sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ref == null) ? 0 : ref.hashCode());
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
		ConstantAssignmentError other = (ConstantAssignmentError) obj;
		if (ref == null) {
			if (other.ref != null)
				return false;
		} else if (!ref.equals(other.ref))
			return false;
		return true;
	}

}
