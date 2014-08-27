package jscompiler.jsobject;

import jscompiler.ast.ASTFunction;
import jscompiler.ast.ASTFunctionDeclaration;
import jscompiler.ast.ASTFunctionExpression;
import jscompiler.execution.Context;

public class JsFunction extends JsObject {

	private ASTFunction function;
	private Context scope;
	
	public JsFunction(ASTFunctionDeclaration function, Context context,JsObject __proto__) {
		super(__proto__);
		this.function = function;
		this.scope = context;
	}
	
	public JsFunction(ASTFunctionExpression function, Context context, JsObject __proto__) {
		super(__proto__);
		this.function = function;
		this.scope = context;
	}

	public JsFunction(ASTFunction function, JsObject __proto__) {
        super(__proto__);
		this.function = function;
	}

	@Override
	public int getCode() {
		return FUNCTION;
	}

	//TODO revisit this method
	@Override
	public Object getRealValue() {
		return "function " 
				+ (function.getName() == null ? "" : function.getName()) 
				+ "(){}";
	}

	public ASTFunction getFunction() {
		return function;
	}

	public void setFunction(ASTFunctionDeclaration function) {
		this.function = function;
	}

	public Context getScope() {
		return scope;
	}

	public void setScope(Context scope) {
		this.scope = scope;
	}

}
