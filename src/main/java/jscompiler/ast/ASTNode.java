package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;
import jscompiler.execution.Scope;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public abstract class ASTNode {
	
	@JsonIgnore
	protected Scope scope;
	
	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	@JsonProperty
	protected String getType(){
		return this.getClass().getSimpleName();
	}
	
	public abstract void accept(ASTVisitor visitor);

}
