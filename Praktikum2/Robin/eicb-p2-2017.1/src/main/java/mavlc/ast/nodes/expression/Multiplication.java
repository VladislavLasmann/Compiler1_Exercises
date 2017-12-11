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
 * AST-node representing a multiplication, including multiplications of 
 * a matrix or vector with a skalar value and element-wise multiplications 
 * of matrices or vectors.
 */
public class Multiplication extends BinaryExpression {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2686370636262779036L;

	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param leftOperand The left operand of this multiplication.
	 * @param rightOperand The right operand of this multiplication.
	 */
	public Multiplication(int sourceLine, int sourceColumn, Expression leftOperand, Expression rightOperand){
		super(sourceLine, sourceColumn, leftOperand, rightOperand);
	}

	@Override
	public String dump() {
		return leftOp.dump()+" * "+rightOp.dump();
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitMultiplication(this, obj);
	}

}
