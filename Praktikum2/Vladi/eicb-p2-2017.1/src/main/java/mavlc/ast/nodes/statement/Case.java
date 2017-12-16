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

/**
 * AST-node representing a case.
 */
public class Case extends SingleCase {
	
	protected final int condition;
	/**
	 * Constructor.
	 * @param sourceLine The source line in which the node was specified.
	 * @param sourceColumn The source column in which the node was specified.
	 * @param condition The integer constant that is compared against the surrounding switch statement's test expression.
	 * @param statement The statement that is executed if the condition is equal to the test expression's value.
	 */
	public Case(int sourceLine, int sourceColumn, int condition, Statement statement) {
		super(sourceLine, sourceColumn, statement);
		this.condition = condition;
	}

	/**
	 * Get the condition.
	 * @return The integer constant associated with this case.
	 */
	public int getCondition () {
		return condition;
	}
	
	@Override
	public boolean isDefault() {
		return false;
	}
	
	@Override
	public String dump() {
		return ("case " + condition + ":\n" )+ " "+  statement.dump();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		
		Case aCase = (Case) o;
		
		return condition == aCase.condition;
	}
	
	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + condition;
		return result;
	}
}
