package jscompiler.ast;

import java.util.List;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTNewExpression extends ASTExpression {

	private ASTExpression memberExpression;
	private List<ASTExpression> arguments;

	public ASTNewExpression(ASTExpression memberExpr,
			List<ASTExpression> arguments) {
		this.setMemberExpression(memberExpr);
		this.setArguments(arguments);
	}

	public ASTExpression getMemberExpression() {
		return memberExpression;
	}

	public void setMemberExpression(ASTExpression memberExpression) {
		this.memberExpression = memberExpression;
	}

	public List<ASTExpression> getArguments() {
		return arguments;
	}

	public void setArguments(List<ASTExpression> arguments) {
		this.arguments = arguments;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		
		if (memberExpression != null) {
			memberExpression.accept(visitor);
		}
		
		if (arguments != null) {
			for (ASTExpression expr : arguments) {
				expr.accept(visitor);
			}
		}
		visitor.endVisit(this);
	}


}
