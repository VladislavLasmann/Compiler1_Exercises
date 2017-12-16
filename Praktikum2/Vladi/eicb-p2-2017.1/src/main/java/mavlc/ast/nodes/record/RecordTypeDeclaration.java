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

import java.util.List;

import mavlc.ast.nodes.ASTNode;
import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-Node representing a record type declaration.
 */
public class RecordTypeDeclaration extends ASTNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7956511775079539788L;

	protected final String name;
	
	protected final List<RecordElementDeclaration> elements;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param name Name of the declared entity.
	 * @param elements List of all record element declarations that are part
	 * 		  of this record type declaration.
	 */
	public RecordTypeDeclaration(int sourceLine, int sourceColumn, String name, List<RecordElementDeclaration> elements) {
		super(sourceLine, sourceColumn);
		this.name = name;
		this.elements = elements;
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj) {
		return visitor.visitRecordTypeDeclaration(this, obj);
	}

	/**
	 * Get the Name of the record type.
	 * @return The name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the list of all elements.
	 * @return The elements.
	 */
	public List<RecordElementDeclaration> getElements() {
		return elements;
	}
	
	/**
	 * Get the element with a specified name.
	 * @param name The specified name.
	 * 
	 * @return The element with the specified name
	 * 			null, if name is null or no such element exists.
	 */
	public RecordElementDeclaration getElement(String name) {
		if (name == null) {
			return null;
		}
		for (RecordElementDeclaration element : elements) {
			if (name.equals(element.getName())) {
				return element;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "record "+name;
	}
	
	@Override
	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.toString());
		sb.append(" {");
		for (RecordElementDeclaration elem : elements) {
			sb.append("\n");
			sb.append(elem.dump());
		}
		sb.append("\n}");
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((elements == null) ? 0 : elements.hashCode());
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
		RecordTypeDeclaration other = (RecordTypeDeclaration) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		return true;
	}
	
}
