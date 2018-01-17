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

import mavlc.ast.nodes.expression.Expression;
import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representing a return statement.
 */
public class ReturnStatement extends Statement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8287968159115989374L;
	
	protected final Expression returnValue;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param returnValue Return value.
	 */
	public ReturnStatement(int sourceLine, int sourceColumn, Expression returnValue) {
		super(sourceLine, sourceColumn);
		this.returnValue = returnValue;
	}

	@Override
	public String dump() {
		return "return "+returnValue.dump()+";";
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitReturnStatement(this, obj);
	}

	/**
	 * Get the {@link Expression} used as return value.
	 * @return The return value.
	 */
	public Expression getReturnValue() {
		return returnValue;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((returnValue == null) ? 0 : returnValue.hashCode());
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
		ReturnStatement other = (ReturnStatement) obj;
		if (returnValue == null) {
			if (other.returnValue != null)
				return false;
		} else if (!returnValue.equals(other.returnValue))
			return false;
		return true;
	}

}
