package jscompiler.ast;

import java.util.List;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTList extends ASTStatement {
	

	private List<ASTVariableDeclaration> list;

	public ASTList(List<ASTVariableDeclaration> list) {
		this.setList(list);
	}

	public List<ASTVariableDeclaration> getList() {
		return list;
	}

	public void setList(List<ASTVariableDeclaration> list) {
		this.list = list;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		
		if (list != null) {
			for (ASTVariableDeclaration decl : list) {				
				decl.accept(visitor);
			}
		}
		visitor.endVisit(this);
		
	}

}
