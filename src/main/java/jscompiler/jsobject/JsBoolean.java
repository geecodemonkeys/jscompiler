package jscompiler.jsobject;

public class JsBoolean extends JsObjectBase {

	private boolean value;
	
	public JsBoolean(boolean value) {
		super();
		this.value = value;
	}

	@Override
	public int getCode() {
		return BOOLEAN;
	}

	@Override
	public Object getRealValue() {
		return value;
	}
	
	public boolean getValue() {
		return value;
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

}
