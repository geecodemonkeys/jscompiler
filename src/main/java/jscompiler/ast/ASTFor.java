package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTFor extends ASTStatement {

	private ASTStatement statement;
	private ASTNode initializer;
	private ASTExpression increment;	
	private ASTExpression condition;
	
	public ASTFor(ASTNode initializer,
			ASTExpression condition, ASTExpression increment, ASTStatement statement) {
		this.initializer = initializer;
		this.statement = statement;
		this.condition = condition;
		this.increment = increment;
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

	public ASTExpression getIncrement() {
		return increment;
	}

	public void setIncrement(ASTExpression increment) {
		this.increment = increment;
	}

	public ASTNode getInitializer() {
		return initializer;
	}

	public void setInitializer(ASTNode initializer) {
		this.initializer = initializer;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		
		if (initializer != null) {
			initializer.accept(visitor);
		}
		
		if (condition != null) {
			condition.accept(visitor);
		}
		
		if (increment != null) {
			increment.accept(visitor);
		}
		
		if (statement != null) {
			statement.accept(visitor);
		}
		visitor.endVisit(this);
	}

}
