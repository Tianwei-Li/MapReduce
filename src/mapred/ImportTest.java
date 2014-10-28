package mapred;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class ImportTest {
	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		Class<?> m = Class.forName("tool.CompComp$Map");
		Method method = m.getMethod("map", String.class, String.class);
		System.out.println(method.getReturnType());
		System.out.println(method.invoke(m.newInstance(), "key","value"));
	}
}
