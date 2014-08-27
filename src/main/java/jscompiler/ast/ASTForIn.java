package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTForIn extends ASTStatement {

	private ASTNode iterationRef;
	private ASTExpression iteratorExpression;
	private ASTStatement statement;

	public ASTForIn(ASTNode initializer, 
			ASTExpression iteratorExpression,
			ASTStatement statement) {
		this.iterationRef = initializer;
		this.iteratorExpression = iteratorExpression;
		this.statement = statement;
	}

	public ASTExpression getIteratorExpression() {
		return iteratorExpression;
	}

	public void setIteratorExpression(ASTExpression iteratorExpression) {
		this.iteratorExpression = iteratorExpression;
	}

	public ASTNode getIterationRef() {
		return iterationRef;
	}

	public void setIterationRef(ASTNode iterationRef) {
		this.iterationRef = iterationRef;
	}

	public ASTStatement getStatement() {
		return statement;
	}

	public void setStatement(ASTStatement statement) {
		this.statement = statement;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		
		if (iterationRef != null) {
			iterationRef.accept(visitor);
		}
		
		if (iteratorExpression != null) {
			iteratorExpression.accept(visitor);
		}
		
		if (statement != null) {
			statement.accept(visitor);
		}
		visitor.endVisit(this);
	}
}
