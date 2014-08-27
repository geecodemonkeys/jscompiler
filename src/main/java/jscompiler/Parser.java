package jscompiler;

import java.util.ArrayList;
import java.util.List;

import jscompiler.ast.ASTArrayConstant;
import jscompiler.ast.ASTAssignmentExpression;
import jscompiler.ast.ASTBlockStatement;
import jscompiler.ast.ASTBreak;
import jscompiler.ast.ASTCase;
import jscompiler.ast.ASTConstant;
import jscompiler.ast.ASTContinue;
import jscompiler.ast.ASTDoWhile;
import jscompiler.ast.ASTEmthyExpression;
import jscompiler.ast.ASTEmthyStatement;
import jscompiler.ast.ASTExpression;
import jscompiler.ast.ASTFor;
import jscompiler.ast.ASTForIn;
import jscompiler.ast.ASTFunctionCall;
import jscompiler.ast.ASTFunctionDeclaration;
import jscompiler.ast.ASTFunctionExpression;
import jscompiler.ast.ASTIdentifier;
import jscompiler.ast.ASTIfStatement;
import jscompiler.ast.ASTIndexExpression;
import jscompiler.ast.ASTLabel;
import jscompiler.ast.ASTList;
import jscompiler.ast.ASTNameValuePair;
import jscompiler.ast.ASTNewExpression;
import jscompiler.ast.ASTNode;
import jscompiler.ast.ASTObjectConst;
import jscompiler.ast.ASTParenExpression;
import jscompiler.ast.ASTPostfixExpression;
import jscompiler.ast.ASTPropertyAccess;
import jscompiler.ast.ASTRegEx;
import jscompiler.ast.ASTReturn;
import jscompiler.ast.ASTRoot;
import jscompiler.ast.ASTStatement;
import jscompiler.ast.ASTSwitch;
import jscompiler.ast.ASTTernaryCondition;
import jscompiler.ast.ASTThrow;
import jscompiler.ast.ASTTry;
import jscompiler.ast.ASTTwoOperandExpression;
import jscompiler.ast.ASTUnaryExpression;
import jscompiler.ast.ASTVariableDeclaration;
import jscompiler.ast.ASTWhile;
import jscompiler.ast.ASTWith;
import jscompiler.token.Token;
import jscompiler.token.TokenType;


//TODO fix the tokenizer to accept unicode chars
//TODO fix the lexer to escape \\ in a String const
//TODO fix add all punctuator .. and ... and >>>= e.t.c.
//TODO add the handling of \u0000 chars in the regex string and identifier
//FIXME does not recognize numbers like .0 test in jquery prod
//FIXME function statements handlet as a function expressions
public class Parser {
	private Tokenizer tokenizer;
	//private Stack<Scope> scopes = null;
	public Parser(String input) {
		tokenizer = new TokenizerImpl(input);
		//scopes = new Stack<Scope>();
	}
	
	private Token next() {
		currToken = tokenizer.next();
		return currToken;
	}
	
	private Token peek() {
		return tokenizer.peek();
	}
	
	private Token currToken;
	
	private boolean acceptToken(TokenType type) {
		if (type == currToken.getType()) {
			next();
			return true;
		}
		return false;
	}
	
	private boolean expectToken(TokenType expectedToken) {
		if (!acceptToken(expectedToken)) {
			throw new RuntimeException(expectedToken + " " +
					"excpected. Found " + currToken.getType() + 
					" at index " + currToken.getTokenStartIndex() 
					+ " at line " + currToken.getLineNumber());
		}
		return true;
		
	}
	
	
	private boolean isPrevTokenEOL() {
		int currIndex = tokenizer.getTokenIdx();
		if (currIndex == 0) {
			return false;
		}
		Token previous = tokenizer.getAtIndex(currIndex - 1);
		if (previous.getType() == TokenType.EOL) {
			return true;
		}
		return false;
	}
	
	/**
	 * Uses the rules for automatic semicolon insertion
	 * http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-262.pdf
	 * in 7.9.1
	 */
	private void expectSemiOrEOL() {
		
		if (currToken.getType() == TokenType.SEMICOLON) {
			next();
			return;
		}
		//7.9.1 point 2 in the ecma script 262 5-th version
		//http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-262.pdf
		if (currToken.getType() == TokenType.BLOCK_EXIT_BRACKET ||
				currToken.getType() == TokenType.EOF) {
			return;
		}
		if (isPrevTokenEOL()) {
			return;
		}
		
		throw new RuntimeException("Excpected EOL or semicolon.Found " + currToken.getType() + 
				" at index " + currToken.getTokenStartIndex() 
				+ " at line " + currToken.getLineNumber());
	}
	
	public ASTRoot parse() {
		//Scope globalScope = new Scope(null);
		//scopes.push(globalScope);
		
		next();
		List<ASTNode> nodes = new ArrayList<ASTNode>();
		while(currToken.getType() != TokenType.EOF) {
			if (currToken.getType() == TokenType.FUNCTION) {
				ASTFunctionDeclaration function = functionDeclaration();
				nodes.add(function);
			} else {			
				ASTStatement statement = statement();
				if (statement != null) {
					nodes.add(statement);	
				}
				//only experimental
				else break;
			}
		}
		return new ASTRoot(nodes);
		
	}

	private ASTStatement statement() {
		switch (currToken.getType()) {
			case BLOCK_ENTER_BRACKET :
				return blockStatement();
			case VAR:
				return variableStatement();
			case SEMICOLON :
				return emthyStatement();
			case IF:
				return ifStatement();
			case WHILE:
				return whileStatement();
			case DO:
				return doWhileStatement();
			case FOR:
				return forStatement();
			case CONTINUE:
				return continueStatement();
			case BREAK:
				return breakStatement();
			case RETURN:
				return returnStatement();
			case WITH:
				return withStatement();
			case SWITCH:
				return switchStatement();
			case THROW:
				return throwStatement();
			case TRY:
				return tryStatement();
			case IDENTIFIER:
				Token peekedToken = peek();
				if (peekedToken.getType() == TokenType.COLON) {
					//the label statement begins with
					// <identifier> : 
					return labelStatement();
				}
				//the expression statement can begin with identifier
				//so we need to two tokens lookahead
				return expressionStatement();
			default:
				return expressionStatement();
		
		}
	}

	
	

	private ASTFunctionDeclaration functionDeclaration() {
		expectToken(TokenType.FUNCTION);
		Token functionName = currToken;
		expectToken(TokenType.IDENTIFIER);
		
		List<ASTIdentifier> arguments = parameterList();
		List<ASTNode> bodyStatements = functionBody();
		
		ASTFunctionDeclaration function = new ASTFunctionDeclaration(new ASTIdentifier(functionName.getValue()),
				arguments, bodyStatements);
		
		return function;
	}

	private List<ASTNode> functionBody() {
		expectToken(TokenType.BLOCK_ENTER_BRACKET);
		
		List<ASTNode> nodes = new ArrayList<ASTNode>();
		boolean isRunning = true;
		while(isRunning && currToken.getType() != TokenType.EOF) {
			switch (currToken.getType()) {
				case BLOCK_EXIT_BRACKET:
					isRunning = false;
					break;
				case FUNCTION:
					ASTFunctionDeclaration function = functionDeclaration();
					nodes.add(function);
					break;
				default:
					ASTStatement statement = statement();
					if (statement != null) {
						nodes.add(statement);	
					}
					break;
			}
		}
		
		expectToken(TokenType.BLOCK_EXIT_BRACKET);
		return nodes;
	}

	private List<ASTIdentifier> parameterList() {
		expectToken(TokenType.LEFT_PARATHESIS);
		List<ASTIdentifier> arguments = new ArrayList<ASTIdentifier>();
		if (!acceptToken(TokenType.RIGHT_PARATHESIS)) {
			do {
				Token token = currToken;
	            expectToken(TokenType.IDENTIFIER);
	            ASTIdentifier identifier = new ASTIdentifier(token.getValue());
				arguments.add(identifier);
	        } while (acceptToken(TokenType.COMMA));
			expectToken(TokenType.RIGHT_PARATHESIS);
		}
		return arguments;
	}

	private ASTStatement blockStatement() {
		expectToken(TokenType.BLOCK_ENTER_BRACKET);
		List<ASTStatement> statements = statements();
		expectToken(TokenType.BLOCK_EXIT_BRACKET);
		ASTBlockStatement block = new ASTBlockStatement(statements);
		return block;
	}
	
	
	private List<ASTStatement> statements() {
		List<ASTStatement> statements = new ArrayList<ASTStatement>();
		while (currToken.getType() != TokenType.EOF &&
				currToken.getType() != TokenType.BLOCK_EXIT_BRACKET &&
				currToken.getType() != TokenType.CASE && 
				currToken.getType() != TokenType.DEFAULT) {
			ASTStatement statement = statement();
			statements.add(statement);
		}
		return statements;
	}
	
	private ASTStatement doWhileStatement() {
		expectToken(TokenType.DO);
		ASTStatement statement = statement();
		expectToken(TokenType.WHILE);
		expectToken(TokenType.LEFT_PARATHESIS);
		ASTExpression expression = expression(true);
		expectToken(TokenType.RIGHT_PARATHESIS);
		expectSemiOrEOL();
		
		ASTDoWhile doWhile = new ASTDoWhile(expression, statement);
		return doWhile;
	}
	
	private ASTStatement ifStatement() {
		expectToken(TokenType.IF);
		expectToken(TokenType.LEFT_PARATHESIS);
		ASTExpression contidion = expression(true);
		expectToken(TokenType.RIGHT_PARATHESIS);
		ASTStatement ifBody = statement();
		ASTStatement elseBody = null;
		if (acceptToken(TokenType.ELSE)) {
			elseBody = statement();
		}
		ASTIfStatement ifStatement = new ASTIfStatement(contidion, ifBody, elseBody);
		return ifStatement;
	}

	//must not eat the last ) because the callers need it
	//must not eat the last ] because the callers need it
	private ASTExpression expression(boolean includeIn) {
		ASTExpression left = assignmentExpression(includeIn);
		while (acceptToken(TokenType.COMMA)) {
			ASTNode right = assignmentExpression(includeIn);
			left = new ASTTwoOperandExpression(TokenType.COMMA, left, right);
		}
		return left;
	}

	//must not eat the last ) cause callers need it
	private ASTExpression assignmentExpression(boolean includeIn) {
		ASTExpression leftHandSide = conditionalExpression(includeIn);
		Token token = currToken;
		switch (currToken.getType()) {
			case MULTIPLY_EQUALS:
				System.out.print("");
			case DIVIDE_EQUALS:
				System.out.print("");
			case MODULUS_EQUALS:
				System.out.print("");
			case PLUS_EQUALS:
				System.out.print("");
			case MINUS_EQUALS:
				System.out.print("");
			case BITWISE_AND_EQUALS:
				System.out.print("");
			case BITWISE_OR_EQUALS:
				System.out.print("");
			case BITWISE_XOR_EQUALS:
				System.out.print("");
			case BITWISE_INVERT_EQUALS:
				System.out.print("");
			case EQUALS:
				//FIXME missing <<=, >>= >>>=
				next();
				ASTExpression rightHandSide = assignmentExpression(includeIn);
				ASTAssignmentExpression expr = 
						new ASTAssignmentExpression(token.getType(), 
								leftHandSide, rightHandSide);
				return expr;
			default:
				return leftHandSide;
		}
		
	}

	private ASTExpression conditionalExpression(boolean includeIn) {
		ASTExpression or = logicalORExpression(includeIn);
		if (acceptToken(TokenType.TERNARY_CONDITION)) {
			ASTExpression trueExpression = assignmentExpression(includeIn);
			expectToken(TokenType.COLON);
			ASTExpression falseExpression = assignmentExpression(includeIn);
			ASTTernaryCondition ternary = new ASTTernaryCondition(
					or, trueExpression, falseExpression);
			return ternary;
		}
		return or;
	}

	private ASTExpression logicalORExpression(boolean includeIn) {
		ASTExpression left = logicalANDExpression(includeIn);
		while (acceptToken(TokenType.OR)) {
			ASTExpression right = logicalANDExpression(includeIn);
			left = new ASTTwoOperandExpression(TokenType.OR, left, right);
		}
		return left;
	}

	private ASTExpression logicalANDExpression(boolean includeIn) {
		ASTExpression left = bitwiseORExpression(includeIn);
		while (acceptToken(TokenType.AND)) {
			ASTExpression right = bitwiseORExpression(includeIn);
			left = new ASTTwoOperandExpression(TokenType.AND, left, right);
		}
		return left;
	}

	private ASTExpression bitwiseORExpression(boolean includeIn) {
		ASTExpression left = bitwiseXORExpression(includeIn);
		while (acceptToken(TokenType.BITWISE_OR)) {
			ASTExpression right = bitwiseXORExpression(includeIn);
			left = new ASTTwoOperandExpression(TokenType.BITWISE_OR, left, right);
		}
		return left;
	}

	private ASTExpression bitwiseXORExpression(boolean includeIn) {
		ASTExpression left = bitwiseANDExpression(includeIn);
		while (acceptToken(TokenType.BITWISE_XOR)) {
			ASTExpression right = bitwiseANDExpression(includeIn);
			left = new ASTTwoOperandExpression(TokenType.BITWISE_XOR, left, right);
		}
		return left;
	}

	private ASTExpression bitwiseANDExpression(boolean includeIn) {
		ASTExpression left = equalityExpression(includeIn);
		while (acceptToken(TokenType.BITWISE_AND)) {
			ASTExpression right = equalityExpression(includeIn);
			left = new ASTTwoOperandExpression(TokenType.BITWISE_AND, left, right);
		}
		return left;
	}

	private ASTExpression equalityExpression(boolean includeIn) {
		ASTExpression left = relationalExpression(includeIn);
		boolean isRunning = true;
		while (isRunning) {
			switch (currToken.getType()) {
				case EQUALS_COMPARISON:
					System.out.print("");
				case NOT_EQUALS:
					System.out.print("");
				case NOT_EQUALS_SPECIAL:
					System.out.print("");
				case EQUALS_SPECIAL:
					TokenType opType = currToken.getType();
					next();
					ASTExpression right = relationalExpression(includeIn);
					left = new ASTTwoOperandExpression(opType, 
							left, right);
					break;
				default:
					isRunning = false;
			}
		}
		return left;
	}

	private ASTExpression relationalExpression(boolean includeIn) {
		ASTExpression left = shiftExpression();
		boolean isRunning = true;
		while (isRunning) {
			switch (currToken.getType()) {
				case IN:
					if (!includeIn) {
						//when we not allow in the expression
						isRunning = false;
						break;
					}
					System.out.print("");
				case INSTANCEOF:
					System.out.print("");
				case GREATER:
					System.out.print("");
				case GREATER_OR_EQ:
					System.out.print("");
				case LESS:
					System.out.print("");
				case LESS_OR_EQ:
					TokenType opType = currToken.getType();
					next();
					ASTExpression right = shiftExpression();
					left = new ASTTwoOperandExpression(opType, 
							left, right);
					break;
				default:
					isRunning = false;
			}
		}
		return left;
	}

	private ASTExpression shiftExpression() {
		ASTExpression left = addExpression();
		boolean isRunning = true;
		while (isRunning) {
			switch (currToken.getType()) {
				case BITWISE_LEFT_SHIFT:
					System.out.print("");
				case BITWISE_RIGHT_SHIFT:
					System.out.print("");
				case BITWISE_RIGHT_SHIFT_SPECIAL:
					TokenType opType = currToken.getType();
					next();
					ASTExpression right = addExpression();
					left = new ASTTwoOperandExpression(opType, 
							left, right);
					break;
				default:
					isRunning = false;
			}
		}
		return left;
	}

	private ASTExpression addExpression() {
		ASTExpression left = multiplyExpression();
		boolean isRunning = true;
		while (isRunning) {
			switch (currToken.getType()) {
				case PLUS:
					System.out.print("");
				case MINUS:
					TokenType opType = currToken.getType();
					next();
					ASTExpression right = multiplyExpression();
					left = new ASTTwoOperandExpression(opType, 
							left, right);
					break;
				default:
					isRunning = false;
			}
		}
		return left;
	}

	private ASTExpression multiplyExpression() {
		ASTExpression left = unaryExpression();
		boolean isRunning = true;
		while (isRunning) {
			switch (currToken.getType()) {
				case MODULUS:
					System.out.print("");
				case MULTIPLICATION:
					System.out.print("");
				case DIVISION:
					TokenType opType = currToken.getType();
					next();
					ASTExpression right = unaryExpression();
					left = new ASTTwoOperandExpression(opType, 
							left, right);
					break;
				default:
					isRunning = false;
			}
		}
		return left;
	}

	private ASTExpression unaryExpression() {
		ASTExpression expr = null;
		TokenType opType = null;
		ASTExpression exprToReturn = null;
		switch (currToken.getType()) {
			case DELETE:
				opType = currToken.getType();
				next();
				expr = memberExpression(true);
				exprToReturn = new ASTUnaryExpression(opType, expr);
				return exprToReturn;
			case VOID:
				System.out.print("");
			case TYPEOF:
				System.out.print("");
			case UNARY_NEGATE:
				System.out.print("");
			case BITWISE_INVERT:
				opType = currToken.getType();
				next();
				expr = unaryExpression();
				exprToReturn = new ASTUnaryExpression(opType, expr);
				return exprToReturn;
			case INCREMENT:
				System.out.print("");
			case DECREMENT:
				opType = currToken.getType();
				next();
				ASTExpression memberExpr = memberExpression(true);
				ASTUnaryExpression unaryExpr = new ASTUnaryExpression(
						opType, memberExpr);
				return unaryExpr;
			case PLUS:
				System.out.print("");
			case MINUS:
				opType = currToken.getType();
				next();
				expr = unaryExpression();
				exprToReturn = new ASTUnaryExpression(
						opType, expr);
				return exprToReturn;
			default:
				//TODO think if we might cancel this if call
				//if there is some tokenType
				ASTExpression member = memberExpression(true);
				TokenType type = currToken.getType();
				if (!isPrevTokenEOL() && (
						type == TokenType.INCREMENT || 
						type == TokenType.DECREMENT)) {
					next();
					ASTPostfixExpression unary3 = new ASTPostfixExpression(
							type, member);
					return unary3;
				}
				return member;
		}
	}

	private ASTIdentifier propertyRefExpression() {
		expectToken(TokenType.DOT);
		Token token = currToken;
		expectToken(TokenType.IDENTIFIER);
		return new ASTIdentifier(token.getValue());
	}

	private ASTExpression index() {
		expectToken(TokenType.LEFT_SQUARE_BRACKET);
		ASTExpression expression = expression(true);
		expectToken(TokenType.RIGHT_SQUARE_BRACKET);
		return expression;
	}

	private List<ASTExpression> arguments() {
		expectToken(TokenType.LEFT_PARATHESIS);
		List<ASTExpression> arguments = new ArrayList<ASTExpression>();
		if (currToken.getType() == TokenType.RIGHT_PARATHESIS) {
			next();
			return arguments;
		}
		do {
			ASTExpression argumentExpression = assignmentExpression(true);
			arguments.add(argumentExpression);
		} while (acceptToken(TokenType.COMMA));
		
		expectToken(TokenType.RIGHT_PARATHESIS);
		return arguments;
	}

	private ASTExpression memberExpression(boolean eatFunction) {
		ASTExpression expr = null;
		switch (currToken.getType()) {
			case FUNCTION:
				expr = functionExpression();
				break;
			case NEW:
				next();
				ASTExpression memberExpr = memberExpression(false);
				expr = new ASTNewExpression(memberExpr,
						null);
				if (currToken.getType() == TokenType.LEFT_PARATHESIS) {
					List<ASTExpression> arguments = arguments();
					((ASTNewExpression)expr).setArguments(arguments);
				}
				break;
			case DIVISION:
			case DIVIDE_EQUALS:
			case THIS:
			case IDENTIFIER:
			case NULL:
			case TRUE_CONST:
			case FALSE_CONST:
			case NUMBER_CONSTANT:
			case STRING_CONSTANT:
			//for array constant
			case LEFT_SQUARE_BRACKET:
			//for object constant
			case BLOCK_ENTER_BRACKET:
			case LEFT_PARATHESIS:
				expr = primaryExpression();
				break;
			default:
				throw new RuntimeException("Bad syntax at line " 
						+ currToken.getLineNumber() 
						+ " at index " + currToken.getTokenStartIndex()); 
		}
		
		//accept any number of ()()()() or [][][] or this.g.g.b.v
		boolean isRunning = true;
		while (isRunning) {
			switch (currToken.getType()) {
				case LEFT_SQUARE_BRACKET:
					ASTExpression indexExpr = index();
					expr = new ASTIndexExpression(expr, indexExpr);
					break;
				case LEFT_PARATHESIS:
					if (!eatFunction) {
						isRunning = false;
						break;
					}
					List<ASTExpression> arguments = arguments();
					expr = new ASTFunctionCall(expr,
							arguments);
					break;
				case DOT:
					ASTIdentifier refExpr = propertyRefExpression();
					expr = new ASTPropertyAccess(expr, refExpr);
					break;
				default:
					isRunning = false;
					break;
			}
		}
		return expr;
	}

	private ASTExpression functionExpression() {
		expectToken(TokenType.FUNCTION);
		ASTIdentifier name = null;
		Token token = currToken;
		if (acceptToken(TokenType.IDENTIFIER)) {
			name = new ASTIdentifier(token.getValue());
		}
		
		List<ASTIdentifier> arguments = parameterList();

		//Scope scope = new Scope(scopes.peek());
		//scopes.push(scope);
		
		List<ASTNode> body = functionBody();
		//scopes.pop();
		ASTExpression funcExpression = new ASTFunctionExpression(name, arguments, body);
		//funcExpression.setScope(scope);
		return funcExpression;
	}

	private ASTExpression primaryExpression() {
		switch (currToken.getType()) {
			case IDENTIFIER:
				Token token = currToken;
				next();
				return new ASTIdentifier(token.getValue());
			case THIS:
				token = currToken;
				next();
				return new ASTIdentifier("this");
			case NULL:
			case TRUE_CONST:
			case FALSE_CONST:
			case STRING_CONSTANT:
			case NUMBER_CONSTANT:
				token = currToken;
				next();
				return new ASTConstant(token.getType(), token.getValue());
			case LEFT_SQUARE_BRACKET:
				next();
				List<ASTExpression> list = new ArrayList<ASTExpression>();
				if (!acceptToken(TokenType.RIGHT_SQUARE_BRACKET)) {
					do {
						ASTExpression assignment = assignmentExpression(true);
						list.add(assignment);
					} while (acceptToken(TokenType.COMMA));
					expectToken(TokenType.RIGHT_SQUARE_BRACKET);
				}
				return new ASTArrayConstant(list);
			case LEFT_PARATHESIS:
				next();
				ASTExpression expression = expression(true);
				ASTParenExpression parenExpression = new ASTParenExpression(expression);
				expectToken(TokenType.RIGHT_PARATHESIS);
				return parenExpression;
			case BLOCK_ENTER_BRACKET:
				ASTExpression objectLiteral = objectLiteral();
				return objectLiteral;
			case DIVISION:
			case DIVIDE_EQUALS:
				//regex constant
				token = tokenizer.eatRegExConst();
				next();
				return new ASTRegEx((String[]) token.getObjectValue());
				
		default:
			throw new RuntimeException("Unexpected Token " + 
					currToken.getType() 
					+ " value " + currToken.getValue());
		}
	}

	private ASTExpression objectLiteral() {
		expectToken(TokenType.BLOCK_ENTER_BRACKET);
		List<ASTNameValuePair> pairList = new ArrayList<ASTNameValuePair>();
		if (acceptToken(TokenType.BLOCK_EXIT_BRACKET)) {
			return new ASTObjectConst(pairList);
		}
		do {
			Token token = currToken;
			ASTExpression assignmentExpression = null;
			ASTIdentifier id = null;
			ASTConstant constant = null;
			switch (currToken.getType()) {
				case IDENTIFIER:
					next();
					id = new ASTIdentifier(token.getValue());
					break;
				case NUMBER_CONSTANT:
					next();
					constant = new ASTConstant(TokenType.NUMBER_CONSTANT, 
							token.getValue());
					break;
				case STRING_CONSTANT:
					next();
					constant = new ASTConstant(TokenType.STRING_CONSTANT, 
							token.getValue());
					break;
				default:
					throw new RuntimeException("Unexpected token " + 
							currToken.getValue() + " at index " + 
							currToken.getTokenStartIndex() + 
							" at line " + currToken.getLineNumber() + 
							". Expected number, string or identifier");
			}
			expectToken(TokenType.COLON);
			assignmentExpression = assignmentExpression(true);
			pairList.add(new ASTNameValuePair(id, constant, assignmentExpression));
		} while (acceptToken(TokenType.COMMA));

		expectToken(TokenType.BLOCK_EXIT_BRACKET);
		return new ASTObjectConst(pairList);
	}

	private ASTStatement emthyStatement() {
		expectToken(TokenType.SEMICOLON);
		return new ASTEmthyStatement();
	}

	private ASTStatement variableStatement() {
		ASTStatement list = variableDeclaration(true);
		expectSemiOrEOL();
		
		return list;
	}
	
	private ASTStatement variableDeclaration(boolean includeIn) {
		expectToken(TokenType.VAR);
		List<ASTVariableDeclaration> list = 
				new ArrayList<ASTVariableDeclaration>();
		do {			
			Token token = currToken;
			expectToken(TokenType.IDENTIFIER);
			ASTExpression assignement = null;
			if (acceptToken(TokenType.EQUALS)) {
				assignement = assignmentExpression(includeIn); 
			}
			ASTVariableDeclaration declaration = 
					new ASTVariableDeclaration(
							new ASTIdentifier(token.getValue()),
							assignement);
			list.add(declaration);
		} while (acceptToken(TokenType.COMMA));
		
		return new ASTList(list);
	}


	private ASTStatement labelStatement() {
		Token id = currToken;
		expectToken(TokenType.IDENTIFIER);
		expectToken(TokenType.COLON);
		ASTStatement statement = statement();
		ASTLabel label = new ASTLabel(
				new ASTIdentifier(id.getValue()), statement);
		return label;
	}

	private ASTStatement tryStatement() {
		expectToken(TokenType.TRY);
		ASTStatement block = blockStatement();
		ASTStatement finallyBlock = null;
		ASTStatement catchBlock = null;
		ASTIdentifier catchIdentifier = null;
		if (acceptToken(TokenType.FINALLY)) {
			finallyBlock = blockStatement();
		} else if (acceptToken(TokenType.CATCH)) {
			expectToken(TokenType.LEFT_PARATHESIS);
			Token idToken = currToken;
			expectToken(TokenType.IDENTIFIER);
			catchIdentifier = new ASTIdentifier(idToken.getValue());
			expectToken(TokenType.RIGHT_PARATHESIS);
			catchBlock = blockStatement();
			if (acceptToken(TokenType.FINALLY)) {
				finallyBlock = blockStatement();
			}
		} else {
			throw new RuntimeException("expected catch or finally found " + 
					currToken + " at index " + currToken.getTokenStartIndex() + 
					" at line " + currToken.getLineNumber() );
		}
		ASTTry tryAst = new ASTTry(block, catchIdentifier, 
				catchBlock, finallyBlock);
		return tryAst;
	}

	private ASTStatement throwStatement() {
		expectToken(TokenType.THROW);
		ASTExpression expression = new ASTEmthyExpression();
		if (!isPrevTokenEOL()) {			
			expression = expression(true);
		}
		expectSemiOrEOL();
		ASTThrow throwAst = new ASTThrow(expression);
		return throwAst;
	}

	private ASTStatement switchStatement() {
		expectToken(TokenType.SWITCH);
		expectToken(TokenType.LEFT_PARATHESIS);
		ASTExpression switchVar = expression(true);
		expectToken(TokenType.RIGHT_PARATHESIS);
		expectToken(TokenType.BLOCK_ENTER_BRACKET);
		if (acceptToken(TokenType.BLOCK_EXIT_BRACKET)) {
			return new ASTSwitch(switchVar, new ArrayList<ASTCase>());
		}
		int numberOfDefaults = 0;
		List<ASTCase> cases = new ArrayList<ASTCase>();
		do {
			if (acceptToken(TokenType.CASE)) {
				ASTExpression expression = expression(true);
				expectToken(TokenType.COLON);
				List<ASTStatement> statements = 
						new ArrayList<ASTStatement>();
				switch (currToken.getType()) {
					case CASE:
					case DEFAULT:
					case BLOCK_EXIT_BRACKET:
						break;
					default:
						statements = statements();
						break;
				}
				cases.add(new ASTCase(expression, statements));
			} else if (acceptToken(TokenType.DEFAULT)) {
				numberOfDefaults++;
				if (numberOfDefaults > 1) {
					throw new RuntimeException("There cannot be two " +
							"defaults in a switch at index" + 
							currToken.getTokenStartIndex() +
							" at line " + currToken.getLineNumber());
				}
				expectToken(TokenType.COLON);
				List<ASTStatement> statements = 
						new ArrayList<ASTStatement>();
				switch (currToken.getType()) {
					case DEFAULT:
						throw new RuntimeException("There cannot be two " +
								"defaults in a switch at index" +
								currToken.getTokenStartIndex() 
								+ " at line " + currToken.getLineNumber() );
					case CASE:
					case BLOCK_EXIT_BRACKET:
						break;
					default:
						statements = statements();
						break;
				}
				cases.add(new ASTCase(statements, true));
			} else {
				throw new RuntimeException("Only case and default are " +
						"allowed in a switch at index "+ 
						currToken.getTokenStartIndex() + 
						" at line " + currToken.getLineNumber() );
			}
		} while (currToken.getType() == TokenType.CASE || 
				currToken.getType() == TokenType.DEFAULT);
		expectToken(TokenType.BLOCK_EXIT_BRACKET);
		return new ASTSwitch(switchVar, cases);
	}

	private ASTStatement withStatement() {
		expectToken(TokenType.WITH);
		expectToken(TokenType.LEFT_PARATHESIS);
		ASTExpression expression = expression(true);
		expectToken(TokenType.RIGHT_PARATHESIS);
		ASTStatement body = statement();
		ASTWith whileAST = new ASTWith(expression, body);
		return whileAST;
	}

	private ASTStatement returnStatement() {
		expectToken(TokenType.RETURN);
		ASTExpression expression = null;
		if (currToken.getType() != TokenType.SEMICOLON &&
				!isPrevTokenEOL()) {
			expression = expression(true);
		}
		expectSemiOrEOL();
		ASTReturn returnAST = new ASTReturn(expression);
		return returnAST;
	}

	private ASTStatement breakStatement() {
		expectToken(TokenType.BREAK);
		ASTIdentifier id = null;
		Token token = currToken;
		if (!isPrevTokenEOL()) {			
			if (acceptToken(TokenType.IDENTIFIER)) {
				id = new ASTIdentifier(token.getValue());
			}
		}
		expectSemiOrEOL();
		ASTBreak astBreak = new ASTBreak(id);
		return astBreak;
	}

	private ASTStatement continueStatement() {
		expectToken(TokenType.CONTINUE);
		ASTIdentifier id = null;
		Token token = currToken;
		if (!isPrevTokenEOL()) {			
			if (acceptToken(TokenType.IDENTIFIER)) {
				id = new ASTIdentifier(token.getValue());
			}
		}
		expectSemiOrEOL();
		ASTContinue astContinue = new ASTContinue(id);
		return astContinue;
	}

	private ASTStatement forStatement() {
		expectToken(TokenType.FOR);
		expectToken(TokenType.LEFT_PARATHESIS);
		ASTStatement firstStatement = null;
		ASTExpression firstExpression = null;
		if (currToken.getType() != TokenType.SEMICOLON) {
			if (currToken.getType() == TokenType.VAR) {
				firstStatement = variableDeclaration(false);
			} else {
				firstExpression = expression(false);
			}
		}
		
		boolean isForIn = false;
		ASTExpression condition = null;
		ASTExpression increment = null;
		ASTExpression inIteratorExpression = null;
		if (currToken.getType() == TokenType.IN) {
			isForIn = true;
			next();
			inIteratorExpression = expression(true);
		} else if (currToken.getType() == TokenType.SEMICOLON) {
			next();
			if (currToken.getType() != TokenType.SEMICOLON) {
				condition = expression(true);
			} else {
				condition = new ASTEmthyExpression();
			}
			expectToken(TokenType.SEMICOLON);
			if (currToken.getType() != TokenType.RIGHT_PARATHESIS) {
				increment = expression(true);
			} else {
				increment = new ASTEmthyExpression();
			}
		} else {
			//error
		}
		expectToken(TokenType.RIGHT_PARATHESIS);
		
		ASTStatement statement = statement();
		
		ASTNode initializer = 
				firstStatement != null ? firstStatement : firstExpression;
		if (isForIn) {
			if (firstStatement != null) {
				ASTList list = ((ASTList) firstStatement);
				if (list.getList().size() != 1) {
					throw new RuntimeException(
							"Only one var is allowed! " + 
									currToken.getTokenStartIndex() + 
									" at line " + currToken.getLineNumber());
				}
			}
			return new ASTForIn(initializer, 
					inIteratorExpression, statement);
		}
		
		return new ASTFor(initializer != null ? 
				initializer : new ASTEmthyExpression(), 
				condition, increment, statement);
	}

	private ASTStatement whileStatement() {
		expectToken(TokenType.WHILE);
		expectToken(TokenType.LEFT_PARATHESIS);
		ASTExpression expression = expression(true);
		expectToken(TokenType.RIGHT_PARATHESIS);
		ASTStatement body = statement();
		ASTWhile whileAST = new ASTWhile(expression, body);
		return whileAST;
	}

	private ASTExpression expressionStatement() {
		ASTExpression expression = expression(true);
		expectSemiOrEOL();
		return expression;
	}
	
}
