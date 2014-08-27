package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTWith extends ASTStatement {

	private ASTExpression expression;
	private ASTStatement body;

	public ASTWith(ASTExpression expression, ASTStatement body) {
		this.expression = expression;
		this.body = body;
	}

	public ASTExpression getExpression() {
		return expression;
	}

	public void setExpression(ASTExpression expression) {
		this.expression = expression;
	}

	public ASTStatement getBody() {
		return body;
	}

	public void setBody(ASTStatement body) {
		this.body = body;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		
		if (expression != null) {			
			expression.accept(visitor);
		}
		
		if (body != null) {			
			body.accept(visitor);
		}
		visitor.endVisit(this);
	}


}
