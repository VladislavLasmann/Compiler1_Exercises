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
package mavlc.parser.recursive_descent;

import mavlc.ast.nodes.expression.*;
import mavlc.ast.nodes.function.FormalParameter;
import mavlc.ast.nodes.function.Function;
import mavlc.ast.nodes.module.Module;
import mavlc.ast.nodes.record.RecordElementDeclaration;
import mavlc.ast.nodes.record.RecordTypeDeclaration;
import mavlc.ast.nodes.statement.*;
import mavlc.ast.type.*;
import mavlc.error_reporting.SyntaxError;

import java.util.*;

import mavlc.parser.recursive_descent.Token.TokenType;
import static mavlc.ast.nodes.expression.Compare.Comparison.*;
import static mavlc.parser.recursive_descent.Token.TokenType.*;

/* TODO: Please fill this out!
 *
 * EiCB group number:
 * Names and student ID numbers of group members:
 */

/**
 * A recursive-descent parser for MAVL.
 */
public final class Parser {

	private final Deque<Token> tokens;
	private Token currentToken;

	/**
	 * Constructor.
	 * 
	 * @param tokens A token stream that was produced by the {@link Scanner}.
	 */
	public Parser(Deque<Token> tokens) {
		this.tokens = tokens;
		currentToken = tokens.poll();
	}

	/**
	 * Parses the MAVL grammar's start symbol, Module.
	 * 
	 * @return A {@link Module} node that is the root of the AST representing the tokenized input progam.
	 * @throws SyntaxError to indicate that an unexpected token was encountered.
	 */
	public Module parse() throws SyntaxError {
		Module compilationUnit = new Module(tokens.peek().line, 0);
		while (currentToken.type != EOF) {
			switch (currentToken.type) {
			case FUNCTION:
				Function func = parseFunction();
				compilationUnit.addFunction(func);
				break;
			case RECORD:
				RecordTypeDeclaration record = parseRecordTypeDeclaration();
				compilationUnit.addRecord(record);
				break;
			default:
				throw new SyntaxError(currentToken, FUNCTION, RECORD);
			}
		}
		return compilationUnit;
	}

	private String accept(TokenType type) throws SyntaxError {
		Token t = currentToken;
		if (t.type != type)
			throw new SyntaxError(t, type);
		acceptIt();
		return t.spelling;
	}

	private void acceptIt() {
		currentToken = tokens.poll();
		if (currentToken.type == ERROR)
			throw new SyntaxError(currentToken);
	}

	private Function parseFunction() throws SyntaxError {
		int line = currentToken.line;
		int column = currentToken.column;

		accept(FUNCTION);
		Type type = parseType();
		String name = accept(ID);

		Function function = new Function(line, column, name, type);

		accept(LPAREN);
		if (currentToken.type != RPAREN) {
			function.addParameter(parseFormalParameter());
			while (currentToken.type != RPAREN) {
				accept(COMMA);
				function.addParameter(parseFormalParameter());
			}
		}
		accept(RPAREN);

		accept(LBRACE);
		while (currentToken.type != RBRACE)
			function.addStatement(parseStatement());
		accept(RBRACE);

		return function;
	}

	private FormalParameter parseFormalParameter() throws SyntaxError {
		int line = currentToken.line;
		int column = currentToken.column;

		Type type = parseType();
		String name = accept(ID);
		
		return new FormalParameter(line, column, name, type);
	}

	/**
	 * Exercise 1.4
	 * @return
	 */
	private RecordTypeDeclaration parseRecordTypeDeclaration() {
		// ::= 'record' ID '{' recordElemDecl+ '}'
		int sourceLine = currentToken.line;
		int sourceColumn = currentToken.column;
		String name;
		List<RecordElementDeclaration> elements = new ArrayList<>();									// not LinkedList!!! (error)

		accept(RECORD);																					// 'record'
		name = accept(ID);																				// ID
		accept(LBRACE);																					// '{'

		elements.add( parseRecordElementDeclaration() );												// recordElemDecl
		while( currentToken.type != RBRACE ){															// recordElemDecl*, if its not an RBRACE is must be an recordElemDecl
			elements.add( parseRecordElementDeclaration() );
		}

		accept(RBRACE);																					// '}'

		return new RecordTypeDeclaration(sourceLine, sourceColumn, name, elements);
	}

	/**
	 * Exercise 1.4
	 * @return
	 */
	private RecordElementDeclaration parseRecordElementDeclaration() {
		// ::= ( ’var’ | ’val’ ) type ID ’;’

		int sourceLine = currentToken.line;
		int sourceColumn = currentToken.column;
		boolean isVariable;
		Type type;
		String name;

		if( currentToken.type == VAR )																	// 'var'? -> isVariable = true
			isVariable = true;
		else if( currentToken.type == VAL)																// 'val'? -> isVariable = false
			isVariable = false;
		else																							//
			throw new SyntaxError(currentToken, VAR, VAL);

		acceptIt();																						// update token
		type = parseType();																				// got type
		name = accept(ID);																				// got ID
		accept(SEMICOLON);																				// ';'

		return new RecordElementDeclaration(sourceLine, sourceColumn, isVariable, type, name);
	}

	/**
	 * Exercise 1.6
	 * @return
	 * @throws SyntaxError
	 */
	private IteratorDeclaration parseIteratorDeclaration() throws SyntaxError {
		// ::= ( ’var’ | ’val’ ) type ID
		int sourceLine = currentToken.line;
		int sourceColumn = currentToken.column;

		boolean isVariable;
		Type type;
		String name;

		if( currentToken.type == VAR )																	// 'var' => isVariable = true
			isVariable = true;
		else if ( currentToken.type == VAL )
			isVariable = false;																			// 'val' => isVariable = false
		else
			throw new SyntaxError(currentToken, currentToken.type, VAR, VAL);

		acceptIt();																						// ( ’var’ | ’val’ )
		type = parseType();																				// type
		name = accept(ID);																				// ID

		return new IteratorDeclaration(sourceLine, sourceColumn, name, type, isVariable);
	}
	
	private Type parseType() throws SyntaxError {
		boolean vector = false;
		switch (currentToken.type) {
		case INT:    acceptIt(); return Type.getIntType();
		case FLOAT:  acceptIt(); return Type.getFloatType();
		case BOOL:   acceptIt(); return Type.getBoolType();
		case VOID:   acceptIt(); return Type.getVoidType();
		case STRING: acceptIt(); return Type.getStringType();
		case VECTOR: accept(VECTOR); vector = true; break;
		case MATRIX: accept(MATRIX); break;
		case ID:	 String name = accept(ID);
					 return new RecordType(name);
		default:
			throw new SyntaxError(currentToken, INT, FLOAT, BOOL, VOID, STRING, VECTOR, MATRIX, ID);
		}

		accept(LANGLE);
		ScalarType subtype = null;
		switch (currentToken.type) {
		case INT:   subtype = Type.getIntType(); break;
		case FLOAT: subtype = Type.getFloatType(); break;
		default:
			throw new SyntaxError(currentToken, INT, FLOAT);
		}
		acceptIt();
		accept(RANGLE);
		accept(LBRACKET);
		int x = parseIntLit();
		accept(RBRACKET);

		if (vector)
			return new VectorType(subtype, x);

		accept(LBRACKET);
		int y = parseIntLit();
		accept(RBRACKET);

		return new MatrixType(subtype, x, y);
	}

	private Statement parseStatement() throws SyntaxError {
		Statement s = null;
		switch (currentToken.type) {
		case VAL:    s = parseValueDef();     break;
		case VAR:    s = parseVarDecl();      break;
		case RETURN: s = parseReturn();       break;
		case ID:     s = parseAssignOrCall(); break;
		case FOR:    s = parseFor();          break;
		case FOREACH:s = parseForEach();      break;
		case IF:     s = parseIf();           break;
		case SWITCH: s = parseSwitch();       break;
		case LBRACE: s = parseCompound();     break;
		default:
			throw new SyntaxError(currentToken, VAL, VAR, RETURN, ID, FOR, FOREACH, IF, SWITCH, LBRACE);
		}

		return s;
	}

	/**
	 * Exercise 1.1
	 * @return
	 * @throws SyntaxError
	 */
	private ValueDefinition parseValueDef() throws SyntaxError {
		// ::= ’val’ type ID ’=’ expr ’;’
		int sourceLine = currentToken.line;
		int sourceColumn = currentToken.column;

		accept(VAL);							// 'val'
		Type valueType = parseType();			// get the Type, updates currentToken
		String valueName = accept( ID );		// checks if token is ID, updates currentToken

		accept(ASSIGN);							// '=' ASSIGN-type

		Expression value = parseExpr();			// should be an expression, update currentToken;
		accept(SEMICOLON);						// ';'

		return new ValueDefinition(sourceLine, sourceColumn, valueType, valueName, value);
	}

	/**
	 * Exercise 1.1
	 * @return
	 * @throws SyntaxError
	 */
	private VariableDeclaration parseVarDecl() throws SyntaxError {
		// ::= ’var’ type ID ’;’
		int sourceLine = currentToken.line;
		int sourceColumn = currentToken.column;

		accept(VAR);								// 'var'

		Type variableType = parseType();			// get the Type
		String variableName = accept(ID);			// check if this token is an ID

		accept(SEMICOLON);							// ";"

		return new VariableDeclaration(sourceLine, sourceColumn, variableType, variableName);
	}

	private ReturnStatement parseReturn() throws SyntaxError {
		int line = currentToken.line;
		int column = currentToken.column;
		accept(RETURN);
		Expression e = parseExpr();
		accept(SEMICOLON);
		
		return new ReturnStatement(line, column, e);
	}

	private Statement parseAssignOrCall() throws SyntaxError {
		int line = currentToken.line;
		int column = currentToken.column;

		String name = accept(ID);

		Statement s;
		if (currentToken.type != LPAREN)
			s = parseAssign(name, line, column);
		else
			s = new CallStatement(line, column, parseCall(name, line, column));
		accept(SEMICOLON);
		
		return s;
	}

	/**
	 * Exercise 1.1 / 1.4
	 * @param name
	 * @param line
	 * @param column
	 * @return
	 * @throws SyntaxError
	 */
	private VariableAssignment parseAssign(String name, int line, int column) throws SyntaxError {
		/**
		 * ((’[’expr’]’(’[’expr’]’)?)|@ID)?’=’expr
		 * Cases:
		 * 1:  						'=' expr
		 * 2:  @ ID 				'=' expr
		 * 3: '['expr']' 			'=' expr
		 * 4: '['expr']''['expr']' 	'=' expr
		 */
		LeftHandIdentifier leftHandIdentifier = new LeftHandIdentifier(line, column, name);

		if( currentToken.type == AT ){
			acceptIt();																						// '@'
			String elementID = accept(ID);																	// ID
			leftHandIdentifier = new RecordLHSIdentifier(line, column, name, elementID);					// assignment to an record-element

		} else if( currentToken.type == LBRACKET){
			acceptIt();																						// '['
			Expression firstIndex = parseExpr();															// expr
			leftHandIdentifier = new VectorLHSIdentifier(line, column, name, firstIndex);					// one expression in brackets means vector
			accept(RBRACKET);																				// ']'

			if(currentToken.type == LBRACKET){
				acceptIt();
				Expression secondIndex = parseExpr();
				leftHandIdentifier = new MatrixLHSIdentifier(line, column, name, firstIndex, secondIndex);	// 2x expression in brackets means matrix
				accept(RBRACKET);
			}
		}

		accept(ASSIGN);																						// '='
		Expression assignmentValue = parseExpr();															// expr

		return new VariableAssignment(line, column, leftHandIdentifier, assignmentValue);
	}
	
	private CallExpression parseCall(String name, int line, int column) {
		CallExpression callExpression = new CallExpression(line, column, name);
		accept(LPAREN);
		if (currentToken.type != RPAREN) {
			callExpression.addActualParameter(parseExpr());
			while (currentToken.type != RPAREN) {
				accept(COMMA);
				callExpression.addActualParameter(parseExpr());
			}
		}
		accept(RPAREN);
		
		return callExpression;
	}

	private ForLoop parseFor() throws SyntaxError {
		int line = currentToken.line;
		int column = currentToken.column;

		accept(FOR);
		accept(LPAREN);
		String name = accept(ID);
		accept(ASSIGN);
		Expression a = parseExpr();
		accept(SEMICOLON);
		Expression b = parseExpr();
		accept(SEMICOLON);
		String inc = accept(ID);
		accept(ASSIGN);
		Expression c = parseExpr();
		accept(RPAREN);
		return new ForLoop(line, column, name, a, b, inc, c, parseStatement());
	}

	/**
	 * Exercise 1.6
	 * @return
	 * @throws SyntaxError
	 */
	private ForEachLoop parseForEach() throws SyntaxError {
		// ’foreach’ ’(’ iteratorDecl ’:’ expr ’)’ statement
		int sourceLine = currentToken.line;
		int sourceColumn = currentToken.column;

		IteratorDeclaration headIterator;
		Expression headExpression;
		Statement loopBody;

		accept(FOREACH);																					// 'foreach'
		accept(LPAREN);																						// '('
		headIterator = parseIteratorDeclaration();															// iteratorDecl
		accept(COLON);																						// ':'
		headExpression = parseExpr();																		// expr
		accept(RPAREN);																						// ')'
		loopBody = parseStatement();																		// statement

		return new ForEachLoop(sourceLine,sourceColumn, headIterator, headExpression, loopBody);
	}
	
	private IfStatement parseIf() throws SyntaxError {
		int line = currentToken.line;
		int column = currentToken.column;
		accept(IF);
		accept(LPAREN);
		Expression test = parseExpr();
		accept(RPAREN);
		Statement then = parseStatement();
		if (currentToken.type == ELSE) {
			acceptIt();
			return new IfStatement(line, column, test, then, parseStatement());
		}
		return new IfStatement(line, column, test, then);
	}

	/**
	 * Exercise 1.5
	 * @return
	 * @throws SyntaxError
	 */
	private SwitchStatement parseSwitch() throws SyntaxError {
		// ::= ’switch’ ’(’expr’)’ ’{’ singleCase* ’}’
		// singleCase ::= case | default

		int sourceLine = currentToken.line;
		int sourceColumn = currentToken.column;

		// Here: elements in SwitchStatement will set per setter, not in constructor
		SwitchStatement switchStatement = new SwitchStatement(sourceLine, sourceColumn);

		accept(SWITCH);																		// 'switch'
		accept(LPAREN);																		// '('

		switchStatement.setTestExpression( parseExpr() );									// expr, and SET it in the SwitchStatement

		accept(RPAREN);																		// ')'
		accept(LBRACE);																		// '{'

		while( currentToken.type == CASE ||currentToken.type == DEFAULT ){					// ( case | default )*
			if( currentToken.type == CASE )
				switchStatement.addCase( parseCase() );										// case, added to SwitchStatement
			else{
				if( switchStatement.getDefaultCases().size() == 0 )
					switchStatement.addDefault( parseDefault() );							// default, added to SwitchStatement
				else
					throw new SyntaxError(currentToken, CASE, RBRACE);	// if there are more than one default -> syntax error
			}
		}

		accept(RBRACE);																		// '}'

		return switchStatement;
	}

	/**
	 * Exercise 1.5
	 * @return
	 * @throws SyntaxError
	 */
	private Case parseCase() throws SyntaxError {
		// ::= ’case’ ’-’? INT ’:’ statement
		int sourceLine = currentToken.line;
		int sourceColumn = currentToken.column;
		int condition;
		Statement statement;

		accept(CASE);															// 'case'
		condition = parseIntLitOrMinusIntLit();									//  ’-’? INT

		accept(COLON);															// ':'
		statement = parseStatement();											// statement

		return new Case(sourceLine, sourceColumn, condition, statement);
	}

	/**
	 * Exercise 1.5
	 * @return
	 * @throws SyntaxError
	 */
	private Default parseDefault() throws SyntaxError {
		// ::= ’default’  ’:’ statement
		int sourceLine = currentToken.line;
		int soureceColumn = currentToken.column;
		Statement statement;

		accept(DEFAULT);														// 'default'
		accept(COLON);															// ':'

		statement = parseStatement();											// statement

		return new Default(sourceLine, soureceColumn, statement);
	}

	private CompoundStatement parseCompound() throws SyntaxError {
		CompoundStatement c = new CompoundStatement(currentToken.line, currentToken.column);
		accept(LBRACE);
		while (currentToken.type != RBRACE)
			c.addStatement(parseStatement());
		accept(RBRACE);
		return c;
	}

	private Expression parseExpr() throws SyntaxError {
		return parseSelect();
	}

	/**
	 * Exercise 1.3
	 * @return
	 * @throws SyntaxError
	 */
	private Expression parseSelect() throws SyntaxError {
		// ::= or (’?’ or ’:’ or)?
		int sourceLine = currentToken.line;
		int sourceColumn = currentToken.column;

		Expression firstExpression = parseOr();									//
		if( currentToken.type == QMARK ){										// '?'
			acceptIt();
			Expression trueCase = parseOr();									// or
			accept(COLON);														// ':'
			Expression falseCase = parseOr();									// or

			return new SelectExpression(sourceLine, sourceColumn, firstExpression, trueCase, falseCase);
		}
		return firstExpression;
	}
	
	private Expression parseOr() throws SyntaxError {
		int line = currentToken.line;
		int column = currentToken.column;

		Expression x = parseAnd();
		while (currentToken.type == OR) {
			acceptIt();
			x = new Or(line, column, x, parseAnd());
		}
		return x;
	}

	private Expression parseAnd() throws SyntaxError {
		int line = currentToken.line;
		int column = currentToken.column;

		Expression x = parseNot();
		while (currentToken.type == AND) {
			acceptIt();
			x = new And(line, column, x, parseNot());
		}
		return x;
	}

	private Expression parseNot() throws SyntaxError {
		int line = currentToken.line;
		int column = currentToken.column;

		if (currentToken.type == NOT) {
			acceptIt();
			return new BoolNot(line, column, parseCompare());
		}
		return parseCompare();
	}

	/**
	 * Exercise 1.2
	 * @return
	 * @throws SyntaxError
	 */
	private Expression parseCompare() throws SyntaxError {
		// ::= addSub ( ( ’>’ | ’<’|’<=’| '>=' | '==' | '!=' ) addSub)*
		int sourceLine = currentToken.line;
		int sourceColumn = currentToken.column;

		Expression expression = parseAddSub();														// this should update currentToken
		while( 	currentToken.type == RANGLE ||
				currentToken.type == LANGLE ||
				currentToken.type == CMPLE 	||
				currentToken.type == CMPGE	||
				currentToken.type == CMPEQ	||
				currentToken.type == CMPNE ) {
			TokenType tokenType = currentToken.type;
			acceptIt();
			switch (tokenType) {
				// ( ( ’>’ | ’<’|’<=’| '>=' | '==' | '!=' ) addSub)*
				// !!!CAUTION: Comparators are in Compare->Comparison
				case RANGLE:
					expression = new Compare(sourceLine, sourceColumn, expression, parseAddSub(), GREATER);
					break;            // '>' addSub
				case LANGLE:
					expression = new Compare(sourceLine, sourceColumn, expression, parseAddSub(), LESS);
					break;                // equivalent ....
				case CMPLE:
					expression = new Compare(sourceLine, sourceColumn, expression, parseAddSub(), LESS_EQUAL);
					break;
				case CMPGE:
					expression = new Compare(sourceLine, sourceColumn, expression, parseAddSub(), GREATER_EQUAL);
					break;
				case CMPEQ:
					expression = new Compare(sourceLine, sourceColumn, expression, parseAddSub(), EQUAL);
					break;
				case CMPNE:
					expression = new Compare(sourceLine, sourceColumn, expression, parseAddSub(), NOT_EQUAL);
					break;
				default:
					break;
			}
		}

		return expression;
	}

	/**
	 * Exercise 1.2
	 * @return
	 * @throws SyntaxError
	 */
	private Expression parseAddSub() throws SyntaxError {
		// ::= mulDiv ( ( ’+’ | ’-’ ) mulDiv )*

		int sourceLine = currentToken.line;
		int sourceColumn = currentToken.column;

		Expression expression = parseMulDiv();														// this should update the currentToken
		while( currentToken.type == ADD || currentToken.type == SUB ){								// ( ( ’+’ | ’-’ ) mulDiv )*
			TokenType tokenType = currentToken.type;
			acceptIt();
			if( tokenType == ADD )
				expression = new Addition(sourceLine, sourceColumn, expression, parseMulDiv()); 	// '+' mulDiv
			else
				expression = new Subtraction(sourceLine, sourceColumn, expression, parseMulDiv());	// '-' mulDiv
		}
		return expression;
	}

	/**
	 * Exercise 1.2
	 * @return
	 * @throws SyntaxError
	 */
	private Expression parseMulDiv() throws SyntaxError {
		// ::= unaryMinus ( ( ’*’ | ’/’ ) unaryMinus )*

		int sourceLine = currentToken.line;
		int sourceColumn = currentToken.column;

		Expression expression = parseUnaryMinus();															// this should update currentToken
		while( currentToken.type == MULT || currentToken.type == DIV ){										// ( ( ’*’ | ’/’ ) unaryMinus )*
			TokenType tokenType = currentToken.type;
			acceptIt();
			if (tokenType == MULT)
				expression = new Multiplication(sourceLine, sourceColumn, expression, parseUnaryMinus());	// '*' unaryMinus
			else
				expression = new Division(sourceLine, sourceColumn, expression, parseUnaryMinus());			// '/' unaryMinus
		}
		return expression;
	}

	/**
	 * Exercise 1.2
	 * @return
	 * @throws SyntaxError
	 */
	private Expression parseUnaryMinus() throws SyntaxError {
		int line = currentToken.line;
		int column = currentToken.column;

		if (currentToken.type == SUB) {
			acceptIt();
			return new UnaryMinus(line, column, parseExponentiation());
		} else {
			return parseExponentiation();
		}
	}

	/**
	 * Exercise 1.2
	 * @return
	 * @throws SyntaxError
	 */
	private Expression parseExponentiation() throws SyntaxError {
		// ::= dim ( ’^’ dim )*
		int sourceLine = currentToken.line;
		int sourceColumn = currentToken.column;

		Expression expression = parseDim();														// dim, should update currentToken
		while(currentToken.type == EXP){
			acceptIt();																			//
			expression = new Exponentiation(sourceLine, sourceColumn, expression, parseDim() );	// '^' dim
		}
		return expression;

	}


	private Expression parseDim() throws SyntaxError {
		int line = currentToken.line;
		int column = currentToken.column;

		Expression x = parseDotProd();
		switch (currentToken.type) {
		case XDIM: acceptIt(); return new MatrixXDimension(line, column, x);
		case YDIM: acceptIt(); return new MatrixYDimension(line, column, x);
		case DIM:  acceptIt(); return new VectorDimension(line, column, x);
		default:
			return x;
		}
	}

	private Expression parseDotProd() throws SyntaxError {
		int line = currentToken.line;
		int column = currentToken.column;

		Expression x = parseMatrixMul();
		while (currentToken.type == DOTPROD) {
			acceptIt();
			x = new DotProduct(line, column, x, parseMatrixMul());
		}
		
		return x;
	}

	private Expression parseMatrixMul() throws SyntaxError {
		int line = currentToken.line;
		int column = currentToken.column;

		Expression x = parseSubrange();
		while (currentToken.type == MATMULT) {
			acceptIt();
			x = new MatrixMultiplication(line, column, x, parseSubrange());
		}
		return x;

	}

	private Expression parseSubrange() throws SyntaxError {
		int line = currentToken.line;
		int column = currentToken.column;

		Expression x = parseElementSelect();

		if (currentToken.type == LBRACE) {
			acceptIt();
			Expression xStartIndex = parseExpr();
			accept(COLON);
			Expression xBaseIndex = parseExpr();
			accept(COLON);
			Expression xEndIndex = parseExpr();
			accept(RBRACE);
			if (currentToken.type != LBRACE)
				return new SubVector(line, column, x, xBaseIndex, xStartIndex, xEndIndex);

			accept(LBRACE);
			Expression yStartIndex = parseExpr();
			accept(COLON);
			Expression yBaseIndex = parseExpr();
			accept(COLON);
			Expression yEndIndex = parseExpr();
			accept(RBRACE);
			return new SubMatrix(line, column, x, xBaseIndex, xStartIndex, xEndIndex, yBaseIndex, yStartIndex, yEndIndex);
		}

		return x;
	}

	private Expression parseElementSelect() throws SyntaxError {
		int line = currentToken.line;
		int column = currentToken.column;

		Expression x = parseRecordElementSelect();

		while (currentToken.type == LBRACKET) {
			acceptIt();
			Expression idx = parseExpr();
			accept(RBRACKET);
			x = new ElementSelect(line, column, x, idx);
		}

		return x;
	}

	/**
	 * Exercise 1.4
	 * @return
	 * @throws SyntaxError
	 */
	private Expression parseRecordElementSelect() throws SyntaxError {
		// ::= atom ( ’@’ ID )?

		int sourceLine = currentToken.line;
		int sourceColumn = currentToken.column;

		Expression record = parseAtom();									// atom

		if( currentToken.type == AT ){										// '@'
			acceptIt();
			String elementName = accept(ID);
			record = new RecordElementSelect(sourceLine, sourceColumn, record, elementName );	// ID
		}

		return record;
	}

	private Expression parseAtom() throws SyntaxError {
		int line = currentToken.line;
		int column = currentToken.column;

		switch (currentToken.type) {
		case INTLIT:    return new IntValue(line, column, parseIntLit());
		case FLOATLIT:  return new FloatValue(line, column, parseFloatLit());
		case BOOLLIT:   return new BoolValue(line, column, parseBoolLit());
		case STRINGLIT: return new StringValue(line, column, accept(STRINGLIT));
		default: /* check other cases below */
		}

		if (currentToken.type == ID) {
			String name = accept(ID);
			if (currentToken.type != LPAREN){
				return  new IdentifierReference(line, column, name);

			}else{
				return parseCall(name, line, column);
			}
		}

		if (currentToken.type == LPAREN) {
			acceptIt();
			Expression x = parseExpr();
			accept(RPAREN);
			return x;
		}

		StructureInit s = new StructureInit(line, column);
		if (currentToken.type == AT) {
			acceptIt();
			String name = accept(ID);
			s = new RecordInit(line, column, name);
		}		
		if (currentToken.type == LBRACKET) {
			acceptIt();
			s.addElement(parseExpr());
			while (currentToken.type == COMMA) {
				accept(COMMA);
				s.addElement(parseExpr());
			}
			accept(RBRACKET);
			return s;
		}
		
		throw new SyntaxError(currentToken, INTLIT, FLOATLIT, BOOLLIT, STRINGLIT, ID, LPAREN, LBRACKET, AT);
	}

	private int parseIntLit() throws SyntaxError {
		String s = accept(INTLIT);
		return Integer.parseInt(s);
	}

	private int parseIntLitOrMinusIntLit() throws SyntaxError {
		switch (currentToken.type) {
			case INTLIT: return parseIntLit();
			case SUB:    acceptIt(); return -1 * parseIntLit();
			default:     throw new SyntaxError(currentToken, INTLIT, SUB);
		}
	}

	private float parseFloatLit() throws SyntaxError {
		return Float.parseFloat(accept(FLOATLIT));
	}

	private boolean parseBoolLit() throws SyntaxError {
		return Boolean.parseBoolean(accept(BOOLLIT));
	}
}
