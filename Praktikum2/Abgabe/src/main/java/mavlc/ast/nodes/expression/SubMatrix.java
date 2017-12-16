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
 * AST-node representing the selection of a sub-matrix from a matrix.
 */
public class SubMatrix extends Expression {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3469423143552459340L;

	protected final Expression struct;
	
	protected final Expression XBase;
	
	protected final Expression XStart;
	
	protected final Expression XEnd;
	
	protected final Expression YBase;
	
	protected final Expression YStart;
	
	protected final Expression YEnd;
	
	/**
	 * Constructor. 
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param struct The underlying matrix.
	 * @param XBaseIndex The base-index in x-direction.
	 * @param XStartIndex The x-start-offset from the x-base-index 
	 * @param XEndIndex The x-end-offset from the x-base-index
	 * @param YBaseIndex The base-index in y-direction.
	 * @param YStartIndex The The y-start-offset from the y-base-index
	 * @param YEndIndex The y-end-offset from the y-base-index.
	 */
	public SubMatrix(int sourceLine, int sourceColumn, Expression struct, Expression XBaseIndex, Expression XStartIndex, 
			Expression XEndIndex, Expression YBaseIndex, Expression YStartIndex, Expression YEndIndex) {
		super(sourceLine, sourceColumn);
		this.struct = struct;
		XBase = XBaseIndex;
		XStart = XStartIndex;
		XEnd = XEndIndex;
		YBase = YBaseIndex;
		YStart = YStartIndex;
		YEnd = YEndIndex;
	}

	@Override
	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append(struct.dump()).append("{");
		sb.append(XStart.dump()).append(":");
		sb.append(XBase.dump()).append(":");
		sb.append(XEnd.dump()).append("}{");
		sb.append(YStart.dump()).append(":");
		sb.append(YBase.dump()).append(":");
		sb.append(YEnd.dump()).append("}");
		return sb.toString();
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitSubMatrix(this, obj);
	}

	/**
	 * Get the underlying matrix.
	 * @return The underlying matrix.
	 */
	public Expression getStruct() {
		return struct;
	}

	/**
	 * Get the base-index in x-direction.
	 * @return The x-base-index.
	 */
	public Expression getXBaseIndex() {
		return XBase;
	}

	/**
	 * Get the x-start-offset from the x-base-index
	 * @return The x-start-offset
	 */
	public Expression getXStartIndex() {
		return XStart;
	}

	/**
	 * Get the x-end-offset from the x-base-index
	 * @return The x-end-offset
	 */
	public Expression getXEndIndex() {
		return XEnd;
	}

	/**
	 * Get the base-index in y-direction.
	 * @return The y-base-index
	 */
	public Expression getYBaseIndex() {
		return YBase;
	}

	/**
	 * Get the y-start-offset from the y-base-index
	 * @return The y-start-offset
	 */
	public Expression getYStartIndex() {
		return YStart;
	}

	/**
	 * Get the y-end-offset from the y-base-index
	 * @return The y-end-offset
	 */
	public Expression getYEndIndex() {
		return YEnd;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((XBase == null) ? 0 : XBase.hashCode());
		result = prime * result + ((XEnd == null) ? 0 : XEnd.hashCode());
		result = prime * result + ((XStart == null) ? 0 : XStart.hashCode());
		result = prime * result + ((YBase == null) ? 0 : YBase.hashCode());
		result = prime * result + ((YEnd == null) ? 0 : YEnd.hashCode());
		result = prime * result + ((YStart == null) ? 0 : YStart.hashCode());
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
		SubMatrix other = (SubMatrix) obj;
		if (XBase == null) {
			if (other.XBase != null)
				return false;
		} else if (!XBase.equals(other.XBase))
			return false;
		if (XEnd == null) {
			if (other.XEnd != null)
				return false;
		} else if (!XEnd.equals(other.XEnd))
			return false;
		if (XStart == null) {
			if (other.XStart != null)
				return false;
		} else if (!XStart.equals(other.XStart))
			return false;
		if (YBase == null) {
			if (other.YBase != null)
				return false;
		} else if (!YBase.equals(other.YBase))
			return false;
		if (YEnd == null) {
			if (other.YEnd != null)
				return false;
		} else if (!YEnd.equals(other.YEnd))
			return false;
		if (YStart == null) {
			if (other.YStart != null)
				return false;
		} else if (!YStart.equals(other.YStart))
			return false;
		if (struct == null) {
			if (other.struct != null)
				return false;
		} else if (!struct.equals(other.struct))
			return false;
		return true;
	}

}
