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

 /* 
 * EiCB group number: 22
 *
 * Names and student ID numbers of group members:
 * 
 * Vladislav Lasmann    2593078
 * Konstantin Müller    2327697
 * Robin Ferrari        2585277
 */

package mavlc.context_analysis;

import mavlc.ast.nodes.statement.Declaration;

/**
 * A table for identifiers used inside a function.
 */
public class IdentificationTable {

    // the Current Scope of this block, first its null
    private Scope currentScope;
	
	/**
	 * Declares the given identifier in the current scope.
	 * 
	 * @param name the identifier to declare
	 * @param declaration the reference to the identifier's declaration site
	 */
	public void addIdentifier(String name, Declaration declaration){
		// TODO: implement (exercise 2.1)
		currentScope.addIdentifier(name, declaration);
		//throw new UnsupportedOperationException();
	}
	
	/**
	 * Looks up the innermost declaration of the given identifier.
	 * 
	 * @param name the identifier to look up
	 * @return the identifier's innermost declaration site
	 */
	public Declaration getDeclaration(String name){
		// TODO: implement (exercise 2.1)
		return currentScope.getDeclaration(name);
		//throw new UnsupportedOperationException();
	}
	
	/**
	 * Opens a new scope.
	 */
	public void openNewScope(){
		// TODO: implement (exercise 2.1)
        currentScope = new Scope( currentScope );
		//throw new UnsupportedOperationException();
	}
	
	/**
	 * Closes the current scope.
	 */
	public void closeCurrentScope(){
		// TODO: implement (exercise 2.1)
        currentScope = currentScope.getParentScope();
		//throw new UnsupportedOperationException();
	}

}
