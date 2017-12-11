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
import mavlc.ast.type.Type;
import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representing the definition of a constant value.
 */
public class ValueDefinition extends Declaration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3471176539695918468L;
	
	protected final Expression value;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param valueType The type of the defined value.
	 * @param valueName The name of the defined value.
	 * @param value The constant value.
	 */
	public ValueDefinition(int sourceLine, int sourceColumn, Type valueType, String valueName, Expression value){
		super(sourceLine, sourceColumn, valueType, valueName);
		this.value = value;
	}

	@Override
	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append("val ");
		sb.append(type.toString()).append(" ");
		sb.append(name);
		sb.append(" = ");
		sb.append(value.dump());
		sb.append(";");
		return sb.toString();
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitValueDefinition(this, obj);
	}

	/**
	 * Get the constant value.
	 * @return The constant value.
	 */
	public Expression getValue() {
		return value;
	}

	@Override
	public boolean isVariable() {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
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
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValueDefinition other = (ValueDefinition) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
