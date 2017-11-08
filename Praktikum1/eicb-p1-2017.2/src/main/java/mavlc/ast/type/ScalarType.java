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
 * Abstract super-type of all MAVL scalar types.
 */
public abstract class ScalarType extends PrimitiveType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7894757145831921845L;

	@Override
	public boolean isScalarType() {
		return true;
	}

}
