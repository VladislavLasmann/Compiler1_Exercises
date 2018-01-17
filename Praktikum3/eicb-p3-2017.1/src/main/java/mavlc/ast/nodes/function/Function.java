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
package mavlc.ast.nodes.function;

import java.util.ArrayList;
import java.util.List;

import mavlc.ast.nodes.ASTNode;
import mavlc.ast.nodes.statement.Statement;
import mavlc.ast.type.Type;
import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representing a function.
 */
public class Function extends ASTNode {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7330054095348191851L;

	protected final String name;
	
	protected final Type returnType;
	
	protected final List<FormalParameter> parameters = new ArrayList<FormalParameter>();
	
	protected final List<Statement> functionBody = new ArrayList<Statement>();
	
	protected int sourceCodeOffset = -1;
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param functionName The name of the function.
	 * @param type Return type of the function.
	 */
	public Function(int sourceLine, int sourceColumn, String functionName, Type type){
		super(sourceLine, sourceColumn);
		name = functionName;
		returnType = type;
	}
	
	/**
	 * Add a {@link FormalParameter} to the function.
	 * @param param Parameter to add to the function.
	 */
	public void addParameter(FormalParameter param){
		parameters.add(param);
	}
	
	/**
	 * Add a {@link Statement} to the function body.
	 * @param stmt Statement to add to the function body.
	 */
	public void addStatement(Statement stmt){
		functionBody.add(stmt);
	}

	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitFunction(this, obj);
	}

	@Override
	public String toString() {
		return "function "+returnType.toString()+" "+name;
	}
	
	/**
	 * Get a string representation of the function signature.
	 * @return Function signature.
	 */
	public String getSignature(){
		StringBuilder sb = new StringBuilder();
		sb.append(toString());
		sb.append("(");
		boolean first = true;
		for(FormalParameter param : parameters){
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
	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append(toString());
		sb.append("(");
		boolean first = true;
		for(FormalParameter arg : parameters){
			if(!first){
				sb.append(",");
			}
			first = false;
			sb.append(arg.dump());
		}
		sb.append(")");
		sb.append("{\n");
		for(Statement stmt: functionBody){
			sb.append(stmt.dump()).append("\n");
		}
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Get the name of the function.
	 * @return Function name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the return type of the function.
	 * @return Function return type.
	 */
	public Type getReturnType() {
		return returnType;
	}

	/**
	 * Get a list of all parameters of the function.
	 * @return List of function parameters.
	 */
	public List<FormalParameter> getParameters() {
		return parameters;
	}

	/**
	 * Get a list of all statements in the function body.
	 * @return List of statements.
	 */
	public List<Statement> getFunctionBody() {
		return functionBody;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((functionBody == null) ? 0 : functionBody.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((returnType == null) ? 0 : returnType.hashCode());
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
		Function other = (Function) obj;
		if (functionBody == null) {
			if (other.functionBody != null)
				return false;
		} else if (!functionBody.equals(other.functionBody))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		if (returnType == null) {
			if (other.returnType != null)
				return false;
		} else if (!returnType.equals(other.returnType))
			return false;
		return true;
	}

	/**
	 * Get the offset from the CB-register 
	 * of the first instruction of this function.
	 * @return Source code offset from the CB-register.
	 */
	public int getSourceCodeOffset() {
		return sourceCodeOffset;
	}

	/**
	 * Set the offset from the CB-register 
	 * of the first instruction of this function.
	 * @param sourceCodeOffset Source code offset from the CB-register.
	 */
	public void setSourceCodeOffset(int sourceCodeOffset) {
		this.sourceCodeOffset = sourceCodeOffset;
	}
	

}
