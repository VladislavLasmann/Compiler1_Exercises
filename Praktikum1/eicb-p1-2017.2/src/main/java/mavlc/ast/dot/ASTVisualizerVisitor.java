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
package mavlc.ast.dot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;

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
import mavlc.ast.nodes.expression.UnaryExpression;
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
import mavlc.ast.visitor.ASTNodeBaseVisitor;

public class ASTVisualizerVisitor extends ASTNodeBaseVisitor<VisualizerNode, Object> {
	
	private int counter, colorCount;
	private ArrayList<VisualizerNode> nodes;
	private ArrayList<VisualizerEdge> edges;
	private IdentityHashMap<Declaration, VisualizerNode> declarations;
	private HashMap<Declaration, String> colors;
	private HashMap<String, VisualizerNode> calls;
	private HashMap<String, VisualizerNode> funcs;
	private boolean decorated;
	
	public ASTVisualizerVisitor(boolean DAST){
		nodes = new ArrayList<>();
		edges = new ArrayList<>();
		declarations = new IdentityHashMap<>();
		colors = new HashMap<>();
		funcs = new HashMap<>();
		calls = new HashMap<>();
		counter = 0;
		colorCount = -1;
		decorated = DAST;
	}
	
	public String toDotFormat(){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("digraph {\n");
		stringBuilder.append("node [shape=box];\ngraph [ordering=\"out\", overlap = false, nodesep=\"0.5\", ranksep=\"2\"];\n");
		if(decorated){
			hookCalls();
		}
		
		for(VisualizerNode node: nodes){
			stringBuilder.append(node.toString()+"\n");
		}
		if(!decorated)
			for(VisualizerEdge edge: edges){
				stringBuilder.append(edge.toASTString() + ";\n");
			}
		else{
			for(VisualizerEdge edge: edges){
				stringBuilder.append(edge.toDASTString() +";\n");
			}
		}
		stringBuilder.append("}");
		return stringBuilder.toString();
	}
	
	private void hookCalls(){
		for(String f: calls.keySet()){
			VisualizerNode call = calls.get(f);
			VisualizerNode def = funcs.get(f);
			addRefEdge(call, def, getColor());
		}
	}
	
	private void addEdge(VisualizerNode from, VisualizerNode to){
		VisualizerEdge ve = new VisualizerEdge(from, to, null);
		edges.add(ve);
	}
	
	private void addRefEdge(VisualizerNode from, VisualizerNode to, String color){
		if(from!=null&&to!=null){
			VisualizerEdge ve = new VisualizerEdge(from, to, "dashed", color);
			edges.add(ve);
		}
	}
	
	private VisualizerNode addNode(String label, Type type){
		VisualizerNode vn = new VisualizerNode(counter, label, type);
		counter++;
		nodes.add(vn);
		return vn;
	}
	
	protected VisualizerNode defaultOperation(ASTNode node, Object obj){
		throw new UnsupportedOperationException();
	}

	@Override
	public VisualizerNode visitModule(Module module, Object obj) {
		VisualizerNode vn = addNode("Module", null);
		for(Function f : module.getFunctions()){
			VisualizerNode sub = f.accept(this, null);
			addEdge(vn, sub);
		}
		return vn;
	}

	@Override
	public VisualizerNode visitFunction(Function functionNode, Object obj) {
		VisualizerNode vn = addNode("Function\\n" + functionNode.getName()+"\\n"+functionNode.getReturnType().toString(),null);
		if(!functionNode.getParameters().isEmpty()){
			VisualizerNode params = addNode("Params", null);
			addEdge(vn, params);
			for(FormalParameter fp: functionNode.getParameters()){
				VisualizerNode param = fp.accept(this, null);
				addEdge(params, param);
			}
		}
		VisualizerNode body = addNode("FuncBody", null);
		addEdge(vn, body);
		for(Statement s: functionNode.getFunctionBody()){
			VisualizerNode instruction = s.accept(this, null);
			addEdge(body, instruction);
		}
		if(decorated)funcs.put(functionNode.getName(), vn);
		return vn;
	}

	@Override
	public VisualizerNode visitFormalParameter(FormalParameter formalParameter, Object obj) {		
		VisualizerNode vn = addNode("Param", formalParameter.getType());
		VisualizerNode name = addNode(formalParameter.getName(), null);
		declarations.put(formalParameter, vn);
		colors.put(formalParameter, getColor());
		addEdge(vn, name);
		return vn;
	}
	
	@Override
	public VisualizerNode visitIteratorDeclaration(IteratorDeclaration iteratorDeclaration, Object obj) {
		VisualizerNode vn = addNode("iterator", iteratorDeclaration.getType());
		VisualizerNode name = addNode(iteratorDeclaration.getName(), null);
		declarations.put(iteratorDeclaration, vn);
		colors.put(iteratorDeclaration, getColor());
		addEdge(vn, name);
		return vn;
	}

	@Override
	public VisualizerNode visitRecordTypeDeclaration(RecordTypeDeclaration recordTypeDeclaration, Object obj) {
		VisualizerNode vn = addNode("Record\\n" + recordTypeDeclaration.getName(), null);
		if(!recordTypeDeclaration.getElements().isEmpty()) {
			VisualizerNode elements = addNode("Elements", null);
			addEdge(vn, elements);
			for(RecordElementDeclaration e : recordTypeDeclaration.getElements()){
				VisualizerNode element = e.accept(this, null);
				addEdge(elements, element);
			}
		}
		return vn;
	}

	@Override
	public VisualizerNode visitRecordElementDeclaration(RecordElementDeclaration recordElementDeclaration, Object obj) {
		VisualizerNode vn = addNode("element", recordElementDeclaration.getType());
		VisualizerNode name = addNode(recordElementDeclaration.getName(), null);
		declarations.put(recordElementDeclaration, vn);
		colors.put(recordElementDeclaration, getColor());
		addEdge(vn, name);
		return vn;
	}
	
	@Override
	public VisualizerNode visitStatement(Statement statement, Object obj) {
		return defaultOperation(statement, obj);
	}

	@Override
	public VisualizerNode visitDeclaration(Declaration declaration, Object obj) {		
		VisualizerNode vn = null;
		if(declaration instanceof ValueDefinition){
			vn = addNode("ValDef", null);
		}else{
			vn = addNode("VarDecl", null);
		}
		VisualizerNode id = addNode("Identifier", null);
		VisualizerNode name = addNode(declaration.getName(), null);
		VisualizerNode typeName = addNode("Type", null);
		VisualizerNode type = addNode(declaration.getType().toString(), null);
		addEdge(id, name);
		addEdge(vn, id);		
		addEdge(typeName, type);
		addEdge(vn, typeName);
		if(declaration instanceof ValueDefinition){
			VisualizerNode val = addNode("Value", null);
			ValueDefinition def = (ValueDefinition)declaration;
			VisualizerNode value = def.getValue().accept(this, null);
			addEdge(vn, val);
			addEdge(val, value);
		}
		declarations.put(declaration, vn);
		colors.put(declaration, getColor());
		return vn;
	}

	@Override
	public VisualizerNode visitValueDefinition(ValueDefinition valueDefinition, Object obj) {
		return visitDeclaration(valueDefinition, obj);
	}

	@Override
	public VisualizerNode visitVariableDeclaration(VariableDeclaration variableDeclaration, Object obj) {
		return visitDeclaration(variableDeclaration, obj);
	}

	@Override
	public VisualizerNode visitVariableAssignment(VariableAssignment variableAssignment, Object obj) {
		VisualizerNode vn = addNode("VarAssign", null);
		VisualizerNode id = variableAssignment.getIdentifier().accept(this, null);
		VisualizerNode val = addNode("Value", null);
		VisualizerNode exp = variableAssignment.getValue().accept(this, null);
		addEdge(vn, id);
		addEdge(vn, val);
		addEdge(val, exp);
		return vn;
	}

	private void handleLHSCommon(LeftHandIdentifier leftHandIdentifier, VisualizerNode vn) {
		VisualizerNode name = addNode(leftHandIdentifier.getName(), null);
		addEdge(vn, name);
		if(leftHandIdentifier.getDeclaration()!=null){
			addRefEdge(vn, declarations.get(leftHandIdentifier.getDeclaration()), colors.get(leftHandIdentifier.getDeclaration()));
		}		
	}
	
	@Override
	public VisualizerNode visitLeftHandIdentifier(LeftHandIdentifier leftHandIdentifier, Object obj) {
		VisualizerNode vn = addNode("LeftHandId", null);
		handleLHSCommon(leftHandIdentifier, vn);
		return vn;
	}

	@Override
	public VisualizerNode visitMatrixLHSIdentifier(MatrixLHSIdentifier matrixLHSIdentifier, Object obj) {
		VisualizerNode vn = addNode("MatrixLHSId", null);
		handleLHSCommon(matrixLHSIdentifier, vn);
		VisualizerNode x = addNode("xIndex", null);
		addEdge(vn, x);
		VisualizerNode y = addNode("yIndex", null);
		addEdge(vn, y);
		VisualizerNode xSub = matrixLHSIdentifier.getXIndex().accept(this, null);
		VisualizerNode ySub = matrixLHSIdentifier.getYIndex().accept(this, null);
		addEdge(x, xSub);
		addEdge(y, ySub);
		return vn;
	}

	@Override
	public VisualizerNode visitVectorLHSIdentifier(VectorLHSIdentifier vectorLHSIdentifier, Object obj) {
		VisualizerNode vn = addNode("VectorLHSId", null);
		handleLHSCommon(vectorLHSIdentifier, vn);
		VisualizerNode index = addNode("Index", null);
		VisualizerNode indexSub = vectorLHSIdentifier.getIndex().accept(this, null);
		addEdge(vn, index);
		addEdge(index, indexSub);
		return vn;
	}

	@Override
	public VisualizerNode visitRecordLHSIdentifier(RecordLHSIdentifier recordLHSIdentifier, Object obj) {
		VisualizerNode vn = addNode("RecordLHSId", null);
		handleLHSCommon(recordLHSIdentifier, vn);
		VisualizerNode element = addNode(recordLHSIdentifier.getName(), null);
		addEdge(vn, element);
		return vn;
	}
	
	@Override
	public VisualizerNode visitForLoop(ForLoop forLoop, Object obj) {
		VisualizerNode vn = addNode("For", null);
		VisualizerNode init = addNode("Init", null);
		VisualizerNode initVar = addNode(forLoop.getInitVariableName(), null);
		VisualizerNode initExp = forLoop.getInitValue().accept(this, null);
		VisualizerNode check = forLoop.getCheck().accept(this, null);
		VisualizerNode body = forLoop.getLoopBody().accept(this, null);
		VisualizerNode inc = addNode("Inc", null);
		VisualizerNode incVar = addNode(forLoop.getIncrementVariableName(), null);
		VisualizerNode incExp = forLoop.getIncrementExpr().accept(this, null);
		addEdge(vn, init);
		addEdge(vn, check);
		addEdge(vn, body);
		addEdge(vn, inc);
		addEdge(init, initVar);
		addEdge(init, initExp);
		addEdge(inc, incVar);
		addEdge(inc, incExp);
		return vn;
	}

	@Override
	public VisualizerNode visitForEachLoop(ForEachLoop forEachLoop, Object obj) {
		VisualizerNode vn = addNode("For", null);
		VisualizerNode iterator = addNode("iterator", null);
		VisualizerNode iteratorDecl = forEachLoop.getIteratorDeclaration().accept(this, null);
		VisualizerNode struct = addNode("Struct", null);
		VisualizerNode structExpr = forEachLoop.getStructExpr().accept(this, null);
		VisualizerNode body = forEachLoop.getLoopBody().accept(this, null);
		addEdge(vn, iterator);
		addEdge(vn, struct);
		addEdge(vn, body);
		addEdge(iterator, iteratorDecl);
		addEdge(struct, structExpr);
		return vn;
	}
		
	@Override
	public VisualizerNode visitIfStatement(IfStatement ifStatement, Object obj) {
		VisualizerNode vn = addNode("IfStatement", null);
		VisualizerNode test = addNode("Test", null);
		VisualizerNode check = ifStatement.getTestExpression().accept(this, null);
		VisualizerNode then = addNode("Then", null);
		VisualizerNode thenBody = ifStatement.getThenStatement().accept(this, null);
		addEdge(vn, test);
		addEdge(test, check);
		addEdge(vn, then);
		addEdge(then, thenBody);
		if(ifStatement.hasElseStatement()){
			VisualizerNode elseTop = addNode("Else", null);
			VisualizerNode elseBody = ifStatement.getElseStatement().accept(this, null);
			addEdge(vn, elseTop);
			addEdge(elseTop, elseBody);
		}
		return vn;
	}
	@Override
	public VisualizerNode visitSwitchStatement(SwitchStatement switchStatement , Object obj){
		VisualizerNode vn = addNode("SwitchStatement", null);
		for (SingleCase sCase : switchStatement.getCases()) {
			VisualizerNode aCase = sCase.accept(this, null);
			addEdge(vn, aCase);
		}
		for (SingleCase dCase : switchStatement.getDefaultCases()) {
			VisualizerNode aCase = dCase.accept(this, null);
			addEdge(vn, aCase);
		}
		
		return vn;
		
	}
	@Override
	public VisualizerNode visitSingleCase(SingleCase singleCase, Object obj){
		VisualizerNode vn = null;
		if(!singleCase.isDefault()){
			vn = addNode("Case", null);
			VisualizerNode test =  addNode("Condition : "+((Case)singleCase).getCondition(),Type.getIntType());;
			addEdge(vn, test);
		}else{
			vn = addNode("Default", null);
		}
		VisualizerNode stmt = singleCase.getStatement().accept(this, obj);
		addEdge(vn, stmt);
		return vn;
	}

	
	@Override
	public VisualizerNode visitCallStatement(CallStatement callStatement, Object obj) {
		VisualizerNode vn = addNode("Call", null);
		VisualizerNode args = callStatement.getCall().accept(this, null);
		if(args!=null)addEdge(vn, args);
		return vn;
	}

	@Override
	public VisualizerNode visitReturnStatement(ReturnStatement returnStatement, Object obj) {
		VisualizerNode vn = addNode("Return", null);
		VisualizerNode body = returnStatement.getReturnValue().accept(this, null);
		addEdge(vn, body);
		return vn;
	}

	@Override
	public VisualizerNode visitCompoundStatement(CompoundStatement compoundStatement, Object obj) {
		VisualizerNode vn = addNode("CompoundStmt", null);
		for(Statement s : compoundStatement.getStatements()){
			VisualizerNode instruction = s.accept(this, null);
			addEdge(vn, instruction);
		}
		return vn;
	}

	@Override
	public VisualizerNode visitExpression(Expression expression, Object obj) {
		return defaultOperation(expression, obj);
	}

	@Override
	public VisualizerNode visitBinaryExpression(BinaryExpression binaryExpression, Object obj) {
		VisualizerNode vn = (VisualizerNode) obj;
		VisualizerNode lhs = binaryExpression.getLeftOp().accept(this, null);
		VisualizerNode rhs = binaryExpression.getRightOp().accept(this, null);
		addEdge(vn, lhs);
		addEdge(vn, rhs);
		return vn;
	}

	@Override
	public VisualizerNode visitMatrixMultiplication(MatrixMultiplication matrixMultiplication, Object obj) {
		return visitBinaryExpression(matrixMultiplication, addNode("#", matrixMultiplication.getType()));
	}

	@Override
	public VisualizerNode visitDotProduct(DotProduct dotProduct, Object obj) {
		return visitBinaryExpression(dotProduct, addNode(".*", dotProduct.getType()));
	}

	@Override
	public VisualizerNode visitMultiplication(Multiplication multiplication, Object obj) {
		return visitBinaryExpression(multiplication, addNode("*", multiplication.getType()));
	}

	@Override
	public VisualizerNode visitDivision(Division division, Object obj) {
		return visitBinaryExpression(division, addNode("/", division.getType()));
	}

	@Override
	public VisualizerNode visitAddition(Addition addition, Object obj) {
		return visitBinaryExpression(addition, addNode("+", addition.getType()));
	}

	@Override
	public VisualizerNode visitSubtraction(Subtraction subtraction, Object obj) {
		return visitBinaryExpression(subtraction, addNode("-", subtraction.getType()));
	}

	@Override
	public VisualizerNode visitCompare(Compare compare, Object obj) {
		return visitBinaryExpression(compare, addNode("\\"+compare.getComparator().getOperator(), compare.getType()));
	}

	@Override
	public VisualizerNode visitAnd(And and, Object obj) {
		return visitBinaryExpression(and, addNode("&", and.getType()));
	}

	@Override
	public VisualizerNode visitOr(Or or, Object obj) {
		return visitBinaryExpression(or, addNode("|", or.getType()));
	}

	@Override
	public VisualizerNode visitExponentiation(Exponentiation exponentiation, Object obj) {
		return visitBinaryExpression(exponentiation, addNode("^", exponentiation.getType()));
	}

	@Override
	public VisualizerNode visitUnaryExpression(UnaryExpression unaryExpression, Object obj) {
		VisualizerNode vn = (VisualizerNode) obj;
		VisualizerNode exp = unaryExpression.getOperand().accept(this, null);
		addEdge(vn, exp);
		return vn;
	}

	@Override
	public VisualizerNode visitMatrixXDimension(MatrixXDimension xDimension, Object obj) {
		return visitUnaryExpression(xDimension, addNode(".xDim", xDimension.getType()));
	}

	@Override
	public VisualizerNode visitMatrixYDimension(MatrixYDimension yDimension, Object obj) {
		return visitUnaryExpression(yDimension, addNode(".yDim", yDimension.getType()));
	}
	
	@Override
	public VisualizerNode visitVectorDimension(VectorDimension vectorDimension, Object obj) {
		return visitUnaryExpression(vectorDimension, addNode(".dim",vectorDimension.getType()));
	}

	@Override
	public VisualizerNode visitUnaryMinus(UnaryMinus unaryMinus, Object obj) {
		return visitUnaryExpression(unaryMinus, addNode("-", unaryMinus.getType()));
	}

	@Override
	public VisualizerNode visitBoolNot(BoolNot boolNot, Object obj) {
		return visitUnaryExpression(boolNot, addNode("!", boolNot.getType()));
	}

	@Override
	public VisualizerNode visitCallExpression(CallExpression callExpression, Object obj) {		
		VisualizerNode vn = addNode("CallExp", callExpression.getType());
		VisualizerNode name = addNode(callExpression.getCalleeName(), null);
		addEdge(vn, name);
		if(!callExpression.getActualParameters().isEmpty()){
			VisualizerNode params = addNode("Params", null);
			addEdge(vn, params);
			for(Expression e : callExpression.getActualParameters()){
				VisualizerNode param = e.accept(this, null);
				addEdge(params, param);
			}
		}
		if(decorated)calls.put(callExpression.getCalleeName(), vn);
		return vn;
	}

	@Override
	public VisualizerNode visitElementSelect(ElementSelect elementSelect, Object obj) {
		VisualizerNode vn = addNode("Select", elementSelect.getType());
		VisualizerNode struct = elementSelect.getStruct().accept(this, null);
		VisualizerNode select = elementSelect.getIndex().accept(this, null);
		addEdge(vn, struct);
		addEdge(vn, select);
		return vn;
	}

	@Override
	public VisualizerNode visitRecordElementSelect(RecordElementSelect recordElementSelect, Object obj) {
		VisualizerNode vn = addNode("Select", recordElementSelect.getType());
		VisualizerNode struct = recordElementSelect.getRecord().accept(this, null);
		VisualizerNode select = addNode(recordElementSelect.getElementName(), recordElementSelect.getType());
		addEdge(vn, struct);
		addEdge(vn, select);
		return vn;
	}
	
	public VisualizerNode visitSelectExpression(SelectExpression selectExpression, Object obj) {
		VisualizerNode vn = addNode("SelectExpression", selectExpression.getType());
		VisualizerNode condLabel = addNode("Cond", null);
		VisualizerNode condExpr = selectExpression.getCondition().accept(this, null);
		VisualizerNode thenLabel = addNode("Then", null);
		VisualizerNode thenExpr = selectExpression.getTrueCase().accept(this, null);
		VisualizerNode elseLabel = addNode("Else", null);
		VisualizerNode elseExpr = selectExpression.getFalseCase().accept(this, null);
		addEdge(vn, condLabel);
		addEdge(condLabel, condExpr);
		addEdge(vn, thenLabel);
		addEdge(thenLabel, thenExpr);
		addEdge(vn, elseLabel);
		addEdge(elseLabel, elseExpr);
		return vn;
	}

	@Override
	public VisualizerNode visitSubMatrix(SubMatrix subMatrix, Object obj){
		VisualizerNode vn = addNode("SubMatrix", subMatrix.getType());
		VisualizerNode xSelect = addNode("x-Selector",null);
		VisualizerNode ySelect = addNode("y-Selector",null);
		addEdge(vn, xSelect);
		addEdge(vn, ySelect);
		VisualizerNode xs = subMatrix.getXStartIndex().accept(this, null);
		addEdge(xSelect, xs);
		VisualizerNode xb = subMatrix.getXBaseIndex().accept(this, null);
		addEdge(xSelect, xb);
		VisualizerNode xe = subMatrix.getXEndIndex().accept(this, null);
		addEdge(xSelect, xe);
		VisualizerNode ys = subMatrix.getYStartIndex().accept(this, null);
		addEdge(ySelect, ys);
		VisualizerNode yb = subMatrix.getYBaseIndex().accept(this, null);
		addEdge(ySelect, yb);
		VisualizerNode ye = subMatrix.getYEndIndex().accept(this, null);
		addEdge(ySelect, ye);
		return vn;
	}
	
	@Override
	public VisualizerNode visitSubVector(SubVector subVector, Object obj){
		VisualizerNode vn = addNode("SubVector", subVector.getType());
		VisualizerNode select = addNode("Selector", null);
		addEdge(vn, select);
		VisualizerNode s = subVector.getStartIndex().accept(this, null);
		addEdge(select, s);
		VisualizerNode b = subVector.getBaseIndex().accept(this, null);
		addEdge(select, b);
		VisualizerNode e = subVector.getEndIndex().accept(this, null);
		addEdge(select,e);
		return vn;
	}

	@Override
	public VisualizerNode visitStructureInit(StructureInit structureInit, Object obj){
		VisualizerNode vn = addNode("StructureInit", null);
		for(Expression exp : structureInit.getElements()){
			VisualizerNode name = exp.accept(this, null);
			addEdge(vn, name);
		}
		return vn;
	}

	@Override
	public VisualizerNode visitRecordInit(RecordInit recordInit, Object obj) {
		VisualizerNode vn = addNode("RecordInit", null);
		for(Expression exp : recordInit.getElements()){
			VisualizerNode name = exp.accept(this, null);
			addEdge(vn, name);
		}
		return vn;
	}
	
	@Override
	public VisualizerNode visitStringValue(StringValue stringValue, Object obj) {
		return addNode(stringValue.getValue().replaceAll("\"", ""), stringValue.getType());
	}

	@Override
	public VisualizerNode visitBoolValue(BoolValue boolValue, Object obj) {
		return addNode(boolValue.getValue()? "True":"False", boolValue.getType());
	}

	@Override
	public VisualizerNode visitIntValue(IntValue intValue, Object obj) {
		return addNode(""+intValue.getValue(), intValue.getType());
	}

	@Override
	public VisualizerNode visitFloatValue(FloatValue floatValue, Object obj) {
		return addNode(""+floatValue.getValue(), floatValue.getType());
	}

	@Override
	public VisualizerNode visitIdentifierReference(IdentifierReference identifierReference, Object obj) {
		VisualizerNode vn = addNode("Identifier",null);
		VisualizerNode id = addNode(identifierReference.getIdentifierName(), identifierReference.getType());
		if(identifierReference.getDeclaration()!=null)
			addRefEdge(vn, declarations.get(identifierReference.getDeclaration()), colors.get(identifierReference.getDeclaration()));
		addEdge(vn, id);
		return vn;
	}

	private String getColor(){
		colorCount++;
		int i = colorCount % 5;
		switch(i){
		case 0: return "red";
		case 1: return "blue";
		case 2: return "orange";
		case 3: return "turquoise3";
		case 4: return "darkorchid1";
		default: return "black";
		}
	}
	
}
