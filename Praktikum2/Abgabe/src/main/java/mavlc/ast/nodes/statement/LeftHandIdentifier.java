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

import mavlc.ast.nodes.ASTNode;
import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representing a left-hand side of an assignment.
 */
public class LeftHandIdentifier extends ASTNode {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3523399448211832832L;

	protected final String name;
	
	protected Declaration declaration;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param variableName The name of the referenced entity.
	 */
	public LeftHandIdentifier(int sourceLine, int sourceColumn, String variableName) {
		super(sourceLine, sourceColumn);
		name = variableName;
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitLeftHandIdentifier(this, obj);
	}

	@Override
	public String dump(){
		return name;
	}

	/**
	 * Get the name of the referenced entity.
	 * @return The referenced name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the declaration side of the referenced entity.
	 * @return The referenced {@link Declaration}
	 */
	public Declaration getDeclaration() {
		return declaration;
	}

	/**
	 * Set the declaration side of the referenced entity.
	 * @param declaration The referenced {@link Declaration}
	 */
	public void setDeclaration(Declaration declaration) {
		this.declaration = declaration;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((declaration == null) ? 0 : declaration.hashCode());
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LeftHandIdentifier other = (LeftHandIdentifier) obj;
		if (declaration == null) {
			if (other.declaration != null)
				return false;
		} else if (!declaration.equals(other.declaration))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
