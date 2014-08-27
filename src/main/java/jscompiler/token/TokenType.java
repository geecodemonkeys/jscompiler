package jscompiler.token;

public enum TokenType {
	
	EOF(0, "@EOF@", false, true),
	IDENTIFIER(13, "@identifier@", true, true),
	STRING_CONSTANT(15, "@string@", true, true),
	NUMBER_CONSTANT(16, "@number@", true, true),
	COMMA(17, ","),
	SEMICOLON(18, ";"),
	COLON(19, ":"),
	NULL(23, "null", true),
	RETURN(20, "return", true),
	FUNCTION(21, "function", true),
	NEW(30, "new", true),
	BREAK(31, "break", true),
	CONTINUE(32, "continue", true),
	CASE(33, "case", true),
	CATCH(34, "catch", true),
	DEBUGGER(35, "debugger", true),
	DEFAULT(36, "default", true),
	DELETE(37,"delete", true),
	DO(38, "do", true),
	ELSE(39, "else", true),
	FINALLY(40, "finally", true),
	FOR(41, "for", true),
	IF(42, "if", true),
	IN(43, "in", true),
	INSTANCEOF(44, "instanceof", true),
	SWITCH(45, "switch", true),
	THIS(46, "this", true),
	THROW(47, "throw", true),
	TRY(48, "try", true),
	TYPEOF(27, "typeof", true),
	VAR(14, "var", true),
	VOID(26, "void", true),
	WHILE(49, "while", true),
	WITH(50, "with", true),
	
	//TODO for future use
	//----------------------- FOR FUTURE USE------------
	CLASS(51, "class", true),
	ENUM(52, "enum", true),
	EXPORT(53, "export", true),
	EXTENDS(54, "extends", true),
	IMPORT(55, "import", true),
	SUPER(56, "super", true),
	//----------------------------------------------------
	EQUALS(1, "="),
	RIGHT_PARATHESIS(2, ")"),
	LEFT_PARATHESIS(3, "("),
	MINUS(4, "-"),
	PLUS(5, "+"),
	DIVISION(6, "/"),
	MULTIPLICATION(7, "*"),
	DOT(8, "."),
	LEFT_SQUARE_BRACKET(9, "["),
	RIGHT_SQUARE_BRACKET(10, "]"),
	BLOCK_ENTER_BRACKET(11, "{"),
	BLOCK_EXIT_BRACKET(12, "}"),
	//----------------------------------------------------
	
	MODULUS(57, "%"),
	INCREMENT(58, "++"),
	DECREMENT(59, "--"),
	PLUS_EQUALS(60, "+="),
	MINUS_EQUALS(61, "-="),
	MULTIPLY_EQUALS(62, "*="),
	DIVIDE_EQUALS(63, "/="),
	MODULUS_EQUALS(64, "%="),
	BITWISE_OR_EQUALS(87, "|="),
	BITWISE_AND_EQUALS(88, "&="),
	BITWISE_XOR_EQUALS(89, "^="),
	BITWISE_INVERT_EQUALS(90, "~="),
	
	//----------------------------------------------------
	
	EQUALS_COMPARISON(24, "=="),
	EQUALS_SPECIAL(25, "==="),
	NOT_EQUALS(65, "!="),
	GREATER(66, ">"),
	GREATER_OR_EQ(67, ">="),
	LESS(68, "<"),
	LESS_OR_EQ(69, "<="),
	NOT_EQUALS_SPECIAL(70, "!=="),
	
	//-----------------------------------------------------
	
	UNARY_NEGATE(71, "!"),
	TERNARY_CONDITION(72, "?"),
	FALSE_CONST(73, "false", true),
	TRUE_CONST(74, "true", true),
	OR(75,"||"),
	AND(76, "&&"),
	NAN(77, "NaN", true),
	//UNDEFINED(78, "undefined", true),
	BITWISE_AND(79, "&"),
	BITWISE_OR(80, "|"),
	BITWISE_XOR(81, "^"),
	BITWISE_LEFT_SHIFT(82, "<<"),
	BITWISE_RIGHT_SHIFT(83, ">>"),
	BITWISE_RIGHT_SHIFT_SPECIAL(84, ">>>"),
	BITWISE_INVERT(85 , "~"), 
	ERROR(86, "@error@", false, true),
	EOL(87, "@EOL@", false, true),
	REG_EX(88, "@REGEX@", true, true)
	;
	
	private int code;
	private String asString;
	private boolean wordLike;
	private boolean constantRepresentation;
	TokenType(int code, String asString, boolean wordLike) {
		this.setCode(code);
		this.setAsString(asString);
		this.setWordLike(wordLike);
		this.constantRepresentation = true;
	}
	
	TokenType(int code, String asString, boolean wordLike, boolean variableChars) {
		this.setCode(code);
		this.setAsString(asString);
		this.setWordLike(wordLike);
		this.constantRepresentation = variableChars;
	}
	
	TokenType(int code, String asString) {
		this.setCode(code);
		this.setAsString(asString);
		this.setWordLike(false);
		this.constantRepresentation = true;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getAsString() {
		return asString;
	}
	public void setAsString(String asString) {
		this.asString = asString;
	}
	public boolean isWordLike() {
		return wordLike;
	}
	public void setWordLike(boolean wordLike) {
		this.wordLike = wordLike;
	}

	public boolean isConstantRepresentation() {
		return constantRepresentation;
	}

	public void setConstantRepresentation(boolean variableChars) {
		this.constantRepresentation = variableChars;
	}
}
