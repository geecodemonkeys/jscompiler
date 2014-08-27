package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTLabel extends ASTStatement {

	private ASTIdentifier identifier;
	private ASTStatement statement;

	public ASTLabel(ASTIdentifier identifier, ASTStatement statement) {
		this.identifier = identifier;
		this.statement = statement;
	}

	public ASTStatement getStatement() {
		return statement;
	}

	public void setStatement(ASTStatement statement) {
		this.statement = statement;
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
		
		if (statement != null) {
			statement .accept(visitor);
		}
		visitor.endVisit(this);
	}

}
