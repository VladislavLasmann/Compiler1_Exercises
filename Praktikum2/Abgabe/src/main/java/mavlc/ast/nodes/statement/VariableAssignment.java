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
 * AST-node representing an assignment of a variable.
 */
public class VariableAssignment extends Statement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5126942219727260060L;

	protected final LeftHandIdentifier identifier;
	
	protected final Expression value;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param identifier The left-hand side of the assignment.
	 * @param value The assigned value.
	 */
	public VariableAssignment(int sourceLine, int sourceColumn, LeftHandIdentifier identifier, Expression value) {
		super(sourceLine, sourceColumn);
		this.identifier = identifier;
		this.value = value;
	}

	@Override
	public String dump() {
		return identifier.dump()+" = "+value.dump()+";";
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitVariableAssignment(this, obj);
	}

	/**
	 * Get the entity referenced on the left-hand side of the assignment.
	 * @return The left-hand side.
	 */
	public LeftHandIdentifier getIdentifier() {
		return identifier;
	}

	/**
	 * Get the assigned value.
	 * @return The assigned value.
	 */
	public Expression getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		VariableAssignment other = (VariableAssignment) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
