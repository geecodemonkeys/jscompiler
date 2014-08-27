package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;
import jscompiler.token.TokenType;

public class ASTTwoOperandExpression extends ASTExpression {

	private TokenType operation;
	private ASTNode left;
	private ASTNode right;

	public ASTTwoOperandExpression(TokenType type, ASTNode left, ASTNode right) {
		this.setOperation(type);
		this.setLeft(left);
		this.setRight(right);
	}

	public TokenType getOperation() {
		return operation;
	}

	public void setOperation(TokenType operation) {
		this.operation = operation;
	}

	public ASTNode getLeft() {
		return left;
	}

	public void setLeft(ASTNode left) {
		this.left = left;
	}

	public ASTNode getRight() {
		return right;
	}

	public void setRight(ASTNode right) {
		this.right = right;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		
		if (left != null) {			
			left.accept(visitor);
		}
		
		if (right != null) {			
			right.accept(visitor);
		}
		visitor.endVisit(this);
	}

}
