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
 * AST-node representing a unary minus, i.e. a mathematical complement.
 */
public class UnaryMinus extends UnaryExpression {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9145028118329472116L;

	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param operand The operand.
	 */
	public UnaryMinus(int sourceLine, int sourceColumn, Expression operand){
		super(sourceLine, sourceColumn, operand);
	}

	@Override
	public String dump() {
		return "-"+op.dump();
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitUnaryMinus(this, obj);
	}
}
