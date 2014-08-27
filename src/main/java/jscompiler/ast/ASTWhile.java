package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTWhile extends ASTStatement {

	private ASTExpression condition;
	private ASTStatement body;

	public ASTWhile(ASTExpression expression, ASTStatement body) {
		this.condition = expression;
		this.body = body;
	}

	public ASTExpression getCondition() {
		return condition;
	}

	public void setCondition(ASTExpression condition) {
		this.condition = condition;
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
		
		if (condition != null) {			
			condition.accept(visitor);
		}
		
		if (body != null) {			
			body.accept(visitor);
		}
		visitor.endVisit(this);
	}

}
