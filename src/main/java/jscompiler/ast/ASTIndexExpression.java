package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTIndexExpression extends ASTExpression {

	private ASTExpression memberExpression;
	private ASTExpression indexExpression;

	public ASTIndexExpression(ASTExpression memberExpression,
			ASTExpression indexExpr) {
		this.setMemberExpression(memberExpression);
		this.setIndexExpression(indexExpr);
	}

	public ASTExpression getMemberExpression() {
		return memberExpression;
	}

	public void setMemberExpression(ASTExpression memberExpression) {
		this.memberExpression = memberExpression;
	}

	public ASTExpression getIndexExpression() {
		return indexExpression;
	}

	public void setIndexExpression(ASTExpression indexExpression) {
		this.indexExpression = indexExpression;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		
		if (memberExpression != null) {
			memberExpression.accept(visitor);
		}
		
		if (indexExpression != null) {
			indexExpression.accept(visitor);
		}
		visitor.endVisit(this);
	}

}
