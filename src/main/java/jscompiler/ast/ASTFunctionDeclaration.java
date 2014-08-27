package jscompiler.ast;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import jscompiler.ast.visitor.ASTVisitor;

@XmlType
public class ASTFunctionDeclaration extends ASTNode implements ASTFunction {

	@XmlAttribute
	private ASTIdentifier name;
	
	@XmlElement
	private List<ASTIdentifier> arguments;
	
	@XmlElement
	List<ASTNode> bodyStatements;
	
	public ASTFunctionDeclaration(ASTIdentifier astIdentifier, 
			List<ASTIdentifier> arguments, List<ASTNode> bodyStatements) {
		this.setName(astIdentifier);
		this.setArguments(arguments);
		this.bodyStatements = bodyStatements;
	}

	public List<ASTNode> getBodyStatements() {
		return bodyStatements;
	}

	public void setBodyStatements(List<ASTNode> bodyStatements) {
		this.bodyStatements = bodyStatements;
	}

	public List<ASTIdentifier> getArguments() {
		return arguments;
	}

	public void setArguments(List<ASTIdentifier> arguments) {
		this.arguments = arguments;
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
		
		if (bodyStatements != null) {
			for (ASTNode node : bodyStatements) {
				node.accept(visitor);
			}
		}
		visitor.endVisit(this);
		
	}

	public List<ASTNode> getBody() {
		return bodyStatements;
	}

	public int getCode() {
		return DECLARATION;
	}

}
