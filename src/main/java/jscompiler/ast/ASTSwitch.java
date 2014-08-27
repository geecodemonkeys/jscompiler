package jscompiler.ast;

import java.util.List;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTSwitch extends ASTStatement {

	private ASTExpression var;
	private List<ASTCase> caseList;

	public ASTSwitch(ASTExpression switchVar, List<ASTCase> object) {
		this.var = switchVar;
		this.caseList = object;
	}

	public ASTExpression getVar() {
		return var;
	}

	public void setVar(ASTExpression var) {
		this.var = var;
	}

	public List<ASTCase> getCaseList() {
		return caseList;
	}

	public void setCaseList(List<ASTCase> caseList) {
		this.caseList = caseList;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		
		if (var != null) {			
			var.accept(visitor);
		}
		
		if (caseList != null) {
			for (ASTCase node : caseList) {				
				node.accept(visitor);
			}
		}
		visitor.endVisit(this);
	}

}
