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
package mavlc.test_wrapper;

import mavlc.ast.nodes.ASTNode;
import mavlc.ast.nodes.module.Module;
import mavlc.ast.visitor.ASTNodeVisitor;

public class ASTTestWrapper extends ASTNode {

	private static final long serialVersionUID = 1L;

	protected final Module AST;
	
	protected final String testFile;
	
	protected final boolean onlySyntax;

	public ASTTestWrapper(Module ast, boolean syntaxOnly, String inputFile) {
		super(-1, -1);
		AST = ast;
		onlySyntax = syntaxOnly;
		testFile = inputFile;
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj) {
		return AST.accept(visitor, obj);
	}

	@Override
	public String dump() {
		return AST.dump();
	}

	/**
	 * @return the AST
	 */
	public Module getAST() {
		return AST;
	}

	/**
	 * @return the testFile
	 */
	public String getTestFile() {
		return testFile;
	}

	/**
	 * @return the onlySyntax
	 */
	public boolean isOnlySyntax() {
		return onlySyntax;
	}

}
