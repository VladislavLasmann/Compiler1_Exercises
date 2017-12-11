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

import mavlc.ast.nodes.expression.Expression;
import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representing a foreach-loop.
 */
public class ForEachLoop extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 998290312444078801L;

	/**
	 * the iterator used for iterating the struct.
	 */
	protected final IteratorDeclaration iterator;
	
	/**
	 * the expression that represents the struct.
	 */
	protected final Expression struct;

	/**
	 * the loop body.
	 */
	protected final Statement body;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param iterator The iterator used for iterating the struct.
	 * @param struct The expression that represents the struct.
	 * @param loopBody The loop body.
	 */
	public ForEachLoop(int sourceLine, int sourceColumn, IteratorDeclaration iterator, Expression struct,
						Statement loopBody) {
		super(sourceLine, sourceColumn);
		this.iterator = iterator;
		this.struct = struct;
		body = loopBody;
	}

	@Override
	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append("for (");
		sb.append(iterator.dump()).append(" : ");
		sb.append(struct.dump()).append(") ");
		sb.append(body.dump());
		return sb.toString();
	}

	/**
	 * Get the declaration of the variable used for iterating the struct.
	 * @return declaration of the iteration variable.
	 */
	public IteratorDeclaration getIteratorDeclaration() {
		return iterator;
	}

	/**
	 * Get the expression that represents the struct.
	 * @return struct expression.
	 */
	public Expression getStructExpr() {
		return struct;
	}

	/**
	 * Get the loop body.
	 * @return The loop body.
	 */
	public Statement getLoopBody() {
		return body;
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitForEachLoop(this, obj);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((iterator == null) ? 0 : iterator.hashCode());
		result = prime * result + ((struct == null) ? 0 : struct.hashCode());
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
		ForEachLoop other = (ForEachLoop) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		if (iterator == null) {
			if (other.iterator != null)
				return false;
		} else if (!iterator.equals(other.iterator))
			return false;
		if (struct == null) {
			if (other.struct != null)
				return false;
		} else if (!struct.equals(other.struct))
			return false;
		return true;
	}
	
}
