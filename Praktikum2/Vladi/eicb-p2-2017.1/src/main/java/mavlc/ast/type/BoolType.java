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
 * MAVL boolean type.
 */
public class BoolType extends PrimitiveType {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3968813112203142511L;
	
	private static BoolType instance = null;
	
	protected BoolType(){}
	
	/**
	 * Get the singleton instance. 
	 * @return The MAVL boolean type.
	 */
	public static BoolType getBoolType(){
		if(instance==null){
			instance = new BoolType();
		}
		return instance;
	}
	
	@Override
	public String toString(){
		return "BOOL";
	}

	@Override
	public boolean isScalarType() {
		return false;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof BoolType){
			return true;
		}
		return false;
	}

}
