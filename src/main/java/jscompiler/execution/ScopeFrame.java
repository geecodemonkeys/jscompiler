package jscompiler.execution;

import java.util.ArrayList;
import java.util.List;

import jscompiler.ast.ASTFunction;
import jscompiler.jsobject.JsObjectBase;
import jscompiler.jsobject.JsReference;

public class ScopeFrame {
	
	private List<JsReference> variables = new ArrayList<JsReference>();
	private ASTFunction function;
	private JsObjectBase returnValue = null;
	
	public void addVarReference(JsReference reference) {
		variables.add(reference);
	}

	public List<JsReference> getVariables() {
		return variables;
	}

	public void setVariables(List<JsReference> variables) {
		this.variables = variables;
	}

	public ASTFunction getFunction() {
		return function;
	}

	public void setFunction(ASTFunction function) {
		this.function = function;
	}

	public JsObjectBase getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(JsObjectBase returnValue) {
		this.returnValue = returnValue;
	} 

}
