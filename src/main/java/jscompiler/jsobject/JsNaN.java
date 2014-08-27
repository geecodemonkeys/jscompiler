package jscompiler.jsobject;

public class JsNaN extends JsObjectBase {

	@Override
	public int getCode() {
		return NAN;
	}

	@Override
	public Object getRealValue() {
		return "NaN";
	}

}
