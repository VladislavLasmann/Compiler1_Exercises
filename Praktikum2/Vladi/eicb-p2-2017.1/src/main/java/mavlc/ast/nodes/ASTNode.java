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
package mavlc.ast.nodes;

import java.io.Serializable;

import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * Abstract super-type of all AST-nodes.
 */
public abstract class ASTNode implements Serializable{
	
	protected final int srcLine;
	
	protected final int srcColumn;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 */
	public ASTNode(int sourceLine, int sourceColumn){
		srcLine = sourceLine;
		srcColumn = sourceColumn;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9185702720966610255L;

	/**
	 * Accepts the given visitor and calls the corresponding visit-method in the visitor.
	 * @param visitor Instance of {@link mavlc.ast.visitor.ASTNodeVisitor}.
	 * @param obj Additional argument, passed on to the visitor.
	 * @param <RetTy> Return type used by visitor methods.
	 * @param <ArgTy> Argument type used by visitor methods.
	 * @return Return value from the visit-method in the visitor.
	 */
	public abstract <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj);

	/**
	 * Returns a pseudo-code representation of an AST-node.
	 * @return Pseudo-code representation.
	 */
	public abstract String dump();

	/**
	 * Get the line in which the node was specified in the program.
	 * @return Source line in the input program.
	 */
	public int getSrcLine() {
		return srcLine;
	}

	/**
	 * Get the column in which the node was specified in the program.
	 * @return Source column in the input program.
	 */
	public int getSrcColumn() {
		return srcColumn;
	}
}
