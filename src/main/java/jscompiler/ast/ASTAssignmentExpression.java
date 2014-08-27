package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;
import jscompiler.token.TokenType;

public class ASTAssignmentExpression extends ASTExpression {

	private TokenType assignmentType;
	private ASTExpression leftHandSide;
	private ASTExpression rightHandSide;

	public ASTAssignmentExpression(TokenType type, ASTExpression leftHandSide,
			ASTExpression rightHandSide) {
		
		this.setAssignmentType(type);
		this.setLeftHandSide(leftHandSide);
		this.setRightHandSide(rightHandSide);
	}

	public TokenType getAssignmentType() {
		return assignmentType;
	}

	public void setAssignmentType(TokenType assignmentType) {
		this.assignmentType = assignmentType;
	}

	public ASTExpression getLeftHandSide() {
		return leftHandSide;
	}

	public void setLeftHandSide(ASTExpression leftHandSide) {
		this.leftHandSide = leftHandSide;
	}

	public ASTExpression getRightHandSide() {
		return rightHandSide;
	}

	public void setRightHandSide(ASTExpression rightHandSide) {
		this.rightHandSide = rightHandSide;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		if (leftHandSide != null) {
			leftHandSide.accept(visitor);
		}
		if (rightHandSide != null) {
			rightHandSide.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
}
