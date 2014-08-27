package jscompiler.execution;

import jscompiler.jsobject.JsObject;
import jscompiler.jsobject.JsObjectBase;
import jscompiler.jsobject.JsReference;
import jscompiler.jsobject.JsUndefined;

public class RefUtil {

	public static JsObjectBase getValue(JsObjectBase obj) {
		if (obj.getCode() == JsObjectBase.REFERENCE) {
			JsReference ref = (JsReference) obj;
			if (ref.getValue() != null) {
				return getRefValue(ref);
			}
		}
		return obj;
	}
	
	public static JsObjectBase getRefValue(JsReference ref) {
		if (!ref.isPropReference()) {
			return ref.getValue();
		}
		JsObjectBase propValue = lookupPropValue(ref.getValue(), ref.getId());
		return propValue;
	}
	
	public static JsObjectBase getRefBase(JsReference ref) {
		if (ref.isPropReference()) {
			return ref.getValue();
		}
		JsObjectBase propValue = lookupPropValue(ref.getValue(), ref.getId());
		return propValue;
		
	}
	
	public static JsObjectBase lookupPropValue(JsObjectBase obj, String prop) {
		if (obj.getCode() == JsObjectBase.REFERENCE) {
			JsReference ref = (JsReference) obj;
			if (ref.getValue() != null) {
				obj =  ref.getValue();
			}
		}
		//TODO do only objects have properties?
		if (obj.getCode() != JsObjectBase.OBJECT && 
				obj.getCode() != JsObjectBase.FUNCTION) {
			throw new RuntimeException("Not an object!");
		}
		JsObject object = (JsObject) obj;
		JsObjectBase propValue = null;
		do {
			propValue = object.getProp(prop);
			if (propValue == null) {
				//----------------see endVisit(ASTObjectConst) comment
				//JsObjectBase baseObj = object.getProp(JsObject.PROTO);
				//object = (JsObject) unpackGeneric(baseObj);
				//------------------------------------------------------
				object = (JsObject) object.getProp(JsObject.PROTO);
			}
		} while (propValue == null && object != null);
		
		if (propValue == null) {
			return new JsUndefined();
		}
		return propValue;
	}
	
	public static void setPropValue(JsReference ref, String prop, JsObjectBase value) {
		//TODO do only objects have properties?
		if (ref.getValue().getCode() != JsObjectBase.OBJECT && 
				ref.getValue().getCode() != JsObjectBase.FUNCTION) {
			throw new RuntimeException("Not an object!");
		}
		JsObject initialObject = (JsObject) ref.getValue();
		JsObject object = (JsObject) ref.getValue();
		JsObjectBase propValue = null;
		do {
			propValue = object.getProp(prop);
			if (propValue == null) {
				object = (JsObject) object.getProp(JsObject.PROTO);
			} else {
				object.setProp(prop, value);
			}
		} while (propValue == null && object != null);
		if (propValue == null) {
			initialObject.setProp(prop, value);
		}
	}
}
