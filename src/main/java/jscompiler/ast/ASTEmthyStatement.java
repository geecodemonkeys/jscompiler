package jscompiler.ast;

import javax.xml.bind.annotation.XmlType;

import jscompiler.ast.visitor.ASTVisitor;

@XmlType
public class ASTEmthyStatement extends ASTStatement {
	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
}
