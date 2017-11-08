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

import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representing a record element as left-hand side of an assignment.
 */
public class RecordLHSIdentifier extends LeftHandIdentifier {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8284232786504107290L;

	protected final String elementName;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param variableName Name of the referenced record.
	 * @param elementName Name of the selected element.
	 */
	public RecordLHSIdentifier(int sourceLine, int sourceColumn, String variableName, String elementName) {
		super(sourceLine, sourceColumn, variableName);
		this.elementName = elementName;
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitRecordLHSIdentifier(this, obj);
	}

	@Override
	public String dump() {
		return super.dump()+"@"+elementName;
	}

	/**
	 * Get the name of the selected element.
	 * @return The element name.
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
		int result = super.hashCode();
		result = prime * result + ((elementName == null) ? 0 : elementName.hashCode());
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
		RecordLHSIdentifier other = (RecordLHSIdentifier) obj;
		if (elementName == null) {
			if (other.elementName != null)
				return false;
		} else if (!elementName.equals(other.elementName))
			return false;
		return true;
	}

}
