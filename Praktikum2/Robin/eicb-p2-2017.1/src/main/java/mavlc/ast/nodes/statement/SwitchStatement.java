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
package mavlc.ast.nodes.statement;

import java.util.ArrayList;
import java.util.List;

import mavlc.ast.nodes.expression.Expression;
import mavlc.ast.visitor.ASTNodeVisitor;
/**
 * AST-node representing a switch-case-statement.
 */
public class SwitchStatement  extends Statement {
	
	private static final long serialVersionUID = -3984538633351524643L;
	
	protected Expression testExpression;
	protected final List<Case> cases = new ArrayList<>();
	protected final List<Default> defaultCases = new ArrayList<>();
	
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 */
	public SwitchStatement(int sourceLine, int sourceColumn) {
		super(sourceLine, sourceColumn);
	}
	
	/**
	 * Set the test expression.
	 * @param test Expression that is evaluated to determine which case is executed.
	 */
	public void setTestExpression(Expression test) {
		testExpression = test;	
	}
	
	/**
	 * Get the test expression.
	 * @return Expression that is evaluated to determine which case is executed.
	 */
	public Expression getTestExpression() {
		return testExpression;
	}
	
	/**
	 * Add a case.
	 * @param aCase The new case.
	 */
	public void addCase(Case aCase){
		cases.add(aCase);
	}
	
	/**
	 * Get all cases currently associated with this statement.
	 * @return List of cases.
	 */
	public List<Case> getCases() {
		return cases;
	}
	
	/**
	 * Add a default case.
	 * @param defaultCase The new default case.
	 */
	public void addDefault(Default defaultCase){
		defaultCases.add(defaultCase);
	}
	
	/**
	 * Get all default cases currently associated with this statement.
	 * @return List of default cases.
	 */
	public List<Default> getDefaultCases() {
		return defaultCases;
	}
	
	@Override
	public <RetTy, ArgTy> RetTy accept(ASTNodeVisitor<? extends RetTy, ArgTy> visitor, ArgTy obj){
		return visitor.visitSwitchStatement(this, obj);
	}
	
	@Override
	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append("switch( ").append(testExpression.dump()).append(" ){ \n");
		for(SingleCase actCase:cases)
			sb.append( actCase.dump()).append( "\n");
		for(SingleCase defaultC: defaultCases)
			sb.append(defaultC.dump()).append("\n");
		sb.append("}\n");
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		SwitchStatement that = (SwitchStatement) o;
		
		if (testExpression != null ? !testExpression.equals(that.testExpression) : that.testExpression != null)
			return false;
		if (cases != null ? !cases.equals(that.cases) : that.cases != null) return false;
		return defaultCases != null ? defaultCases.equals(that.defaultCases) : that.defaultCases == null;
	}
	
	@Override
	public int hashCode() {
		int result = testExpression != null ? testExpression.hashCode() : 0;
		result = 31 * result + (cases != null ? cases.hashCode() : 0);
		result = 31 * result + (defaultCases != null ? defaultCases.hashCode() : 0);
		return result;
	}
}
	
