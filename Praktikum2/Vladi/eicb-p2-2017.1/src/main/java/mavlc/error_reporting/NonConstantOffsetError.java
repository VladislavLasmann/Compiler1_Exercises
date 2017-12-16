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

import mavlc.ast.nodes.expression.Expression;

/**
 * Error class to signal a non-constant offset in a submatrix-/subvector-expression.
 */
public class NonConstantOffsetError extends CompilationError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6099076852045473097L;

	private final Expression offset;
	
	/**
	 * Constructor.
	 * @param offsetExpr The non-constant offset expression.
	 */
	public NonConstantOffsetError(Expression offsetExpr){
		offset = offsetExpr;
		
		StringBuilder sb = new StringBuilder();
		sb.append("Error @ \"").append(offset.dump()).append("\" in line ").append(offset.getSrcLine());
		sb.append(", column ").append(offset.getSrcColumn()).append(": \n");
		sb.append("Offset must be a constant integer value: ").append(offset.dump());
		this.message = sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((offset == null) ? 0 : offset.hashCode());
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
		NonConstantOffsetError other = (NonConstantOffsetError) obj;
		if (offset == null) {
			if (other.offset != null)
				return false;
		} else if (!offset.equals(other.offset))
			return false;
		return true;
	}
}
