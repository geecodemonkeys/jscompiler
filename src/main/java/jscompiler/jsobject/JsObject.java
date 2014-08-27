package jscompiler.jsobject;

import java.util.HashMap;
import java.util.Map;

public class JsObject extends JsObjectBase {
	
	public static final String PROTO = "__proto__";
	public static final String PROTOTYPE_EXPLICIT = "prototype";
	public static final String CONSTRUCTOR = "constructor";
	protected Map<String, JsObjectBase> properties;
	
	public JsObject(JsObjectBase __proto__) {
		properties = new HashMap<String, JsObjectBase>();
		properties.put(PROTO, __proto__);
	}
	
	public void setProp(String name, JsObjectBase value) {
		properties.put(name, value);
	}
	
	public JsObjectBase getProp(String name) {
		return properties.get(name);
	}

	@Override
	public int getCode() {
		return OBJECT;
	}

	@Override
	public Object getRealValue() {
		StringBuilder sb = new StringBuilder();
		sb.append("Object { ");
		int i = properties.size() - 1;
		for (Map.Entry<String, JsObjectBase> entry : properties.entrySet()) {
			i--;
			if (PROTO.equals(entry.getKey())) {
				continue;
			}
			sb.append(entry.getKey()).append(" : ")
				.append(entry.getValue().getRealValue());
			if (i > 0) {				
				sb.append(" , ");
			}
		}
		sb.append(" }");
		return sb.toString();
	}

	public Map<String, JsObjectBase> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, JsObjectBase> properties) {
		this.properties = properties;
	}

}
