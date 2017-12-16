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

import java.io.Serializable;

/**
 * Abstract super-class of all MAVL types.
 */
public abstract class Type implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9018239028173577604L;

	public static StringType getStringType(){
		return StringType.getStringType();
	}
	
	public static IntType getIntType(){
		return IntType.getIntType();
	}
	
	public static FloatType getFloatType(){
		return FloatType.getFloatType();
	}
	
	public static BoolType getBoolType(){
		return BoolType.getBoolType();
	}
	
	public static VoidType getVoidType(){
		return VoidType.getVoidType();
	}

	/**
	 * Check if the type is a MAVL primitive type.
	 * @return True if the type is a MAVL primitive type.
	 */
	public abstract boolean isPrimitiveType();
	
	/**
	 * Check if the type is a MAVL scalar type, which 
	 * can be used in matrices and vectors.
	 * @return True if the type is a MAVL scalar type.
	 */
	public abstract boolean isScalarType();
	
	/**
	 * Get the size in words of this type on the MTAM
	 * @return Size in words on the MTAM.
	 */
	public abstract int wordSize();
}
