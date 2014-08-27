package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTEmthyExpression extends ASTExpression {

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
}
