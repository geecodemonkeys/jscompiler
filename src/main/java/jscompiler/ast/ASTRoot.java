package jscompiler.ast;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import jscompiler.ast.visitor.ASTVisitor;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ASTRoot extends ASTNode {
	
	public ASTRoot() {
		super();
	}

	@XmlElement
	private List<ASTNode> nodes;

	public ASTRoot(List<ASTNode> nodes) {
		this.setNodes(nodes);
	}

	public List<ASTNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<ASTNode> nodes) {
		this.nodes = nodes;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		
		if (nodes != null) {
			for (ASTNode node : nodes) {				
				node.accept(visitor);
			}
		}
		visitor.endVisit(this);
	}


}
