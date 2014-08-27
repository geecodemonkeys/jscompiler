package jscompiler.ast;

import java.util.List;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTObjectConst extends ASTExpression {

	private List<ASTNameValuePair> fields;

	public ASTObjectConst() {
		
	}

	public ASTObjectConst(List<ASTNameValuePair> pairList) {
		this.fields = pairList;
	}

	public List<ASTNameValuePair> getFields() {
		return fields;
	}

	public void setFields(List<ASTNameValuePair> fields) {
		this.fields = fields;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		
		if (fields != null) {
			for (ASTNameValuePair pair : fields) {
				pair.accept(visitor);
			}
		}
		visitor.endVisit(this);
	}
}
