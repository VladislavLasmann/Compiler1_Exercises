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
 * AST-node representing the selection of a sub-vector from a vector.
 */
public class SubVector extends Expression {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2031156525388302545L;

	protected final Expression struct;
	
	protected final Expression base;
	
	protected final Expression start;
	
	protected final Expression end;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param struct The underlying vector.
	 * @param baseIndex The base-index.
	 * @param startIndex The start-offset from the base-index.
	 * @param endIndex The end-offset from the base-index.
	 */
	public SubVector(int sourceLine, int sourceColumn, Expression struct, Expression baseIndex, Expression startIndex, Expression endIndex) {
		super(sourceLine, sourceColumn);
		this.struct = struct;
		base = baseIndex;
		start = startIndex;
		end = endIndex;
	}

	@Override
	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append(struct.dump()).append("<");
		sb.append(start.dump()).append(":");
		sb.append(base.dump()).append(":");
		sb.append(end.dump()).append(">");
		return sb.toString();
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitSubVector(this, obj);
	}

	/**
	 * Get the underlying vector.
	 * @return The underlying vector.
	 */
	public Expression getStruct() {
		return struct;
	}

	/**
	 * Get the base-index.
	 * @return The base-index.
	 */
	public Expression getBaseIndex() {
		return base;
	}

	/**
	 * Get the start-offset from the base-index.
	 * @return The start-offset.
	 */
	public Expression getStartIndex() {
		return start;
	}

	/**
	 * Get the end-offset from the base-index.
	 * @return The end-offset.
	 */
	public Expression getEndIndex() {
		return end;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((base == null) ? 0 : base.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
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
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubVector other = (SubVector) obj;
		if (base == null) {
			if (other.base != null)
				return false;
		} else if (!base.equals(other.base))
			return false;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		if (struct == null) {
			if (other.struct != null)
				return false;
		} else if (!struct.equals(other.struct))
			return false;
		return true;
	}
}
