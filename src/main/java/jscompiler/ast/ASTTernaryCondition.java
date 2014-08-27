package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTTernaryCondition extends ASTExpression {

	private ASTExpression trueExpression;
	private ASTExpression falseExpression;
	private ASTExpression condition;

	public ASTTernaryCondition(ASTExpression condition, ASTExpression trueExpression,
			ASTExpression falseExpression) {
		this.setCondition(condition);
		this.setTrueExpression(trueExpression);
		this.setFalseExpression(falseExpression);
	}

	public ASTExpression getTrueExpression() {
		return trueExpression;
	}

	public void setTrueExpression(ASTExpression trueExpression) {
		this.trueExpression = trueExpression;
	}

	public ASTExpression getFalseExpression() {
		return falseExpression;
	}

	public void setFalseExpression(ASTExpression falseExpression) {
		this.falseExpression = falseExpression;
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
		
		if (trueExpression != null) {			
			trueExpression.accept(visitor);
		}
		
		if (falseExpression != null) {			
			falseExpression.accept(visitor);
		}
		visitor.endVisit(this);
	}

}
