package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTVariableDeclaration extends ASTStatement {

	private ASTExpression initializer;
	private ASTIdentifier identifier;

	public ASTVariableDeclaration(ASTIdentifier identifier,
			ASTExpression assignement) {
		this.setIdentifier(identifier);
		this.setInitializer(assignement);
		
	}

	public ASTExpression getInitializer() {
		return initializer;
	}

	public void setInitializer(ASTExpression initializer) {
		this.initializer = initializer;
	}

	public ASTIdentifier getIdentifier() {
		return identifier;
	}

	public void setIdentifier(ASTIdentifier identifier) {
		this.identifier = identifier;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		
		if (identifier != null) {			
			identifier.accept(visitor);
		}
		
		if (initializer != null) {			
			initializer.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("var ").append(identifier);
		if (initializer != null) {
			builder.append(" = ").append(initializer);
		}
		builder.append(";");
		return builder.toString();
	}

}
