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
package mavlc.ast.nodes.record;

import mavlc.ast.nodes.statement.Declaration;
import mavlc.ast.type.Type;
import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-Node representing a record element declaration.
 */
public class RecordElementDeclaration extends Declaration {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2249695309078647050L;
	
	protected final boolean isVariable;

	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param isVariable Whether the declared entity is variable.
	 * @param type {@link Type} of the declared entity.
	 * @param name Name of the declared entity.
	 */
	public RecordElementDeclaration(int sourceLine, int sourceColumn, boolean isVariable, Type type, String name) {
		super(sourceLine, sourceColumn, type, name);
		this.isVariable = isVariable;
	}

	@Override
	public boolean isVariable() {
		return isVariable;
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj) {
		return visitor.visitRecordElementDeclaration(this, obj);
	}

	@Override
	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append(isVariable ? "var " : "val ");
		sb.append(type.toString());
		sb.append(" ");
		sb.append(name);
		sb.append(";");
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isVariable ? 0 : 1);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecordElementDeclaration other = (RecordElementDeclaration) obj;
		return isVariable == other.isVariable;
	}

}
