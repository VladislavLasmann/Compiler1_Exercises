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

import java.util.ArrayList;
import java.util.List;

import mavlc.ast.nodes.function.Function;
import mavlc.ast.visitor.ASTNodeVisitor;


/**
 * AST-node representing a function-call expression.
 */
public class CallExpression extends Expression {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4083567829995489740L;

	protected final String name;
	
	protected Function callee;
	
	protected final List<Expression> actualParameters = new ArrayList<>();
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param calleeName Name of the called {@link Function}
	 */
	public CallExpression(int sourceLine, int sourceColumn, String calleeName){
		super(sourceLine, sourceColumn);
		name = calleeName;
	}
	
	/**
	 * Adds an actual parameter, i.e. a parameter value, to this call expression.
	 * @param parameter Parameter value.
	 */
	public void addActualParameter(Expression parameter){
		actualParameters.add(parameter);
	}
	
	/**
	 * Get the list of all actual parameters, i.e. the parameter values.
	 * @return List of parameter values.
	 */
	public List<Expression> getActualParameters(){
		return actualParameters;
	}
	
	/**
	 * Get the actual parameter, i.e. the parameter value at the given index.
	 * @param index Index.
	 * @return Parameter value at the given index.
	 */
	public Expression getParameterAtIndex(int index){
		return actualParameters.get(index);
	}

	@Override
	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append(name).append("(");
		boolean first = true;
		for(Expression param : actualParameters){
			if(!first){
				sb.append(", ");
			}
			first = false;
			sb.append(param.dump());
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj) {
		return visitor.visitCallExpression(this, obj);
	}
	
	/**
	 * Get the name of the called {@link Function}
	 * @return Name of the called {@link Function}
	 */
	public String getCalleeName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actualParameters == null) ? 0 : actualParameters.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		CallExpression other = (CallExpression) obj;
		if (actualParameters == null) {
			if (other.actualParameters != null)
				return false;
		} else if (!actualParameters.equals(other.actualParameters))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * Get the defining AST-node of the called function.
	 * @return The callee definition
	 */
	public Function getCalleeDefinition() {
		return callee;
	}

	/**
	 * Set the defining AST-node of the called function.
	 * @param calleeDefinition The callee definition
	 */
	public void setCalleeDefinition(Function calleeDefinition) {
		this.callee = calleeDefinition;
	}

}
