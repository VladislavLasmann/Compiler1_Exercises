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

import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representing a default-case.
 */
public class Default extends SingleCase{
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param statement The statement that gets executed.
	 */
	public Default(int sourceLine, int sourceColumn, Statement statement) {
		super(sourceLine, sourceColumn, statement);
	}
	
	@Override
	public boolean isDefault() {
		return true;
	}

	@Override
	public String dump() {
		return  "default:\n "+  statement.dump();
	}
	
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitDefault(this, obj);
	}
}
