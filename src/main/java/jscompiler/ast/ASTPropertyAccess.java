package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTPropertyAccess extends ASTExpression {

	private ASTIdentifier propertyIdentifier;
	private ASTExpression memberExpression;

	public ASTPropertyAccess(ASTExpression member, ASTIdentifier prop) {
		this.setMemberExpression(member);
		this.setPropertyIdentifier(prop);
	}

	public ASTExpression getMemberExpression() {
		return memberExpression;
	}

	public void setMemberExpression(ASTExpression memberExpression) {
		this.memberExpression = memberExpression;
	}

	public ASTIdentifier getPropertyIdentifier() {
		return propertyIdentifier;
	}

	public void setPropertyIdentifier(ASTIdentifier propertyIdentifier) {
		this.propertyIdentifier = propertyIdentifier;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		
		if (memberExpression != null) {
			memberExpression.accept(visitor);
		}
		
		if (propertyIdentifier != null) {
			propertyIdentifier.accept(visitor);
		}
		visitor.endVisit(this);
	}

}
