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
package mavlc.error_reporting;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mavlc.ast.nodes.ASTNode;
import mavlc.ast.type.Type;

/**
 * Error class to signal that an operation is not applicable to 
 * the type of the underlying element.
 */
public class InapplicableOperationError extends CompilationError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8699248314710828860L;
	
	private final ASTNode op;
	
	private final Type actualType;
	
	private final List<Class<? extends Type>> appTypes;
	
	/**
	 * Constructor.
	 * @param operation The operation.
	 * @param actualType The type of the underlying element.
	 * @param applicableTypes The types to which the operation is applicable.
	 */
	@SafeVarargs
	public InapplicableOperationError(ASTNode operation, Type actualType, Class<? extends Type> ... applicableTypes){
		op = operation;
		this.actualType =  actualType;
		
		appTypes = Arrays.asList(applicableTypes);
		/*
		 * Sort for stable XML-serialization.
		 */
		Collections.sort(appTypes, new Comparator<Class<? extends Type>>() {

			@Override
			public int compare(Class<? extends Type> o1, Class<? extends Type> o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		StringBuilder sb = new StringBuilder();
		sb.append("Error @ ").append(op.dump()).append("in line ").append(op.getSrcLine());
		sb.append(", column ").append(op.getSrcColumn()).append(": \n");
		sb.append("Operation ").append(op.getClass().getName()).append(" is only applicable to ");
		boolean first = true;
		for(Class<? extends Type> t : appTypes){
			if(!first){
				sb.append(", ");
			}
			sb.append(t.getName());
			first = false;
		}
		sb.append(" but not to ").append(this.actualType.toString());
		this.message = sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actualType == null) ? 0 : actualType.hashCode());
		result = prime * result + ((appTypes == null) ? 0 : appTypes.hashCode());
		result = prime * result + ((op == null) ? 0 : op.hashCode());
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
		InapplicableOperationError other = (InapplicableOperationError) obj;
		if (actualType == null) {
			if (other.actualType != null)
				return false;
		} else if (!actualType.equals(other.actualType))
			return false;
		if (appTypes == null) {
			if (other.appTypes != null)
				return false;
		} else if (!appTypes.equals(other.appTypes))
			return false;
		if (op == null) {
			if (other.op != null)
				return false;
		} else if (!op.equals(other.op))
			return false;
		return true;
	}

}
