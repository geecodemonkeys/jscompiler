package jscompiler.ast;

import java.util.List;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTCase extends ASTNode {

	private ASTExpression expression;
	private List<ASTStatement> statements;
	private boolean isDefault = false;

	public ASTCase(ASTExpression expression, List<ASTStatement> statements) {
		this.expression = expression;
		this.statements = statements;
	}

	public ASTCase(List<ASTStatement> statements, boolean isDefault) {
		this.statements = statements;
		this.isDefault = isDefault;
	}

	public ASTExpression getExpression() {
		return expression;
	}

	public void setExpression(ASTExpression expression) {
		this.expression = expression;
	}

	public List<ASTStatement> getStatements() {
		return statements;
	}

	public void setStatements(List<ASTStatement> statements) {
		this.statements = statements;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		
		if (expression != null) {
			expression.accept(visitor);
		}
		
		if (statements != null) {
			for (ASTStatement stmt : statements) {
				stmt.accept(visitor);
			}
		}
		visitor.endVisit(this);
	}

}
