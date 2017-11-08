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
 * MAVL floating-point value type.
 */
public class FloatType extends ScalarType {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7893768395052644874L;
	private static FloatType instance = null;
	
	protected FloatType(){}
	
	/**
	 * Get the singleton instance.
	 * @return The MAVL float type.
	 */
	public static FloatType getFloatType(){
		if(instance==null){
			instance = new FloatType();
		}
		return instance;
	}
	
	@Override
	public String toString(){
		return "FLOAT";
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof FloatType){
			return true;
		}
		return false;
	}

}
