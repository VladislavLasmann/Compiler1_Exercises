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
 * Error class to signal a mismatch in the dimensions of a matrix or vector.
 */
public class StructureDimensionError extends CompilationError {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7330470221630892161L;

	private final ASTNode node;
	
	private final int dim1;
	
	private final int dim2;
	
	/**
	 * Constructor.
	 * @param node AST-node where the error occurred.
	 * @param dimension1 The first of the compared dimensions.
	 * @param dimension2 The second of the compared dimensions.
	 */
	public StructureDimensionError(ASTNode node, int dimension1, int dimension2){
		this.node = node;
		dim1 = dimension1;
		dim2 = dimension2;
		
		StringBuilder sb = new StringBuilder();
		sb.append("Error @ ").append(this.node.dump()).append("in line ").append(this.node.getSrcLine());
		sb.append(", column ").append(this.node.getSrcColumn()).append(": \n");
		sb.append("Dimension ").append(dim1).append(" vs. dimension ").append(dim2);
		this.message = sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dim1;
		result = prime * result + dim2;
		result = prime * result + ((node == null) ? 0 : node.hashCode());
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
		StructureDimensionError other = (StructureDimensionError) obj;
		if (dim1 != other.dim1)
			return false;
		if (dim2 != other.dim2)
			return false;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		return true;
	}

}
