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
package mavlc.ast.nodes.statement;

import mavlc.ast.nodes.expression.Expression;
import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representing a matrix element as left-hand side of an assignment.
 */
public class MatrixLHSIdentifier extends LeftHandIdentifier {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8284232786504107290L;

	protected final Expression xIndex;
	
	protected final Expression yIndex;

	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param variableName Name of the referenced matrix.
	 * @param xIndex The x-index.
	 * @param yIndex The y-index.
	 */
	public MatrixLHSIdentifier(int sourceLine, int sourceColumn, String variableName, Expression xIndex, Expression yIndex) {
		super(sourceLine, sourceColumn, variableName);
		this.xIndex = xIndex;
		this.yIndex = yIndex;
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitMatrixLHSIdentifier(this, obj);
	}

	@Override
	public String dump() {
		return super.dump()+"["+xIndex.dump()+"]["+yIndex.dump()+"]";
	}

	/**
	 * Get the index in x-direction.
	 * @return The X-index.
	 */
	public Expression getXIndex() {
		return xIndex;
	}

	/**
	 * Get the index in y-direction.
	 * @return The Y-index.
	 */
	public Expression getYIndex() {
		return yIndex;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((xIndex == null) ? 0 : xIndex.hashCode());
		result = prime * result + ((yIndex == null) ? 0 : yIndex.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MatrixLHSIdentifier other = (MatrixLHSIdentifier) obj;
		if (xIndex == null) {
			if (other.xIndex != null)
				return false;
		} else if (!xIndex.equals(other.xIndex))
			return false;
		if (yIndex == null) {
			if (other.yIndex != null)
				return false;
		} else if (!yIndex.equals(other.yIndex))
			return false;
		return true;
	}

}
