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

import mavlc.ast.type.Type;
import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * Abstract super-class of all declaration sites of 
 * referenceable entities.
 */
public abstract class Declaration extends Statement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3435650667836540694L;

	protected final Type type;
	
	protected final String name;
	
	protected int localBaseOffset;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param type {@link Type} of the declared entity.
	 * @param name Name of the declared entity.
	 */
	public Declaration(int sourceLine, int sourceColumn, Type type, String name){
		super(sourceLine, sourceColumn);
		this.type = type;
		this.name = name;
	}

	@Override
	public abstract String dump();
	
	/**
	 * Get the {@link Type} of the declared entity.
	 * @return The type of the entity.
	 */
	public Type getType() {
		return type;
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj) {
		return visitor.visitDeclaration(this, obj);
	}
	
	/**
	 * Get the name of the declared entity. 
	 * @return Name of the entity.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Check if the declared entity is variable, i.e. assignable.
	 * @return True if the entity is assignable.
	 */
	public abstract boolean isVariable();

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Declaration other = (Declaration) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	/**
	 * Get the offset from the local base of the 
	 * surrounding frame for the declared entity.
	 * @return Offset from LB.
	 */
	public int getLocalBaseOffset() {
		return localBaseOffset;
	}

	/**
	 * Set the offset from the local base of the 
	 * surrounding frame for the declared entity.
	 * @param localBaseOffset Offset from LB.
	 */
	public void setLocalBaseOffset(int localBaseOffset) {
		this.localBaseOffset = localBaseOffset;
	}


}
