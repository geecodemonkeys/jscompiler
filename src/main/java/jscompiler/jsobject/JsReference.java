package jscompiler.jsobject;

import jscompiler.ast.ASTIdentifier;


public class JsReference extends JsObjectBase {

	private int scopeDepth;
	private int offset;
	private int compileTimeDepth = 0;
	private String id;
	private JsObjectBase value;
	private boolean isPropReference = false;

	public JsReference(String id, int depth, int offset, int compileTimeDepth) {
		this.id = id;
		this.scopeDepth = depth;
		this.offset = offset;
		this.compileTimeDepth = compileTimeDepth;
	}
	
	public JsReference(String id, int depth, int offset, 
			int compileTimeDepth, JsObjectBase value) {
		
		this(id, depth, offset, compileTimeDepth);
		this.value = value;
	}

	public JsReference(ASTIdentifier node) {
		this(node.getName(), node.getScopeDepth(),
				node.getOffset(), node.getRealScopeDepth());
	}

	public JsReference(ASTIdentifier name, JsObjectBase obj) {
		this(name);
		this.value = obj;
	}
	
	public JsReference(ASTIdentifier name, JsObjectBase obj, boolean isPropReference) {
		this(name);
		this.value = obj;
		this.isPropReference = isPropReference;
	}

	@Override
	public int getCode() {
		return REFERENCE;
	}
	
	

	public JsObjectBase getValue() {
		return value;
	}

	public void setValue(JsObjectBase value) {
		this.value = value;
	}

	@Override
	public Object getRealValue() {
		return value.getRealValue();
	}

	public int getScopeDepth() {
		return scopeDepth;
	}

	public void setScopeDepth(int scopeDepth) {
		this.scopeDepth = scopeDepth;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCompileTimeDepth() {
		return compileTimeDepth;
	}

	public void setCompileTimeDepth(int compileTimeDepth) {
		this.compileTimeDepth = compileTimeDepth;
	}

	public boolean isPropReference() {
		return isPropReference;
	}

	public void setPropReference(boolean isPropReference) {
		this.isPropReference = isPropReference;
	}

}
