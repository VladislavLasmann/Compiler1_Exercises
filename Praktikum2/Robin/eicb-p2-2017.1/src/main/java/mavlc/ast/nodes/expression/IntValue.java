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

import mavlc.ast.type.IntType;
import mavlc.ast.type.Type;
import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representation of a integer value.
 */
public class IntValue extends Expression {

	/**
	 * 
	 */
	private static final long serialVersionUID = -717740825559075028L;
	
	protected final int val;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param value The numeric value of this integer value.
	 */
	public IntValue(int sourceLine, int sourceColumn, int value){
		super(sourceLine, sourceColumn);
		val = value;
	}

	@Override
	public String dump() {
		return String.valueOf(val);
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitIntValue(this, obj);
	}

	@Override
	public IntType getType() {
		return Type.getIntType();
	}

	@Override
	public void setType(Type type) {
		if(!Type.getIntType().equals(type)){
			throw new UnsupportedOperationException("Cannot set another type than IntType for IntValue!");
		}
	}
	
	/**
	 * Get the numeric value of this integer value.
	 * @return Numeric value of this node.
	 */
	public int getValue(){
		return val;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + val;
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
		IntValue other = (IntValue) obj;
		if (val != other.val)
			return false;
		return true;
	}

}
