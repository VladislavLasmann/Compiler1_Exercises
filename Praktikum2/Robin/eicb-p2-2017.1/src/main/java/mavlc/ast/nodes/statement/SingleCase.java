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

import mavlc.ast.nodes.ASTNode;
import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representing a case or default-case.
 */
public abstract class SingleCase extends ASTNode{
	
	private static final long serialVersionUID = 8209373474957207223L;
	
	protected final Statement statement;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param statement The statement that gets executed if the case's condition applies.
	 */
	public SingleCase(int sourceLine, int sourceColumn, Statement statement) {
		super(sourceLine, sourceColumn);
		this.statement = statement;
	}
	
	/**
	 * Get the statement.
	 * @return The case's statement.
	 * */
	public Statement getStatement() {
		return statement;
	}
	
	/**
	 * Distinguish whether a case is a default case.
	 * @return true if case is a default case, false otherwise.
	 */
	public abstract boolean isDefault();
	
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitSingleCase(this, obj);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		SingleCase that = (SingleCase) o;
		
		return statement != null ? statement.equals(that.statement) : that.statement == null;
	}
	
	@Override
	public int hashCode() {
		return statement != null ? statement.hashCode() : 0;
	}
}
