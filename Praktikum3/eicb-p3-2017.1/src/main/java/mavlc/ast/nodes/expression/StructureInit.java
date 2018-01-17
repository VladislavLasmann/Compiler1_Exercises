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

import java.util.LinkedList;
import java.util.List;

import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representing the construction of a matrix or vector from 
 * the underlying values. 
 */
public class StructureInit extends Expression {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4113133824801642436L;
	
	protected final List<Expression> elements = new LinkedList<>();
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 */
	public StructureInit(int sourceLine, int sourceColumn) {
		super(sourceLine, sourceColumn);
	}
	
	/**
	 * Add an element to the construction.
	 * @param element An element of the construction.
	 */
	public void addElement(Expression element){
		elements.add(element);
	}
	
	@Override
	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		boolean first = true;
		for(Expression elem : elements){
			if(!first){
				sb.append(", ");
			}
			first = false;
			sb.append(elem.dump());
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Get the list of elements used for construction of 
	 * this matrix or vector.
	 * @return List of elements used for construction.
	 */
	public List<Expression> getElements() {
		return elements;
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitStructureInit(this, obj);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((elements == null) ? 0 : elements.hashCode());
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
		StructureInit other = (StructureInit) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		return true;
	}

}
