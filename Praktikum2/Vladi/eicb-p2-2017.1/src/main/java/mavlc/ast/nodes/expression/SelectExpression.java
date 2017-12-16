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
package mavlc.ast.nodes.expression;

import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representing a ternary ?-expression.
 */
public class SelectExpression extends Expression{
	/**
	 * 
	 */
	private static final long serialVersionUID = -127511455495959257L;
	protected Expression condition, trueCase, falseCase;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param condition Condition that determines which result the expression returns.
	 * @param trueCase  Expression that is evaluated if the condition is true.
	 * @param falseCase Expression that is evaluated if the condition is false.
	 */
	public SelectExpression(int sourceLine, int sourceColumn, Expression condition, Expression trueCase, Expression falseCase) {
		super(sourceLine, sourceColumn);
		this.condition = condition;
		this.trueCase = trueCase;
		this.falseCase = falseCase;
	}

	/**
	 * Get the condition.
	 * @return Condition that determines which result the expression returns .
	 * */
	public Expression getCondition() {
		return condition;
	}

	/**
	 * Get the result of the expression if the condition is true.
	 * @return Expression that is evaluated if the condition is true.
	 * */
	public Expression getTrueCase() {
		return trueCase;
	}

	/**
	 * Get the result of the expression if the condition is false.
	 * @return Expression that is evaluated if the condition is false.
	 * */
	public Expression getFalseCase() {
		return falseCase;
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitSelectExpression(this, obj);
	}
	
	@Override
	public String dump() {	
		return condition.dump() + " ? " + trueCase.dump() + " : " + falseCase.dump();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		
		SelectExpression that = (SelectExpression) o;
		
		if (condition != null ? !condition.equals(that.condition) : that.condition != null) return false;
		if (trueCase != null ? !trueCase.equals(that.trueCase) : that.trueCase != null) return false;
		return falseCase != null ? falseCase.equals(that.falseCase) : that.falseCase == null;
	}
	
	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (condition != null ? condition.hashCode() : 0);
		result = 31 * result + (trueCase != null ? trueCase.hashCode() : 0);
		result = 31 * result + (falseCase != null ? falseCase.hashCode() : 0);
		return result;
	}
}
