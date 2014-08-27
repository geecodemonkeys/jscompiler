package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTParenExpression extends ASTExpression {

	private ASTExpression expression;

	public ASTParenExpression(ASTExpression expression) {
		this.expression = expression;
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
