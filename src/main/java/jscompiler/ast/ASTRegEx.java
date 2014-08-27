package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTRegEx extends ASTExpression {

	private String regex;
	private String flags;

	public ASTRegEx(String[] objectValue) {
		this.regex = objectValue[0];
		this.flags = objectValue[1];
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getFlags() {
		return flags;
	}

	public void setFlags(String flags) {
		this.flags = flags;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
