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
package mavlc.ast.nodes.statement;

import mavlc.ast.nodes.expression.CallExpression;
import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representing calls to void-functions.
 */
public class CallStatement extends Statement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6000301667462335483L;
	
	protected final CallExpression call;
	
	/**
	 * Constructor.
	 * @param callExpression The actual call expression.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 */
	public CallStatement(int sourceLine, int sourceColumn, CallExpression callExpression) {
		super(sourceLine, sourceColumn);
		call = callExpression;
	}

	@Override
	public String dump() {
		return call.dump()+";";
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitCallStatement(this, obj);
	}

	/**
	 * Get the actual {@link CallExpression}
	 * @return The actual call expression.
	 */
	public CallExpression getCall() {
		return call;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((call == null) ? 0 : call.hashCode());
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
		CallStatement other = (CallStatement) obj;
		if (call == null) {
			if (other.call != null)
				return false;
		} else if (!call.equals(other.call))
			return false;
		return true;
	}

}
