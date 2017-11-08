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
package mavlc.ast.serialization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import mavlc.ast.nodes.expression.Addition;
import mavlc.ast.nodes.expression.And;
import mavlc.ast.nodes.expression.BoolNot;
import mavlc.ast.nodes.expression.BoolValue;
import mavlc.ast.nodes.expression.CallExpression;
import mavlc.ast.nodes.expression.Compare;
import mavlc.ast.nodes.expression.Division;
import mavlc.ast.nodes.expression.DotProduct;
import mavlc.ast.nodes.expression.ElementSelect;
import mavlc.ast.nodes.expression.Exponentiation;
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
import mavlc.ast.nodes.statement.SwitchStatement;
import mavlc.ast.nodes.statement.ValueDefinition;
import mavlc.ast.nodes.statement.VariableAssignment;
import mavlc.ast.nodes.statement.VariableDeclaration;
import mavlc.ast.nodes.statement.VectorLHSIdentifier;
import mavlc.ast.type.BoolType;
import mavlc.ast.type.FloatType;
import mavlc.ast.type.IntType;
import mavlc.ast.type.MatrixType;
import mavlc.ast.type.RecordType;
import mavlc.ast.type.StringType;
import mavlc.ast.type.VectorType;
import mavlc.ast.type.VoidType;
import mavlc.error_reporting.CompilationError;

public class ErrorXMLSerialization {

	protected final XStream xstream;
	
	public ErrorXMLSerialization(){
		xstream = new XStream(new StaxDriver());
		xstream.alias("module", Module.class);
		xstream.addImplicitCollection(Module.class, "functions");
		xstream.alias("function", Function.class);
		xstream.alias("formalParameter", FormalParameter.class);
		xstream.alias("recordTypeDeclaration", RecordTypeDeclaration.class);
		xstream.alias("recordElementDeclaration", RecordElementDeclaration.class);
		xstream.alias("iteratorDeclaration", IteratorDeclaration.class);
		xstream.alias("addition", Addition.class);
		xstream.alias("logicAnd", And.class);
		xstream.alias("boolNot", BoolNot.class);
		xstream.alias("boolVal", BoolValue.class);
		xstream.alias("callExpr", CallExpression.class);
		xstream.alias("compare", Compare.class);
		xstream.alias("division", Division.class);
		xstream.alias("dotProduct", DotProduct.class);
		xstream.alias("elemSelect", ElementSelect.class);
		xstream.alias("recordElemSelect", RecordElementSelect.class);
		xstream.alias("floatVal", FloatValue.class);
		xstream.alias("idRef", IdentifierReference.class);
		xstream.alias("intVal", IntValue.class);
		xstream.alias("matrixMul", MatrixMultiplication.class);
		xstream.alias("xDimension", MatrixXDimension.class);
		xstream.alias("yDimension", MatrixYDimension.class);
		xstream.alias("multiplication", Multiplication.class);
		xstream.alias("logicOr", Or.class);
		xstream.alias("stringVal", StringValue.class);
		xstream.alias("structInit", StructureInit.class);
		xstream.alias("recordInit", RecordInit.class);
		xstream.alias("subMatrix", SubMatrix.class);
		xstream.alias("subtraction", Subtraction.class);
		xstream.alias("exponentiation", Exponentiation.class);
		xstream.alias("subVector", SubVector.class);
		xstream.alias("unaryMinus", UnaryMinus.class);
		xstream.alias("dimension", VectorDimension.class);
		xstream.alias("pow", Exponentiation.class);
		xstream.alias("select", SelectExpression.class);
		xstream.alias("callStmt", CallStatement.class);
		xstream.alias("compoundStmt", CompoundStatement.class);
		xstream.alias("declaration", Declaration.class);
		xstream.alias("forLoop", ForLoop.class);
		xstream.alias("forEachLoop", ForEachLoop.class);
		xstream.alias("if", IfStatement.class);
		xstream.alias("switch", SwitchStatement.class);
		xstream.alias("case", Case.class);
		xstream.alias("default", Default.class);
		xstream.alias("lhsId", LeftHandIdentifier.class);
		xstream.alias("matrixId", MatrixLHSIdentifier.class);
		xstream.alias("return", ReturnStatement.class);
		xstream.alias("valDef", ValueDefinition.class);
		xstream.alias("varAssign", VariableAssignment.class);
		xstream.alias("varDecl", VariableDeclaration.class);
		xstream.alias("vectorId", VectorLHSIdentifier.class);
		xstream.alias("recordId", RecordLHSIdentifier.class);
		
		xstream.alias("boolType", BoolType.class);
		xstream.alias("floatType", FloatType.class);
		xstream.alias("intType", IntType.class);
		xstream.alias("matrixType", MatrixType.class);
		xstream.alias("stringType", StringType.class);
		xstream.alias("vectorType", VectorType.class);
		xstream.alias("recordType", RecordType.class);
		xstream.alias("voidType", VoidType.class);
	}
	
	public void serialize(CompilationError error, String fileName){
		try {
			BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileName));
			HierarchicalStreamWriter streamWriter = new PrettyPrintWriter(fileWriter);
			xstream.marshal(error, streamWriter);
		} 
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public CompilationError deserialize(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			return (CompilationError) xstream.fromXML(reader);
		} 
		catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public String serialize(CompilationError error) {
		return xstream.toXML(error);
	}
}
