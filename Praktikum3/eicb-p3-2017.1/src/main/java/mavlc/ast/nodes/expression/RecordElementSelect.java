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
 * AST-node representing the selection of an element from a record.
 */
public class RecordElementSelect extends Expression {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4364234946008914777L;

	protected final Expression record;
	
	protected final String elementName;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param record A record.
	 * @param elementName The name of the chosen element.
	 */
	public RecordElementSelect(int sourceLine, int sourceColumn, Expression record, String elementName){
		super(sourceLine, sourceColumn);
		this.record = record;
		this.elementName = elementName;
	}

	@Override
	public String dump() {
		return record.dump()+"@"+elementName;
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitRecordElementSelect(this, obj);
	}

	/**
	 * Get the record from which the element shall be chosen.
	 * @return The underlying record.
	 */
	public Expression getRecord() {
		return record;
	}

	/**
	 * Get the name of the chosen element.
	 * @return The name used.
	 */
	public String getElementName() {
		return elementName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elementName == null) ? 0 : elementName.hashCode());
		result = prime * result + ((record == null) ? 0 : record.hashCode());
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
		RecordElementSelect other = (RecordElementSelect) obj;
		if (elementName == null) {
			if (other.elementName != null)
				return false;
		} else if (!elementName.equals(other.elementName))
			return false;
		if (record == null) {
			if (other.record != null)
				return false;
		} else if (!record.equals(other.record))
			return false;
		return true;
	}

}
