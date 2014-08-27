package jscompiler.jsobject;

public class JsUndefined extends JsObjectBase {

	@Override
	public int getCode() {
		return UNDEFINED;
	}

	@Override
	public Object getRealValue() {
		return "undefined";
	}

}
