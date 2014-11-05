package mapred;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;


public class ImportTest {
	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		//Class<?> m = Class.forName("tool.CompComp$Map");
		Class<?> m = Class.forName("test.MapRedTest$testMapper");
		System.out.println(getClass(m.getGenericInterfaces()[0], 2, 0));
//		Method method = m.getMethod("map", String.class, String.class, List.class);
//		List<Pair<String, String>> pair = new ArrayList<>();
//		System.out.println(method.getReturnType());
//		method.invoke(m.newInstance(), "key","value", pair);
//		method.invoke(m.newInstance(), "key1","value1", pair);
//		System.out.println(pair);
//		
//		System.out.println(Arrays.toString(method.getGenericParameterTypes()));
//		System.out.println(method.getGenericParameterTypes()[2]);
//		Class<?> list = getClass(method.getGenericParameterTypes()[2], 0, 2);
//		
//		System.out.println(list);
	}
	
	@SuppressWarnings("rawtypes")
	private static Class getGenericClass(ParameterizedType parameterizedType, int i, int j) {     
        Object genericClass = parameterizedType.getActualTypeArguments()[i];     
        if (genericClass instanceof ParameterizedType) { // multi-generic
        	return getGenericClass((ParameterizedType) genericClass, j);
            //return (Class) ((ParameterizedType) genericClass).getRawType();     
        } else if (genericClass instanceof GenericArrayType) { // generic array 
            return (Class) ((GenericArrayType) genericClass).getGenericComponentType();     
        } else if (genericClass instanceof TypeVariable) { // generic variable
            return (Class) getClass(((TypeVariable) genericClass).getBounds()[0], 0, j);     
        } else {     
            return (Class) genericClass;     
        }     
    }   
	
	@SuppressWarnings("rawtypes")
	private static Class getGenericClass(ParameterizedType parameterizedType, int j) {     
        Object genericClass = parameterizedType.getActualTypeArguments()[j];     
        if (genericClass instanceof ParameterizedType) { // multi-generic
        	return getGenericClass((ParameterizedType) genericClass, j);
            //return (Class) ((ParameterizedType) genericClass).getRawType();     
        } else if (genericClass instanceof GenericArrayType) { // generic array 
            return (Class) ((GenericArrayType) genericClass).getGenericComponentType();     
        } else if (genericClass instanceof TypeVariable) { // generic variable
            return (Class) getClass(((TypeVariable) genericClass).getBounds()[0], 0, j);     
        } else {     
            return (Class) genericClass;     
        }     
    }   
	@SuppressWarnings("rawtypes")
	private static Class getClass(Type type, int i, int j) {     
        if (type instanceof ParameterizedType) { // generic
            return getGenericClass((ParameterizedType) type, i, j);     
        } else if (type instanceof TypeVariable) {     
            return (Class) getClass(((TypeVariable) type).getBounds()[0], 0, j); // generic variable 
        } else {// type is a class 
            return (Class) type;     
        }     
    }     
	
}
