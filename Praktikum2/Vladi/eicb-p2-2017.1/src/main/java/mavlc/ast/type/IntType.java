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
 * MAVL integer value type.
 */
public class IntType extends ScalarType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1304983858585329685L;
	
	private static IntType instance = null;
	
	protected IntType(){}
	
	/**
	 * Get the singleton instance.
	 * @return The MAVL integer type.
	 */
	public static IntType getIntType(){
		if(instance==null){
			instance = new IntType();
		}
		return instance;
	}
	
	@Override
	public String toString(){
		return "INT";
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof IntType){
			return true;
		}
		return false;
	}
}
