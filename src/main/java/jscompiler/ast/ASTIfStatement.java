package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTIfStatement extends ASTStatement {

	private ASTExpression condition;
	private ASTStatement ifBody;
	private ASTStatement elseBody;

	public ASTIfStatement(ASTExpression condition, ASTStatement ifBody,
			ASTStatement elseBody) {
		this.setCondition(condition);
		this.setIfBody(ifBody);
		this.setElseBody(elseBody);
	}

	public ASTStatement getIfBody() {
		return ifBody;
	}

	public void setIfBody(ASTStatement ifBody) {
		this.ifBody = ifBody;
	}

	public ASTExpression getCondition() {
		return condition;
	}

	public void setCondition(ASTExpression condition) {
		this.condition = condition;
	}

	public ASTStatement getElseBody() {
		return elseBody;
	}

	public void setElseBody(ASTStatement elseBody) {
		this.elseBody = elseBody;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		
		if (condition != null) {
			condition.accept(visitor);
		}
		
		if (ifBody != null) {
			ifBody.accept(visitor);
		}
		
		if (elseBody != null) {
			elseBody.accept(visitor);
		}
		visitor.endVisit(this);
	}

}
