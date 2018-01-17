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

import mavlc.ast.nodes.statement.Declaration;

/**
 * A table for identifiers used inside a function.
 */
public class IdentificationTable {
	
	protected Scope currentScope = null;
	
	/**
	 * Declares the given identifier in the current scope.
	 * 
	 * @param name the identifier to declare
	 * @param declaration the reference to the identifier's declaration site
	 */
	public void addIdentifier(String name, Declaration declaration){
		currentScope.addIdentifier(name, declaration);
	}
	
	/**
	 * Looks up the innermost declaration of the given identifier.
	 * 
	 * @param name the identifier to look up
	 * @return the identifier's innermost declaration site
	 */
	public Declaration getDeclaration(String name){
		return currentScope.getDeclaration(name);
	}
	
	/**
	 * Opens a new scope.
	 */
	public void openNewScope(){
		currentScope = new Scope(currentScope);
	}
	
	/**
	 * Closes the current scope.
	 */
	public void closeCurrentScope(){
		currentScope = currentScope.getParentScope();
	}

}
