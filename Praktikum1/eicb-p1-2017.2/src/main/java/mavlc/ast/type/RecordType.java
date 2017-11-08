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
package mavlc.ast.type;

import mavlc.ast.nodes.record.RecordElementDeclaration;
import mavlc.ast.nodes.record.RecordTypeDeclaration;

/**
 * Record type.
 */
public class RecordType extends Type {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1452905740445442317L;

	protected final String name;
	
	protected RecordTypeDeclaration declaration;
	
	/**
	 * Constructor.
	 * @param name The name of the record type.
	 */
	public RecordType(String name) {
		this.name = name;
		this.declaration = null;
	}
	
	/**
	 * Constructor.
	 * @param name The name of the record type.
	 * @param declaration the record type declaration of the record type.
	 */
	public RecordType(String name, RecordTypeDeclaration declaration) {
		this.name = name;
		this.declaration = declaration;
	}

	/**
	 * Get the name of the record type.
	 * @return The name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the record type declaration.
	 * @return The record type declaration.
	 */
	public RecordTypeDeclaration getTypeDeclaration() {
		return declaration;
	}
	
	/**
	 * Set the record type declaration.
	 * @param declaration The new record type declaration.
	 */
	public void setTypeDeclaration(RecordTypeDeclaration declaration) {
		this.declaration = declaration;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean isPrimitiveType() {
		return false;
	}

	@Override
	public boolean isScalarType() {
		return false;
	}

	@Override
	public int wordSize() {
		int sum = 0;
		for (RecordElementDeclaration element : declaration.getElements()) {
			sum += element.getType().wordSize();
		}
		return sum;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((declaration == null) ? 0 : declaration.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		RecordType other = (RecordType) obj;
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
