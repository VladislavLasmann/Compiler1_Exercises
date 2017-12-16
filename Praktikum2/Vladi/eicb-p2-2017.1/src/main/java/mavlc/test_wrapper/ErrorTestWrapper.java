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
package mavlc.test_wrapper;

import mavlc.error_reporting.CompilationError;

public class ErrorTestWrapper extends CompilationError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected final CompilationError error;
	
	protected final boolean onlySyntax;
	
	protected final String testFile;
	
	public ErrorTestWrapper(CompilationError expectedError, boolean syntaxOnly, String inputFile){
		error = expectedError;
		onlySyntax = syntaxOnly;
		testFile = inputFile;
	}

	/**
	 * @return the error
	 */
	public CompilationError getError() {
		return error;
	}

	/**
	 * @return the onlySyntax
	 */
	public boolean isOnlySyntax() {
		return onlySyntax;
	}

	/**
	 * @return the testFile
	 */
	public String getTestFile() {
		return testFile;
	}
	
	

}
