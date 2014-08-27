package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;
import jscompiler.token.TokenType;

public class ASTConstant extends ASTExpression {

	private String value;
	private TokenType constType;

	public ASTConstant(TokenType type, String value) {
		this.constType = type;
		this.setValue(value);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public TokenType getConstType() {
		return constType;
	}

	public void setConstType(TokenType constType) {
		this.constType = constType;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (constType == TokenType.NUMBER_CONSTANT) {
			builder.append(value);
		} else {
			builder.append("\"").append(value).append("\"");
		}
		return builder.toString();
	}

}
