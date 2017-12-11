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
package mavlc.ast.type;

/**
 * Super-type of matrix- and vector-types.
 */
public abstract class StructType extends Type {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2765958549508165861L;
	
	protected final ScalarType elemType;
	
	/**
	 * Constructor.
	 * @param elementType The type of elements.
	 */
	public StructType(ScalarType elementType){
		elemType = elementType;
	}

	@Override
	public boolean isPrimitiveType() {
		return false;
	}

	public ScalarType getElementType() {
		return elemType;
	}

	@Override
	public boolean isScalarType() {
		return false;
	}


}
