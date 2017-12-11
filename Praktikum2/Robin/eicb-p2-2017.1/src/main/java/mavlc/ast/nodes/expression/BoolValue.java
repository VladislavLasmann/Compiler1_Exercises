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

import mavlc.ast.type.Type;
import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representing a boolean value, i.e. true or false.
 */
public class BoolValue extends Expression {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5714691131927997179L;
	
	protected final boolean val;
	
	/**
	 * Constructs a boolean value from a truth value.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param value Truth value.
	 */
	public BoolValue(int sourceLine, int sourceColumn, boolean value){
		super(sourceLine, sourceColumn);
		val = value;
	}

	@Override
	public String dump() {
		return String.valueOf(val);
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitBoolValue(this, obj);
	}

	@Override
	public Type getType() {
		return Type.getBoolType();
	}

	@Override
	public void setType(Type type) {
		if(!Type.getBoolType().equals(type)){
			throw new UnsupportedOperationException("Cannot set another type than BoolType for BoolValue!");
		}
	}
	
	/**
	 * Get the truth-value (true or false) of this boolean value.
	 * @return  The truth-value.
	 */
	public boolean getValue(){
		return val;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (val ? 1231 : 1237);
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
		BoolValue other = (BoolValue) obj;
		if (val != other.val)
			return false;
		return true;
	}

}
