package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTContinue extends ASTStatement {

	private ASTIdentifier label;

	public ASTContinue(ASTIdentifier id) {
		this.label = id;
	}

	public ASTIdentifier getLabel() {
		return label;
	}

	public void setLabel(ASTIdentifier label) {
		this.label = label;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		if (label != null) {
			label.accept(visitor);
		}
		visitor.endVisit(this);
	}

}
