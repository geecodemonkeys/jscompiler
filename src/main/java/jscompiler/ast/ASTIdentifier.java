package jscompiler.ast;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonIgnore;

import jscompiler.ast.visitor.ASTVisitor;

@XmlType
public class ASTIdentifier extends ASTExpression {
	
	@XmlAttribute
	private String name;
	
	@JsonIgnore
	private int scopeDepth;
	
	@JsonIgnore
	private int offset;
	
	@JsonIgnore
	private int realScopeDepth = 0;

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public ASTIdentifier(String value) {
		this.setName(value);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	public int getScopeDepth() {
		return scopeDepth;
	}

	public void setScopeDepth(int scopeDepth) {
		this.scopeDepth = scopeDepth;
	}

	public int getRealScopeDepth() {
		return realScopeDepth;
	}

	public void setRealScopeDepth(int realScopeDepth) {
		this.realScopeDepth = realScopeDepth;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(name);
		return builder.toString();
	}

}
