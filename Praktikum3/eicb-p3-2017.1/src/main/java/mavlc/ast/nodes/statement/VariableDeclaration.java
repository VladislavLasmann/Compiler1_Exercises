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

import mavlc.ast.type.Type;
import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representing a variable declaration.
 */
public class VariableDeclaration extends Declaration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7247565966012999330L;

	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param variableType Type of the declared variable.
	 * @param variableName Name of the declared variable.
	 */
	public VariableDeclaration(int sourceLine, int sourceColumn, Type variableType, String variableName) {
		super(sourceLine, sourceColumn, variableType, variableName);
	}

	@Override
	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append("var ");
		sb.append(type.toString());
		sb.append(" ").append(name).append(";");
		return sb.toString();
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitVariableDeclaration(this, obj);
	}

	@Override
	public boolean isVariable() {
		return true;
	}

}
