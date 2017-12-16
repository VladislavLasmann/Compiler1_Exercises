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
package mavlc.ast.nodes.module;

import java.util.LinkedList;
import java.util.List;

import mavlc.ast.nodes.ASTNode;
import mavlc.ast.nodes.function.Function;
import mavlc.ast.nodes.record.RecordTypeDeclaration;
import mavlc.ast.visitor.ASTNodeVisitor;

/**
 * AST-node representing a MAVL module.
 */
public class Module extends ASTNode {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2252434872257383675L;

	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 */
	public Module(int sourceLine, int sourceColumn) {
		super(sourceLine, sourceColumn);
	}
	
	protected final List<Function> functions = new LinkedList<Function>();

	protected final List<RecordTypeDeclaration> records = new LinkedList<RecordTypeDeclaration>();
	
	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitModule(this, obj);
	}

	@Override
	public String dump() {
		StringBuilder sb = new StringBuilder();
		for(RecordTypeDeclaration rec : records){
			sb.append(rec.dump()).append("\n");
		}
		for(Function func : functions){
			sb.append(func.dump()).append("\n");
		}
		return sb.toString();
	}
	
	/**
	 * Add a {@link Function} to the module.
	 * @param function Function to add to the module.
	 */
	public void addFunction(Function function){
		functions.add(function);
	}

	/**
	 * Get a list of all functions defined in this module.
	 * @return List of all defined functions.
	 */
	public List<Function> getFunctions() {
		return functions;
	}
	
	/**
	 * Add a {@link RecordTypeDeclaration} to the module.
	 * @param record Record to add to the module.
	 */
	public void addRecord(RecordTypeDeclaration record){
		records.add(record);
	}

	/**
	 * Get a list of all records defined in this module.
	 * @return List of all defined records.
	 */
	public List<RecordTypeDeclaration> getRecords() {
		return records;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((functions == null) ? 0 : functions.hashCode());
		result = prime * result + ((records == null) ? 0 : records.hashCode());
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
		Module other = (Module) obj;
		if (functions == null) {
			if (other.functions != null)
				return false;
		} else if (!functions.equals(other.functions))
			return false;
		if (records == null) {
			if (other.records != null)
				return false;
		} else if (!records.equals(other.records))
			return false;
		return true;
	}

}
