package jscompiler.execution.operators;

import jscompiler.execution.RefUtil;
import jscompiler.jsobject.JsBoolean;
import jscompiler.jsobject.JsNaN;
import jscompiler.jsobject.JsNumber;
import jscompiler.jsobject.JsObjectBase;
import jscompiler.jsobject.JsString;
import jscompiler.jsobject.JsUndefined;

public class Operators {
	
	public static JsObjectBase add(JsObjectBase left, JsObjectBase right) {
		left = RefUtil.getValue(left);
		right = RefUtil.getValue(right);
		JsObjectBase leftPrimitive = toPrimitive(left);
		JsObjectBase rightPrimitive = toPrimitive(right);
		if (leftPrimitive.getCode() == JsObjectBase.STRING || 
				rightPrimitive.getCode() == JsObjectBase.STRING) {
			String str = toString(leftPrimitive) + toString(rightPrimitive);
			JsString string = new JsString(str);
			return string;
		}

		if (leftPrimitive.getCode() == JsObjectBase.NAN || 
				rightPrimitive.getCode() == JsObjectBase.NAN) {
			
			return new JsNaN();
		}
		
		double leftDouble = toNumber(leftPrimitive);
		double rightDouble = toNumber(rightPrimitive);
		
		if (leftDouble == Double.NaN || rightDouble == Double.NaN) {
			return new JsNaN();
		}
		
		double sum = leftDouble + rightDouble;
		if (sum == Double.NaN) {
			return new JsNaN();
		} 
		
		JsNumber num = new JsNumber(sum);
		
		return num;
		
	}
	
	public static JsObjectBase substract(JsObjectBase left, JsObjectBase right) {
		left = RefUtil.getValue(left);
		right = RefUtil.getValue(right);
		
		double leftDouble = toNumber(left);
		double rightDouble = toNumber(right);
		
		if (leftDouble == Double.NaN || rightDouble == Double.NaN) {
			return new JsNaN();
		}
		
		double sum = leftDouble - rightDouble;
		if (sum == Double.NaN) {
			return new JsNaN();
		} 
		
		JsNumber num = new JsNumber(sum);
		
		return num;
		
	}
	
	public static JsObjectBase multiply(JsObjectBase left, JsObjectBase right) {
		left = RefUtil.getValue(left);
		right = RefUtil.getValue(right);
		
		double leftDouble = toNumber(left);
		double rightDouble = toNumber(right);
		
		if (leftDouble == Double.NaN || rightDouble == Double.NaN) {
			return new JsNaN();
		}
		
		double sum = leftDouble * rightDouble;
		if (sum == Double.NaN) {
			return new JsNaN();
		} 
		
		JsNumber num = new JsNumber(sum);
		
		return num;
		
	}
	
	public static JsObjectBase divide(JsObjectBase left, JsObjectBase right) {
		left = RefUtil.getValue(left);
		right = RefUtil.getValue(right);
		
		double leftDouble = toNumber(left);
		double rightDouble = toNumber(right);
		
		if (leftDouble == Double.NaN || rightDouble == Double.NaN) {
			return new JsNaN();
		}
		
		double sum = leftDouble / rightDouble;
		if (sum == Double.NaN) {
			return new JsNaN();
		} 
		
		JsNumber num = new JsNumber(sum);
		
		return num;
		
	}
	
	public static JsObjectBase modulus(JsObjectBase left, JsObjectBase right) {
		left = RefUtil.getValue(left);
		right = RefUtil.getValue(right);
		
		double leftDouble = toNumber(left);
		double rightDouble = toNumber(right);
		
		if (leftDouble == Double.NaN || rightDouble == Double.NaN) {
			return new JsNaN();
		}
		
		double sum = leftDouble % rightDouble;
		if (sum == Double.NaN) {
			return new JsNaN();
		} 
		
		JsNumber num = new JsNumber(sum);
		
		return num;
		
	}
	
	public static JsObjectBase lessThan(JsObjectBase left, JsObjectBase right) {
		left = RefUtil.getValue(left);
		right = RefUtil.getValue(right);
		JsObjectBase retVal = abstractComparison(left, right, true);
		if (retVal.getCode() == JsObjectBase.UNDEFINED) {
			return new JsBoolean(false);
		}
		return retVal;
	}
	
	public static JsObjectBase lessOrEq(JsObjectBase left, JsObjectBase right) {
		left = RefUtil.getValue(left);
		right = RefUtil.getValue(right);
		JsObjectBase retVal = abstractComparison(right, left, false);
		if (retVal.getCode() == JsObjectBase.UNDEFINED) {
			return new JsBoolean(false);
		}
		
		JsBoolean retBool = (JsBoolean) retVal;
		retBool.setValue(!retBool.getValue());
		return retVal;
	}
	
	public static JsObjectBase greaterThan(JsObjectBase left, JsObjectBase right) {
		left = RefUtil.getValue(left);
		right = RefUtil.getValue(right);
		JsObjectBase retVal = abstractComparison(right, left, false);
		if (retVal.getCode() == JsObjectBase.UNDEFINED) {
			return new JsBoolean(false);
		}
		return retVal;
	}
	
	public static JsObjectBase greaterOrEq(JsObjectBase left, JsObjectBase right) {
		left = RefUtil.getValue(left);
		right = RefUtil.getValue(right);
		JsObjectBase retVal = abstractComparison(left, right, true);
		if (retVal.getCode() == JsObjectBase.UNDEFINED) {
			return new JsBoolean(false);
		}
		
		JsBoolean retBool = (JsBoolean) retVal;
		retBool.setValue(!retBool.getValue());
		return retVal;
	}
	
	/**
	 * See section 11.11 in ECMAScript 262 5.1 edition spec
	 * @param left
	 * @param right
	 * @return
	 */
	public static JsObjectBase logicalAnd(JsObjectBase left, JsObjectBase right) {
		left = RefUtil.getValue(left);
		JsBoolean leftBool = toBoolean(left);
		if (!leftBool.getValue()) {
			return leftBool;
		}
		right = RefUtil.getValue(right);
		return right;
	}
	
	public static JsObjectBase logicalOr(JsObjectBase left, JsObjectBase right) {
		left = RefUtil.getValue(left);
		JsBoolean leftBool = toBoolean(left);
		if (leftBool.getValue()) {
			return leftBool;
		}
		right = RefUtil.getValue(right);
		return right;
	}
	

	public static JsObjectBase logicalNot(JsObjectBase value) {
		value = RefUtil.getValue(value);
		JsBoolean boolValue = toBoolean(value);
		return new JsBoolean(!boolValue.getValue());
	}
	

	public static JsObjectBase unaryMinus(JsObjectBase value) {
		value = RefUtil.getValue(value);
		if (type(value) == JsObjectBase.NAN) {
			return value;
		}
		double number = toNumber(value);
		return new JsNumber(-number);
	}

	public static JsObjectBase strictEquals(JsObjectBase left,
			JsObjectBase right) {
		left = RefUtil.getValue(left);
		right = RefUtil.getValue(right);
		
		if (type(left) != type(right)) {
			return new JsBoolean(false);
		}
		
		if (type(left) == JsObjectBase.UNDEFINED ||
				type(left) == JsObjectBase.NULL) {
			return new JsBoolean(true);
		}
		
		if (type(left) == JsObjectBase.NAN) {
			return new JsBoolean(false);
		}
		
		if (type(left) == JsObjectBase.NUMBER) {
			if (((JsNumber)left).getValue() == Double.NaN) {
				return new JsBoolean(false);
			}
			if (type(right) == JsObjectBase.NAN) {
				return new JsBoolean(false);
			}
			
			boolean result = ((JsNumber)left).getValue() == 
					((JsNumber)right).getValue();
			return new JsBoolean(result);
			
		}
		
		if (type(left) == JsObjectBase.STRING) {
			boolean result = ((JsString)left).getValue()
					.equals(((JsString)right).getValue());
			return new JsBoolean(result); 
		}
		
		if (type(left) == JsObjectBase.BOOLEAN) {
			boolean result = ((JsBoolean)left).getValue() == 
					((JsBoolean)right).getValue();
			return new JsBoolean(result); 
		}
		
		return new JsBoolean(left == right);
	}
	
	private static int type(JsObjectBase value) {
		return value.getCode();
	}
	
	private static JsObjectBase abstractComparison(JsObjectBase left, JsObjectBase right, boolean leftFirst) {
		JsObjectBase leftPrimitive = null;;
		JsObjectBase rightPrimitive = null;
		if (leftFirst) {
			leftPrimitive = toPrimitive(left, JsObjectBase.NUMBER);
			rightPrimitive = toPrimitive(right, JsObjectBase.NUMBER);
		} else {
			rightPrimitive = toPrimitive(right, JsObjectBase.NUMBER);
			leftPrimitive = toPrimitive(left, JsObjectBase.NUMBER);
		}
		if (leftPrimitive.getCode() == JsObjectBase.STRING && 
				rightPrimitive.getCode() == JsObjectBase.STRING) {
			String leftStr = ((JsString) leftPrimitive).getValue() == null ? "" : ((JsString) leftPrimitive).getValue();
			String rightStr = ((JsString) rightPrimitive).getValue() == null ? "" : ((JsString) rightPrimitive).getValue();
			if (leftStr.startsWith(rightStr)) {
				return new JsBoolean(false);
			}
			
			if (rightStr.startsWith(leftStr)) {
				return new JsBoolean(true);
			}
			
			for (int i = 0; i < Math.min(leftStr.length(), rightStr.length()); i++) {
				int leftChar = leftStr.charAt(i);
				int rightChar = rightStr.charAt(i);
				if (leftChar == rightChar) {
					continue;
				}
				return new JsBoolean(leftChar < rightChar);
			}
		}
		
		double leftDouble = toNumber(leftPrimitive);
		double rightDouble = toNumber(rightPrimitive);
		
		if (leftDouble == Double.NaN || rightDouble == Double.NaN) {
			return new JsUndefined();
		}
		
		if (leftDouble == Double.MIN_VALUE || rightDouble == Double.MAX_VALUE) {
			return new JsBoolean(true);
		}
		
		if (leftDouble == Double.MAX_VALUE || rightDouble == Double.MIN_VALUE) {
			return new JsBoolean(false);
		}
		
		boolean comparisonResult = leftDouble < rightDouble;
		
		JsBoolean retVal = new JsBoolean(comparisonResult);
		return retVal;
	}

	public static JsBoolean toBoolean(JsObjectBase obj) {
		obj = RefUtil.getValue(obj);
		if (obj.getCode() != JsObjectBase.BOOLEAN) {
			throw new RuntimeException("Not a boolean!");
		}
		return (JsBoolean) obj;
	}

	public static double toNumber(JsObjectBase obj) {
		if (obj.getCode() == JsObjectBase.NUMBER) {
			return (Double) obj.getRealValue();
		}
		return Double.NaN;
	}

	private static String toString(JsObjectBase obj) {
		if (obj.getCode() == JsObjectBase.STRING) {
			return ((JsString)obj).getValue();
		}
		return String.valueOf(obj.getRealValue());
	}

	private static JsObjectBase toPrimitive(JsObjectBase left) {
		return left;
	}
	
	private static JsObjectBase toPrimitive(JsObjectBase left, int hint) {
		return left;
	}

}
