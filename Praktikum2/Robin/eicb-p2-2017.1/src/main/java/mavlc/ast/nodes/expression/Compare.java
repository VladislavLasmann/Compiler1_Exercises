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
 * AST-node representing the comparison of two values, yielding a boolean value.
 */
public class Compare extends BinaryExpression {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5270590072659197773L;

	/**
	 * Possible comparators for comparisons.
	 */
	public enum Comparison{
		LESS("<"),
		GREATER(">"),
		LESS_EQUAL("<="),
		GREATER_EQUAL(">="),
		NOT_EQUAL("!="),
		EQUAL("==");
		
		private final String op;
		Comparison(String operator){
			this.op = operator;
		}
		
		/**
		 * String representation of the comparator.
		 * @return String representation.
		 */
		public String getOperator(){
			return op;
		}
	}

	protected final Comparison comparator;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param leftOperand The left operand of the comparison.
	 * @param rightOperand The right operand of the comparison.
	 * @param comparator The comparator of the comparison.
	 */
	public Compare(int sourceLine, int sourceColumn, Expression leftOperand, Expression rightOperand, Comparison comparator){
		super(sourceLine, sourceColumn, leftOperand, rightOperand);
		this.comparator = comparator;
	}

	@Override
	public String dump() {
		return leftOp.dump()+" "+comparator.getOperator()+" "+rightOp.dump();
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitCompare(this, obj);
	}

	/**
	 * Get the comparator used for this comparison.
	 * @return The {@link Comparison} used in this compare-expression.
	 */
	public Comparison getComparator(){
		return comparator;
	}

}
