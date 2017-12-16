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

import mavlc.ast.nodes.ASTNode;
import mavlc.ast.type.Type;
import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * Abstract super-type of all AST-nodes representing expressions.
 */
public abstract class Expression extends ASTNode {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 740517418862319021L;
	
	protected Type type;

	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 */
	public Expression(int sourceLine, int sourceColumn) {
		super(sourceLine, sourceColumn);
	}
	
	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitExpression(this, obj);
	}

	@Override
	public abstract String dump();

	
	/**
	 * Get the {@link mavlc.ast.type.Type} of this expression.
	 * @return The {@link mavlc.ast.type.Type} of this expression.
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Set the {@link mavlc.ast.type.Type} of this expression.
	 * @param type The {@link mavlc.ast.type.Type} of this expression.
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Expression other = (Expression) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
