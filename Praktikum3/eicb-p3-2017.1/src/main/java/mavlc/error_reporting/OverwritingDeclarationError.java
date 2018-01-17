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
import mavlc.ast.nodes.function.Function;

/**
 * Error class to signal a non-permitted overwriting of an already 
 * existing declaration.
 */
public class OverwritingDeclarationError extends CompilationError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8652153954683393054L;
	
	private final ASTNode prevDecl;
	
	private final String declName;
	
	/**
	 * Constructor.
	 * @param declaredName The name of the re-declared entity.
	 * @param previousDeclaration The previously exisiting declaration.
	 */
	public OverwritingDeclarationError(String declaredName, ASTNode previousDeclaration){
		prevDecl = previousDeclaration;
		declName = declaredName;
		
		StringBuilder sb = new StringBuilder();
		sb.append(declName).append(" has already been declared and cannot be overwritten in this scope.");
		sb.append("\n Previous declaration: ");
		if(prevDecl instanceof Function){
			sb.append(((Function) prevDecl).getSignature());
		}
		else{
			sb.append(prevDecl.dump());
		}
		sb.append(" in line ");
		sb.append(prevDecl.getSrcLine()).append(", column ").append(prevDecl.getSrcColumn());
		
		this.message = sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((declName == null) ? 0 : declName.hashCode());
		result = prime * result + ((prevDecl == null) ? 0 : prevDecl.hashCode());
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
		OverwritingDeclarationError other = (OverwritingDeclarationError) obj;
		if (declName == null) {
			if (other.declName != null)
				return false;
		} else if (!declName.equals(other.declName))
			return false;
		if (prevDecl == null) {
			if (other.prevDecl != null)
				return false;
		} else if (!prevDecl.equals(other.prevDecl))
			return false;
		return true;
	}

}
