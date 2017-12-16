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
package mavlc.error_reporting;

public class MissingMainFunctionError extends CompilationError {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9045557253277512230L;

	/**
	 * Constructor.
	 */
	public MissingMainFunctionError(){
		this.message = "This module does not contain a main-function compliant to the signature \"function void main()\"!";
	}

}
