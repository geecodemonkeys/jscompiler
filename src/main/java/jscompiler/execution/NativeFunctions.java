package jscompiler.execution;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class NativeFunctions {
	public static Map<String, Class<?>> importedClasses;
	private static Map<String, String> shortFullNameMap ;
	
	private static Map<String, Method> functions;
	static {
		shortFullNameMap = new HashMap<String, String>();
		importedClasses = new HashMap<String, Class<?>>();
		functions = new HashMap<String, Method>();
		try {
			Method method = NativeFunctions.class.getMethod("importClass", String.class);
			functions.put("importClass", method);
			functions.put("log", method);
			//importedClasses.put("NativeFunctions", NativeFunctions.class);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public static Map<String, Method> getFunctions() {
		return functions;
	}
	
	public static boolean isNativeFunctionOrClass(String name) {
		if (functions.containsKey(name)) {
			return true;
		}
		Class<?> clazz = importedClasses.get(shortFullNameMap.get(name));
		if (clazz == null) {
			return false;
		}
		return true;
	}
	
	public static void importClass(String clazzName) {
		Class<?> clazz = null;
		try {
			clazz = Class.forName(clazzName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		shortFullNameMap.put(clazz.getSimpleName(), clazz.getName());
		importedClasses.put(clazz.getName(), clazz);
	}

}
