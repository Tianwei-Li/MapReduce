package util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class MRType {
	@SuppressWarnings("rawtypes")
	public static Class getGenericClass(ParameterizedType parameterizedType, int i, int j) {     
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
	public static Class getGenericClass(ParameterizedType parameterizedType, int j) {     
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
	public static Class getClass(Type type, int i, int j) {     
        if (type instanceof ParameterizedType) { // generic
            return getGenericClass((ParameterizedType) type, i, j);     
        } else if (type instanceof TypeVariable) {     
            return (Class) getClass(((TypeVariable) type).getBounds()[0], 0, j); // generic variable 
        } else {// type is a class 
            return (Class) type;     
        }     
    }     
}
