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
 * AST-node representing the construction of a record from 
 * the underlying values. 
 */
public class RecordInit extends StructureInit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected final String name;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param name The name of the record type
	 */
	public RecordInit(int sourceLine, int sourceColumn, String name) {
		super(sourceLine, sourceColumn);
		this.name = name;
	}
	
	/**
	 * Get the name of the record type initiated by this expression 
	 * @return The name
	 */
	public String getName() {
		return name;
	}
	
	@Override
	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append("@");
		sb.append(name);
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
	
	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitRecordInit(this, obj);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((elements == null) ? 0 : elements.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		RecordInit other = (RecordInit) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;		
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
