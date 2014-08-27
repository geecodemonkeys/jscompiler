package jscompiler.ast.visitor;

import java.util.List;
import java.util.Stack;

import jscompiler.ast.ASTFunctionDeclaration;
import jscompiler.ast.ASTIdentifier;
import jscompiler.ast.ASTNode;
import jscompiler.ast.ASTRoot;
import jscompiler.ast.ASTVariableDeclaration;
import jscompiler.execution.Scope;
import jscompiler.execution.SymbolLookup;

public class IdResolveVisitor extends ASTVisitor {
	
	private Stack<Scope> scopes = null;
	private boolean skipNextVar;
	public IdResolveVisitor() {
		scopes = new Stack<Scope>();
		scopes.push(new Scope(null, 0));
	}
	
	/**
	 * Searches for all function declarations directly into the 
	 * body
	 * @param scope
	 * @param list
	 */
	private void addFunctionDeclarations(Scope scope,
			List<ASTNode> list) {
		for (ASTNode node : list) {
			if (node instanceof ASTFunctionDeclaration) {
				ASTFunctionDeclaration function = (ASTFunctionDeclaration) node;
				ASTIdentifier id = function.getName();
				id.setRealScopeDepth(scope.getScopeDepth());
				scope.getBindings().put(id.getName(), 
							new SymbolLookup(0, 
									scope.getOffset()));
				scope.setOffset(scope.getOffset() + 1);
			}
		}
	}
	
	/*
	 private void addFunctionDeclarations(Scope scope,
			List<ASTNode> list) {
		for (ASTNode node : list) {
			if (node instanceof ASTFunctionDeclaration) {
				ASTFunctionDeclaration function = (ASTFunctionDeclaration) node;
				ASTIdentifier id = function.getName();
				scope.getBindings().put(id.getName(), 
							new SymbolLookup(scope.getScopeDepth(), 
									scope.getOffset()));
				scope.setOffset(scope.getOffset() + 1);
			}
		}
	}
	 */
	
	@Override
	public boolean visit(ASTRoot node) {
		addFunctionDeclarations(scopes.peek(), node.getNodes());
		return true;
	}
	
	@Override
	public boolean visit(ASTIdentifier node) {
		if (skipNextVar) {
			skipNextVar = false;
			return false;
		}
		Scope scope = scopes.peek();
		ASTIdentifier id = node;
		
		int i = 0;
		while (scope != null) {
			if (scope.getBindings().containsKey(id.getName())) {
				break;
			}
			scope = scope.getParentScope();
			i--;
		}
		if (scope != null) {
			SymbolLookup lookup = scope.getBindings().get(id.getName());
			id.setOffset(lookup.getOffset());
			id.setScopeDepth(i);
			id.setRealScopeDepth(scopes.peek().getScopeDepth());
		} else {
			id.setOffset(Integer.MIN_VALUE);
			id.setScopeDepth(Integer.MIN_VALUE);
			id.setRealScopeDepth(scopes.peek().getScopeDepth());
		}
		
		
		return true;
	}
	
	/*test with nested var elements like
	 function a () {
	  var f = (function () {var b = 5;}) ();
	}*/
	@Override
	public boolean visit(ASTVariableDeclaration node) {
		
		ASTIdentifier id = node.getIdentifier();
		
		Scope scope = scopes.peek();
		id.setScopeDepth(scope.getScopeDepth());
		id.setRealScopeDepth(scopes.peek().getScopeDepth());
		putVarInScope(id, scope);
		skipNextVar = true;
		
		return true;
	}

	private void putVarInScope(ASTIdentifier id, Scope scope) {
		//if (!scope.getBindings().containsKey(id.getName())) {
			scope.getBindings().put(id.getName(), 
					new SymbolLookup(0, scope.getOffset()));
			
			//set the offset and the index in the lexical table
			id.setOffset(scope.getOffset());
			id.setRealScopeDepth(scope.getScopeDepth());
			scope.setOffset(scope.getOffset() + 1);
		//}
	}

	//FIXME Does not handle function and var  with same names
	@Override
	public boolean visit(ASTFunctionDeclaration node) {
		Scope parent = scopes.peek();
		
		Scope scope = new Scope(parent, 
				parent.getScopeDepth() + 1);
		scopes.push(scope);
		//TESTED in chromium on ubuntu 12
		
		//1. put first my func name in the scope
		putVarInScope(node.getName(), scope);
		
		//2. put my args in the scope
		for (ASTIdentifier arg : node.getArguments()) {
			putVarInScope(arg, scope);
		}
		
		//3. put my func declarations which occur on the first level
		addFunctionDeclarations(scope, node.getBodyStatements());
		
		//4. add var declarations
		if (node.getBodyStatements() != null) {
			for (ASTNode bodyStmt : node.getBodyStatements()) {
				bodyStmt.accept(this);
			}
		}
		scopes.pop();
		
		return false;
	}
	
	@Override
	public void endVisit(ASTFunctionDeclaration node) {
		//scopes.pop();
	}

}
