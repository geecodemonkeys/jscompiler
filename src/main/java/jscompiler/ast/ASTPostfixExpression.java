package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;
import jscompiler.token.TokenType;

public class ASTPostfixExpression extends ASTExpression {

	private TokenType opType;
	private ASTExpression expression;

	public ASTPostfixExpression(TokenType opType, ASTExpression expr) {
		this.setOpType(opType);
		this.expression = expr;
	}

	public TokenType getOpType() {
		return opType;
	}

	public void setOpType(TokenType opType) {
		this.opType = opType;
	}

	public ASTExpression getExpression() {
		return expression;
	}

	public void setExpression(ASTExpression expression) {
		this.expression = expression;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		
		if (expression != null) {
			expression.accept(visitor);
		}
		visitor.endVisit(this);
	}

}
