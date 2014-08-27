package jscompiler.ast;

import java.util.List;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTFunctionExpression extends ASTExpression implements ASTFunction {

	private List<ASTIdentifier> arguments;
	private List<ASTNode> body;
	private ASTIdentifier name;

	public ASTFunctionExpression(ASTIdentifier name,
			List<ASTIdentifier> arguments, List<ASTNode> body) {
		this.name = name;
		this.arguments = arguments;
		this.body = body;
	}

	public List<ASTIdentifier> getArguments() {
		return arguments;
	}

	public void setArguments(List<ASTIdentifier> arguments) {
		this.arguments = arguments;
	}

	public List<ASTNode> getBody() {
		return body;
	}

	public void setBody(List<ASTNode> body) {
		this.body = body;
	}

	public ASTIdentifier getName() {
		return name;
	}

	public void setName(ASTIdentifier name) {
		this.name = name;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		
		if (name != null) {
			name.accept(visitor);
		}
		
		if (arguments != null) {
			for (ASTIdentifier id : arguments) {
				id.accept(visitor);
			}
		}
		
		if (body != null) {
			for (ASTNode node : body) {
				node.accept(visitor);
			}
		}
		visitor.endVisit(this);
		
	}

	public int getCode() {
		return EXPRESSION;
	}

}
