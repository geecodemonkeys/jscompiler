package jscompiler.token;

public class Token {

	protected TokenType type;
	protected String value;
	protected Object objectValue;
	private int tokenStartIndex;
	private int lineNumber;

	public Token(TokenType type, String value) {
		super();
		this.type = type;
		this.value = value;
	}
	

	public Token(TokenType type, Object value) {
		super();
		this.type = type;
		this.objectValue = value;
	}
	
	public Token(TokenType type) {
		super();
		this.type = type;
		this.value = null;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public TokenType getType() {
		return type;
	}

	public void setType(TokenType type) {
		this.type = type;
	}
	
	public String toString() {
		return this.type + " " + this.getValue();
	}

	public void setStartIndex(int tokenStartIndex) {
		this.tokenStartIndex = tokenStartIndex;
		
	}

	public int getTokenStartIndex() {
		return tokenStartIndex;
	}

	public void setTokenStartIndex(int tokenStartIndex) {
		this.tokenStartIndex = tokenStartIndex;
	}
	
	public Object getObjectValue() {
		return objectValue;
	}


	public void setObjectValue(Object objectValue) {
		this.objectValue = objectValue;
	}


	public int getLineNumber() {
		return lineNumber;
	}


	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

}
