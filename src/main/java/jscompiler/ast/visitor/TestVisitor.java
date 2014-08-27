package jscompiler.ast.visitor;

import jscompiler.ast.ASTConstant;

public class TestVisitor extends ASTVisitor {
	
	@Override
	public boolean visit(ASTConstant node) {
		System.out.println(node.getValue() + " " + node.getConstType());
		
		return true;
	}

}
