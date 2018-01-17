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
package mavlc.ast.visitor;

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
import mavlc.ast.nodes.expression.UnaryExpression;
import mavlc.ast.nodes.expression.UnaryMinus;
import mavlc.ast.nodes.expression.VectorDimension;
import mavlc.ast.nodes.expression.MatrixXDimension;
import mavlc.ast.nodes.expression.MatrixYDimension;
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
import mavlc.ast.nodes.statement.VariableDeclaration;
import mavlc.ast.nodes.statement.VectorLHSIdentifier;
import mavlc.ast.type.Type;

/**
 * Basic visitor implementation. Each visit-method calls the visit method of the 
 * corresponding super-type.
 *
 * @param <RetTy> Return type used by visitor methods.
 * @param <ArgTy> Argument type used by visitor methods.
 */
public class ASTNodeBaseVisitor<RetTy, ArgTy> implements ASTNodeVisitor<RetTy, ArgTy> {
	
	protected RetTy defaultOperation(ASTNode node, ArgTy obj){
		throw new UnsupportedOperationException();
	}

	@Override
	public RetTy visitModule(Module module, ArgTy obj) {
		return defaultOperation(module, obj);
	}

	@Override
	public RetTy visitFunction(Function functionNode, ArgTy obj) {
		return defaultOperation(functionNode, obj);
	}

	@Override
	public RetTy visitFormalParameter(FormalParameter formalParameter, ArgTy obj) {
		return defaultOperation(formalParameter, obj);
	}
	
	@Override
	public RetTy visitRecordTypeDeclaration(RecordTypeDeclaration recordTypeDeclaration, ArgTy obj) {
		return defaultOperation(recordTypeDeclaration, obj);
	}
	
	@Override
	public RetTy visitRecordElementDeclaration(RecordElementDeclaration recordElementDeclaration, ArgTy obj) {
		return defaultOperation(recordElementDeclaration, obj);
	}

	public RetTy visitIteratorDeclaration(IteratorDeclaration iteratorDeclaration, ArgTy obj) {
		return defaultOperation(iteratorDeclaration, obj);
	}
	
	@Override
	public RetTy visitStatement(Statement statement, ArgTy obj) {
		return defaultOperation(statement, obj);
	}

	@Override
	public RetTy visitDeclaration(Declaration declaration, ArgTy obj) {
		return visitStatement(declaration, obj);
	}

	@Override
	public RetTy visitValueDefinition(ValueDefinition valueDefinition, ArgTy obj) {
		return visitDeclaration(valueDefinition, obj);
	}

	@Override
	public RetTy visitVariableDeclaration(VariableDeclaration variableDeclaration, ArgTy obj) {
		return visitDeclaration(variableDeclaration, obj);
	}

	@Override
	public RetTy visitVariableAssignment(VariableAssignment variableAssignment, ArgTy obj) {
		return visitStatement(variableAssignment, obj);
	}

	@Override
	public RetTy visitLeftHandIdentifier(LeftHandIdentifier leftHandIdentifier, ArgTy obj) {
		return defaultOperation(leftHandIdentifier, obj);
	}

	@Override
	public RetTy visitMatrixLHSIdentifier(MatrixLHSIdentifier matrixLHSIdentifier, ArgTy obj) {
		return visitLeftHandIdentifier(matrixLHSIdentifier, obj);
	}

	@Override
	public RetTy visitVectorLHSIdentifier(VectorLHSIdentifier vectorLHSIdentifier, ArgTy obj) {
		return visitLeftHandIdentifier(vectorLHSIdentifier, obj);
	}

	@Override
	public RetTy visitRecordLHSIdentifier(RecordLHSIdentifier recordLHSIdentifier, ArgTy obj) {
		return visitLeftHandIdentifier(recordLHSIdentifier, obj);
	}
	
	@Override
	public RetTy visitForLoop(ForLoop forLoop, ArgTy obj) {
		return visitStatement(forLoop, obj);
	}

	@Override
	public RetTy visitForEachLoop(ForEachLoop forEachLoop, ArgTy obj) {
		return visitStatement(forEachLoop, obj);
	}
	
	@Override
	public RetTy visitIfStatement(IfStatement ifStatement, ArgTy obj) {
		return visitStatement(ifStatement, obj);
	}

	@Override
	public RetTy visitCallStatement(CallStatement callStatement, ArgTy obj) {
		return visitStatement(callStatement, obj);
	}

	@Override
	public RetTy visitReturnStatement(ReturnStatement returnStatement, ArgTy obj) {
		return visitStatement(returnStatement, obj);
	}

	@Override
	public RetTy visitCompoundStatement(CompoundStatement compoundStatement, ArgTy obj) {
		return visitStatement(compoundStatement, obj);
	}
	@Override
	public RetTy visitSwitchStatement(SwitchStatement switchCaseStatement,  ArgTy obj) {
		return visitStatement(switchCaseStatement, obj);
	}
	
	@Override
	public RetTy visitExpression(Expression expression, ArgTy obj) {
		return defaultOperation(expression, obj);
	}

	@Override
	public RetTy visitBinaryExpression(BinaryExpression binaryExpression, ArgTy obj) {
		return visitExpression(binaryExpression, obj);
	}

	@Override
	public RetTy visitMatrixMultiplication(MatrixMultiplication matrixMultiplication, ArgTy obj) {
		return visitBinaryExpression(matrixMultiplication, obj);
	}

	@Override
	public RetTy visitDotProduct(DotProduct dotProduct, ArgTy obj) {
		return visitBinaryExpression(dotProduct, obj);
	}

	@Override
	public RetTy visitMultiplication(Multiplication multiplication, ArgTy obj) {
		return visitBinaryExpression(multiplication, obj);
	}

	@Override
	public RetTy visitDivision(Division division, ArgTy obj) {
		return visitBinaryExpression(division, obj);
	}

	@Override
	public RetTy visitAddition(Addition addition, ArgTy obj) {
		return visitBinaryExpression(addition, obj);
	}

	@Override
	public RetTy visitSubtraction(Subtraction subtraction, ArgTy obj) {
		return visitBinaryExpression(subtraction, obj);
	}

	@Override
	public RetTy visitCompare(Compare compare, ArgTy obj) {
		return visitBinaryExpression(compare, obj);
	}

	@Override
	public RetTy visitAnd(And and, ArgTy obj) {
		return visitBinaryExpression(and, obj);
	}

	@Override
	public RetTy visitOr(Or or, ArgTy obj) {
		return visitBinaryExpression(or, obj);
	}

	@Override
	public RetTy visitExponentiation(Exponentiation exponentiation, ArgTy obj) {
		return visitBinaryExpression(exponentiation, obj);
	}

	@Override
	public RetTy visitUnaryExpression(UnaryExpression unaryExpression, ArgTy obj) {
		return visitExpression(unaryExpression, obj);
	}

	@Override
	public RetTy visitMatrixXDimension(MatrixXDimension xDimension, ArgTy obj) {
		return visitUnaryExpression(xDimension, obj);
	}

	@Override
	public RetTy visitMatrixYDimension(MatrixYDimension yDimension, ArgTy obj) {
		return visitUnaryExpression(yDimension, obj);
	}
	
	@Override
	public RetTy visitVectorDimension(VectorDimension vectorDimension, ArgTy obj) {
		return visitUnaryExpression(vectorDimension, obj);
	}

	@Override
	public RetTy visitUnaryMinus(UnaryMinus unaryMinus, ArgTy obj) {
		return visitUnaryExpression(unaryMinus, obj);
	}

	@Override
	public RetTy visitBoolNot(BoolNot boolNot, ArgTy obj) {
		return visitUnaryExpression(boolNot, obj);
	}

	@Override
	public RetTy visitCallExpression(CallExpression callExpression, ArgTy obj) {
		return visitExpression(callExpression, obj);
	}

	@Override
	public RetTy visitElementSelect(ElementSelect elementSelect, ArgTy obj) {
		return visitExpression(elementSelect, obj);
	}
	
	@Override
	public RetTy visitRecordElementSelect(RecordElementSelect recordElementSelect, ArgTy obj) {
		return visitExpression(recordElementSelect, obj);
	}
	
	@Override
	public RetTy visitSubMatrix(SubMatrix subMatrix, ArgTy obj) {
		return visitExpression(subMatrix, obj);
	}
	
	@Override
	public RetTy visitSubVector(SubVector subVector, ArgTy obj) {
		return visitExpression(subVector, obj);
	}

	@Override
	public RetTy visitStructureInit(StructureInit structureInit, ArgTy obj) {
		return visitExpression(structureInit, obj);
	}

	@Override
	public RetTy visitRecordInit(RecordInit recordInit, ArgTy obj) {
		return visitStructureInit(recordInit, obj);
	}
	
	@Override
	public RetTy visitStringValue(StringValue stringValue, ArgTy obj) {
		return visitExpression(stringValue, obj);
	}

	@Override
	public RetTy visitBoolValue(BoolValue boolValue, ArgTy obj) {
		return visitExpression(boolValue, obj);
	}

	@Override
	public RetTy visitIntValue(IntValue intValue, ArgTy obj) {
		return visitExpression(intValue, obj);
	}

	@Override
	public RetTy visitFloatValue(FloatValue floatValue, ArgTy obj) {
		return visitExpression(floatValue, obj);
	}

	@Override
	public RetTy visitIdentifierReference(IdentifierReference identifierReference, ArgTy obj) {
		return visitExpression(identifierReference, obj);
	}

	@Override
	public RetTy visitSingleCase(SingleCase singleCase, ArgTy obj) {
		return defaultOperation(singleCase, obj);
	}

	@Override
	public RetTy visitCase(Case aCase, ArgTy obj) {
		return visitSingleCase(aCase,obj);
	}

	@Override
	public RetTy visitDefault(Default defCase, ArgTy obj) {
		return visitSingleCase(defCase,obj);
	}

	public RetTy visitSelectExpression(SelectExpression expr, ArgTy obj) {
		return visitExpression(expr, obj);
	}

}
