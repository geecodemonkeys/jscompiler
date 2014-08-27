package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;
import jscompiler.token.TokenType;

public class ASTUnaryExpression extends ASTExpression {

	private TokenType opType;
	private ASTExpression expression;

	public ASTUnaryExpression(TokenType opType, ASTExpression unaryExpression) {
		this.setOpType(opType);
		this.setExpression(unaryExpression);
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
