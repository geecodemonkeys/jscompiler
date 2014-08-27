package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTDoWhile extends ASTStatement {

	private ASTExpression condition;
	private ASTStatement statement;

	public ASTDoWhile(ASTExpression expression, ASTStatement statement) {
		this.setCondition(expression);
		this.setStatement(statement);
	}

	public ASTStatement getStatement() {
		return statement;
	}

	public void setStatement(ASTStatement statement) {
		this.statement = statement;
	}

	public ASTExpression getCondition() {
		return condition;
	}

	public void setCondition(ASTExpression condition) {
		this.condition = condition;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		
		if (condition != null) {
			condition.accept(visitor);
		}
		
		if (statement != null) {
			statement.accept(visitor);
		}
		visitor.endVisit(this);
	}

}
