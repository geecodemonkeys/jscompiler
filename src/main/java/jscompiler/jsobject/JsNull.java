package jscompiler.jsobject;

public class JsNull extends JsObjectBase {

	@Override
	public int getCode() {
		return NULL;
	}

	@Override
	public Object getRealValue() {
		return null;
	}

}
