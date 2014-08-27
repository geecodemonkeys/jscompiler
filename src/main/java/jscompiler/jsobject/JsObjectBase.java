package jscompiler.jsobject;


public abstract class JsObjectBase {
	
	public final static int NUMBER = 1;
	public final static int STRING = 2;
	public final static int REFERENCE = 3;
	public final static int OBJECT = 4;
	public final static int NAN = 5;
	public final static int UNDEFINED = 6;
	public final static int FUNCTION = 7;
	public final static int BOOLEAN = 8;
	public final static int NULL = 9;
	
	public abstract int getCode();
	
	public abstract Object getRealValue();

	
}
