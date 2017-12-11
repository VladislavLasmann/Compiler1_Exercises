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

import mavlc.ast.nodes.statement.SingleCase;
import mavlc.ast.nodes.statement.SwitchStatement;

/**
 * Error class to signal duplicate (default) cases in a SwitchStmt.
 */
public class DuplicateCaseException extends CompilationError{
	private static final long serialVersionUID = -496184142868548538L;
	
	/**
	 * Constructor.
	 *
	 * @param switchCaseStatement the surrounding switch statement.
	 * @param isDefaultCase true if multiple default cases were found, false otherwise (non-unique cases).
	 * @param singleCase the first affected (default) case.
	 * @param singleCase2 the second affected (default) case.
	 */
	public DuplicateCaseException(SwitchStatement switchCaseStatement, boolean isDefaultCase, SingleCase singleCase, SingleCase singleCase2) {
		StringBuilder sb = new StringBuilder();
		sb.append("Error in Switch Case Statement @ \n").append(switchCaseStatement.dump()).append("in line ").append(switchCaseStatement.getSrcLine());
		sb.append(", column ").append(switchCaseStatement.getSrcColumn()).append(": \n");
		if(isDefaultCase){
			sb.append("Multiple default cases were found:").append("\n");
			sb.append(singleCase.dump()).append("\n");
			sb.append(singleCase2.dump()).append("\n");
		}else{
			sb.append("Two cases with the same condition were found:").append("\n");
			sb.append(singleCase.dump()).append("\n");
			sb.append(singleCase2.dump()).append("\n");
		}
		
		this.message = sb.toString();
		
	}

}
