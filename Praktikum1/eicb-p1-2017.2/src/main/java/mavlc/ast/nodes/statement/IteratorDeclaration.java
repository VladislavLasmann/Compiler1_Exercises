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

import mavlc.ast.type.Type;
import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representing the iterator in a forEach loop
 */
public class IteratorDeclaration extends Declaration {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6494933779615012783L;

	/**
	 * whether the declared entity is variable, i.e. assignable.
	 */
	protected final boolean isVariable;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param name Name of the formal parameter.
	 * @param type Type of the formal parameter.
	 * @param isVariable whether the declared entity is variable, i.e. assignable.
	 */
	public IteratorDeclaration(int sourceLine, int sourceColumn, String name, Type type, boolean isVariable){
		super(sourceLine, sourceColumn, type, name);
		this.isVariable = isVariable;
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitIteratorDeclaration(this, obj);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isVariable ? 0 : 1);
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
		IteratorDeclaration other = (IteratorDeclaration) obj;
		return isVariable == other.isVariable;
	}

	@Override
	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append(isVariable ? "var " : "val ");
		sb.append(type.toString()).append(" ");
		sb.append(this.getName());
		return sb.toString();
	}

	@Override
	public boolean isVariable() {
		return isVariable;
	}
	
}
