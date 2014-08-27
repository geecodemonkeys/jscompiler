package jscompiler.jsobject;

public class JsNumber extends JsObjectBase {
	int intValue = 0;
	double value = 0.0;
	
	public JsNumber(double value) {
		super();
		this.value = value;
	}
	
	public JsNumber(int value) {
		super();
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public int getCode() {
		return NUMBER;
	}
	
	@Override
	public String toString() {
		return "JsNumber [value=" + value + "]";
	}

	@Override
	public Object getRealValue() {
		return value;
	}

}
