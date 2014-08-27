package jscompiler.ast;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import jscompiler.ast.visitor.ASTVisitor;

@XmlType
public class ASTBlockStatement extends ASTStatement {

	@XmlElement
	private List<ASTStatement> statements;

	public ASTBlockStatement(List<ASTStatement> statements) {
		super();
		this.statements = statements;
	}

	public List<ASTStatement> getStatements() {
		return statements;
	}

	public void setStatements(List<ASTStatement> statements) {
		this.statements = statements;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		if (statements != null) {
			for (ASTStatement statement : statements) {
				statement.accept(visitor);
			}
		}
		visitor.endVisit(this);
	}
}
