package jscompiler.jsobject;

public class JsString extends JsObjectBase {

	private String value;
	
	public JsString(String value) {
		super();
		this.value = value;
	}

	@Override
	public int getCode() {
		return STRING;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "JsString [value=" + value + "]";
	}

	@Override
	public Object getRealValue() {
		return "\"" + value + "\"";
	}

}
