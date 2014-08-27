package jscompiler.execution;

import java.util.HashMap;
import java.util.Map;

public class Scope {

	private Scope parentScope;
	private int scopeDepth;
	private int offset = 0;
	private Map<String, SymbolLookup> bindings = new HashMap<String, SymbolLookup>();

	public Scope(Scope parent, int depth) {
		this.parentScope = parent;
		this.scopeDepth = depth;
	}

	public Scope getParentScope() {
		return parentScope;
	}

	public void setParentScope(Scope parentScope) {
		this.parentScope = parentScope;
	}

	public Map<String, SymbolLookup> getBindings() {
		return bindings;
	}

	public void setBindings(Map<String, SymbolLookup> bindings) {
		this.bindings = bindings;
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

}
