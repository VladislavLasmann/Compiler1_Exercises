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
package mavlc.ast.nodes.expression;

import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representing the selection of a single element from a matrix or vector.
 */
public class ElementSelect extends Expression {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4364234946008914777L;

	protected final Expression struct;
	
	protected final Expression index;
	
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param struct A matrix or vector.
	 * @param index The index of the chosen element.
	 */
	public ElementSelect(int sourceLine, int sourceColumn, Expression struct, Expression index){
		super(sourceLine, sourceColumn);
		this.struct = struct;
		this.index = index;
	}

	@Override
	public String dump() {
		return struct.dump()+"["+index.dump()+"]";
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitElementSelect(this, obj);
	}

	/**
	 * Get the matrix or vector from which the element shall be chosen.
	 * @return The underlying matrix or vector.
	 */
	public Expression getStruct() {
		return struct;
	}

	/**
	 * Get the index of the chosen element.
	 * @return The index used.
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
		int result = 1;
		result = prime * result + ((index == null) ? 0 : index.hashCode());
		result = prime * result + ((struct == null) ? 0 : struct.hashCode());
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
		ElementSelect other = (ElementSelect) obj;
		if (index == null) {
			if (other.index != null)
				return false;
		} else if (!index.equals(other.index))
			return false;
		if (struct == null) {
			if (other.struct != null)
				return false;
		} else if (!struct.equals(other.struct))
			return false;
		return true;
	}
	
	

}
