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
 * AST-node representing a for-loop.
 */
public class ForLoop extends Statement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3550925366906276298L;

	protected final String initVarName;
	
	protected Declaration initVarDecl;
	
	protected final Expression initValue;
	
	protected final Expression check;
	
	protected final String incrVarName;
	
	protected Declaration incrVarDecl;
		
	protected final Expression incrExpr;
	
	protected final Statement body;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param initVarName Name of the initialized variable.
	 * @param initValue Initialized value.
	 * @param check The check expression.
	 * @param incrementVarName The name of the incremented variable.
	 * @param incrementExpression The increment expression.
	 * @param loopBody The loop body.
	 */
	public ForLoop(int sourceLine, int sourceColumn, String initVarName, Expression initValue, Expression check,
						String incrementVarName, Expression incrementExpression,
						Statement loopBody) {
		super(sourceLine, sourceColumn);
		this.initVarName = initVarName;
		this.initValue = initValue;
		this.check = check;
		incrVarName = incrementVarName;
		incrExpr = incrementExpression;
		body = loopBody;
	}

	@Override
	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append("for (");
		sb.append(initVarName).append(" = ");
		sb.append(initValue.dump()).append("; ");
		sb.append(check.dump()).append("; ");
		sb.append(incrVarName).append(" = ");
		sb.append(incrExpr.dump()).append(")");
		sb.append(body.dump());
		return sb.toString();
	}

	/**
	 * Get the name of the variable used in the initializing expression.
	 * @return Name of the initialized variable.
	 */
	public String getInitVariableName() {
		return initVarName;
	}

	/**
	 * Get the expression used for initialization.
	 * @return Initializing expression.
	 */
	public Expression getInitValue() {
		return initValue;
	}

	/**
	 * Get the expression used for the loop-bound checking.
	 * @return Loop-bound check expression.
	 */
	public Expression getCheck() {
		return check;
	}

	/**
	 * Get the name of the variable used in the increment expression.
	 * @return The incremented variable's name.
	 */
	public String getIncrementVariableName() {
		return incrVarName;
	}

	/**
	 * Get the expression used for the increment.
	 * @return Increment expression.
	 */
	public Expression getIncrementExpr() {
		return incrExpr;
	}

	/**
	 * Get the loop body.
	 * @return The loop body.
	 */
	public Statement getLoopBody() {
		return body;
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitForLoop(this, obj);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((check == null) ? 0 : check.hashCode());
		result = prime * result + ((incrExpr == null) ? 0 : incrExpr.hashCode());
		result = prime * result + ((incrVarName == null) ? 0 : incrVarName.hashCode());
		result = prime * result + ((initValue == null) ? 0 : initValue.hashCode());
		result = prime * result + ((initVarName == null) ? 0 : initVarName.hashCode());
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
		ForLoop other = (ForLoop) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		if (check == null) {
			if (other.check != null)
				return false;
		} else if (!check.equals(other.check))
			return false;
		if (incrExpr == null) {
			if (other.incrExpr != null)
				return false;
		} else if (!incrExpr.equals(other.incrExpr))
			return false;
		if (incrVarName == null) {
			if (other.incrVarName != null)
				return false;
		} else if (!incrVarName.equals(other.incrVarName))
			return false;
		if (initValue == null) {
			if (other.initValue != null)
				return false;
		} else if (!initValue.equals(other.initValue))
			return false;
		if (initVarName == null) {
			if (other.initVarName != null)
				return false;
		} else if (!initVarName.equals(other.initVarName))
			return false;
		return true;
	}
	
	/**
	 * @return the initVarDecl
	 */
	public Declaration getInitVarDeclaration() {
		return initVarDecl;
	}

	/**
	 * @param initVarDecl the initVarDecl to set
	 */
	public void setInitVarDeclaration(Declaration initVarDecl) {
		this.initVarDecl = initVarDecl;
	}

	/**
	 * @return the incrVarDecl
	 */
	public Declaration getIncrVarDeclaration() {
		return incrVarDecl;
	}

	/**
	 * @param incrVarDecl the incrVarDecl to set
	 */
	public void setIncrVarDeclaration(Declaration incrVarDecl) {
		this.incrVarDecl = incrVarDecl;
	}

}
