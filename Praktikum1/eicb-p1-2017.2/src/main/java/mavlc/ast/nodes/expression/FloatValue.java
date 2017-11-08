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

import mavlc.ast.type.FloatType;
import mavlc.ast.type.Type;
import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representation of a floating-point value.
 */
public class FloatValue extends Expression {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1160506276190167106L;
	
	protected final float val;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified. 
	 * @param value The floating-point value.
	 */
	public FloatValue(int sourceLine, int sourceColumn, float value){
		super(sourceLine, sourceColumn);
		val = value;
	}

	@Override
	public String dump() {
		return String.valueOf(val);
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitFloatValue(this, obj);
	}

	@Override
	public FloatType getType() {
		return Type.getFloatType();
	}

	@Override
	public void setType(Type type) {
		if(!Type.getFloatType().equals(type)){
			throw new UnsupportedOperationException("Cannot set another type than FloatType for FloatValue!");
		}
	}
	
	/**
	 * Get the numeric value of this node.
	 * @return Floating-point numeric value.
	 */
	public float getValue(){
		return val;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Float.floatToIntBits(val);
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
		FloatValue other = (FloatValue) obj;
		if (Float.floatToIntBits(val) != Float.floatToIntBits(other.val))
			return false;
		return true;
	}

}
