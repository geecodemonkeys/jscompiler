package jscompiler.ast.visitor;

import jscompiler.ast.ASTFunctionDeclaration;
import jscompiler.ast.ASTFunctionExpression;
import jscompiler.ast.ASTVariableDeclaration;
import jscompiler.execution.ScopeFrame;
import jscompiler.jsobject.JsReference;
import jscompiler.jsobject.JsUndefined;

public class VarFinderVisitor extends ASTVisitor {

	private ScopeFrame scope = null;
	
	public VarFinderVisitor(ScopeFrame scope) {
		this.scope = scope;
	}
	
	@Override
	public boolean visit(ASTVariableDeclaration node) {
		scope.addVarReference(new JsReference(node.getIdentifier(), 
				new JsUndefined()));
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ASTFunctionDeclaration node) {
		return false;
	}
	
	@Override
	public boolean visit(ASTFunctionExpression node) {
		return false;
	}

}
