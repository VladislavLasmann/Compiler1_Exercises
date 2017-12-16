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
package mavlc.error_reporting;

import mavlc.ast.nodes.ASTNode;
import mavlc.ast.type.RecordType;

/**
 * Error class to signal an error with an element of a record.
 */
public class RecordElementError extends CompilationError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -555377159523571873L;

	private final ASTNode errorOccur;
	
	private final RecordType type;
	
	private final String elementName;
	
	/**
	 * Constructor. 
	 * @param errorOccurence The AST-node where the error occurred.
	 * @param type The type of the record.
	 * @param elementName The name of the element.
	 */
	public RecordElementError(ASTNode errorOccurence, RecordType type, String elementName){
		errorOccur = errorOccurence;
		this.type = type;
		this.elementName = elementName;
		
		StringBuilder sb = new StringBuilder();
		sb.append("Type error @ ").append(errorOccur.dump());
		sb.append(" in line ").append(errorOccur.getSrcLine());
		sb.append(", column ").append(errorOccur.getSrcColumn());
		sb.append(": \n");
		sb.append("record "+type.toString());
		sb.append(" : error with member ").append(elementName);
		
		this.message = sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((errorOccur == null) ? 0 : errorOccur.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecordElementError other = (RecordElementError) obj;
		if (errorOccur == null) {
			if (other.errorOccur != null)
				return false;
		} else if (!errorOccur.equals(other.errorOccur))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (elementName == null) {
			if (other.elementName != null)
				return false;
		} else if (!elementName.equals(other.elementName))
			return false;
		return true;
	}

}
