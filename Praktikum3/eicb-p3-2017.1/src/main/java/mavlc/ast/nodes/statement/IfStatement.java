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
 * AST-node representing a MAVL if-statement.
 */
public class IfStatement extends Statement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8884228686689928633L;

	protected final Expression test;
	
	protected final Statement thenStatement;
	
	protected final Statement elseStatement;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param testExpression Test expression.
	 * @param thenStatement Statement for the true-case.
	 */
	public IfStatement(int sourceLine, int sourceColumn, Expression testExpression, Statement thenStatement) {
		super(sourceLine, sourceColumn);
		test = testExpression;
		this.thenStatement = thenStatement;
		this.elseStatement = null;
	}
	
	/**
	 * Alternative constructor for if-statement with an else-statement.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param testExpression Test-expression.
	 * @param thenStatement Statement for the true-case.
	 * @param elseStatement Statement for the false-case.
	 */
	public IfStatement(int sourceLine, int sourceColumn, Expression testExpression, Statement thenStatement, Statement elseStatement) {
		super(sourceLine, sourceColumn);
		test = testExpression;
		this.thenStatement = thenStatement;
		this.elseStatement = elseStatement;
	}
	
	

	@Override
	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append("if (");
		sb.append(test.dump()).append(") ");
		sb.append(thenStatement.dump());
		if(hasElseStatement()){
			sb.append(" else ");
			sb.append(elseStatement.dump());
		}
		return sb.toString();
	}

	/**
	 * Check if the if-statement has an else-statement.
	 * @return True if this if-statement has an else-statement.
	 */
	public boolean hasElseStatement() {
		return elseStatement!=null;
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitIfStatement(this, obj);
	}

	/**
	 * Get the test expression.
	 * @return The test expression.
	 */
	public Expression getTestExpression() {
		return test;
	}

	/**
	 * Get the statement for the true-case
	 * @return The true-case statement.
	 */
	public Statement getThenStatement() {
		return thenStatement;
	}

	/**
	 * Get the statement for the false case.
	 * @return The false-case statement.
	 */
	public Statement getElseStatement() {
		return elseStatement;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elseStatement == null) ? 0 : elseStatement.hashCode());
		result = prime * result + ((test == null) ? 0 : test.hashCode());
		result = prime * result + ((thenStatement == null) ? 0 : thenStatement.hashCode());
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
		IfStatement other = (IfStatement) obj;
		if (elseStatement == null) {
			if (other.elseStatement != null)
				return false;
		} else if (!elseStatement.equals(other.elseStatement))
			return false;
		if (test == null) {
			if (other.test != null)
				return false;
		} else if (!test.equals(other.test))
			return false;
		if (thenStatement == null) {
			if (other.thenStatement != null)
				return false;
		} else if (!thenStatement.equals(other.thenStatement))
			return false;
		return true;
	}

}
