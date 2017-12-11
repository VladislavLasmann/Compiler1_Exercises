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
package mavlc.context_analysis;

import java.util.HashMap;
import java.util.Map;

import mavlc.ast.nodes.statement.Declaration;
import mavlc.error_reporting.OverwritingDeclarationError;
import mavlc.error_reporting.UndeclaredReferenceError;

/**
 * Representation of a scope.
 */
public class Scope {
	
	protected final Scope parentScope;
	
	protected final Map<String, Declaration> identifiers = new HashMap<>();
	
	/**
	 * Constructor.
	 * @param parentScope The enclosing scope or null if there is none.
	 */
	public Scope(Scope parentScope){
		this.parentScope = parentScope;
	}
	
	/**
	 * Add an identifier to this scope.
	 * Throws an {@link OverwritingDeclarationError} if the identifier 
	 * has been declared before in this scope.
	 * @param name The identifier name.
	 * @param declaration The declaration side of the identifier.
	 */
	public void addIdentifier(String name, Declaration declaration){
		if(identifiers.containsKey(name)){
			throw new OverwritingDeclarationError(name, identifiers.get(name));
		}
		identifiers.put(name, declaration);
	}
	
	/**
	 * Get the closest declaration site of an identifier declared in this scope or 
	 * any of the enclosing scopes.
	 * Throws an {@link UndeclaredReferenceError} if the identifier has not 
	 * been declared before.
	 * @param name Name of the referenced identifier.
	 * @return The closest declaration site of the referenced identifier.
	 */
	public Declaration getDeclaration(String name){
		if(identifiers.containsKey(name)){
			return identifiers.get(name);
		}
		if(parentScope!=null){
			Declaration parentDecl = parentScope.getDeclaration(name);
			if(parentDecl!=null){
				return parentDecl;
			}
		}
		throw new UndeclaredReferenceError(name);
	}

	/**
	 * Get the directly enclosing scope of this scope.
	 * @return The directly enclosing scope or null if there is none.
	 */
	public Scope getParentScope() {
		return parentScope;
	}

}
