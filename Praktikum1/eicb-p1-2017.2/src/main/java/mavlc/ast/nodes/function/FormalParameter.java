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
package mavlc.ast.nodes.function;

import mavlc.ast.nodes.statement.Declaration;
import mavlc.ast.type.Type;
import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representing a formal parameter in a function definition.
 */
public class FormalParameter extends Declaration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6494933779615012783L;

	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param name Name of the formal parameter.
	 * @param type Type of the formal parameter.
	 */
	public FormalParameter(int sourceLine, int sourceColumn, String name, Type type){
		super(sourceLine, sourceColumn, type, name);
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitFormalParameter(this, obj);
	}

	@Override
	public String dump() {
		return type.toString();
	}

	@Override
	public boolean isVariable() {
		return true;
	}
	
	

}
