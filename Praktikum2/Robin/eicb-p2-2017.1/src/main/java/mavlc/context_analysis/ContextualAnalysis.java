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

import java.util.HashSet;
import java.util.List;

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
	
	@Override
	public Type visitIteratorDeclaration(IteratorDeclaration iteratorDeclaration, Boolean __) {
		// TODO: implement (exercise 2.3)
		throw new UnsupportedOperationException();
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

	@Override
	public Type visitVariableAssignment(VariableAssignment variableAssignment, Boolean __) {
        // first call accept methods for left and right site.
        Type lhs = variableAssignment.getIdentifier().accept(this, null);
        Type rhs = variableAssignment.getValue().accept(this, null);
        
        // now check the types
        checkType(variableAssignment, lhs, rhs);
        return null;
	}

	@Override
	public Type visitLeftHandIdentifier(LeftHandIdentifier leftHandIdentifier, Boolean __) {
        Declaration declaration = table.getDeclaration(leftHandIdentifier.getName());

        // lets check that ident points on a variable and not a constant
        if (! declaration.isVariable())
            throw new ConstantAssignmentError(leftHandIdentifier);

        leftHandIdentifier.setDeclaration(declaration);
        return  declaration.getType();
	}

	@Override
	public Type visitMatrixLHSIdentifier(MatrixLHSIdentifier matrixLHSIdentifier, Boolean __) {
        // lets check that the types of y and x are int
        Type xType = matrixLHSIdentifier.getXIndex().accept(this, null);
        Type yType = matrixLHSIdentifier.getYIndex().accept(this, null);
        checkType(matrixLHSIdentifier, xType, Type.getIntType());
        checkType (matrixLHSIdentifier, yType, Type.getIntType());

        // lets check the declaration
        Declaration declaration = table.getDeclaration(matrixLHSIdentifier.getName());
        if (! (declaration.getType() instanceof MatrixType))
                throw new InapplicableOperationError(matrixLHSIdentifier, declaration.getType(), MatrixType.class);

        int declaredX = ((MatrixType) declaration.getType()).getxDimension();
        int declaredY = ((MatrixType) declaration.getType()).getyDimension();

        System.out.
        
        int passedX = getOffSet(matrixLHSIdentifier.getXIndex());
        int passedY = getOffSet(matrixLHSIdentifier.getYIndex());

        if (passedX < 0)
            throw new StructureDimensionError(matrixLHSIdentifier, passedX, 0);

        if (passedX >= declaredX)
            throw new StructureDimensionError(matrixLHSIdentifier, passedX, declaredX);

        if (passedY < 0)
            throw new StructureDimensionError(matrixLHSIdentifier, passedY, 0);

        if (passedY >= declaredY) 
            throw new StructureDimensionError(matrixLHSIdentifier, passedY, declaredY);

       
       if (! declaration.isVariable())
           throw new ConstantAssignmentError(matrixLHSIdentifier);

       // all okay
       matrixLHSIdentifier.setDeclaration(declaration);
       return ((MatrixType) declaration.getType()).getElementType();
	}

	@Override
	public Type visitVectorLHSIdentifier(VectorLHSIdentifier vectorLHSIdentifier, Boolean __) {
        // lets check that the index is a int
        Type index = vectorLHSIdentifier.getIndex().accept(this, null);
        checkType(vectorLHSIdentifier, index, Type.getIntType());

        // check declaration
        Declaration declaration = table.getDeclaration(vectorLHSIdentifier.getName());
        if ( ! (declaration.getType() instanceof VectorType))
            throw new InapplicableOperationError(vectorLHSIdentifier, declaration.getType(), VectorType.class);

        int declaredIndex = ((VectorType) declaration.getType()).getDimension();
        int passedIndex = getOffSet(vectorLHSIdentifier.getIndex());

        if (passedIndex < 0)
            throw new StructureDimensionError(vectorLHSIdentifier, passedIndex, 0);

        if (passedIndex >= declaredIndex)
            throw new StructureDimensionError(vectorLHSIdentifier, passedIndex, declaredIndex);

        vectorLHSIdentifier.setDeclaration(declaration);
        return ((VectorType) declaration.getType()).getElementType();
	}

	@Override
	public Type visitRecordLHSIdentifier(RecordLHSIdentifier recordLHSIdentifier, Boolean __) {
        Declaration decl = table.getDeclaration(recordLHSIdentifier.getName());

        if ( ! (decl.getType() instanceof RecordType))
            throw new InapplicableOperationError(recordLHSIdentifier, decl.getType(), RecordType.class);

        if (! decl.isVariable())
            throw new ConstantAssignmentError(recordLHSIdentifier);

        RecordElementDeclaration recordElemDecl = 
            ((RecordType) decl.getType())
            .getTypeDeclaration()
            .getElement(recordLHSIdentifier.getElementName());

        if (! recordElemDecl.isVariable()) 
            throw new ConstantAssignmentError(recordLHSIdentifier);

        checkType(
                recordLHSIdentifier,
                recordElemDecl.accept(this, null), 
                recordElemDecl.getType()
                );

        recordLHSIdentifier.setDeclaration(decl);
        return recordElemDecl.accept(this, null);

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

	@Override
	public Type visitForEachLoop(ForEachLoop forEachLoop, Boolean __) {
        Default initVarDecl = table.getDeclaration(forEachLoop.
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

	@Override
	public Type visitCompoundStatement(CompoundStatement compoundStatement, Boolean __) {
        // Here we need to open a new scope which we close before return
        table.openNewScope();
        for (Statement stm : compoundStatement.getStatements()) {
            stm.accept(this, null);
        }
        table.closeCurrentScope();
        return null;
	}
	
	@Override
	public Type visitSwitchStatement(SwitchStatement switchCaseStatement, Boolean __) {
		// TODO: implement (exercise 2.4)
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Type visitSingleCase(SingleCase sCase,Boolean __){
		// TODO: implement (exercise 2.4)
		throw new UnsupportedOperationException();
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

	@Override
	public Type visitMultiplication(Multiplication multiplication, Boolean __) {
		// TODO: implement (exercise 2.5)
		throw new UnsupportedOperationException();
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

	@Override
	public Type visitAddition(Addition addition, Boolean __) {
		// TODO: implement (exercise 2.5)
		throw new UnsupportedOperationException();
	}

	@Override
	public Type visitSubtraction(Subtraction subtraction, Boolean __) {
		// TODO: implement (exercise 2.5)
		throw new UnsupportedOperationException();
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

	@Override
	public Type visitExponentiation(Exponentiation exponentiation, Boolean __) {
		// TODO: implement (exercise 2.5)
		throw new UnsupportedOperationException();
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
	
	@Override
	public Type visitSelectExpression(SelectExpression exp,Boolean __){
		// TODO: implement (exercise 2.5)
		throw new UnsupportedOperationException();
	}

}
