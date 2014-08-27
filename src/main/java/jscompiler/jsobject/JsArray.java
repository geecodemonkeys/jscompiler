package jscompiler.jsobject;

public class JsArray extends JsObject {

	public JsArray(JsObjectBase __proto__) {
		super(__proto__);
		properties.put("length", new JsNumber(0.0));
	}
	
	public void addValue(int index, JsObjectBase value) {
		int length = (int) ((JsNumber)properties.get("length")).getValue();
		if (index >= length) {
			length = index + 1;
		}
		properties.put(String.valueOf(index), value);
		properties.put("length", new JsNumber(length));
	}

	@Override
	public void setProp(String name, JsObjectBase value) {
		int index = 0;
		int length = (int) ((JsNumber)properties.get("length")).getValue();
		try {
			index = Integer.parseInt(name);
			if (index >= length) {
				length = index + 1;
			}
		} catch (NumberFormatException ex) {
			super.setProp(name, value);
			return;
		}
		properties.put(String.valueOf(index), value);
		properties.put("length", new JsNumber(length));
	}
	
	@Override
	public Object getRealValue() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		int length = (int)((JsNumber)properties.get("length")).getValue();
		for (int i = 0; i < length; i++) {
			if (properties.containsKey(String.valueOf(i))) {
				Object valueToAdd = properties.get(String.valueOf(i)).getRealValue();
				sb.append(valueToAdd);
			} else {
				sb.append("undefined");
			}
			if (i < length - 1) {				
				sb.append(", ");
			}
		}
		sb.append("]");
		return sb.toString();
	}

}
