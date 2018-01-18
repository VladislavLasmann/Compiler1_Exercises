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

package mavlc.codegen;

import java.util.List;
import java.util.ArrayList;

import MTAM.Instruction;
import mavlc.ast.nodes.ASTNode;
import mavlc.ast.nodes.expression.Addition;
import mavlc.ast.nodes.expression.And;
import mavlc.ast.nodes.expression.BoolNot;
import mavlc.ast.nodes.expression.BoolValue;
import mavlc.ast.nodes.expression.CallExpression;
import mavlc.ast.nodes.expression.Compare;
import mavlc.ast.nodes.expression.Compare.Comparison;
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
import mavlc.ast.type.FloatType;
import mavlc.ast.type.IntType;
import mavlc.ast.type.MatrixType;
import mavlc.ast.type.RecordType;
import mavlc.ast.type.StructType;
import mavlc.ast.type.Type;
import mavlc.ast.type.VectorType;
import mavlc.ast.visitor.ASTNodeBaseVisitor;
import mavlc.codegen.TAMAssembler.Register;
import mavlc.context_analysis.ModuleEnvironment;

public class CodeGeneration extends ASTNodeBaseVisitor<Instruction, Integer> {

	protected TAMAssembler assembler;

	protected final ModuleEnvironment env;

	public CodeGeneration(ModuleEnvironment environment, TAMAssembler ass) {
		env = environment;
		assembler = ass;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#defaultOperation(mavlc.ast.nodes.ASTNode, java.lang.Function)
	 */
	@Override
	protected Instruction defaultOperation(ASTNode node, Integer arg1) {
		throw new UnsupportedOperationException("Code generation for this element is not implemented!");
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitModule(mavlc.ast.nodes.module.Module, java.lang.Function)
	 */
	@Override
	public Instruction visitModule(Module module, Integer arg1) {
		for(Function func : module.getFunctions()){
			func.accept(this, null);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitFunction(mavlc.ast.nodes.function.Function, java.lang.Function)
	 */
	@Override
	public Instruction visitFunction(Function functionNode, Integer arg1) {
		assembler.addNewFunction(functionNode);
		/*
		 * Visit all parameters.
		 */
		int parameterSize = 0;
		for(FormalParameter param : functionNode.getParameters()){
			parameterSize += param.getType().wordSize();
		}
		int argSize = parameterSize;
		parameterSize *= -1;
		for(FormalParameter param : functionNode.getParameters()){
			/*
			 * Calculate the (negative) offset relative to this functions LB.
			 */
			param.accept(this, parameterSize);
			parameterSize += param.getType().wordSize();
		}
		/*
		 * Visit all statements in the loop.
		 */
		for(Statement stmt : functionNode.getFunctionBody()){
			stmt.accept(this, argSize);
		}
		/*
		 * Emit halt instruction for the main method
		 */
		if(functionNode.getName().equals("main")){
			assembler.emitHaltInstruction();
		}
		/*
		 * Explicitly emit return for void functions.
		 */
		else if(functionNode.getReturnType().equals(Type.getVoidType())){
			assembler.emitReturn(0, argSize);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitFormalParameter(mavlc.ast.nodes.function.FormalParameter, java.lang.Function)
	 */
	@Override
	public Instruction visitFormalParameter(FormalParameter formalParameter, Integer arg1) {
		int startOffset = arg1;
		formalParameter.setLocalBaseOffset(startOffset);
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitIteratorDeclaration(mavlc.ast.nodes.function.IteratorDeclaration, java.lang.Function)
	 */
	@Override
	public Instruction visitIteratorDeclaration(IteratorDeclaration iteratorDeclaration, Integer arg1) {
		int startOffset = arg1;
		iteratorDeclaration.setLocalBaseOffset(startOffset);
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitValueDefinition(mavlc.ast.nodes.statement.ValueDefinition, java.lang.Function)
	 */
	@Override
	public Instruction visitValueDefinition(ValueDefinition valueDefinition, Integer arg1) {
		valueDefinition.getValue().accept(this, null);
		assembler.addDeclaredEntity(valueDefinition);
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitVariableDeclaration(mavlc.ast.nodes.statement.VariableDeclaration, java.lang.Function)
	 */
	@Override
	public Instruction visitVariableDeclaration(VariableDeclaration variableDeclaration, Integer arg1) {
		assembler.emitPush(variableDeclaration.getType().wordSize());
		assembler.addDeclaredEntity(variableDeclaration);
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitVariableAssignment(mavlc.ast.nodes.statement.VariableAssignment, java.lang.Function)
     * Task 3.2 
	 */
	@Override
	public Instruction visitVariableAssignment(VariableAssignment variableAssignment, Integer arg1) {
        // first get the type of the assignment
        Type type = variableAssignment.getValue().getType();

        // put the value on the stack
        variableAssignment.getValue().accept(this, null);

        if (type.equals(IntType.getIntType())) {
            // now we need the address of the declaration where to store the int
            variableAssignment.getIdentifier().accept(this, null); //is on stack now

            // now we tell the assembler to store the value at the address
            assembler.storeToStackAddress(1);

        } else if (type instanceof VectorType) {
            System.out.println("Hallo Robin");
        } else {
            throw new UnsupportedOperationException();
        }

        return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitLeftHandIdentifier(mavlc.ast.nodes.statement.LeftHandIdentifier, java.lang.Function)
	 * Task 3.2
     */
	@Override
	public Instruction visitLeftHandIdentifier(LeftHandIdentifier leftHandIdentifier, Integer arg1) {
        // get the address  of the declared variable
        int offset = leftHandIdentifier.getDeclaration().getLocalBaseOffset();
        // now we push the address on the stack
        assembler.loadAddress(Register.LB, offset);
        return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitMatrixLHSIdentifier(mavlc.ast.nodes.statement.MatrixLHSIdentifier, java.lang.Function)
     *
     * Task 3.2
	 */
	@Override
	public Instruction visitMatrixLHSIdentifier(MatrixLHSIdentifier matrixLHSIdentifier, Integer arg1) {
        // get the address of the declared Matrix
        int offset = matrixLHSIdentifier.getDeclaration().getLocalBaseOffset();

        // load the address on the stack
        assembler.loadAddress(Register.LB, offset);
        return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitVectorLHSIdentifier(mavlc.ast.nodes.statement.VectorLHSIdentifier, java.lang.Function)
     *
     * Task 3.2
	 */
	@Override
	public Instruction visitVectorLHSIdentifier(VectorLHSIdentifier vectorLHSIdentifier, Integer arg1) {
        // get the address of the declared Vector
        int offset = vectorLHSIdentifier.getDeclaration().getLocalBaseOffset();

        // load the address on the stack
        assembler.loadAddress(Register.LB, offset);

        return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitRecordLHSIdentifier(mavlc.ast.nodes.statement.RecordLHSIdentifier, java.lang.Function)
	 */
	@Override
	public Instruction visitRecordLHSIdentifier(RecordLHSIdentifier recordLHSIdentifier, Integer arg1) {
		//TODO Task 3.2
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitForLoop(mavlc.ast.nodes.statement.ForLoop, java.lang.Function)
	 */
	@Override
	public Instruction visitForLoop(ForLoop forLoop, Integer arg1) {
		/*
		 * Evaluate init expression and store to the variable
		 */
		forLoop.getInitValue().accept(this, null);
		int offset  = forLoop.getInitVarDeclaration().getLocalBaseOffset();
		assembler.storeLocalValue(forLoop.getInitValue().getType().wordSize(), offset);
		/*
		 * Jump to the test.
		 * The jump needs to backpatched later on.
		 */
		Instruction jump2test = assembler.emitJump(-1);
		int loopBodyBegin = assembler.getNextInstructionAddress();
		/*
		 * Emit loop body.
		 */
		forLoop.getLoopBody().accept(this, null);
		/*
		 * Execute increment and store to the variable.
		 */
		forLoop.getIncrementExpr().accept(this, null);
		int incrOffset = forLoop.getIncrVarDeclaration().getLocalBaseOffset();
		assembler.storeLocalValue(forLoop.getIncrementExpr().getType().wordSize(), incrOffset);
		/*
		 * Backpatch the jump to the test.
		 */
		assembler.backPatchJump(jump2test, assembler.getNextInstructionAddress());
		/*
		 * Evaluate the test expression.
		 */
		forLoop.getCheck().accept(this, null);
		/*
		 * Jump to loop body if test yielded true.
		 */
		assembler.emitConditionalJump(true, loopBodyBegin);
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitForEachLoop(mavlc.ast.nodes.statement.ForEachLoop, java.lang.Function)
	 */
	@Override
	public Instruction visitForEachLoop(ForEachLoop forEachLoop, Integer arg1) {
		//TODO Task 3.4.2
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitIfStatement(mavlc.ast.nodes.statement.IfStatement, java.lang.Function)
	 */
	@Override
	public Instruction visitIfStatement(IfStatement ifStatement, Integer arg1) {
		// Evaluate conditional
		ifStatement.getTestExpression().accept(this, null);
		/*
		 *  Jump to else part if conditional is false.
		 *  The jump must be backpatched later on.
		 */
		Instruction jump2else = assembler.emitConditionalJump(false, -1);
		/*
		 * Emit code for then-part
		 */
		ifStatement.getThenStatement().accept(this, null);
		if(ifStatement.hasElseStatement()){
			/*
			 * Emit jump over the else-part
			 * The jump must be backpatched later on.
			 */
			Instruction jumpOverElse = assembler.emitJump(-1);
			/*
			 * Backpatch jump to the else part.
			 */
			int startElse = assembler.getNextInstructionAddress();
			assembler.backPatchJump(jump2else, startElse);
			ifStatement.getElseStatement().accept(this, null);
			/*
			 * Backpatch jump over else.
			 */
			int endElse = assembler.getNextInstructionAddress();
			assembler.backPatchJump(jumpOverElse, endElse);
		}
		else{
			/*
			 * Backpatch jump to else part.
			 */
			int endIf = assembler.getNextInstructionAddress();
			assembler.backPatchJump(jump2else, endIf);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitCallStatement(mavlc.ast.nodes.statement.CallStatement, java.lang.Function)
	 */
	@Override
	public Instruction visitCallStatement(CallStatement callStatement, Integer arg1) {
		callStatement.getCall().accept(this, null);
		/*
		 * We need to clean the stack, i.e. pop the unused result if a non-void
		 * function was called in this call statement.
		 */
		Function callee = env.getFunctionDeclaration(callStatement.getCall().getCalleeName());
		if(!callee.getReturnType().equals(Type.getVoidType())){
			assembler.emitPop(0, callee.getReturnType().wordSize());
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitReturnStatement(mavlc.ast.nodes.statement.ReturnStatement, java.lang.Function)
	 */
	@Override
	public Instruction visitReturnStatement(ReturnStatement returnStatement, Integer arg1) {
		returnStatement.getReturnValue().accept(this, null);
		int resultSize = returnStatement.getReturnValue().getType().wordSize();
		int argSize = arg1;
//		Function func = (Function) obj;
//		for(FormalParameter param : func.getParameters()){
//			argSize += param.getType().wordSize();
//		}
		assembler.emitReturn(resultSize, argSize);
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitCompoundStatement(mavlc.ast.nodes.statement.CompoundStatement, java.lang.Function)
	 */
	@Override
	public Instruction visitCompoundStatement(CompoundStatement compoundStatement, Integer arg1) {
		int nextOffset = assembler.getNextOffset();
		for(Statement stmt : compoundStatement.getStatements()){
			stmt.accept(this, null);
		}
		int size = assembler.getNextOffset() - nextOffset;
		assembler.emitPop(0, size);
		assembler.setNextOffset(nextOffset);
		return null;
	}

	@Override
	public Instruction visitSwitchStatement(SwitchStatement switchCaseStatement, Integer arg1) {
		//TODO Task 3.4.1
		throw new UnsupportedOperationException();
	}

	@Override
	public Instruction visitCase(Case aCase,Integer arg1){
		//TODO Task 3.4.1
		throw new UnsupportedOperationException();
	}

	@Override
	public Instruction visitDefault(Default defCase,Integer arg1){
		//TODO Task 3.4.1
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitMatrixMultiplication(mavlc.ast.nodes.expression.MatrixMultiplication, java.lang.Function)
	 */
	@Override
	public Instruction visitMatrixMultiplication(MatrixMultiplication matrixMultiplication, Integer arg1) {
		matrixMultiplication.getLeftOp().accept(this, null);
		matrixMultiplication.getRightOp().accept(this, null);
		StructType structAType = (StructType)matrixMultiplication.getLeftOp().getType();
		StructType structBType = (StructType)matrixMultiplication.getRightOp().getType();
		if(structAType instanceof MatrixType && structBType instanceof MatrixType){
			MatrixType matAType = (MatrixType) structAType;
			MatrixType matBType = (MatrixType) structBType;
			assembler.loadIntegerValue(matAType.getxDimension());
			assembler.loadIntegerValue(matAType.getyDimension());
			assembler.loadIntegerValue(matBType.getyDimension());
			if(structAType.getElementType().equals(Type.getIntType())){
				assembler.emitIntegerMatrixMultiplication();
			}else{
				assembler.emitFloatMatrixMultiplication();
			}
			return null;
		}else if(structAType instanceof VectorType && structBType instanceof MatrixType){
			VectorType vecAType = (VectorType) structAType;
			MatrixType matBType = (MatrixType) structBType;
			assembler.loadIntegerValue(1);
			assembler.loadIntegerValue(vecAType.getDimension());
			assembler.loadIntegerValue(matBType.getyDimension());
			if(structAType.getElementType().equals(Type.getIntType())){
				assembler.emitIntegerMatrixMultiplication();
			}else{
				assembler.emitFloatMatrixMultiplication();
			}
			return null;
		}else if(structBType instanceof VectorType && structAType instanceof MatrixType){
			MatrixType matAType = (MatrixType) structAType;
			assembler.loadIntegerValue(matAType.getxDimension());
			assembler.loadIntegerValue(matAType.getyDimension());
			assembler.loadIntegerValue(1);
			if(structAType.getElementType().equals(Type.getIntType())){
				assembler.emitIntegerMatrixMultiplication();
			}else{
				assembler.emitFloatMatrixMultiplication();
			}
			return null;
		}else{
			VectorType vecAType = (VectorType) structAType;
			assembler.loadIntegerValue(1);
			assembler.loadIntegerValue(vecAType.getDimension());
			assembler.loadIntegerValue(1);
			if(structAType.getElementType().equals(Type.getIntType())){
				assembler.emitIntegerMatrixMultiplication();
			}else{
				assembler.emitFloatMatrixMultiplication();
			}
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitDotProduct(mavlc.ast.nodes.expression.DotProduct, java.lang.Function)
	 */
	@Override
	public Instruction visitDotProduct(DotProduct dotProduct, Integer arg1) {
		VectorType vectorType = (VectorType)dotProduct.getLeftOp().getType();
		dotProduct.getLeftOp().accept(this, null);
		dotProduct.getRightOp().accept(this, null);
		assembler.loadIntegerValue(0);
		if(vectorType.getElementType().equals(Type.getIntType())){
			assembler.loadIntegerValue(0);
		}else{
			assembler.loadFloatValue(0);
		}

		int loopBegin = assembler.getNextInstructionAddress();
		assembler.loadValue(Register.ST, 1, -2);
		assembler.loadAddress(Register.ST, -3
				- dotProduct.getLeftOp().getType().wordSize()
				- dotProduct.getRightOp().getType().wordSize());
		assembler.emitIntegerAddition();
		assembler.loadFromStackAddress(1);
		assembler.loadValue(Register.ST, 1, -3);
		assembler.loadAddress(Register.ST, -4
				- dotProduct.getRightOp().getType().wordSize());
		assembler.emitIntegerAddition();
		assembler.loadFromStackAddress(1);
		if(vectorType.getElementType().equals(Type.getIntType())){
			assembler.emitIntegerMultiplication();
		} else {
			assembler.emitFloatMultiplication();
		}
		assembler.loadValue(Register.ST, 1, -2);
		if(vectorType.getElementType().equals(Type.getIntType())){
			assembler.emitIntegerAddition();
		} else {
			assembler.emitFloatAddition();
		}
		assembler.storeValue(Register.ST, 1, -2);
		assembler.loadValue(Register.ST, 1, -2);
		assembler.loadIntegerValue(1);
		assembler.emitIntegerAddition();
		assembler.storeValue(Register.ST, 1, -3);
		assembler.loadValue(Register.ST, 1, -2);
		assembler.loadIntegerValue(dotProduct.getLeftOp().getType().wordSize());
		assembler.emitIntegerComparison(Comparison.LESS);
		assembler.emitConditionalJump(true, loopBegin);
		assembler.emitPop(1, 1
				+ dotProduct.getLeftOp().getType().wordSize()
				+ dotProduct.getRightOp().getType().wordSize());
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitMultiplication(mavlc.ast.nodes.expression.Multiplication, java.lang.Function)
	 */
	@Override
	public Instruction visitMultiplication(Multiplication multiplication, Integer arg1) {
		Type resultType = multiplication.getType();
		Type leftType = multiplication.getLeftOp().getType();
		Type rightType = multiplication.getRightOp().getType();
		if(resultType.equals(IntType.getIntType())){
			multiplication.getLeftOp().accept(this, null);
			multiplication.getRightOp().accept(this, null);
			assembler.emitIntegerMultiplication();
		} else if(resultType.equals(FloatType.getFloatType())){
			multiplication.getLeftOp().accept(this, null);
			multiplication.getRightOp().accept(this, null);
			assembler.emitFloatMultiplication();
		} else if (leftType instanceof StructType && rightType instanceof StructType) {
			multiplication.getLeftOp().accept(this, null);
			multiplication.getRightOp().accept(this, null);
			StructType structType =(StructType)multiplication.getLeftOp().getType();
			assembler.loadIntegerValue(0);
			int loopBegin = assembler.getNextInstructionAddress();
			assembler.loadValue(Register.ST, 1, -1);
			assembler.loadAddress(Register.ST, -2
					- multiplication.getLeftOp().getType().wordSize()
					- multiplication.getRightOp().getType().wordSize());
			assembler.emitIntegerAddition();
			assembler.loadFromStackAddress(1);
			assembler.loadValue(Register.ST, 1, -2);
			assembler.loadAddress(Register.ST, -3
					- multiplication.getRightOp().getType().wordSize());
			assembler.emitIntegerAddition();
			assembler.loadFromStackAddress(1);
			if(structType.getElementType().equals(Type.getIntType())){
				assembler.emitIntegerMultiplication();
			} else {
				assembler.emitFloatMultiplication();
			}
			assembler.loadValue(Register.ST, 1, -2);
			assembler.loadAddress(Register.ST, -3
					- multiplication.getLeftOp().getType().wordSize()
					- multiplication.getRightOp().getType().wordSize());
			assembler.emitIntegerAddition();
			assembler.storeToStackAddress(1);
			assembler.loadValue(Register.ST, 1, -1);
			assembler.loadIntegerValue(1);
			assembler.emitIntegerAddition();
			assembler.loadAddress(Register.ST, -2);
			assembler.storeToStackAddress(1);
			assembler.loadValue(Register.ST, 1, -1);
			assembler.loadIntegerValue(multiplication.getLeftOp().getType().wordSize());
			assembler.emitIntegerComparison(Comparison.LESS);
			assembler.emitConditionalJump(true, loopBegin);
			assembler.emitPop(0, 1 + multiplication.getRightOp().getType().wordSize());
		} else {
			/*
			 * One Operand is Skalar and one is matrix or vector. Since the
			 * Mat/Vec scalar multiplication is commutative, we may switch them
			 * to always have the same Stack Layout. Additionally we load a counter.
			 *
			 */
			int matSize;
			Type scalarType;
			if(leftType instanceof StructType){
				multiplication.getLeftOp().accept(this, null);
				multiplication.getRightOp().accept(this, null);
				matSize = multiplication.getLeftOp().getType().wordSize();
				scalarType = multiplication.getRightOp().getType();
			} else {
				multiplication.getRightOp().accept(this, null);
				multiplication.getLeftOp().accept(this, null);
				matSize = multiplication.getRightOp().getType().wordSize();
				scalarType = multiplication.getLeftOp().getType();
			}
			assembler.loadIntegerValue(0);
			/*
			 * We now get started with the loop. Since every value is multiplied
			 * with the same scalar, we can use the same loop with matrizes and
			 * vectors.
			 * Each Iteration loads a Vec/Mat-element, as well as the scalar and
			 * then stores the scalar back to the mat/vec. Afterwards we increment
			 * the counter and check if it is LESSEQUAL to the wordSize of Mat/vec.
			 * If so we continue, else we clean up the stack and are done.
			 */
			int loopStart = assembler.getNextInstructionAddress();
			assembler.loadValue(Register.ST, 1, -1);
			assembler.loadAddress(Register.ST, -3 -matSize);
			assembler.emitIntegerAddition();
			assembler.loadFromStackAddress(1);
			assembler.loadValue(Register.ST, 1, -3);
			if(scalarType.equals(Type.getIntType())){
				assembler.emitIntegerMultiplication();
			}else{
				assembler.emitFloatMultiplication();
			}
			assembler.loadValue(Register.ST, 1, -2);
			assembler.loadAddress(Register.ST, -4 -matSize);
			assembler.emitIntegerAddition();
			assembler.storeToStackAddress(1);
			/*
			 * Loop Body ends here... Now we increment and check.
			 */
			assembler.loadValue(Register.ST, 1, -1);
			assembler.loadIntegerValue(1);
			assembler.emitIntegerAddition();
			assembler.storeValue(Register.ST, 1, -2);
			assembler.loadValue(Register.ST, 1, -1);
			assembler.loadIntegerValue(matSize);
			assembler.emitIntegerComparison(Comparison.LESS_EQUAL);
			assembler.emitConditionalJump(true, loopStart);
			assembler.emitPop(0, 2);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitDivision(mavlc.ast.nodes.expression.Division, java.lang.Function)
     *
     * Task 3.1
	 */
	@Override
	public Instruction visitDivision(Division division, Integer arg1) {
        // First get the types of result that the operands are the same type is covered in the
        // contex analises
        Type resultType = division.getType();
        
        // accept the Operands which will put them on the stack
        division.getLeftOp().accept(this, null);
        division.getRightOp().accept(this, null);

        // Result must be int or float
        if (resultType.equals(IntType.getIntType())) {
            // gets the 2 most up elements from the stack and makes a division and pushes the result 
            // back to the stack
            assembler.emitIntegerDivision();
        } else {
            assembler.emitFloatDivision();
        }
        return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitAddition(mavlc.ast.nodes.expression.Addition, java.lang.Function)
	 */
	@Override
	public Instruction visitAddition(Addition addition, Integer arg1) {
		Type resultType = addition.getType();
		Type leftType = addition.getLeftOp().getType();
		Type rightType = addition.getRightOp().getType();
		if(resultType.equals(IntType.getIntType())){
			addition.getLeftOp().accept(this, null);
			addition.getRightOp().accept(this, null);
			assembler.emitIntegerAddition();
		}
		else if(resultType.equals(FloatType.getFloatType())){
			addition.getLeftOp().accept(this, null);
			addition.getRightOp().accept(this, null);
			assembler.emitFloatAddition();
		} else if(leftType instanceof StructType && rightType instanceof StructType) {
			addition.getLeftOp().accept(this, null);
			addition.getRightOp().accept(this, null);
			StructType structType =(StructType)addition.getLeftOp().getType();
			assembler.loadIntegerValue(0);
			int loopBegin = assembler.getNextInstructionAddress();
			assembler.loadValue(Register.ST, 1, -1);
			assembler.loadAddress(Register.ST, -2
					- addition.getLeftOp().getType().wordSize()
					- addition.getRightOp().getType().wordSize());
			assembler.emitIntegerAddition();
			assembler.loadFromStackAddress(1);
			assembler.loadValue(Register.ST, 1, -2);
			assembler.loadAddress(Register.ST, -3
					- addition.getRightOp().getType().wordSize());
			assembler.emitIntegerAddition();
			assembler.loadFromStackAddress(1);
			if(structType.getElementType().equals(Type.getIntType())){
				assembler.emitIntegerAddition();
			} else {
				assembler.emitFloatAddition();
			}
			assembler.loadValue(Register.ST, 1, -2);
			assembler.loadAddress(Register.ST, -3
					- addition.getLeftOp().getType().wordSize()
					- addition.getRightOp().getType().wordSize());
			assembler.emitIntegerAddition();
			assembler.storeToStackAddress(1);
			assembler.loadValue(Register.ST, 1, -1);
			assembler.loadIntegerValue(1);
			assembler.emitIntegerAddition();
			assembler.loadAddress(Register.ST, -2);
			assembler.storeToStackAddress(1);
			assembler.loadValue(Register.ST, 1, -1);
			assembler.loadIntegerValue(addition.getLeftOp().getType().wordSize());
			assembler.emitIntegerComparison(Comparison.LESS);
			assembler.emitConditionalJump(true, loopBegin);
			assembler.emitPop(0, 1 + addition.getRightOp().getType().wordSize());
		} else {
			/*
			 * One Operand is Skalar and one is matrix or vector. Since the
			 * Mat/Vec scalar multiplication is commutative, we may switch them
			 * to always have the same Stack Layout. Additionally we load a counter.
			 *
			 */
			int matSize;
			Type scalarType;
			if(leftType instanceof StructType){
				addition.getLeftOp().accept(this, null);
				addition.getRightOp().accept(this, null);
				matSize = addition.getLeftOp().getType().wordSize();
				scalarType = addition.getRightOp().getType();
			} else {
				addition.getRightOp().accept(this, null);
				addition.getLeftOp().accept(this, null);
				matSize = addition.getRightOp().getType().wordSize();
				scalarType = addition.getLeftOp().getType();
			}
			assembler.loadIntegerValue(0);
			/*
			 * We now get started with the loop. Since every value is multiplied
			 * with the same scalar, we can use the same loop with matrizes and
			 * vectors.
			 * Each Iteration loads a Vec/Mat-element, as well as the scalar and
			 * then stores the scalar back to the mat/vec. Afterwards we increment
			 * the counter and check if it is LESSEQUAL to the wordSize of Mat/vec.
			 * If so we continue, else we clean up the stack and are done.
			 */
			int loopStart = assembler.getNextInstructionAddress();
			assembler.loadValue(Register.ST, 1, -1);
			assembler.loadAddress(Register.ST, -3 -matSize);
			assembler.emitIntegerAddition();
			assembler.loadFromStackAddress(1);
			assembler.loadValue(Register.ST, 1, -3);
			if(scalarType.equals(Type.getIntType())){
				assembler.emitIntegerAddition();
			}else{
				assembler.emitFloatAddition();
			}
			assembler.loadValue(Register.ST, 1, -2);
			assembler.loadAddress(Register.ST, -4 -matSize);
			assembler.emitIntegerAddition();
			assembler.storeToStackAddress(1);
			/*
			 * Loop Body ends here... Now we increment and check.
			 */
			assembler.loadValue(Register.ST, 1, -1);
			assembler.loadIntegerValue(1);
			assembler.emitIntegerAddition();
			assembler.storeValue(Register.ST, 1, -2);
			assembler.loadValue(Register.ST, 1, -1);
			assembler.loadIntegerValue(matSize);
			assembler.emitIntegerComparison(Comparison.LESS_EQUAL);
			assembler.emitConditionalJump(true, loopStart);
			assembler.emitPop(0, 2);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitSubtraction(mavlc.ast.nodes.expression.Subtraction, java.lang.Function)
	 */
	@Override
	public Instruction visitSubtraction(Subtraction subtraction, Integer arg1) {
		subtraction.getLeftOp().accept(this, null);
		subtraction.getRightOp().accept(this, null);
		if(subtraction.getType().equals(IntType.getIntType())){
			assembler.emitIntegerSubtraction();
		}
		else if(subtraction.getType().equals(FloatType.getFloatType())){
			assembler.emitFloatSubtraction();
		} else {
			StructType structType =(StructType)subtraction.getLeftOp().getType();
			assembler.loadIntegerValue(0);
			int loopBegin = assembler.getNextInstructionAddress();
			assembler.loadValue(Register.ST, 1, -1);
			assembler.loadAddress(Register.ST, -2
					- subtraction.getLeftOp().getType().wordSize()
					- subtraction.getRightOp().getType().wordSize());
			assembler.emitIntegerAddition();
			assembler.loadFromStackAddress(1);
			assembler.loadValue(Register.ST, 1, -2);
			assembler.loadAddress(Register.ST, -3
					- subtraction.getRightOp().getType().wordSize());
			assembler.emitIntegerAddition();
			assembler.loadFromStackAddress(1);
			if(structType.getElementType().equals(Type.getIntType())){
				assembler.emitIntegerSubtraction();
			} else {
				assembler.emitFloatSubtraction();
			}
			assembler.loadValue(Register.ST, 1, -2);
			assembler.loadAddress(Register.ST, -3
					- subtraction.getLeftOp().getType().wordSize()
					- subtraction.getRightOp().getType().wordSize());
			assembler.emitIntegerAddition();
			assembler.storeToStackAddress(1);
			assembler.loadValue(Register.ST, 1, -1);
			assembler.loadIntegerValue(1);
			assembler.emitIntegerAddition();
			assembler.loadAddress(Register.ST, -2);
			assembler.storeToStackAddress(1);
			assembler.loadValue(Register.ST, 1, -1);
			assembler.loadIntegerValue(subtraction.getLeftOp().getType().wordSize());
			assembler.emitIntegerComparison(Comparison.LESS);
			assembler.emitConditionalJump(true, loopBegin);
			assembler.emitPop(0, 1 + subtraction.getRightOp().getType().wordSize());

		}
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitCompare(mavlc.ast.nodes.expression.Compare, java.lang.Function)
	 */
	@Override
	public Instruction visitCompare(Compare compare, Integer arg1) {
		compare.getLeftOp().accept(this, null);
		compare.getRightOp().accept(this, null);
		if(compare.getLeftOp().getType().equals(IntType.getIntType())){
			assembler.emitIntegerComparison(compare.getComparator());
		}
		else if(compare.getLeftOp().getType().equals(FloatType.getFloatType())){
			assembler.emitFloatComparison(compare.getComparator());
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitAnd(mavlc.ast.nodes.expression.And, java.lang.Function)
	 */
	@Override
	public Instruction visitAnd(And and, Integer arg1) {
		and.getLeftOp().accept(this, null);
		and.getRightOp().accept(this, null);
		assembler.emitLogicalAnd();
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitOr(mavlc.ast.nodes.expression.Or, java.lang.Function)
	 */
	@Override
	public Instruction visitOr(Or or, Integer arg1) {
		or.getLeftOp().accept(this, null);
		or.getRightOp().accept(this, null);
		assembler.emitLogicalOr();
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitExponentiation(mavlc.ast.nodes.expression.Exponentiation, java.lang.Function)
	 */
	@Override
	public Instruction visitExponentiation(Exponentiation exponentiation, Integer arg1) {
		Type leftType = exponentiation.getLeftOp().getType();
		if(leftType.equals(IntType.getIntType())){
			exponentiation.getLeftOp().accept(this, null);
			exponentiation.getRightOp().accept(this, null);
			assembler.emitIntegerExponentiation();
			return null;
		} else if(leftType.equals(FloatType.getFloatType())){
			exponentiation.getLeftOp().accept(this, null);
			exponentiation.getRightOp().accept(this, null);
			assembler.emitFloatExponentiation();
			return null;
		}
		if (!(leftType instanceof StructType)) {
			return null;
		}
		Type rightType = exponentiation.getRightOp().getType();
		if (rightType.isScalarType()) {
			exponentiation.getLeftOp().accept(this, null);
			exponentiation.getRightOp().accept(this, null);
			int matSize     = exponentiation.getLeftOp().getType().wordSize();
			Type scalarType = exponentiation.getRightOp().getType();
			assembler.loadIntegerValue(0);
			/*
			 * We now get started with the loop. Since every value is exponentiated
			 * by the same scalar, we can use the same loop with matrizes and
			 * vectors.
			 * Each Iteration loads a Vec/Mat-element, as well as the scalar and
			 * then stores the scalar back to the mat/vec. Afterwards we increment
			 * the counter and check if it is LESSEQUAL to the wordSize of Mat/vec.
			 * If so we continue, else we clean up the stack and are done.
			 */
			int loopStart = assembler.getNextInstructionAddress();
			assembler.loadValue(Register.ST, 1, -1);
			assembler.loadAddress(Register.ST, -3 -matSize);
			assembler.emitIntegerAddition();
			assembler.loadFromStackAddress(1);
			assembler.loadValue(Register.ST, 1, -3);
			if(scalarType.equals(Type.getIntType())){
				assembler.emitIntegerExponentiation();
			}else{
				assembler.emitFloatExponentiation();
			}
			assembler.loadValue(Register.ST, 1, -2);
			assembler.loadAddress(Register.ST, -4 -matSize);
			assembler.emitIntegerAddition();
			assembler.storeToStackAddress(1);
			/*
			 * Loop Body ends here... Now we increment and check.
			 */
			assembler.loadValue(Register.ST, 1, -1);
			assembler.loadIntegerValue(1);
			assembler.emitIntegerAddition();
			assembler.storeValue(Register.ST, 1, -2);
			assembler.loadValue(Register.ST, 1, -1);
			assembler.loadIntegerValue(matSize);
			assembler.emitIntegerComparison(Comparison.LESS_EQUAL);
			assembler.emitConditionalJump(true, loopStart);
			assembler.emitPop(0, 2);
		}
		if (rightType instanceof StructType) {
			exponentiation.getLeftOp().accept(this, null);
			exponentiation.getRightOp().accept(this, null);
			StructType structType =(StructType)exponentiation.getLeftOp().getType();
			assembler.loadIntegerValue(0);
			int loopBegin = assembler.getNextInstructionAddress();
			assembler.loadValue(Register.ST, 1, -1);
			assembler.loadAddress(Register.ST, -2
					- exponentiation.getLeftOp().getType().wordSize()
					- exponentiation.getRightOp().getType().wordSize());
			assembler.emitIntegerAddition();
			assembler.loadFromStackAddress(1);
			assembler.loadValue(Register.ST, 1, -2);
			assembler.loadAddress(Register.ST, -3
					- exponentiation.getRightOp().getType().wordSize());
			assembler.emitIntegerAddition();
			assembler.loadFromStackAddress(1);
			if(structType.getElementType().equals(Type.getIntType())){
				assembler.emitIntegerExponentiation();
			} else {
				assembler.emitFloatExponentiation();
			}
			assembler.loadValue(Register.ST, 1, -2);
			assembler.loadAddress(Register.ST, -3
					- exponentiation.getLeftOp().getType().wordSize()
					- exponentiation.getRightOp().getType().wordSize());
			assembler.emitIntegerAddition();
			assembler.storeToStackAddress(1);
			assembler.loadValue(Register.ST, 1, -1);
			assembler.loadIntegerValue(1);
			assembler.emitIntegerAddition();
			assembler.loadAddress(Register.ST, -2);
			assembler.storeToStackAddress(1);
			assembler.loadValue(Register.ST, 1, -1);
			assembler.loadIntegerValue(exponentiation.getLeftOp().getType().wordSize());
			assembler.emitIntegerComparison(Comparison.LESS);
			assembler.emitConditionalJump(true, loopBegin);
			assembler.emitPop(0, 1 + exponentiation.getRightOp().getType().wordSize());
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitMatrixXDimension(mavlc.ast.nodes.expression.MatrixXDimension, java.lang.Function)
	 */
	@Override
	public Instruction visitMatrixXDimension(MatrixXDimension xDimension, Integer arg1) {
		MatrixType type = (MatrixType) xDimension.getOperand().getType();
		assembler.loadIntegerValue(type.getxDimension());
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitMatrixYDimension(mavlc.ast.nodes.expression.MatrixYDimension, java.lang.Function)
	 */
	@Override
	public Instruction visitMatrixYDimension(MatrixYDimension yDimension, Integer arg1) {
		MatrixType type = (MatrixType) yDimension.getOperand().getType();
		assembler.loadIntegerValue(type.getyDimension());
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitVectorDimension(mavlc.ast.nodes.expression.VectorDimension, java.lang.Function)
	 */
	@Override
	public Instruction visitVectorDimension(VectorDimension vectorDimension, Integer arg1) {
		VectorType type = (VectorType) vectorDimension.getOperand().getType();
		assembler.loadIntegerValue(type.getDimension());
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitUnaryMinus(mavlc.ast.nodes.expression.UnaryMinus, java.lang.Function)
	 */
	@Override
	public Instruction visitUnaryMinus(UnaryMinus unaryMinus, Integer arg1) {
		unaryMinus.getOperand().accept(this, null);
		if(unaryMinus.getType().equals(IntType.getIntType())){
			assembler.emitIntegerNegation();
		}
		else if(unaryMinus.getType().equals(FloatType.getFloatType())){
			assembler.emitFloatNegation();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitBoolNot(mavlc.ast.nodes.expression.BoolNot, java.lang.Function)
	 */
	@Override
	public Instruction visitBoolNot(BoolNot boolNot, Integer arg1) {
		boolNot.getOperand().accept(this, null);
		assembler.emitLogicalNot();
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitCallExpression(mavlc.ast.nodes.expression.CallExpression, java.lang.Function)
	 */
	@Override
	public Instruction visitCallExpression(CallExpression callExpression, Integer arg1) {
		/*
		 * Evaluate operands, placing them on the stack one after the other.
		 */
		for(Expression arg : callExpression.getActualParameters()){
			arg.accept(this, null);
		}
		assembler.emitFunctionCall(env.getFunctionDeclaration(callExpression.getCalleeName()));
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitElementSelect(mavlc.ast.nodes.expression.ElementSelect, java.lang.Function)
	 */
	@Override
	public Instruction visitElementSelect(ElementSelect elementSelect, Integer arg1) {
		// Put the underlying vector/matrix on top of the stack
		Expression struct = elementSelect.getStruct();
		struct.accept(this, null);
		/*
		 * The matrix/vector is now on top of the stack, with ST
		 * pointing to the first word behind the struct.
		 * Therefore the base address for the element select is
		 * ST - size of the struct.
		 */
		assembler.loadAddress(Register.ST, struct.getType().wordSize()*-1);
		/*
		 * Put the desired index on top of the stack.
		 */
		elementSelect.getIndex().accept(this, null);
		/*
		 * We need to multiply the index with the element size.
		 */
		assembler.loadIntegerValue(elementSelect.getType().wordSize());
		assembler.emitIntegerMultiplication();
		/*
		 * Add up base address and index.
		 */
		assembler.emitIntegerAddition();
		/*
		 * Now load the element at the address on top of the stack.
		 */
		assembler.loadFromStackAddress(elementSelect.getType().wordSize());
		/*
		 * Remove the underlying struct from the stack and put the result
		 * of the element select on top.
		 */
		assembler.emitPop(elementSelect.getType().wordSize(), struct.getType().wordSize());
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitRecordElementSelect(mavlc.ast.nodes.expression.RecordElementSelect, java.lang.Function)
	 */
	@Override
	public Instruction visitRecordElementSelect(RecordElementSelect recordElementSelect, Integer arg1) {
		//TODO Task 3.3
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitSubMatrix(mavlc.ast.nodes.expression.SubMatrix, java.lang.Function)
	 */
	@Override
	public Instruction visitSubMatrix(SubMatrix subMatrix, Integer arg1) {
		Type matRes = subMatrix.getType();
		int resSize = matRes.wordSize();
		MatrixType matA = (MatrixType)subMatrix.getStruct().getType();
		int ASize = matA.wordSize();
		int AyDim = matA.getyDimension();

		subMatrix.getStruct().accept(this, null);
		assembler.emitPush(resSize);

		subMatrix.getXStartIndex().accept(this, null);
		subMatrix.getXBaseIndex().accept(this, null);
		subMatrix.getXEndIndex().accept(this, null);
		subMatrix.getYStartIndex().accept(this, null);
		subMatrix.getYBaseIndex().accept(this, null);
		subMatrix.getYEndIndex().accept(this, null);
		/*
		 * All needed Expressions are now evaluated and on the Stack
		 * The next step is the calculation of the Bounds.
		 */
		assembler.loadValue(Register.ST, 2, -5);
		assembler.emitIntegerAddition();

		assembler.loadValue(Register.ST, 2, -3);
		assembler.emitIntegerAddition();

		assembler.loadValue(Register.ST, 2, -8);
		assembler.emitIntegerAddition();

		assembler.loadValue(Register.ST, 2, -6);
		assembler.emitIntegerAddition();

		assembler.emitPop(4, 6);
		assembler.loadIntegerValue(AyDim);
		assembler.loadValue(Register.ST, 2, -3);
		assembler.loadIntegerValue(0);
		/*
		 * Stack at this Point: inMat, outMat, xEnd, yEnd ,xStart, yStart, AyDim, i, j, c
		 * Everything on here is called Base in following comments
		 */
		int loop = assembler.getNextInstructionAddress();
		assembler.loadValue(Register.ST, 2, -4);
		assembler.emitIntegerMultiplication();
		assembler.loadValue(Register.ST, 1, -3);
		assembler.emitIntegerAddition();
		assembler.loadAddress(Register.ST, -1 - 8 - resSize - ASize);
		assembler.emitIntegerAddition();
		assembler.loadFromStackAddress(1);				//Stack: Base, A[i*AyDim+j]
		assembler.loadValue(Register.ST, 1, -2);
		assembler.loadAddress(Register.ST, - 2 - 8 - resSize);
		assembler.emitIntegerAddition();
		assembler.storeToStackAddress(1); 				//Stack: Base
		/*
		 * Now we increment the counters and check for loop breaks;
		 */
		assembler.loadValue(Register.ST, 1, -1);
		assembler.loadIntegerValue(1);
		assembler.emitIntegerAddition();
		assembler.storeValue(Register.ST, 1, -2);

		assembler.loadValue(Register.ST, 1, -2);
		assembler.loadIntegerValue(1);
		assembler.emitIntegerAddition();
		assembler.storeValue(Register.ST, 1, -3);

		assembler.loadValue(Register.ST, 1, -2);
		assembler.loadValue(Register.ST, 1, -8);
		assembler.emitIntegerComparison(Comparison.LESS_EQUAL);
		assembler.emitConditionalJump(true, loop);
		//Inner Loop Done!
		//Reset j, then increment i
		assembler.loadValue(Register.ST, 1, -5);
		assembler.storeValue(Register.ST, 1, -3);

		assembler.loadValue(Register.ST, 1, -3);
		assembler.loadIntegerValue(1);
		assembler.emitIntegerAddition();
		assembler.storeValue(Register.ST, 1, -4);

		assembler.loadValue(Register.ST, 1, -3);
		assembler.loadValue(Register.ST, 1, -9);
		assembler.emitIntegerComparison(Comparison.LESS_EQUAL);
		assembler.emitConditionalJump(true, loop);
		//Both Loops done, clean stack and return
		assembler.emitPop(0, 8);
		assembler.emitPop(resSize, ASize);
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitSubVector(mavlc.ast.nodes.expression.SubVector, java.lang.Function)
	 */
	@Override
	public Instruction visitSubVector(SubVector subVector, Integer arg1) {
		Type res = subVector.getType();
		VectorType struct = (VectorType) subVector.getStruct().getType();
		assembler.emitPush(res.wordSize());
		subVector.getStruct().accept(this, null);
		subVector.getBaseIndex().accept(this, null);
		subVector.getStartIndex().accept(this, null);
		subVector.getEndIndex().accept(this, null);
		assembler.loadValue(Register.ST, 1, -3);
		assembler.loadValue(Register.ST, 1, -3);
		assembler.emitIntegerAddition();
		assembler.emitPop(1, 3);
		assembler.loadAddress(Register.ST, -1 - struct.wordSize());
		assembler.emitIntegerAddition();
		assembler.loadFromStackAddress(res.wordSize());
		assembler.loadAddress(Register.ST, -res.wordSize()-res.wordSize()-struct.wordSize());
		assembler.storeToStackAddress(res.wordSize());
		assembler.emitPop(0, struct.wordSize());
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitStructureInit(mavlc.ast.nodes.expression.StructureInit, java.lang.Function)
	 */
	@Override
	public Instruction visitStructureInit(StructureInit structureInit, Integer arg1) {
		for(Expression elem : structureInit.getElements()){
			elem.accept(this, null);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitRecordInit(mavlc.ast.nodes.expression.RecordInit, java.lang.Function)
	 */
	@Override
	public Instruction visitRecordInit(RecordInit recordInit, Integer arg1) {
		return this.visitStructureInit(recordInit, arg1);
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitStringValue(mavlc.ast.nodes.expression.StringValue, java.lang.Function)
	 */
	@Override
	public Instruction visitStringValue(StringValue stringValue, Integer arg1) {
		assembler.loadStringValue(stringValue.getValue());
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitBoolValue(mavlc.ast.nodes.expression.BoolValue, java.lang.Function)
	 */
	@Override
	public Instruction visitBoolValue(BoolValue boolValue, Integer arg1) {
		assembler.loadBooleanValue(boolValue.getValue());
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitIntValue(mavlc.ast.nodes.expression.IntValue, java.lang.Function)
	 */
	@Override
	public Instruction visitIntValue(IntValue intValue, Integer arg1) {
		assembler.loadIntegerValue(intValue.getValue());
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitFloatValue(mavlc.ast.nodes.expression.FloatValue, java.lang.Function)
	 */
	@Override
	public Instruction visitFloatValue(FloatValue floatValue, Integer arg1) {
		assembler.loadFloatValue(floatValue.getValue());
		return null;
	}

	/* (non-Javadoc)
	 * @see mavlc.ast.visitor.ASTNodeBaseVisitor#visitIdentifierReference(mavlc.ast.nodes.expression.IdentifierReference, java.lang.Function)
	 */
	@Override
	public Instruction visitIdentifierReference(IdentifierReference identifierReference, Integer arg1) {
		Declaration decl = identifierReference.getDeclaration();
		int wordSize = decl.getType().wordSize();
		int offset = decl.getLocalBaseOffset();
		assembler.loadLocalValue(wordSize, offset);
		return null;
	}
	@Override
	public Instruction visitSelectExpression(SelectExpression exp, Integer arg1){
		//TODO Task 3.4.3
		throw new UnsupportedOperationException();

	}

}
