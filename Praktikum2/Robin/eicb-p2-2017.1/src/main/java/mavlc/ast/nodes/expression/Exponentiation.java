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
 * AST-node representing a exponentiation of base ^ power.
 */
public class Exponentiation extends BinaryExpression {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5410230943688770723L;

	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param base base operand of the exponentiation.
	 * @param power power operand of the exponentiation.
	 */
	public Exponentiation(int sourceLine, int sourceColumn, Expression base, Expression power){
		super(sourceLine, sourceColumn, base, power);
	}

	@Override
	public String dump() {
		return leftOp.dump()+" ^ "+rightOp.dump();
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitExponentiation(this, obj);
	}
	
}
