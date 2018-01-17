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

import java.util.ArrayList;
import java.util.List;

import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representing a block of statements.
 */
public class CompoundStatement extends Statement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 624055651825261122L;

	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 */
	public CompoundStatement(int sourceLine, int sourceColumn) {
		super(sourceLine, sourceColumn);
	}
	
	protected final List<Statement> statements = new ArrayList<>();

	@Override
	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		for(Statement stmt : statements){
			sb.append(stmt.dump()).append("\n");
		}
		sb.append("}\n");
		return sb.toString();
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitCompoundStatement(this, obj);
	}
	
	/**
	 * Adds a {@link Statement} to the block of statements.
	 * @param stmt Statement to add to the block
	 */
	public void addStatement(Statement stmt){
		statements.add(stmt);
	}

	/**
	 * Get the list of all statements in this block.
	 * @return List of statements.
	 */
	public List<Statement> getStatements() {
		return statements;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((statements == null) ? 0 : statements.hashCode());
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
		CompoundStatement other = (CompoundStatement) obj;
		if (statements == null) {
			if (other.statements != null)
				return false;
		} else if (!statements.equals(other.statements))
			return false;
		return true;
	}

}
