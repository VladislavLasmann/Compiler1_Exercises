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

/* TODO: Please fill this out!
 * 
 * EiCB group number:
 * Names and student ID numbers of group members:
 */

package mavlc.context_analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

import mavlc.ast.nodes.ASTNode;
import mavlc.ast.nodes.expression.Addition;
import mavlc.ast.nodes.expression.And;
import mavlc.ast.nodes.expression.BinaryExpression;
import mavlc.ast.nodes.expression.BoolNot;
import mavlc.ast.nodes.expression.BoolValue;
import mavlc.ast.nodes.expression.CallExpression;
import mavlc.ast.nodes.expression.Compare;
import mavlc.ast.nodes.expression.Division;
import mavlc.ast.nodes.expression.DotProduct;
import mavlc.ast.nodes.expression.ElementSelect;
import mavlc.ast.nodes.expression.Exponentiation;
import mavlc.ast.nodes.expression.Expression;
import mavlc.ast.nodes.expression.FloatValue;
import mavlc.ast.nodes.expression.IdentifierReference;
import mavlc.ast.nodes.expression.IntValue;
import mavlc.ast.nodes.expression.MatrixMultiplication;
import mavlc.ast.nodes.expression.MatrixXDimension;
import mavlc.ast.nodes.expression.MatrixYDimension;
import mavlc.ast.nodes.expression.Multiplication;
import mavlc.ast.nodes.expression.Or;
import mavlc.ast.nodes.expression.RecordElementSelect;
import mavlc.ast.nodes.expression.RecordInit;
import mavlc.ast.nodes.expression.SelectExpression;
import mavlc.ast.nodes.expression.StringValue;
import mavlc.ast.nodes.expression.StructureInit;
import mavlc.ast.nodes.expression.SubMatrix;
import mavlc.ast.nodes.expression.SubVector;
import mavlc.ast.nodes.expression.Subtraction;
import mavlc.ast.nodes.expression.UnaryMinus;
import mavlc.ast.nodes.expression.VectorDimension;
import mavlc.ast.nodes.function.FormalParameter;
import mavlc.ast.nodes.function.Function;
import mavlc.ast.nodes.module.Module;
import mavlc.ast.nodes.record.RecordElementDeclaration;
import mavlc.ast.nodes.record.RecordTypeDeclaration;
import mavlc.ast.nodes.statement.CallStatement;
import mavlc.ast.nodes.statement.Case;
import mavlc.ast.nodes.statement.CompoundStatement;
import mavlc.ast.nodes.statement.Declaration;
import mavlc.ast.nodes.statement.Default;
import mavlc.ast.nodes.statement.ForEachLoop;
import mavlc.ast.nodes.statement.ForLoop;
import mavlc.ast.nodes.statement.IfStatement;
import mavlc.ast.nodes.statement.IteratorDeclaration;
import mavlc.ast.nodes.statement.LeftHandIdentifier;
import mavlc.ast.nodes.statement.MatrixLHSIdentifier;
import mavlc.ast.nodes.statement.RecordLHSIdentifier;
import mavlc.ast.nodes.statement.ReturnStatement;
import mavlc.ast.nodes.statement.SingleCase;
import mavlc.ast.nodes.statement.Statement;
import mavlc.ast.nodes.statement.SwitchStatement;
import mavlc.ast.nodes.statement.ValueDefinition;
import mavlc.ast.nodes.statement.VariableAssignment;
import mavlc.ast.nodes.statement.VectorLHSIdentifier;
import mavlc.ast.type.BoolType;
import mavlc.ast.type.FloatType;
import mavlc.ast.type.IntType;
import mavlc.ast.type.MatrixType;
import mavlc.ast.type.PrimitiveType;
import mavlc.ast.type.RecordType;
import mavlc.ast.type.ScalarType;
import mavlc.ast.type.StructType;
import mavlc.ast.type.Type;
import mavlc.ast.type.VectorType;
import mavlc.ast.visitor.ASTNodeBaseVisitor;
import mavlc.error_reporting.ArgumentCountError;
import mavlc.error_reporting.ConstantAssignmentError;
import mavlc.error_reporting.DuplicateCaseException;
import mavlc.error_reporting.InapplicableOperationError;
import mavlc.error_reporting.MisplacedReturnError;
import mavlc.error_reporting.MissingMainFunctionError;
import mavlc.error_reporting.NonConstantOffsetError;
import mavlc.error_reporting.RecordElementError;
import mavlc.error_reporting.StructureDimensionError;
import mavlc.error_reporting.TypeError;

/**
 * A combined identifiation and type checking visitor.
 */
public class ContextualAnalysis extends ASTNodeBaseVisitor<Type, Boolean> {
	
	protected final ModuleEnvironment env;
	
	protected final IdentificationTable table;
	
	protected Function currentFunction;
	
	/**
	 * Constructor.
	 * 
	 * @param moduleEnvironment i.e. a simple identification table for a module's functions.
	 */
	public ContextualAnalysis(ModuleEnvironment moduleEnvironment){
		env = moduleEnvironment;
		table = new IdentificationTable();
	}
	
	private void checkType(ASTNode node, Type t1, Type t2){
		if(!t1.equals(t2)){
			throw new TypeError(node, t1, t2);
		}
	}
	
	private void resolveRecordType(RecordType type) {
		RecordTypeDeclaration decl = env.getRecordTypeDeclaration(type.getName());
		type.setTypeDeclaration(decl);
	}
	
	@Override
	public Type visitModule(Module module, Boolean __) {
		boolean hasMain = false;
		for (RecordTypeDeclaration record : module.getRecords()) {
			record.accept(this, null);
		}
		for(Function function : module.getFunctions()){
			currentFunction = function;
			function.accept(this, null);
			if(isMainFunction(function)){
				hasMain = true;
			}
		}
		if(!hasMain){
			throw new MissingMainFunctionError();
		}
		
		return null;
	}
	
	private boolean isMainFunction(Function func){
		/*
		 * Signature of the main method is "function void main()"
		 */
		if(!func.getName().equals("main")){
			return false;
		}
		if(!func.getParameters().isEmpty()){
			return false;
		}
		if(!func.getReturnType().equals(Type.getVoidType())){
			return false;
		}
		return true;
	}

	@Override
	public Type visitFunction(Function functionNode, Boolean __) {
		table.openNewScope();
		for(FormalParameter param : functionNode.getParameters()){
			table.addIdentifier(param.getName(), param);
		}
		if (functionNode.getReturnType() instanceof RecordType) {
			resolveRecordType((RecordType) functionNode.getReturnType());
		}
		Iterator<Statement> it = functionNode.getFunctionBody().iterator();
		while(it.hasNext()){
			Statement stmt = it.next();
			if(!it.hasNext() && !functionNode.getReturnType().equals(Type.getVoidType())){
				/*
				 *  Last statement in a non-void function, the only location where 
				 *  a return statement is allowed
				 */
				stmt.accept(this, true);
			}
			else{
				stmt.accept(this, false);
			}
		}
		table.closeCurrentScope();
		return null;
	}

	@Override
	public Type visitFormalParameter(FormalParameter formalParameter, Boolean __) {
		Type type = formalParameter.getType();
		if (type instanceof RecordType) {
			resolveRecordType((RecordType) type);
		}
		return type;
	}
	
	/**
	 * Exercise 2.3
	 */
	@Override
	public Type visitIteratorDeclaration(IteratorDeclaration iteratorDeclaration, Boolean __) {
		table.addIdentifier(iteratorDeclaration.getName(), iteratorDeclaration);
		return iteratorDeclaration.getType();
	}

	@Override
	public Type visitRecordTypeDeclaration(RecordTypeDeclaration recordTypeDeclaration, Boolean __) {
		Set<String> elementNames = new HashSet<>();
		RecordType type = new RecordType(recordTypeDeclaration.getName(), recordTypeDeclaration);
		for (RecordElementDeclaration element : recordTypeDeclaration.getElements()) {
			if (!elementNames.add(element.getName())) {
				// two elements with the same name
				throw new RecordElementError(recordTypeDeclaration, type, element.getName());
			}
		}		
		return type;
	}
	
	@Override
	public Type visitRecordElementDeclaration(RecordElementDeclaration recordElementDeclaration, Boolean __) {
		return recordElementDeclaration.getType();
	}
	
	@Override
	public Type visitDeclaration(Declaration declaration, Boolean __) {
		Type type = declaration.getType();
		if(type instanceof StructType){
			Type elemType = ((StructType) declaration.getType()).getElementType();
			if(!elemType.isScalarType()){
				throw new InapplicableOperationError(declaration, elemType, FloatType.class, IntType.class);
			}
		} else if (type instanceof RecordType) {
			resolveRecordType((RecordType) type);
		}
		table.addIdentifier(declaration.getName(), declaration);
		return null;
	}
	
	@Override
	public Type visitValueDefinition(ValueDefinition valueDefinition, Boolean __) {
		visitDeclaration(valueDefinition, null);
		Type lhs = valueDefinition.getType();
		Type rhs = valueDefinition.getValue().accept(this, null);
		checkType(valueDefinition, lhs, rhs);
		return null;
	}

	/**
	 * Exercise 2.2
	 */
	@Override
	public Type visitVariableAssignment(VariableAssignment variableAssignment, Boolean __) {
		Type identifier = variableAssignment.getIdentifier().accept(this, null);
		Type value = variableAssignment.getValue().accept(this, null);
		
		checkType(variableAssignment, identifier, value);

		return null;
	}

	/**
	 * Exercise 2.2
	 */
	@Override
	public Type visitLeftHandIdentifier(LeftHandIdentifier leftHandIdentifier, Boolean __) {
		String name = leftHandIdentifier.getName();
		Declaration declaration = table.getDeclaration(name);

		// Check whether declaration is a variable. if not -> throw error
		if( ! declaration.isVariable() ){
			throw new ConstantAssignmentError(leftHandIdentifier);
		}
		// check sucessfull, set declaration
		leftHandIdentifier.setDeclaration(declaration);
	
		// return declarations type
		return declaration.getType();
	}

	/**
	 * Exercise 2.2
	 */
	@Override
	public Type visitMatrixLHSIdentifier(MatrixLHSIdentifier matrixLHSIdentifier, Boolean __) {
		// get name from indentifier and declaration from table
		String name = matrixLHSIdentifier.getName();
		Declaration declaration = table.getDeclaration(name);
		Type declarationType = declaration.getType();

		// Check whether declaration is a variable. if not -> throw error
		if( ! declaration.isVariable() ){
			throw new ConstantAssignmentError( matrixLHSIdentifier );
		}
		// check sucessfull, set declaration 
		matrixLHSIdentifier.setDeclaration(declaration);


		// check whether the declaration type is a matrix type. if not -> error
		if( ! (declarationType instanceof MatrixType) ){
			throw new InapplicableOperationError(matrixLHSIdentifier, declaration.getType(), MatrixType.class);
		}

		// get x and y indices and check their types (should be integer types)
		Type xIndexType = matrixLHSIdentifier.getXIndex().accept(this, null);
		Type yIndexType = matrixLHSIdentifier.getYIndex().accept(this, null);
		checkType(matrixLHSIdentifier, xIndexType, Type.getIntType() );
		checkType(matrixLHSIdentifier, yIndexType, Type.getIntType() );

		int xDimension = ((MatrixType) declarationType).getxDimension();
		int yDimension = ((MatrixType) declarationType).getyDimension();
		int identifierDimensionX = getOffSet(matrixLHSIdentifier.getXIndex());
		int identifierDimensionY = getOffSet(matrixLHSIdentifier.getYIndex());


		if( identifierDimensionX < 0  ){
			throw new StructureDimensionError(matrixLHSIdentifier, identifierDimensionX, 0);
		}
		if( identifierDimensionX >  xDimension ){
			throw new StructureDimensionError(matrixLHSIdentifier, xDimension, identifierDimensionX);
		}
		if( identifierDimensionY < 0 ){
			throw new StructureDimensionError(matrixLHSIdentifier, identifierDimensionY, 0);
		}
		if( identifierDimensionY < 0 || identifierDimensionY > yDimension){
			throw new StructureDimensionError(matrixLHSIdentifier, yDimension, identifierDimensionY);
		}

		// Typecast declarations type to a matrix
		Type matrixType = ((MatrixType) declaration.getType()).getElementType();

		return matrixType;
	}

	/**
	 * Exercise 2.2
	 */
	@Override
	public Type visitVectorLHSIdentifier(VectorLHSIdentifier vectorLHSIdentifier, Boolean __) {
		// get declaration from table
		Declaration declaration = table.getDeclaration( vectorLHSIdentifier.getName() );

		// Check whether declaration is a variable. if not -> error
		if( ! declaration.isVariable() ){
			throw new ConstantAssignmentError( vectorLHSIdentifier );
		}
		// check successfull, set declaration
		vectorLHSIdentifier.setDeclaration( declaration );
		
		// check whether the declaration type is a vector type. if not -> error
		if( !(declaration.getType() instanceof VectorType ) ){
			throw new InapplicableOperationError(vectorLHSIdentifier, declaration.getType(), VectorType.class);
		}

		// get index and check its type (should be integer)
		Type indexType = vectorLHSIdentifier.getIndex().accept(this, null);
		checkType(vectorLHSIdentifier, indexType, Type.getIntType() );

		int declarationOffset = declaration.getLocalBaseOffset();
		int vectorIdentifierOffset = getOffSet(vectorLHSIdentifier.getIndex());

		if( vectorIdentifierOffset < 0){
			throw new StructureDimensionError(vectorLHSIdentifier, vectorIdentifierOffset, 0);
		}
		if( vectorIdentifierOffset > declarationOffset ){
			throw new StructureDimensionError(vectorLHSIdentifier, declarationOffset, vectorIdentifierOffset);
		}

		// Typecast declarations type to a matrix
		Type vectorType = ((VectorType) declaration.getType()).getElementType();
		
		return vectorType;
	}

	/**
	 * Exercise 2.2
	 */
	@Override
	public Type visitRecordLHSIdentifier(RecordLHSIdentifier recordLHSIdentifier, Boolean __) {
		// get declaration from table
		Declaration declaration = table.getDeclaration( recordLHSIdentifier.getName() );
		Type declarationType = null;
		Type returnType = null;

		// Check whether declaration is variable. if not -> error
		if( ! declaration.isVariable() ){
			throw new ConstantAssignmentError( recordLHSIdentifier );
		}
		// check successfull, set declaration
		recordLHSIdentifier.setDeclaration( declaration );

		// check whether declaration type is record type. if not -> error
		declarationType = declaration.getType();
		if( !(declarationType instanceof RecordType) ){
			throw new InapplicableOperationError(recordLHSIdentifier, declarationType, RecordType.class);
		}

		// get the record element declaration
		RecordTypeDeclaration recordTypeDeclaration = ((RecordType) declarationType).getTypeDeclaration();
		RecordElementDeclaration recordElementDeclaration = recordTypeDeclaration.getElement( recordLHSIdentifier.getElementName() );

		if( ! recordElementDeclaration.isVariable() ){
			throw new ConstantAssignmentError( recordLHSIdentifier );
		}

		returnType = recordElementDeclaration.accept(this, null);
		checkType(recordLHSIdentifier, returnType, recordElementDeclaration.getType());

		return returnType;
	}
	
	@Override
	public Type visitForLoop(ForLoop forLoop, Boolean __) {
		/*
		 * Check for equal type on both sides of the initializer.
		 */
		Declaration initVarDecl = table.getDeclaration(forLoop.getInitVariableName());
 		if(!initVarDecl.isVariable()){
 			throw new ConstantAssignmentError(forLoop);
 		}
 		forLoop.setInitVarDeclaration(initVarDecl);
		Type initVarType = initVarDecl.getType();
		Type initValType = forLoop.getInitValue().accept(this, null);
		checkType(forLoop, initVarType, initValType);
		/*
		 * Check that the loop test has type boolean.
		 */
		Type testType = forLoop.getCheck().accept(this, null);
		checkType(forLoop, testType, Type.getBoolType());
		/*
		 * Check for equal type on both sides of the increment.
		 */
		Declaration incrVarDecl = table.getDeclaration(forLoop.getIncrementVariableName());
		if(!incrVarDecl.isVariable()){
			throw new ConstantAssignmentError(forLoop);
		}
		forLoop.setIncrVarDeclaration(incrVarDecl);
		Type incrVarType = incrVarDecl.getType();
		Type incrValType = forLoop.getIncrementExpr().accept(this, null);
		checkType(forLoop, incrVarType, incrValType);
		/*
		 * Process loop body.
		 */
		table.openNewScope();
		forLoop.getLoopBody().accept(this, false);
		table.closeCurrentScope();
		return null;
	}

	/**
	 * Exercise 2.3
	 */
	@Override
	public Type visitForEachLoop(ForEachLoop forEachLoop, Boolean __) {
		/**
		 * Bedingungen an forEachLoop
		 * - iterator-deklaration
		 * 	1. im eigenen Scope 
		 *  2. var oder val + Typ + namen
		 *  3. Bei var, element veränderbar
		 * 
		 * - Ausdruck
		 *  1. zu Vektor/Matrix evaluiert und über Elemente iteriert
		 * 
		 * - Zusätzlich:
		 *  1. Iterator nur var, wenn Ausdruck var
		 *  2. Typ von iterator = Typ von Ausdruck 
		 * 
		 * 
		 */
		 // open scope
		table.openNewScope();

		IteratorDeclaration iteratorDeclaration = forEachLoop.getIteratorDeclaration();
		Expression structExpression = forEachLoop.getStructExpr();
		Statement loopBody = forEachLoop.getLoopBody();
		//accept both
		iteratorDeclaration.accept(this, null);
		structExpression.accept(this, null);

		if( structExpression instanceof IdentifierReference ){
			Declaration declaration = ((IdentifierReference) structExpression).getDeclaration();

			if( iteratorDeclaration.isVariable() && !(declaration.isVariable()) ){
				throw new ConstantAssignmentError( forEachLoop );
			}
		}

		Type returnType = loopBody.accept(this, null);

		// closing scope
		table.closeCurrentScope();

		// return nothing
		return returnType;
	}
	
	@Override
	public Type visitIfStatement(IfStatement ifStatement, Boolean __) {
		Type testType = ifStatement.getTestExpression().accept(this, null);
		checkType(ifStatement, testType, Type.getBoolType());
		table.openNewScope();
		ifStatement.getThenStatement().accept(this, false);
		table.closeCurrentScope();
		if(ifStatement.hasElseStatement()){
			table.openNewScope();
			ifStatement.getElseStatement().accept(this, false);
			table.closeCurrentScope();
		}
		return null;
	}

	@Override
	public Type visitCallStatement(CallStatement callStatement, Boolean __) {
		callStatement.getCall().accept(this, null);
		return null;
	}

	@Override
	public Type visitReturnStatement(ReturnStatement returnStatement, Boolean returnAllowed) {
		if(!returnAllowed){
			throw new MisplacedReturnError(returnStatement);
		}
		Type retVal = returnStatement.getReturnValue().accept(this, null);
		checkType(returnStatement, retVal, currentFunction.getReturnType());
		return retVal;
	}

	/**
	 * Exercise 2.1
	 */
	@Override
	public Type visitCompoundStatement(CompoundStatement compoundStatement, Boolean __) {
		// compount -> new Scope
		table.openNewScope();

		// traverse through all Statements in the this compound statement
		for(Statement statement : compoundStatement.getStatements()){
			// this because of this extends ASTBaseVisitor (implements ASTNodeVisitor)
			statement.accept(this, false);
		}
		
		// 
		table.closeCurrentScope();

		// no other type fits
		return null;
	}
	
	/**
	 * Exercise 2.4
	 */
	@Override
	public Type visitSwitchStatement(SwitchStatement switchCaseStatement, Boolean __) {
		/**
		 * Bedingungen:
		 *  - Bedingungsvariable vorher deklariert
		 *  - Bedingung darf nur Int sein
		 *  - maximal ein default case!
		 */
		Expression testExpression = switchCaseStatement.getTestExpression();
		List<Case> cases = switchCaseStatement.getCases();
		List<Default> defaultCases = switchCaseStatement.getDefaultCases();

		// get TestExpression and check the Type;
		Type testExpressionType = testExpression.accept(this, null);
		checkType(switchCaseStatement, testExpressionType, Type.getIntType() );

		// process cases and check if some have the same condition
		Map<Integer, Case> caseConditions = new HashMap<Integer, Case>();
		for( Case aCase : cases ){
			Integer condition = aCase.getCondition();
			if( caseConditions.containsKey(condition) ){
				throw new DuplicateCaseException(switchCaseStatement, false, caseConditions.get(condition), aCase);
			}
			caseConditions.put(condition, aCase);
			visitSingleCase(aCase, null);
		}

		// should contain only one default statement
		if( defaultCases.size() > 1 ){
			throw new DuplicateCaseException(switchCaseStatement, true, defaultCases.get(0), defaultCases.get(1));
		} 
		// process default Case, if there's one
		for( Default theDefault : defaultCases ){
			visitSingleCase(theDefault, null);
		}

		table.openNewScope();
		// blabla
		table.closeCurrentScope();
		
		return null;
	}
	
	/**
	 * Exercise 2.4
	 */
	@Override
	public Type visitSingleCase(SingleCase sCase,Boolean __){
		/**
		 *  - switch oder default
		 *  - hat nur integer constanten
		 *  - am ende ein break
		 */
		
		return sCase.getStatement().accept(this, null);
	}	
	
	@Override
	public Type visitMatrixMultiplication(MatrixMultiplication matrixMultiplication, Boolean __) {
		/*
		 *  Complex Version of MatrixMul.
		 */
		Type leftOp = matrixMultiplication.getLeftOp().accept(this, null);
		Type rightOp = matrixMultiplication.getRightOp().accept(this, null);
		ScalarType elementType = null;
		if(!(leftOp instanceof StructType)){
			throw new InapplicableOperationError(matrixMultiplication, leftOp, MatrixType.class, VectorType.class);
		}
		if(!(rightOp instanceof StructType)){
			throw new InapplicableOperationError(matrixMultiplication, rightOp, MatrixType.class, VectorType.class);
		}
		else{
			checkType(matrixMultiplication, ((StructType) leftOp).getElementType(), ((StructType) rightOp).getElementType());
			elementType = ((StructType) leftOp).getElementType();
		}
		int lm = -1;
		int n = -1;
		if(leftOp instanceof MatrixType){
			// Y-Dimension = Number of columns in the matrix
			lm = ((MatrixType) leftOp).getyDimension();
			// X-Dimension = Number of rows in the matrix
			n = ((MatrixType) leftOp).getxDimension();
		}
		else if(leftOp instanceof VectorType){
			/*
			 * Vector implicitly treated as row-vector, 
			 * dimension = number of columns
			 */
			lm = ((VectorType) leftOp).getDimension();
			n = 1;
		}
		int rm = -1;
		int p = -1;
		if(rightOp instanceof MatrixType){
			rm = ((MatrixType) rightOp).getxDimension();
			p = ((MatrixType) rightOp).getyDimension();
		}
		if(rightOp instanceof VectorType){
			/*
			 * Vector implicitly treated as column-vector,
			 * dimension = number of rows
			 */
			rm = ((VectorType) rightOp).getDimension();
			p = 1;
		}
		if(lm != rm){
			throw new StructureDimensionError(matrixMultiplication, lm, rm);
		}
		if(n==1){
			// Only one row in the first operand
			if(p==1){
				// Only one column in the second operand, result is just a single element
				matrixMultiplication.setType(elementType);
				return elementType;
			}
			else{
				// More than one column in the second operand, result is a vector of p elements
				VectorType resultType = new VectorType(elementType, p);
				matrixMultiplication.setType(resultType);
				return resultType;
			}
		}
		else{
			// More than one row in the first operand
			if(p==1){
				// Only one column in the second operand, result is a vector of n elements
				VectorType resultType = new VectorType(elementType, n);
				matrixMultiplication.setType(resultType);
				return resultType;
			}
			else{
				// More than one column in the second operand, result is a matrix of nxp elements
				MatrixType resultType = new MatrixType(elementType, n, p);
				matrixMultiplication.setType(resultType);
				return resultType;
			}
		}
		
	}

	@Override
	public Type visitDotProduct(DotProduct dotProduct, Boolean __) {
		Type leftOp = dotProduct.getLeftOp().accept(this, null);
		Type rightOp = dotProduct.getRightOp().accept(this, null);
		PrimitiveType elementType = null;
		if(!(leftOp instanceof VectorType)){
			/*
			 * We define the dot-product only for vectors, for a matrix-vector dot-product
			 * the matrix multiplication operator can be used
			 */
			throw new InapplicableOperationError(dotProduct, leftOp, VectorType.class);
		}
		if(!(rightOp instanceof VectorType)){
			/*
			 * We define the dot-product only for vectors, for a matrix-vector dot-product
			 * the matrix multiplication operator can be used
			 */
			throw new InapplicableOperationError(dotProduct, rightOp, VectorType.class);
		}
		else{
			VectorType leftVec = (VectorType) leftOp;
			VectorType rightVec = (VectorType) rightOp;
			checkType(dotProduct, leftVec.getElementType(), rightVec.getElementType());
			if(leftVec.getDimension()!=rightVec.getDimension()){
				throw new StructureDimensionError(dotProduct, leftVec.getDimension(), rightVec.getDimension());
			}
			elementType = ((VectorType) leftOp).getElementType();
			dotProduct.setType(elementType);
			return elementType;
		}
	}

	/**
	 * Exercise 2.5
	 */
	@Override
	public Type visitMultiplication(Multiplication multiplication, Boolean __) {
		Type leftOperandType = multiplication.getLeftOp().accept(this, null);
		Type rightOperandType = multiplication.getRightOp().accept(this, null);

		if(!(leftOperandType.isScalarType()) && !( leftOperandType instanceof StructType) ){
			throw new InapplicableOperationError(multiplication, leftOperandType, IntType.class, FloatType.class, VectorType.class, MatrixType.class);
		}
		else if(!(rightOperandType.isScalarType()) && !( rightOperandType instanceof StructType )){
			throw new InapplicableOperationError(multiplication, rightOperandType, IntType.class, FloatType.class, VectorType.class, MatrixType.class);
		}
		else if( leftOperandType.isScalarType() && (rightOperandType instanceof StructType) ){
			// get struct element type
			Type structElementType = ((StructType) rightOperandType).getElementType();
			checkType(multiplication, leftOperandType, structElementType);
			multiplication.setType(rightOperandType);
			return rightOperandType;

		}
		else if( rightOperandType.isScalarType() && (leftOperandType instanceof StructType) ){
			Type structElementType = ((StructType) leftOperandType).getElementType();
			checkType(multiplication, rightOperandType, structElementType);
			multiplication.setType(leftOperandType);
			return leftOperandType;
		}
		else{
			checkType(multiplication, leftOperandType, rightOperandType);
			multiplication.setType(leftOperandType);

			return leftOperandType;
		}
	}
	
	@Override
	public Type visitDivision(Division division, Boolean __) {
		Type leftOp = division.getLeftOp().accept(this, null);
		Type rightOp = division.getRightOp().accept(this, null);
		if(!leftOp.isScalarType()){
			throw new InapplicableOperationError(division, leftOp, FloatType.class, IntType.class);
		}
		if(!rightOp.isScalarType()){
			throw new InapplicableOperationError(division, rightOp, FloatType.class, IntType.class);
		}
		checkType(division, leftOp, rightOp);
		division.setType(leftOp);
		return leftOp;
	}

	/**
	 * Exercise 2.5
	 */
	@Override
	public Type visitAddition(Addition addition, Boolean __) {
		Type leftOperandType = addition.getLeftOp().accept(this, null);
		Type rightOperandType = addition.getRightOp().accept(this, null);

		if(!(leftOperandType.isScalarType()) && !( leftOperandType instanceof StructType) ){
			throw new InapplicableOperationError(addition, leftOperandType, IntType.class, FloatType.class, VectorType.class, MatrixType.class);
		}
		else if(!(rightOperandType.isScalarType()) && !( rightOperandType instanceof StructType) ){
			throw new InapplicableOperationError(addition, rightOperandType, IntType.class, FloatType.class, VectorType.class, MatrixType.class);
		}
		else if( leftOperandType.isScalarType() && (rightOperandType instanceof StructType) ){
			// get struct element type
			Type structElementType = ((StructType) rightOperandType).getElementType();
			checkType(addition, leftOperandType, structElementType);
			addition.setType(rightOperandType);
			return rightOperandType;

		}
		else if( rightOperandType.isScalarType() && (leftOperandType instanceof StructType) ){
			Type structElementType = ((StructType) leftOperandType).getElementType();
			checkType(addition, rightOperandType, structElementType);
			addition.setType(leftOperandType);
			return leftOperandType;
		}
		else{
			checkType(addition, leftOperandType, rightOperandType);
			addition.setType(leftOperandType);

			return leftOperandType;
		}
	}

	/**
	 * Exercise 2.5
	 */
	@Override
	public Type visitSubtraction(Subtraction subtraction, Boolean __) {
		Type leftOperandType = subtraction.getLeftOp().accept(this, null);
		Type rightOperandType = subtraction.getRightOp().accept(this, null);

		if( !(leftOperandType.isScalarType() ) && !( leftOperandType instanceof StructType ) ){
			throw new InapplicableOperationError(subtraction, leftOperandType, IntType.class, FloatType.class, VectorType.class, MatrixType.class);
		}
		if( !(rightOperandType.isScalarType() ) && !( rightOperandType instanceof StructType) ){
			throw new InapplicableOperationError(subtraction, rightOperandType, IntType.class, FloatType.class, VectorType.class, MatrixType.class);
		}
		checkType(subtraction, leftOperandType, rightOperandType);
		subtraction.setType(leftOperandType);

		return leftOperandType;
	}
	
	@Override
	public Type visitCompare(Compare compare, Boolean __) {
		Type leftOp = compare.getLeftOp().accept(this, null);
		Type rightOp = compare.getRightOp().accept(this, null);
		if(!leftOp.isScalarType()){
			throw new InapplicableOperationError(compare, leftOp, FloatType.class, IntType.class);
		}
		if(!rightOp.isScalarType()){
			throw new InapplicableOperationError(compare, rightOp, FloatType.class, IntType.class);
		}
		checkType(compare, leftOp, rightOp);
		compare.setType(Type.getBoolType());
		return Type.getBoolType();
	}
	
	@Override
	public Type visitAnd(And and, Boolean __) {
		return visitBooleanExpression(and, null);
	}

	@Override
	public Type visitOr(Or or, Boolean __) {
		return visitBooleanExpression(or, null);
	}

	/**
	 * Exercise 2.5
	 */
	@Override
	public Type visitExponentiation(Exponentiation exponentiation, Boolean __) {
		Type leftOperandType = exponentiation.getLeftOp().accept(this, null);
		Type rightOperandType = exponentiation.getRightOp().accept(this, null);

		if(!(leftOperandType.isScalarType()) && !( leftOperandType instanceof StructType) ){
			throw new InapplicableOperationError(exponentiation, leftOperandType, IntType.class, FloatType.class, VectorType.class, MatrixType.class);
		}
		else if(!(rightOperandType.isScalarType()) && !( rightOperandType instanceof StructType )){
			throw new InapplicableOperationError(exponentiation, rightOperandType, IntType.class, FloatType.class, VectorType.class, MatrixType.class);
		}
		else if( leftOperandType.isScalarType() && (rightOperandType instanceof StructType) ){
			throw new InapplicableOperationError(exponentiation, leftOperandType, VectorType.class, MatrixType.class);
		}
		else if( (leftOperandType instanceof StructType) && rightOperandType.isScalarType() ){
			Type leftStructElementType = ((StructType) leftOperandType).getElementType();
			checkType(exponentiation, leftStructElementType, rightOperandType);
			exponentiation.setType(leftOperandType);
			return leftOperandType;
		}
		else if( (leftOperandType instanceof StructType) && (rightOperandType instanceof StructType)){
			Type leftStructElementType = ((StructType) leftOperandType).getElementType();
			Type rightStructElementType = ((StructType) rightOperandType).getElementType();
			checkType(exponentiation, leftStructElementType, rightStructElementType);
			exponentiation.setType(leftOperandType);
			return leftOperandType;
		}
		else{
			checkType(exponentiation, leftOperandType, rightOperandType);
			exponentiation.setType(leftOperandType);
			return leftOperandType;
		}

	}
	
	private Type visitBooleanExpression(BinaryExpression exp, Boolean __){
		Type leftOp = exp.getLeftOp().accept(this, null);
		Type rightOp = exp.getRightOp().accept(this, null);
		if(!(leftOp instanceof BoolType)){
			throw new InapplicableOperationError(exp, leftOp, BoolType.class);
		}
		if(!(rightOp instanceof BoolType)){
			throw new InapplicableOperationError(exp, rightOp, BoolType.class);
		}
		exp.setType(Type.getBoolType());
		return Type.getBoolType();
	}

	@Override
	public Type visitMatrixXDimension(MatrixXDimension xDimension, Boolean __) {
		Type opType = xDimension.getOperand().accept(this, null);
		if(!(opType instanceof MatrixType)){
			throw new InapplicableOperationError(xDimension, opType, MatrixType.class);
		}
		xDimension.setType(Type.getIntType());
		return Type.getIntType();
	}

	@Override
	public Type visitMatrixYDimension(MatrixYDimension yDimension, Boolean __) {
		Type opType = yDimension.getOperand().accept(this, null);
		if(!(opType instanceof MatrixType)){
			throw new InapplicableOperationError(yDimension, opType, MatrixType.class);
		}
		yDimension.setType(Type.getIntType());
		return Type.getIntType();
	}

	@Override
	public Type visitVectorDimension(VectorDimension vectorDimension, Boolean __) {
		Type opType = vectorDimension.getOperand().accept(this, null);
		if(!(opType instanceof VectorType)){
			throw new InapplicableOperationError(vectorDimension, opType, VectorType.class);
		}
		vectorDimension.setType(Type.getIntType());
		return Type.getIntType();
	}
	
	@Override
	public Type visitUnaryMinus(UnaryMinus unaryMinus, Boolean __) {
		Type opType = unaryMinus.getOperand().accept(this, null);
		if(!opType.isScalarType()){
			throw new InapplicableOperationError(unaryMinus, opType, FloatType.class, IntType.class);
		}
		unaryMinus.setType(opType);
		return opType;
	}

	@Override
	public Type visitBoolNot(BoolNot boolNot, Boolean __) {
		Type opType = boolNot.getOperand().accept(this, null);
		checkType(boolNot, opType, Type.getBoolType());
		boolNot.setType(Type.getBoolType());
		return Type.getBoolType();
	}

	@Override
	public Type visitCallExpression(CallExpression callExpression, Boolean __) {
		Function callee = env.getFunctionDeclaration(callExpression.getCalleeName());
		if(callExpression.getActualParameters().size()>callee.getParameters().size()
				|| callExpression.getActualParameters().size() < callee.getParameters().size()){
			throw new ArgumentCountError(callExpression, callee, callee.getParameters().size(), 
											callExpression.getActualParameters().size());
		}
		Iterator<FormalParameter> it = callee.getParameters().iterator();
		for(Expression param : callExpression.getActualParameters()){
			Type actual = param.accept(this, null);
			Type formal = it.next().getType();
			if (formal instanceof RecordType) {
				// maybe formal has not been visited yet
				resolveRecordType((RecordType) formal);
			}
			checkType(callExpression, actual, formal);
		}
		callExpression.setCalleeDefinition(callee);
		callExpression.setType(callee.getReturnType());
		if (callee.getReturnType() instanceof RecordType) {
			// maybe callee has not been visited yet
			resolveRecordType((RecordType) callee.getReturnType());
		}
		return callee.getReturnType();
	}

	@Override
	public Type visitElementSelect(ElementSelect elementSelect, Boolean __) {
		Type baseType = elementSelect.getStruct().accept(this, null);
		if(!(baseType instanceof StructType)){
			throw new InapplicableOperationError(elementSelect, baseType, MatrixType.class, VectorType.class);
		}
		Type indexType = elementSelect.getIndex().accept(this, null);
		if(!indexType.equals(Type.getIntType())){
			throw new TypeError(elementSelect, indexType, IntType.getIntType());
		}
		if(baseType instanceof VectorType){
			Type resultType = ((VectorType) baseType).getElementType();
			elementSelect.setType(resultType);
			return resultType;
		}
		else if(baseType instanceof MatrixType){
			ScalarType elementType = ((MatrixType) baseType).getElementType();
			int size = ((MatrixType) baseType).getyDimension();
			Type resultType = new VectorType(elementType, size);
			elementSelect.setType(resultType);
			return resultType;
		}
		return null;
	}
	
	@Override
	public Type visitRecordElementSelect(RecordElementSelect recordElementSelect, Boolean __) {
		Type baseType = recordElementSelect.getRecord().accept(this, null);
		if(!(baseType instanceof RecordType)){
			throw new InapplicableOperationError(recordElementSelect, baseType, RecordType.class);
		}
		String elementName = recordElementSelect.getElementName();
		RecordElementDeclaration element = 
				(((RecordType) baseType).getTypeDeclaration().getElement(elementName));
		if (element == null) {
			throw new RecordElementError(recordElementSelect, (RecordType) baseType, elementName);			
		}
		recordElementSelect.setType(element.getType());
		return element.getType();
	}
	
	@Override
	public Type visitSubMatrix(SubMatrix subMatrix, Boolean __) {
		int xLB = getOffSet(subMatrix.getXStartIndex());
		int xUB = getOffSet(subMatrix.getXEndIndex());
		int yLB = getOffSet(subMatrix.getYStartIndex());
		int yUB = getOffSet(subMatrix.getYEndIndex());
		Type xIndex = subMatrix.getXBaseIndex().accept(this, null);
		checkType(subMatrix, xIndex, Type.getIntType());
		Type yIndex = subMatrix.getYBaseIndex().accept(this, null);
		checkType(subMatrix, yIndex, Type.getIntType());
		Type baseType = subMatrix.getStruct().accept(this, null);
		if(!(baseType instanceof MatrixType)){
			throw new InapplicableOperationError(subMatrix, baseType, MatrixType.class);
		}
		MatrixType matrix = (MatrixType) baseType;
		if(xUB < xLB){
			throw new StructureDimensionError(subMatrix, xUB, xLB);
		}
		int xSize = xUB - xLB + 1;
		if(matrix.getxDimension()<xSize){
			throw new StructureDimensionError(subMatrix, matrix.getxDimension(), xSize);
		}
		if(yUB < yLB){
			throw new StructureDimensionError(subMatrix, yUB, yLB);
		}
		int ySize = yUB - yLB + 1;
		if(matrix.getyDimension()<ySize){
			throw new StructureDimensionError(subMatrix, matrix.getyDimension(), ySize);
		}
		Type resultType;
		if(ySize == 1 && xSize == 1){//SubMatrix is Scalar
			resultType = ((MatrixType) baseType).getElementType();
		}else if(xSize == 1){ //SubMatrix is a Vector
			resultType = new VectorType(((MatrixType) baseType).getElementType(), ySize);
		}else{
			resultType = new MatrixType(((MatrixType) baseType).getElementType(), xSize, ySize);
		}
		subMatrix.setType(resultType);
		return resultType;
	}
	
	@Override
	public Type visitSubVector(SubVector subVector, Boolean __) {
		int lb = getOffSet(subVector.getStartIndex());
		int ub = getOffSet(subVector.getEndIndex());
		Type indexType = subVector.getBaseIndex().accept(this, null);
		checkType(subVector, indexType, Type.getIntType());
		Type baseType = subVector.getStruct().accept(this, null);
		if(!(baseType instanceof VectorType)){
			throw new InapplicableOperationError(subVector, baseType, VectorType.class);
		}
		VectorType vector = (VectorType) baseType;
		if(ub < lb){
			throw new StructureDimensionError(subVector, ub, lb);
		}
		int size = ub-lb+1;
		if(vector.getDimension()<size){
			throw new StructureDimensionError(subVector, vector.getDimension(), size);
		}
		Type resultType;
		if(size == 1){//Subvector is scalar
			resultType = ((VectorType) baseType).getElementType();
		}else{
			resultType = new VectorType(((VectorType) baseType).getElementType(), size);
		}
		subVector.setType(resultType);
		return resultType;
	}
	
	private int getOffSet(Expression offset){
		if(offset instanceof IntValue){
			offset.accept(this, null);
			return ((IntValue) offset).getValue();
		}
		else if(offset instanceof UnaryMinus){
			if(((UnaryMinus) offset).getOperand() instanceof IntValue){
				offset.accept(this, null);
				return -1* ((IntValue) ((UnaryMinus) offset).getOperand()).getValue();
			}
		}
		throw new NonConstantOffsetError(offset);
	}

	@Override
	public Type visitStructureInit(StructureInit structureInit, Boolean __) {
		// The type of the first element determines the structure
		Type firstElem = structureInit.getElements().iterator().next().accept(this, null);
		if(firstElem instanceof VectorType){
			// Matrix init
			ScalarType elemType = ((VectorType) firstElem).getElementType();
			int size = ((VectorType) firstElem).getDimension();
			int x = 0;
			for(Expression element : structureInit.getElements()){
				Type t = element.accept(this, null);
				checkType(structureInit, firstElem, t);
				++x;
			}
			MatrixType resultType = new MatrixType(elemType, x, size);
			structureInit.setType(resultType);
			return resultType;
		}
		else{
			// Vector init
			if(!firstElem.isScalarType()){
				throw new InapplicableOperationError(structureInit, firstElem, FloatType.class, IntType.class);
			}
			ScalarType elemType = (ScalarType) firstElem;
			int size=0;
			for(Expression element : structureInit.getElements()){
				Type t = element.accept(this, null);
				checkType(structureInit, elemType, t);
				++size;
			}
			VectorType resultType = new VectorType(elemType, size);
			structureInit.setType(resultType);
			return resultType;
		}
	}
	
	@Override
	public Type visitRecordInit(RecordInit recordInit, Boolean __) {
		RecordTypeDeclaration decl = env.getRecordTypeDeclaration(recordInit.getName());
		recordInit.setType(decl.accept(this, null));
		if (recordInit.getElements().size() != decl.getElements().size()) {
			throw new StructureDimensionError(recordInit, recordInit.getElements().size(), decl.getElements().size());
		}
		for (int i = 0; i < recordInit.getElements().size(); i++) {
			Type initType = recordInit.getElements().get(i).accept(this, null);
			Type testType = decl.getElements().get(i).getType();
			checkType(recordInit, initType, testType);
		}
		return recordInit.getType();
	}
	
	@Override
	public Type visitStringValue(StringValue stringValue, Boolean __) {
		return Type.getStringType();
	}

	@Override
	public Type visitBoolValue(BoolValue boolValue, Boolean __) {
		return Type.getBoolType();
	}

	@Override
	public Type visitIntValue(IntValue intValue, Boolean __) {
		return Type.getIntType();
	}

	@Override
	public Type visitFloatValue(FloatValue floatValue, Boolean __) {
		return Type.getFloatType();
	}

	@Override
	public Type visitIdentifierReference(IdentifierReference identifierReference, Boolean __) {
		Declaration decl = table.getDeclaration(identifierReference.getIdentifierName());
		identifierReference.setDeclaration(decl);
		identifierReference.setType(decl.getType());
		return decl.getType();
	}
	
	/**
	 * Exercise 2.5
	 */
	@Override
	public Type visitSelectExpression(SelectExpression exp,Boolean __){
		Expression condition = exp.getCondition();
		Type conditionType = condition.accept(this, null);
		Type trueCaseType = exp.getTrueCase().accept(this, null);
		Type falseCaseType = exp.getFalseCase().accept(this, null);

		// Check if condition is bool type
		checkType(exp, conditionType, Type.getBoolType() );
		// check if the cases are the same type
		checkType(exp, trueCaseType, falseCaseType);

		exp.setType(trueCaseType);
		
		return falseCaseType;
	}

}
