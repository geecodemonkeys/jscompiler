package jscompiler.execution;

import java.util.ArrayList;
import java.util.List;

import jscompiler.jsobject.JsNumber;
import jscompiler.jsobject.JsObjectBase;
import jscompiler.jsobject.JsReference;

public class Context {
	
	private List<ScopeFrame> scopes = new ArrayList<ScopeFrame>();
	private JsObjectBase thisValue;

	public ScopeFrame popScope() {
		if (scopes.size() == 0) {
			return null;
		}
		ScopeFrame frame = scopes.get(scopes.size() - 1);
		scopes.remove(scopes.size() - 1);
		return frame;
	}
	
	public Context clone() {
		Context ctx = new Context();
		ctx.setScopes(new ArrayList<ScopeFrame>(scopes));
		return ctx;
	}
	
	public ScopeFrame peek() {
		return scopes.get(scopes.size() - 1);
	}
	
	public void pushScope(ScopeFrame scope) {
		scopes.add(scope);
	}
	
	public void updateReference(JsReference ref) {
		JsReference storedRef = lookupGeneric(ref.getId(), ref.getScopeDepth(), 
				ref.getOffset(), ref.getCompileTimeDepth());
		if (storedRef == null) {
			ScopeFrame scopeFrame = peek();
			scopeFrame.addVarReference(ref);
			return;
		}
		storedRef.setValue(RefUtil.getRefValue(ref));
	}

	public JsNumber lookup(JsReference left) {
		try {
			JsReference ref = lookupGeneric(left.getId(), 
					left.getScopeDepth(), left.getOffset(), 
					left.getCompileTimeDepth());
			return (JsNumber) RefUtil.getRefValue(ref);
		} catch (ClassCastException e) {
			throw new RuntimeException("Not a Number " + left.getId());
		} catch (Exception e) {
			throw new RuntimeException("Reference Exception!");
		}
		
	}
	
	public JsReference lookupGeneric(String name, int depth, 
			int offset, int compileTimeDepth) {
		
		//this is 0 or negative number relative to the current
		//frame
		try {
			JsReference ref = null;
			for (int i = scopes.size() -1; ref == null && i >= 0; i--) {
				ScopeFrame frame = scopes.get(i);
				for (int j = frame.getVariables().size() - 1; j >=0; j--) {
					JsReference refVar = frame.getVariables().get(j);
					if (refVar.getId().equals(name)) {
						ref = refVar;
						break;
					}
				}
			}
			return ref;
		} catch (Exception e) {
			return null;
		}
	}
	
	public int getBase(String name) {
		
		//this is 0 or negative number relative to the current
		//frame
		try {
			JsReference ref = null;
			for (int i = scopes.size() -1; ref == null && i >= 0; i--) {
				ScopeFrame frame = scopes.get(i);
				for (int j = frame.getVariables().size() - 1; j >=0; j--) {
					JsReference refVar = frame.getVariables().get(j);
					if (refVar.getId().equals(name)) {
						return i;
					}
				}
			}
			return -1;
		} catch (Exception e) {
			return -1;
		}
	}

	public List<ScopeFrame> getScopes() {
		return scopes;
	}

	public void setScopes(List<ScopeFrame> scopes) {
		this.scopes = scopes;
	}

	public JsObjectBase getThisValue() {
		return thisValue;
	}

	public void setThisValue(JsObjectBase thisValue) {
		this.thisValue = thisValue;
	}
	
}

/*
 
	public JsNumber lookup(JsReference left) {
		int offset = left.getOffset();
		int depth = left.getScopeDepth();
		try {
			int currFrameIndex = scopes.size() - 1;
			int runtimeOffset = 0;//currFrameIndex - left.getCompileTimeDepth();
			List<JsReference> list = scopes.get(
					currFrameIndex + depth - runtimeOffset).getVariables();
			JsReference ref = list.get(offset);
			return (JsNumber) ref.getValue();
		} catch (ClassCastException e) {
			throw new RuntimeException("Not a Number " + left.getId());
		} catch (Exception e) {
			throw new RuntimeException("Reference Exception!");
		}
		
	}
	
	public JsReference lookupGeneric(int depth, int offset, int compileTimeDepth) {
		
		//this is 0 or negative number relative to the current
		//frame
		try {
			int currFrameIndex = scopes.size() - 1;
			int runtimeOffset = 0;//currFrameIndex - compileTimeDepth;
			List<JsReference> list = scopes.get(
					currFrameIndex + depth - runtimeOffset).getVariables();
			JsReference ref = list.get(offset);
			return ref;
		} catch (Exception e) {
			return null;
		}
	}*/
