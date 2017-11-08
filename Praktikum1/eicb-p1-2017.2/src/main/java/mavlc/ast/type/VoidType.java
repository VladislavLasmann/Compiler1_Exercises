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
 * MAVL void type.
 */
public class VoidType extends Type {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1451175651889708146L;
	
	private static VoidType instance = null;
	
	protected VoidType(){}
	
	/**
	 * Get the singleton instance.
	 * @return MAVL void type.
	 */
	public static VoidType getVoidType(){
		if(instance==null){
			instance = new VoidType();
		}
		return instance;
	}
	
	@Override
	public String toString(){
		return "VOID";
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
		return 0;
	}

}
