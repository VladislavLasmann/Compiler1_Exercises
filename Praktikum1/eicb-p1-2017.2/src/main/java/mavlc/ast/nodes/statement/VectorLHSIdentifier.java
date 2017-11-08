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
 * AST-node representing a vector element as left-hand side of an assignment.
 */
public class VectorLHSIdentifier extends LeftHandIdentifier {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1555782912814145870L;
	protected final Expression index;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param name Name of the referenced vector.
	 * @param index Index of the referenced element.
	 */
	public VectorLHSIdentifier(int sourceLine, int sourceColumn, String name, Expression index) {
		super(sourceLine, sourceColumn, name);
		this.index = index;
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitVectorLHSIdentifier(this, obj);
	}

	@Override
	public String dump() {
		return super.dump()+"["+index.dump()+"]";
	}

	/**
	 * Get the index of the referenced element.
	 * @return The index.
	 */
	public Expression getIndex() {
		return index;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((index == null) ? 0 : index.hashCode());
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
		VectorLHSIdentifier other = (VectorLHSIdentifier) obj;
		if (index == null) {
			if (other.index != null)
				return false;
		} else if (!index.equals(other.index))
			return false;
		return true;
	}
	
	

}
