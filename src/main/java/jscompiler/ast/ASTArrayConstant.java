package jscompiler.ast;

import java.util.List;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTArrayConstant extends ASTExpression {

	private List<ASTExpression> elements;

	public ASTArrayConstant(List<ASTExpression> list) {
		this.setElements(list);
	}

	public List<ASTExpression> getElements() {
		return elements;
	}

	public void setElements(List<ASTExpression> elements) {
		this.elements = elements;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		
		if (elements != null) {
			for (ASTExpression expr : elements ) {
				expr.accept(visitor);
			}
		}
		visitor.endVisit(this);
	}

}
