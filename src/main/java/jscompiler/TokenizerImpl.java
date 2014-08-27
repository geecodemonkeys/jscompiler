package jscompiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jscompiler.token.Token;
import jscompiler.token.TokenType;

public class TokenizerImpl extends Tokenizer {
	
	private char[] chars = null;
	private int currMarker = 0;
	Map<String, TokenType> keyWordsCache = null;
	int currBuffIndex = 0;
	List<Token> tokenList = new ArrayList<Token>();
	private int currTokenIndex = -1;
	private int currLineNumber = 1;
	private int currLineIndex;
	
	public TokenizerImpl(String program) {
		super(program);
		chars = program.toCharArray();
		keyWordsCache = new HashMap<String, TokenType>();
		for (TokenType type : TokenType.values()) {
			if (type.isWordLike() && type.isConstantRepresentation()) {
				keyWordsCache.put(type.getAsString(), type);
			}
		}
	}
	
	public Token next() {
		//currTokenIndex holds the index of the current index at which we are in the 
		//list already parsed tokens. This is needed because of the special meaning 
		//of the End of Line (EOL) in javascrips Automatic semicolon insertion.
		if (tokenList.size() - 1 > currTokenIndex) {
			currTokenIndex++;
			return tokenList.get(currTokenIndex);
		}
		
		//used for reporting the lines in an error message.
		int tokenStartIndex = currMarker;
		Token token = null;
		
		//we need to find the first Token which is not EOL cause in most
		//places in the parsing we need to skip the EOL's nomater how many they
		//are. But we save the EOL because of the Automatic semicolon insertion.
		do {
			token = nextInternall();
			tokenList.add(token);
			currTokenIndex++;
		} while (token.getType() == TokenType.EOL);
		token.setStartIndex(tokenStartIndex - currLineIndex);
		token.setLineNumber(currLineNumber);
		return token;
	}
	
	@Override
	public Token nextOrEOL() {
		int tokenStartIndex = currMarker;
		Token token = nextInternall();
		token.setStartIndex(tokenStartIndex - currLineIndex);
		token.setLineNumber(currLineNumber);
		if (token.getType() != TokenType.EOL) {			
			tokenList.add(token);
			currTokenIndex++;
		}
		return token;
	}
	/*
	 * if (tokenList.size() - 1 > currTokenIndex) {
			return tokenList.get(currTokenIndex + 1);
		}
	 */
	@Override
	public Token peek() {
		if (tokenList.size() - 1 > currTokenIndex) {
			return tokenList.get(tokenList.size() - 1);
		}
		
		Token token = nextInternall();
		tokenList.add(token);
		return token;
	}
	
	private Token nextInternall() {
		//currMarker is the global index in the global array chars holding
		//the whole program.
		if (currMarker >= chars.length) {
			//we have reached the end of the output and report this as
			//an End of File (EOF) token. It has a very important role in the parsing also
			//EOF has very important part in Automatic semicolon insertion.
			return new Token(TokenType.EOF);
		}
		
		//we move the currMarker until we find something which is not a comment or
		//whitespace. In javascript these <link> symbols are whitespaces.
		//we keep the token and return it if it as an EOL all other whitespaces are skipped.
		Token eol = eatWhitespaceAndComments();
		if (eol != null) {
			return eol;
		}
		
		//After removing all whitespaces we can have hit the EOF
		if (currMarker >= chars.length) {
			return new Token(TokenType.EOF);
		}
		char c = chars[currMarker];
		
		//we choose what token we have based on the first character. This
		//holds true except for the regex constant (/some/g) which is the most tricky part.
		if (isJavaScriptIdentifierStart(c)) {
			//handles all identifiers and keywords.
			Token token = eatWordLikeToken();
			return token;
		} else {
			if (Character.isDigit(c)) {
				//handles the numbers
				Token token = eatNumberConst();
				return token;
			} else {
				if (c == '"' || c == '\'') {
					//string constants
					Token token = eatStringConst();
					return token;
				}
				//all other symbols like + - /* %= === etc.
				Token token = eatSpecialCharacterToken();
				return token;
			}
		}
	}
	
	private boolean isLineTerminator(char ch) {
		return ch == '\u2028' || ch == '\u2029' || ch == '\n' || ch == '\r';
	}

	private Token eatStringConst() {
		char c = currChar();
		int begin = currMarker + 1;
		//"string 'const'"
		char startEndCh = '"';
		if (c == '\'') {
			//'string consts ""'
			startEndCh = '\'';
		}
		currMarker++;
		TokenType type = TokenType.STRING_CONSTANT;
		int symbolsMoveAhead = 0;
		boolean escapeNext = false;
		for (;currMarker < chars.length; currMarker++) {
			c = chars[currMarker];
			
			//make the same in the string const
			if (escapeNext) {
				escapeNext = false;
				continue;
			} else {
				if (c == '\\') {
					escapeNext = true;
					continue;
				}
				if (c == startEndCh) {
					symbolsMoveAhead = 1;
					break;
				}
			}
			
			if ( c == '\r' && nextChar() == '\n') {
				type = TokenType.ERROR;
				symbolsMoveAhead = 2;
				currLineNumber++;
				currLineIndex = currMarker;
				break;
			}
			if (isLineTerminator(c)) {
				type = TokenType.ERROR;
				symbolsMoveAhead = 1;
				currLineNumber++;
				currLineIndex = currMarker;
				break;
			}
		}
		int end = currMarker - 1;
		char[] token = new char[end - begin + 1];
		System.arraycopy(chars, begin, token, 0, token.length);
		String tokenAsString = new String(token);
		currMarker += symbolsMoveAhead;
		return new Token(type, tokenAsString);
	}

	private char prevChar() {
		if (currMarker - 1 < 0) {
			return (char) -1;
		}
		return chars[currMarker - 1];
	}

	private boolean isJavaScriptIdentifierStart(char ch) {
		return Character.isLetter(ch) || ch == '_' || ch == '$';
	}
	
	private boolean isJavaScriptIdentifierPart(char ch) {
		return Character.isLetter(ch) || 
				Character.isDigit(ch) || ch == '_' || ch == '$';
	}

	private Token eatSpecialCharacterToken() {
		
		char c = currChar();
		char next = nextChar();
		char next2 = next2Char();
		TokenType type = null;
		switch (c) {
			case '{':
				type = TokenType.BLOCK_ENTER_BRACKET;
				break;
			case '}':
				type = TokenType.BLOCK_EXIT_BRACKET;
				break;
			case '[':
				type = TokenType.LEFT_SQUARE_BRACKET;
				break;
			case ']':
				type = TokenType.RIGHT_SQUARE_BRACKET;
				break;
			case '(':
				type = TokenType.LEFT_PARATHESIS;
				break;
			case ')':
				type = TokenType.RIGHT_PARATHESIS;
				break;
			case '+' :
				switch (next) {
					case '=' :
						type = TokenType.PLUS_EQUALS;
						break;
					case '+' :
						type = TokenType.INCREMENT;
						break;
					default:
						type = TokenType.PLUS;
				}
				break;
			case '-' :
				switch (next) {
					case '=' :
						type = TokenType.MINUS_EQUALS;
						break;
					case '-' :
						type = TokenType.DECREMENT;
						break;
					default:
						type = TokenType.MINUS;
				}
				break;
			case '/' : 
				if (next == '=') {
					type = TokenType.DIVIDE_EQUALS;
				} else {
					type = TokenType.DIVISION;
				}
				break;
			
			case '*' : 
				if (next == '=') {
					type = TokenType.MULTIPLY_EQUALS;
				} else {
					type = TokenType.MULTIPLICATION;
				}
				break;
			case '%' : 
				if (next == '=') {
					type = TokenType.MODULUS_EQUALS;
				} else {
					type = TokenType.MODULUS;
				}
				break;
			case '<' : 
				if (next == '=') {
					type = TokenType.LESS_OR_EQ;
				} else if (next == '<') {
					type = TokenType.BITWISE_LEFT_SHIFT;
				} else {
					type = TokenType.LESS;
				}
				break;
			case '>' : 
				switch (next) {
					case '>' :
						if (next2 == '>') {
							type = TokenType.BITWISE_RIGHT_SHIFT_SPECIAL;
						} else {
							type = TokenType.BITWISE_RIGHT_SHIFT;
						}
						break;
					case '=':
						type = TokenType.GREATER_OR_EQ;
						break;
					default:
						type = TokenType.GREATER;
				}
				break;
			case '|' : 
				if (next == '|') {
					type = TokenType.OR;
				} else if (next == '=' ){
					type = TokenType.BITWISE_OR_EQUALS;
				} else {
					type = TokenType.BITWISE_OR;
				}
				break;
			case '&' : 
				if (next == '&') {
					type = TokenType.AND;
				} else if (next == '='){
					type = TokenType.BITWISE_AND_EQUALS;
				} else {
					type = TokenType.BITWISE_AND;
				}
				break;
			case '=' :
				if (next == '=' && next2 == '=') {
					type = TokenType.EQUALS_SPECIAL;
				} else if (next == '=') {
					type = TokenType.EQUALS_COMPARISON;
				} else {
					type = TokenType.EQUALS;
				}
				break;
			case '!':
				if (next == '=' && next2 == '=') {
					type = TokenType.NOT_EQUALS_SPECIAL;
				} else if (next == '=') {
					type = TokenType.NOT_EQUALS;
				} else {
					type = TokenType.UNARY_NEGATE;
				}
				break;
			case '^':
				if (next == '=') {
					type = TokenType.BITWISE_XOR_EQUALS;
				} else {					
					type = TokenType.BITWISE_XOR;
				}
				break;
			case '~':
				if (next == '=') {
					type = TokenType.BITWISE_INVERT_EQUALS;
				} else {
					type = TokenType.BITWISE_INVERT;
				}
				break;
			case '?':
				type = TokenType.TERNARY_CONDITION;
				break;
			case '.' :
				type = TokenType.DOT;
				break;
			case ';' :
				type = TokenType.SEMICOLON;
				break;
			case ':' :
				type = TokenType.COLON;
				break;
			case ',' :
				type = TokenType.COMMA;
				break;
			default: 
				return new Token(TokenType.ERROR);
		
		}
		currMarker += type.getAsString().length();
		return new Token(type);
	}

	private Token eatWordLikeToken() {
		
		int begin = currMarker;
		currMarker++;
		for (; currMarker < chars.length; currMarker++) {
			char c = chars[currMarker];
			if (!isJavaScriptIdentifierPart(c)) {
				break;
			}
		}
		int end = currMarker - 1;
		char[] token = new char[end - begin + 1];
		System.arraycopy(chars, begin, token, 0, token.length);
		String tokenAsString = new String(token);
		if (keyWordsCache.containsKey(tokenAsString)) {
			return new Token(keyWordsCache.get(tokenAsString));
		}
		
		//now here the word is an identifier
		Token tokenToReturn = new Token(TokenType.IDENTIFIER, tokenAsString);
		return tokenToReturn;
	}

	private Token eatWhitespaceAndComments() {
		int commentStart = -1;
		for (;currMarker < chars.length; currMarker++) {
			char c = chars[currMarker];
			
			//we handle the current char differently if it is seen
			//in a comment or not. Also it depends on the type of
			//comment in which we are.
			if (commentStart == -1) {
				if (c == '\n' ) {
					currLineNumber++;
					currLineIndex = currMarker;
					currMarker++;
					return new Token(TokenType.EOL);
				} else if (c == '\r' && nextChar() == '\n') {
					currMarker++;
					currLineNumber ++;
					currLineIndex = currMarker;
					return new Token(TokenType.EOL);
				} else if (isLineTerminator(c)) {
					currMarker++;
					currLineNumber++;
					currLineIndex = currMarker;
					return new Token(TokenType.EOL);
				}
				if (Character.isWhitespace(c) ) {
					continue;
				}
				
				//matches comments which start with //
				if (c == '/' && nextChar() == '/') {
					commentStart = 1;
					currMarker++;
				} else {
					//matches comments which start with/**/
					if (c == '/' && nextChar() == '*') {
						commentStart = 2;
						currMarker++;
					} else {						
						break;
					}
				}
			} else if (commentStart == 1) {					
				if (c == '\r' && nextChar() == '\n') {
					currMarker++;
					commentStart = -1;
					currLineNumber++;
					currLineIndex = currMarker;
					return new Token(TokenType.EOL);
				}
				if (isLineTerminator(c)){
					commentStart = -1;
					currLineNumber++;
					currLineIndex = currMarker;
					return new Token(TokenType.EOL);
				}
			} else if (commentStart == 2) {
				if (c == '*' && nextChar() == '/') {
					currMarker++;
					commentStart = -1;
				}
				if (c == '\r' && nextChar() == '\n') {
					currLineNumber++;
					currLineIndex = currMarker;
				}
				if (isLineTerminator(c)){
					currLineNumber++;
					currLineIndex = currMarker;
				}
			}
			
		}
		return null;
		
	}
	
	@Override
	public Token eatRegExConst() {
		char c = prevChar();
		int begin = currMarker;
		if (c == '=') {
			//it starts with /=
			begin = currMarker - 1;
			currMarker--;
		}
		TokenType type = TokenType.REG_EX;
		int symbolsMoveAhead = 0;
		boolean escapeNext = false;
		for (;currMarker < chars.length; currMarker++) {
			c = chars[currMarker];
			
			//make the same in the string const
			if (escapeNext) {
				escapeNext = false;
				continue;
			} else {
				if (c == '\\') {
					escapeNext = true;
					continue;
				}
				if (c == '/') {
					break;
				}
			}
			
			if ( c == '\r' && nextChar() == '\n') {
				type = TokenType.ERROR;
				symbolsMoveAhead = 2;
				currLineNumber++;
				currLineIndex = currMarker;
				break;
			}
			if (isLineTerminator(c)) {
				type = TokenType.ERROR;
				symbolsMoveAhead = 1;
				currLineNumber++;
				currLineIndex = currMarker;
				break;
			}
		}
		int end = currMarker - 1;
		char[] token = new char[end - begin + 1];
		System.arraycopy(chars, begin, token, 0, token.length);
		String regex = new String(token);
		currMarker += symbolsMoveAhead;
		if (type != TokenType.REG_EX) {			
			return new Token(type, regex);
		}
		currMarker++;
		begin = currMarker;
		for (;currMarker < chars.length; currMarker++) {
			c = chars[currMarker];
			if (!isJavaScriptIdentifierPart(c)) {
				break;
			}
		}
		end = currMarker - 1;
		char[] flagsChars = new char[end - begin + 1];
		String flags = "";
		if (flagsChars.length != 0) {
			System.arraycopy(chars, begin, flagsChars, 0, flagsChars.length);
			flags = new String(flagsChars);
		}
		return new Token(TokenType.REG_EX, new String[] {regex, flags});
	}

	private char nextChar() {
		if (currMarker +1 >= chars.length) {
			return (char) -1;
		}
		return chars[currMarker + 1];
	}
	
	private char next2Char() {
		if (currMarker + 2 >= chars.length) {
			return (char) -1;
		}
		return chars[currMarker + 2];
	}

	private Token eatNumberConst() {
		//TODO handle digits like 4.5 
				// or octal 0898 or hex digits 0xAF56
				//or 3.45e2
				/*
				 * from wikipedia
		345;    // an "integer", although there is only one numeric type in JavaScript
		34.5;   // a floating-point number
		3.45e2; // another floating-point, equivalent to 345
		0377;   // an octal integer equal to 255
		0xFF;   // a hexadecimal integer equal to 255, digits represented by the ...
		        // ... letters A-F may be upper or lowercase
				 */
		boolean isErrorInNumber = false;
		char startChar = chars[currMarker];
		int begin = currMarker;
		boolean isHex = false;
		if (startChar == '0' && nextChar() != '.') {
			//HEX or OCTAL
			if (nextChar() == 'x') {
				//hex
				//skip the 0x
				currMarker += 2;
				isHex = true;
			}
			
			//octal and hex
			for (;currMarker < chars.length; currMarker++) {
				char c = chars[currMarker];
				if (isHex) {
					if (!isHexNumberSymbol(c)) {						
						break;
					}
				} else if (!Character.isDigit(c) ) {
					break;
				}
			}
		} else {
			boolean isDotUsed = false;
			boolean isEused = false;
			for (;currMarker < chars.length; currMarker++) {
				char c = chars[currMarker];
				if (!isDotUsed) {
					if (Character.isDigit(c)) {
						continue;
					} else if (c == '.' ) {
						isDotUsed = true;
					} else {
						break;
					}
				} else {
					if (isEused) {
						//this code passes when the number is like
						//3.45e4 and isEused means exponent
						if (c == '-' || c == '+') {
							if (!Character.isDigit(nextChar())) {
								isErrorInNumber = true;
								currMarker += 1;
								eatUntilThereIsAnError();
								break;
							}
							continue;
						} else if (!Character.isDigit(c)){
							break;
						}
					} else {
						if (Character.isDigit(c)) {
							continue;
						} else if (c == 'e') {
							isEused = true;
							if (nextChar() != '-' && 
									nextChar() != '+' && 
									!Character.isDigit(nextChar())) {
								isErrorInNumber = true;
								currMarker += 2;
								eatUntilThereIsAnError();
								break;
							}
						} else {
							break;
						}
					}
				}
			}
		}

		int end = currMarker - 1;
		char[] token = new char[end - begin + 1];
		System.arraycopy(chars, begin, token, 0, token.length);
		String tokenAsString = new String(token);
		
		//when there is an error there can almost any character after a wrong digit
		tokenAsString = tokenAsString.trim();
		if (isErrorInNumber) {
			Token tokenToReturn = new Token(TokenType.ERROR, tokenAsString);
			return tokenToReturn;
		}
		Token tokenToReturn = new Token(TokenType.NUMBER_CONSTANT, tokenAsString);
		return tokenToReturn;
	}

	private void eatUntilThereIsAnError() {
		if (Character.isLetter(currChar())) {
			while(Character.isLetter(currChar())) {
				currMarker++;
			}
		} else if (isSpecialSymbol(currChar())) {
		} else if (Character.isWhitespace(currChar())) {
			
		}
		
	}

	private boolean isSpecialSymbol(char c) {
		return c == '=' || 
				c == '(' ||
				c == ')' ||
				c == '+' ||
				c == '-' ||
				c == '/' ||
				c == '*' ||
				c == '.' ||
				c == '{' ||
				c == '}' ||
				c == '[' ||
				c == ']' ||
				c == ',' ||
				c == ';' ||
				c == ':' ||
				c == '%' ||
				c == '<' ||
				c == '>' ||
				c == '!' ||
				c == '?' ||
				c == '|' ||
				c == '&' ||
				c == '~' ||
				c == '^';
	}

	private char currChar() {
		if (currMarker >= chars.length) {
			return (char) -1;
		}
		return chars[currMarker];
	}

	private boolean isHexNumberSymbol(char c) {
		return Character.isDigit(c) 
				|| c == 'A' || c == 'B' || c == 'C' || c == 'D' 
				|| c == 'E' | c == 'F'|| c == 'a' || c == 'b' 
				|| c == 'c' || c == 'd' || c == 'e' || c == 'f';
	}

	@Override
	public void back() {
		
	}

	@Override
	public int getTokenIdx() {
		return currTokenIndex;
	}

	public int getCurrLineNumber() {
		return currLineNumber;
	}

	public void setCurrLineNumber(int currLineNumber) {
		this.currLineNumber = currLineNumber;
	}

	@Override
	public int getCurrentLine() {
		return currLineNumber;
	}

	@Override
	public Token getAtIndex(int index) {
		return tokenList.get(index);
	}

	


}
