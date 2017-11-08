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

import mavlc.ast.type.StringType;
import mavlc.ast.type.Type;
import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representing a string value.
 */
public class StringValue extends Expression {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4515468787000892742L;
	
	protected final String val;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param value The text of this string value.
	 */
	public StringValue(int sourceLine, int sourceColumn, String value){
		super(sourceLine, sourceColumn);
		val = value;
	}

	@Override
	public String dump() {
		return val;
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitStringValue(this, obj);
	}

	@Override
	public StringType getType() {
		return Type.getStringType();
	}

	@Override
	public void setType(Type type) {
		if(!Type.getStringType().equals(type)){
			throw new UnsupportedOperationException("Cannot set another type than StringType for StringValue!");
		}
	}
	
	/**
	 * Get the text associated with this string value.
	 * @return The associated text.
	 */
	public String getValue(){
		return val;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((val == null) ? 0 : val.hashCode());
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
		StringValue other = (StringValue) obj;
		if (val == null) {
			if (other.val != null)
				return false;
		} else if (!val.equals(other.val))
			return false;
		return true;
	}

}
