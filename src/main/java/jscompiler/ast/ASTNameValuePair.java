package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTNameValuePair extends ASTExpression {
	
	private ASTIdentifier identifier;
	private ASTExpression expression;
	private ASTConstant constant;

	public ASTNameValuePair(ASTIdentifier id, ASTConstant constant, 
			ASTExpression assignmentExpression) {
		this.identifier = id;
		this.constant = constant;
		this.expression = assignmentExpression;
	}

	public ASTExpression getExpression() {
		return expression;
	}

	public void setExpression(ASTExpression expression) {
		this.expression = expression;
	}

	public ASTIdentifier getIdentifier() {
		return identifier;
	}

	public void setIdentifier(ASTIdentifier identifier) {
		this.identifier = identifier;
	}

	public ASTConstant getConstant() {
		return constant;
	}

	public void setConstant(ASTConstant constant) {
		this.constant = constant;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		
		if (identifier != null) {
			identifier.accept(visitor);
		}
		
		if (constant != null) {
			constant.accept(visitor);
		}
		
		if (expression != null) {
			expression.accept(visitor);
		}
		visitor.endVisit(this);
	}

}
