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

import mavlc.ast.nodes.statement.Declaration;
import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representing a reference to a named entity.
 */
public class IdentifierReference extends Expression {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7445518272400148014L;

	protected Declaration declaration;
	
	protected final String idName;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param identifierName The name of the referenced entity.
	 */
	public IdentifierReference(int sourceLine, int sourceColumn, String identifierName){
		super(sourceLine, sourceColumn);
		idName = identifierName;
	}

	@Override
	public String dump() {
		return idName;
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitIdentifierReference(this, obj);
	}

	/**
	 * Get the referenced name.
	 * @return The referenced name.
	 */
	public String getIdentifierName() {
		return idName;
	}

	/**
	 * Get the declaration side of the referenced entity.
	 * @return The referenced {@link Declaration}.
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
		int result = super.hashCode();
		result = prime * result + ((declaration == null) ? 0 : declaration.hashCode());
		result = prime * result + ((idName == null) ? 0 : idName.hashCode());
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
		IdentifierReference other = (IdentifierReference) obj;
		if (declaration == null) {
			if (other.declaration != null)
				return false;
		} else if (!declaration.equals(other.declaration))
			return false;
		if (idName == null) {
			if (other.idName != null)
				return false;
		} else if (!idName.equals(other.idName))
			return false;
		return true;
	}

}
