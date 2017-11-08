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
 * MAVL string value type.
 */
public class StringType extends Type {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7871197148348505752L;
	
	private static StringType instance = null;
	
	protected StringType(){}
	
	/**
	 * Get the singleton instance.
	 * @return The MAVL string type.
	 */
	public static StringType getStringType(){
		if(instance==null){
			instance = new StringType();
		}
		return instance;
	}

	@Override
	public String toString() {
		return "STRING";
	}

	@Override
	public boolean isPrimitiveType() {
		return false;
	}

	@Override
	public boolean isScalarType() {
		return false;
	}

	@Override
	public int wordSize() {
		return 1;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof StringType){
			return true;
		}
		return false;
	}

}
