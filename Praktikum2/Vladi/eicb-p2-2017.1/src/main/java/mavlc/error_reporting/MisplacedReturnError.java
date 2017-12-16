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

import mavlc.ast.nodes.ASTNode;

/**
 * Error class to signal a misplaced return statement. A return 
 * statement can be misplaced if it occurs in any other position 
 * than the last in a non-void function.
 */
public class MisplacedReturnError extends CompilationError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6139677299644834984L;
	
	private final ASTNode ret;
	
	/**
	 * Constructor.
	 * @param returnStmt The misplaced return statement.
	 */
	public MisplacedReturnError(ASTNode returnStmt){
		ret = returnStmt;
		
		StringBuilder sb = new StringBuilder();
		sb.append("Misplaced return statement @ ").append(ret.dump()).append("in line ").append(ret.getSrcLine());
		sb.append(", column ").append(ret.getSrcColumn()).append(": \n");
		this.message = sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ret == null) ? 0 : ret.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MisplacedReturnError other = (MisplacedReturnError) obj;
		if (ret == null) {
			if (other.ret != null)
				return false;
		} else if (!ret.equals(other.ret))
			return false;
		return true;
	}
}
